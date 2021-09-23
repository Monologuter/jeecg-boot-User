package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.CallActivity;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormField;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.service.WfConfService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;

import static org.jeecg.modules.workflow.common.WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL;

/**
 * @Description: 任务接口实现类
 * @author: liuchun
 * @date: 2021年03月18日 9:34
 */
@Service
@Slf4j
public class WfConfServiceImpl implements WfConfService {

    /**
     * 获取任务节点信息 : 根据Bpmn流程xml文件
     * @param procdefId : 流程定义id
     * @return : Result<RecordsListPageVO>
     */
    @Override
    public Result<RecordsListPageVO> showActivityByBpmn(Integer pageNo, Integer pageSize, String procdefId){

        if(StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(procdefId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //读取bpmn文件流
        List<ActivityVO> list = new ArrayList<>();

        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);

        //再遍历任务节点中所有的任务节点信息，并且将数据集合放入到list中
        Collection<UserTask> userTasks = bpmnModelInstance.getModelElementsByType(UserTask.class);
        for(UserTask userTask : userTasks){
            ActivityVO activityVO = new ActivityVO();

            activityVO.setId(userTask.getId());
            activityVO.setName(userTask.getName());
            activityVO.setFormKey(userTask.getCamundaFormKey());
            activityVO.setAssignee(userTask.getCamundaAssignee());

            list.add(activityVO);
        }


        //数据总条数
        Long total = Long.valueOf(list.size());
        List<ActivityVO> activityVOS = null;

        //其中subList(0, 5)取得的是下标为0到4的元素,不包含下标为5的元素
        if(((pageNo -1)*pageSize + pageSize) > total){
            activityVOS = list.subList((pageNo - 1) * pageSize, total.intValue());
        }else{
            activityVOS = list.subList((pageNo - 1) * pageSize, (pageNo - 1) * pageSize + pageSize);
        }

        return Result.OK(UtilTools.getListPage(total,pageNo,pageSize,activityVOS));

    }

    /**
     * 展示动态表单 ： 根据Bpmn流程xml文件
     * @param procdefId : 流程定义id
     * @param activityId : 相关节点id
     * @return ：List<FormFieldVO>
     */
    @Override
    public List<FormFieldVO> showFormByBpmn(String procdefId, String activityId){

        if(StringUtils.isEmpty(procdefId) || StringUtils.isEmpty(activityId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WORK_FLOW_PARAMETER_NULL);
        }

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //读取bpmn文件流
        List<FormFieldVO> list = new ArrayList<>();


        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);

        //查询id为activityid的任务节点
        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById(activityId);
        ExtensionElements extensionElements = null;
        Collection<CamundaFormData> formDatas = null;

        //如果该节点是开始节点就通过类型转换为UserTask类型
        UserTask userTask = (UserTask) modelElementInstance;
        extensionElements = userTask.getExtensionElements();

        //如果该节点的子节点不是null，就继续便利子节点
        if(extensionElements != null){
            formDatas = extensionElements.getChildElementsByType(CamundaFormData.class);
            for(CamundaFormData formData : formDatas){
                Collection<CamundaFormField> formFields = formData.getCamundaFormFields();

                //获取对应formField信息
                for(CamundaFormField formField : formFields){
                    FormFieldVO formFieldVO = new FormFieldVO();
                    Map<String,String> map = new HashMap<>();

                    formFieldVO.setId(formField.getCamundaId());
                    formFieldVO.setLabel(formField.getCamundaLabel());
                    formFieldVO.setType(formField.getCamundaType());
                    formFieldVO.setDefaultValue(formField.getCamundaDefaultValue());

                    CamundaProperties camundaProperties = formField.getCamundaProperties();

                    //判断表单的property属性是否为空
                    if(camundaProperties != null){
                        Collection<CamundaProperty> properties = camundaProperties.getCamundaProperties();

                        for(CamundaProperty property : properties){
                            map.put(property.getCamundaId(),property.getCamundaValue());
                        }
                    }

                    formFieldVO.setProperty(map);

                    //将所有动态表单属性放入list中
                    list.add(formFieldVO);

                }

            }
        }

        return list;

    }

    /**获取扩展属性信息：根据流程定义id
     * @param procdefId : 流程定义id
     * @return : ConfInfoVO
     */
    @Override
    public Result<ConfInfoVO> getExtension(String procdefId) {

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //判断流程定义id参数是否为空
        if(StringUtils.isEmpty(procdefId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WORK_FLOW_PARAMETER_NULL);
        }

        //定义所有配置信息属性
        ConfInfoVO allConfInfoVO = new ConfInfoVO();
        List<ExtensionVO> extensionVOS = new ArrayList<>();
        List<String> examines = new ArrayList<>();

        //获取信息，并放入到主对象中
        ConfInfoVO confInfoVO = UtilTools.getExaminesByProcedefId(procdefId);
        extensionVOS.addAll(confInfoVO.getExtensionVOS());
        examines.addAll(confInfoVO.getExamines());

        //判断流程中是否有调用活动节点，有的话获取调用活动中的申请节点等信息
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
        if(!bpmnModelInstance.getModelElementsByType(CallActivity.class).isEmpty()){
            Collection<CallActivity> callActivities = bpmnModelInstance.getModelElementsByType(CallActivity.class);
            for(CallActivity callActivity : callActivities){
                procdefId = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(callActivity.getCalledElement())
                        .processDefinitionVersion(Integer.valueOf(callActivity.getCamundaCalledElementVersion()))
                        .singleResult()
                        .getId();

                ConfInfoVO tempConfInfoVO = UtilTools.getExaminesByProcedefId(procdefId);

                //将集合合并
                extensionVOS.addAll(tempConfInfoVO.getExtensionVOS());
                examines.addAll(tempConfInfoVO.getExamines());
            }
        }

        allConfInfoVO.setExtensionVOS(extensionVOS);
        allConfInfoVO.setExamines(examines);

        return Result.OK(allConfInfoVO);
    }
}
