package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.utils.StringUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.form.FormField;
import org.camunda.bpm.engine.form.TaskFormData;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.*;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ConfInfoVO;
import org.jeecg.modules.workflow.entity.vo.FormDataCorrelationVO;
import org.jeecg.modules.workflow.entity.vo.FormFieldVO;
import org.jeecg.modules.workflow.entity.vo.FormRole;
import org.jeecg.modules.workflow.service.WfFormService;
import org.jeecg.modules.workflow.utils.HttpUtil;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Transient;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import static org.jeecg.modules.workflow.common.DataType.DATA_TYPE_DATE;
import static org.jeecg.modules.workflow.common.DataType.DATA_TYPE_ENUM;
import static org.jeecg.modules.workflow.common.WorkflowExtensionConstant.*;
import static org.jeecg.modules.workflow.common.WorkflowFormConstant.*;
import static org.jeecg.modules.workflow.common.WorkflowMessageConstant.*;

/**
 * @Description: 工作流表单业务层
 * @author: liuchun
 * @date: 2021年03月18日 9:41
 */
@Service
@Slf4j
public class WfFormServiceImpl implements WfFormService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取任务节点相关流程变量
     * @param taskId : 任务节点id
     * @param processInstanceId : 流程实例id
     * @return : List<FormField>
     */
    @Override
    public List<FormFieldVO> showFormByTaskId(String taskId, String processInstanceId, String procdefId) {

        //判断传入参数是否为空
        if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(processInstanceId) || StringUtils.isEmpty(procdefId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        List<FormFieldVO> formFieldVOS = new ArrayList<>();
        String examine = "";

        //查询历史流程变量表中变量
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        Map<String,Object> varMap = new HashMap<>();

        for(HistoricVariableInstance historicVariableInstance : historicVariableInstances){
            varMap.put(historicVariableInstance.getName(),historicVariableInstance.getValue());
        }

        //获取活动id
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        String activityId = taskInstance.getTaskDefinitionKey();

        //获取bpmn配置文件
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);

        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById(activityId);
        ExtensionElements extensionElements = null;
        Collection<CamundaFormData> formDatas = null;

        //如果该节点是开始节点就通过类型转换为UserTask类型
        UserTask userTask = (UserTask) modelElementInstance;
        extensionElements = userTask.getExtensionElements();

        //如果该节点的子节点不是null，就继续便利子节点
        if(extensionElements != null){
            Collection<CamundaProperties> childElementsByType = extensionElements.getChildElementsByType(CamundaProperties.class);

            for(CamundaProperties camundaProperties : childElementsByType){
                Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
                for(CamundaProperty camundaProperty : camundaProperties1){
                    if(WORK_FLOW_EXTENSION_EXAMINE.equals(camundaProperty.getCamundaName())){
                        examine = camundaProperty.getCamundaValue();

                        //如果流程中有调用活动节点
                        if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
                            Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
                            for(CallActivity callActivity : callActivities){
                                procdefId = repositoryService.createProcessDefinitionQuery()
                                        .processDefinitionKey(callActivity.getCalledElement())
                                        .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                                        .singleResult()
                                        .getId();

                                ConfInfoVO confInfoVO = UtilTools.getExaminesByProcedefId(procdefId);

                                if(confInfoVO.getExamines().contains(examine)){
                                    bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
                                }
                            }
                        }
                        modelElementInstance = bpmnModelInstance.getModelElementById(examine);
                        userTask = (UserTask)modelElementInstance;
                        extensionElements = userTask.getExtensionElements();
                    }
                }
            }

            formDatas = extensionElements.getChildElementsByType(CamundaFormData.class);
            for(CamundaFormData formData : formDatas){
                Collection<CamundaFormField> formFields = formData.getCamundaFormFields();

                //获取对应formField信息
                for(CamundaFormField formField : formFields){
                    FormFieldVO formFieldVO = new FormFieldVO();

                    formFieldVO.setId(formField.getCamundaId());
                    formFieldVO.setLabel(formField.getCamundaLabel());
                    formFieldVO.setType(formField.getCamundaType());
                    formFieldVO.setDefaultValue(formField.getCamundaDefaultValue());

                    if(DATA_TYPE_DATE.equals(formField.getCamundaType())){
                        formFieldVO.setValue(stringRedisTemplate.opsForValue().get(processInstanceId+formField.getCamundaId()));
                    }else{
                        formFieldVO.setValue(varMap.get(formField.getCamundaId()));
                    }

                    //如果是枚举，还需取出枚举map
                    if(DATA_TYPE_ENUM.equals(formField.getCamundaType())){
                        Collection<CamundaValue> camundaValues = formField.getCamundaValues();
                        Map<String,String> enumMap = new HashMap<>();

                        for(CamundaValue camundaProperties : camundaValues){
                            enumMap.put(camundaProperties.getCamundaId(),camundaProperties.getCamundaName());
                        }

                        formFieldVO.setProperty(enumMap);
                    }

                    //将所有动态表单属性放入list中
                    formFieldVOS.add(formFieldVO);

                }

            }
        }
        return formFieldVOS;
    }

    /**
     * 提交表单和意见 ：通过任务id，流程实例id，评论内容和表单中属性
     * @param formData ：所有传参
     *          formMap : 表单属性
     *          taskId ：任务节点id
     *          processInstanceId ：流程实例id
     *          message ：评论内容
     */
    @Override
    @Transient
    public void submitForm(FormDataCorrelationVO formData) {

        //判空
        if(StringUtils.isEmpty(formData.toString())){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        FormService formService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getFormService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        //判断流程实例是否被挂起
        String processInstanceId = formData.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if(processInstance.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_SUSPENSION);
        }

        //获取formMap集合中的表单数据 ：以id-value形式存储
        Map<String,Object> formMap = formData.getFormMap();
        String message = formData.getMessage();
        String taskId = formData.getTaskId();

        //判断改活动节点中的表单数据是否为空
        if(formService.getTaskFormData(taskId).getFormFields() != null){
            TaskFormData taskFormData = formService.getTaskFormData(taskId);
            List<FormField> formFields = taskFormData.getFormFields();

            //遍历表单表中所有id，查询是否有时间类型
            for(FormField formField : formFields){

                //如果是时间类型的话存储到redis中
                if(DATA_TYPE_DATE.equals(formField.getTypeName())) {
                    String var = formField.getId();
                    String value = formMap.get(var).toString();

                    stringRedisTemplate.opsForValue().set(processInstanceId + var, value);
                }
            }
        }

        if(message != null){
            taskService.createComment(taskId,processInstanceId,message);
        }
        formService.submitTaskForm(taskId,formMap);
    }

    /**
     * 展示外置表单根据任务id
     * @param taskId : 任务id
     * @return String : 外置表单字符串
     * @throws IOException
     */
    @Override
    public Map<String, Object> showOutFormByTaskId(String taskId) throws IOException {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        FormService formService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getFormService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();

        if(StringUtils.isEmpty(taskId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_PARAMETER_NULL);
        }

        //根据taskId获取流程定义id和任务定义key
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        String processDefinitionId = taskInstance.getProcessDefinitionId();
        String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
        String processInstId = taskInstance.getProcessInstanceId();

        //根据taskId获取各个表id
        Map<String,Object> map = new HashMap<>();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        String deploymentId = processDefinition.getDeploymentId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

        String taskFormKey = "";

        String examine = UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_EXAMINE);

        //如果是申请人角色，直接用当前活动节点id查外置表单
        if(WORK_FLOW_EXTENSION_APPLY.equals(UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_ROLE))){
            taskFormKey = formService.getTaskFormKey(processDefinitionId, taskDefinitionKey);
        }else if(!UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_EXAMINE).isEmpty()){
            //查询审批节点真正对应的外置表单
            String activityId = examine;

            //查询调用活动中的活动节点
            if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
                Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
                for(CallActivity callActivity : callActivities){
                    processDefinitionId = repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(callActivity.getCalledElement())
                            .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                            .singleResult()
                            .getId();

                    //考虑多个调用活动的情况
                    ConfInfoVO confInfoVO = UtilTools.getExaminesByProcedefId(processDefinitionId);
                    if(confInfoVO.getExamines().contains(activityId)){
                        taskFormKey = formService.getTaskFormKey(processDefinitionId,activityId);
                        deploymentId = repositoryService.createProcessDefinitionQuery()
                                .processDefinitionId(processDefinitionId)
                                .singleResult()
                                .getDeploymentId();
                    }
                }
            }else{
                taskFormKey = formService.getTaskFormKey(processDefinitionId,activityId);
            }
        }

        InputStream inputStream = repositoryService.getResourceAsStream(deploymentId,taskFormKey);

        //获取json字符串
        BufferedReader bw = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String tempStr;
        while((tempStr = bw.readLine())!=null){
            sb.append(tempStr);
        }
        inputStream.close();

        map.put(WORK_FLOW_EXTERNAL_FORM,sb.toString());

        Map<String,Object> variables = new HashMap<>();

        List<HistoricVariableInstance> list = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstId).list();
        for(HistoricVariableInstance historicVariableInstance : list){
            variables.put(historicVariableInstance.getName(),historicVariableInstance.getValue());
        }
        if(variables != null) {

            //要求返回数据为空
            Set keySet = variables.keySet();
            Iterator iterator = keySet.iterator();

            while (iterator.hasNext()) {
                Object key = iterator.next();
                Object value = variables.get(key);
                if (value == null) {
                    iterator.remove();
                }
            }
            map.put(WORK_FLOW_MAP_FORM, variables);
        }
        //判断是否是第一次加载
        String firstTaskId = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService()
                .createHistoricTaskInstanceQuery()
                .processInstanceId(processInstId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list()
                .get(0)
                .getId();
        if (taskId.equals(firstTaskId)){
            map.put("isInit",true);
        }else {
            map.put("isInit",false);
        }
        //增加动态数据源信息
        String token = HttpUtil.getToken();
        String formJson = HttpUtil.getFormDataSource(token, taskFormKey);
        map.put("dataSource",formJson);
        return map;
    }


    /**
     * 驳回（跳转到开始节点）
     * @param formData
     *          formMap:表单 key value 值
     *          taskId:任务id
     *          processInstanceId:实例id
     *          message:意见内容
     */
    @Override
    public void jumpStartEvent(FormDataCorrelationVO formData) {

        //判断流程实例是否被挂起
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();

        String processInstanceId = formData.getProcessInstanceId();

        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if(processInstance.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_SUSPENSION);
        }

        String taskId = formData.getTaskId();

        String message= formData.getMessage();

        ActivityInstance tree = runtimeService.getActivityInstance(processInstanceId);
        List<HistoricActivityInstance> resultList = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .activityType(WORK_FLOW_TYPE_STARTEVENT)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .asc()
                .list();

        //得到开始节点的id
        HistoricActivityInstance historicActivityInstance = resultList.get(0);
        String toActId = historicActivityInstance.getActivityId();

        if(message != null){
            taskService.createComment(taskId, processInstanceId, WORK_FLOW_REJECT_REASON + message);
        }

        //启动目标活动节点，并关闭相关任务
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(tree.getId())
                .startBeforeActivity(toActId)
                .execute();
    }

     /**
     * 返回formKey根据任务id
     * @param taskId : 任务id
     * @return : 返回formKey的值用来判断走的是那个表单
     */
    @Override
    public FormRole showFormKeyByTaskId(String taskId) {

        if(StringUtils.isEmpty(taskId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_PARAMETER_NULL);
        }

        FormService formService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getFormService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //根据taskId获取流程定义id和任务定义key
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        String processDefinitionId = taskInstance.getProcessDefinitionId();
        String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
        String taskFormKey = "";

        //获取扩展属性
        //读取Bpmn文件获取配置信息
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

        //因为任务节点都是任务节点，所以直接类型转换
        ModelElementInstance modelElementById = bpmnModelInstance.getModelElementById(taskDefinitionKey);
        UserTask userTask = (UserTask) modelElementById;
        ExtensionElements extensionElements = userTask.getExtensionElements();

        Collection<CamundaProperties> childElementsByType = extensionElements.getChildElementsByType(CamundaProperties.class);

        FormRole formRole = new FormRole();

        for(CamundaProperties camundaProperties : childElementsByType){
            Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
            for(CamundaProperty camundaProperty : camundaProperties1){
                if(WORK_FLOW_EXTENSION_ROLE.equals(camundaProperty.getCamundaName())){
                    formRole.setRole(camundaProperty.getCamundaValue());
                    //如果是申请人角色，直接用当前活动节点id查外置表单
                    if(WORK_FLOW_EXTENSION_APPLY.equals(camundaProperty.getCamundaValue())){
                        formRole.setFormKey(formService.getTaskFormKey(processDefinitionId,taskDefinitionKey));
                    }
                }else if(WORK_FLOW_EXTENSION_EXAMINE.equals(camundaProperty.getCamundaName())){

                    //查询审批节点真正对应的外置表单
                    String activityId = camundaProperty.getCamundaValue();

                    //查询调用活动中的活动节点
                    if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
                        Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
                        for(CallActivity callActivity : callActivities){
                            processDefinitionId = repositoryService.createProcessDefinitionQuery()
                                                    .processDefinitionKey(callActivity.getCalledElement())
                                                    .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                                                    .singleResult()
                                                    .getId();

                            //考虑多个调用活动的情况
                            ConfInfoVO confInfoVO = UtilTools.getExaminesByProcedefId(processDefinitionId);
                            if(confInfoVO.getExamines().contains(activityId)){
                                taskFormKey = formService.getTaskFormKey(processDefinitionId,activityId);
                            }
                        }
                    }else{
                        taskFormKey = formService.getTaskFormKey(processDefinitionId,activityId);
                    }

                    formRole.setFormKey(taskFormKey);
                }
            }
        }

        return formRole;
    }

    /**
     * 保存表单参数
     * @param taskId : 任务id
     * @param variables : 表单参数信息
     * @return : 返回是否保存成功信息
     */
    @Override
    public Result<Object> saveFormVar(String taskId, Map<String, Object> variables) {

        //判空
        if(taskId == null){
            throw new HttpServerErrorException(HttpStatus.MULTI_STATUS, WORK_FLOW_PARAMETER_NULL);
        }

        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        //保存表单参数
        taskService.setVariables(taskId,variables);

        return Result.OK();
    }
}
