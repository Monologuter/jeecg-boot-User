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
 * @Description: ????????????????????????
 * @author: liuchun
 * @date: 2021???03???18??? 9:41
 */
@Service
@Slf4j
public class WfFormServiceImpl implements WfFormService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * ????????????????????????????????????
     * @param taskId : ????????????id
     * @param processInstanceId : ????????????id
     * @return : List<FormField>
     */
    @Override
    public List<FormFieldVO> showFormByTaskId(String taskId, String processInstanceId, String procdefId) {

        //??????????????????????????????
        if(StringUtils.isEmpty(taskId) || StringUtils.isEmpty(processInstanceId) || StringUtils.isEmpty(procdefId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        List<FormFieldVO> formFieldVOS = new ArrayList<>();
        String examine = "";

        //????????????????????????????????????
        List<HistoricVariableInstance> historicVariableInstances = historyService.createHistoricVariableInstanceQuery().processInstanceId(processInstanceId).list();
        Map<String,Object> varMap = new HashMap<>();

        for(HistoricVariableInstance historicVariableInstance : historicVariableInstances){
            varMap.put(historicVariableInstance.getName(),historicVariableInstance.getValue());
        }

        //????????????id
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();
        String activityId = taskInstance.getTaskDefinitionKey();

        //??????bpmn????????????
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);

        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById(activityId);
        ExtensionElements extensionElements = null;
        Collection<CamundaFormData> formDatas = null;

        //??????????????????????????????????????????????????????UserTask??????
        UserTask userTask = (UserTask) modelElementInstance;
        extensionElements = userTask.getExtensionElements();

        //?????????????????????????????????null???????????????????????????
        if(extensionElements != null){
            Collection<CamundaProperties> childElementsByType = extensionElements.getChildElementsByType(CamundaProperties.class);

            for(CamundaProperties camundaProperties : childElementsByType){
                Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
                for(CamundaProperty camundaProperty : camundaProperties1){
                    if(WORK_FLOW_EXTENSION_EXAMINE.equals(camundaProperty.getCamundaName())){
                        examine = camundaProperty.getCamundaValue();

                        //????????????????????????????????????
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

                //????????????formField??????
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

                    //????????????????????????????????????map
                    if(DATA_TYPE_ENUM.equals(formField.getCamundaType())){
                        Collection<CamundaValue> camundaValues = formField.getCamundaValues();
                        Map<String,String> enumMap = new HashMap<>();

                        for(CamundaValue camundaProperties : camundaValues){
                            enumMap.put(camundaProperties.getCamundaId(),camundaProperties.getCamundaName());
                        }

                        formFieldVO.setProperty(enumMap);
                    }

                    //?????????????????????????????????list???
                    formFieldVOS.add(formFieldVO);

                }

            }
        }
        return formFieldVOS;
    }

    /**
     * ????????????????????? ???????????????id???????????????id?????????????????????????????????
     * @param formData ???????????????
     *          formMap : ????????????
     *          taskId ???????????????id
     *          processInstanceId ???????????????id
     *          message ???????????????
     */
    @Override
    @Transient
    public void submitForm(FormDataCorrelationVO formData) {

        //??????
        if(StringUtils.isEmpty(formData.toString())){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        FormService formService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getFormService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        //?????????????????????????????????
        String processInstanceId = formData.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();

        if(processInstance.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_SUSPENSION);
        }

        //??????formMap???????????????????????? ??????id-value????????????
        Map<String,Object> formMap = formData.getFormMap();
        String message = formData.getMessage();
        String taskId = formData.getTaskId();

        //???????????????????????????????????????????????????
        if(formService.getTaskFormData(taskId).getFormFields() != null){
            TaskFormData taskFormData = formService.getTaskFormData(taskId);
            List<FormField> formFields = taskFormData.getFormFields();

            //????????????????????????id??????????????????????????????
            for(FormField formField : formFields){

                //????????????????????????????????????redis???
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
     * ??????????????????????????????id
     * @param taskId : ??????id
     * @return String : ?????????????????????
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

        //??????taskId??????????????????id???????????????key
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        String processDefinitionId = taskInstance.getProcessDefinitionId();
        String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
        String processInstId = taskInstance.getProcessInstanceId();

        //??????taskId???????????????id
        Map<String,Object> map = new HashMap<>();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        String deploymentId = processDefinition.getDeploymentId();
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

        String taskFormKey = "";

        String examine = UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_EXAMINE);

        //??????????????????????????????????????????????????????id???????????????
        if(WORK_FLOW_EXTENSION_APPLY.equals(UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_ROLE))){
            taskFormKey = formService.getTaskFormKey(processDefinitionId, taskDefinitionKey);
        }else if(!UtilTools.getExtValByKey(processDefinitionId, taskDefinitionKey, WORK_FLOW_EXTENSION_EXAMINE).isEmpty()){
            //?????????????????????????????????????????????
            String activityId = examine;

            //????????????????????????????????????
            if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
                Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
                for(CallActivity callActivity : callActivities){
                    processDefinitionId = repositoryService.createProcessDefinitionQuery()
                            .processDefinitionKey(callActivity.getCalledElement())
                            .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                            .singleResult()
                            .getId();

                    //?????????????????????????????????
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

        //??????json?????????
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

            //????????????????????????
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
        //??????????????????????????????
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
        //???????????????????????????
        String token = HttpUtil.getToken();
        String formJson = HttpUtil.getFormDataSource(token, taskFormKey);
        map.put("dataSource",formJson);
        return map;
    }


    /**
     * ?????????????????????????????????
     * @param formData
     *          formMap:?????? key value ???
     *          taskId:??????id
     *          processInstanceId:??????id
     *          message:????????????
     */
    @Override
    public void jumpStartEvent(FormDataCorrelationVO formData) {

        //?????????????????????????????????
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

        //?????????????????????id
        HistoricActivityInstance historicActivityInstance = resultList.get(0);
        String toActId = historicActivityInstance.getActivityId();

        if(message != null){
            taskService.createComment(taskId, processInstanceId, WORK_FLOW_REJECT_REASON + message);
        }

        //????????????????????????????????????????????????
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(tree.getId())
                .startBeforeActivity(toActId)
                .execute();
    }

     /**
     * ??????formKey????????????id
     * @param taskId : ??????id
     * @return : ??????formKey???????????????????????????????????????
     */
    @Override
    public FormRole showFormKeyByTaskId(String taskId) {

        if(StringUtils.isEmpty(taskId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_PARAMETER_NULL);
        }

        FormService formService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getFormService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //??????taskId??????????????????id???????????????key
        HistoricTaskInstance taskInstance = historyService.createHistoricTaskInstanceQuery().taskId(taskId).singleResult();

        String processDefinitionId = taskInstance.getProcessDefinitionId();
        String taskDefinitionKey = taskInstance.getTaskDefinitionKey();
        String taskFormKey = "";

        //??????????????????
        //??????Bpmn????????????????????????
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);

        //???????????????????????????????????????????????????????????????
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
                    //??????????????????????????????????????????????????????id???????????????
                    if(WORK_FLOW_EXTENSION_APPLY.equals(camundaProperty.getCamundaValue())){
                        formRole.setFormKey(formService.getTaskFormKey(processDefinitionId,taskDefinitionKey));
                    }
                }else if(WORK_FLOW_EXTENSION_EXAMINE.equals(camundaProperty.getCamundaName())){

                    //?????????????????????????????????????????????
                    String activityId = camundaProperty.getCamundaValue();

                    //????????????????????????????????????
                    if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
                        Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
                        for(CallActivity callActivity : callActivities){
                            processDefinitionId = repositoryService.createProcessDefinitionQuery()
                                                    .processDefinitionKey(callActivity.getCalledElement())
                                                    .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                                                    .singleResult()
                                                    .getId();

                            //?????????????????????????????????
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
     * ??????????????????
     * @param taskId : ??????id
     * @param variables : ??????????????????
     * @return : ??????????????????????????????
     */
    @Override
    public Result<Object> saveFormVar(String taskId, Map<String, Object> variables) {

        //??????
        if(taskId == null){
            throw new HttpServerErrorException(HttpStatus.MULTI_STATUS, WORK_FLOW_PARAMETER_NULL);
        }

        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        //??????????????????
        taskService.setVariables(taskId,variables);

        return Result.OK();
    }
}
