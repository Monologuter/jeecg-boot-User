package org.jeecg.modules.workflow.utils;

import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaFormData;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ConfInfoVO;
import org.jeecg.modules.workflow.entity.vo.ExtensionVO;
import org.jeecg.modules.workflow.entity.vo.HistoryProcessVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author lz,qjc
 * @company DXC.technology
 * @dete 2021-04-09
 */

public class UtilTools {
    /**
     * 私有化构造方法
     */
    private UtilTools(){};

    /**
     * 通过分页参数获取分页结果
     * @param total 总条数
     * @param pageNo 当前页
     * @param pageSize 总页数
     * @param records 数据
     * @return
     */
    public static RecordsListPageVO getListPage(Long total, Integer pageNo, Integer pageSize,List records){
        if (total==0||records==null){
            return new RecordsListPageVO(0L,0,pageNo,0,new ArrayList<>());
        }
        RecordsListPageVO recordsListPageVO = new RecordsListPageVO();
        Integer pages = Math.toIntExact(total/pageSize);
        if(total%pageSize!=0){
            pages++;
        }
        if (records.size() > pageSize){
            if (pageNo.equals(pages)){
                records = records.subList((pageNo-1)*pageSize, Math.toIntExact((pageNo - 1) * pageSize + (total % pageSize)));
            }else {
                records = records.subList((pageNo-1)*pageSize,pageNo*pageSize);
            }
        }
        recordsListPageVO.setTotal(total);
        recordsListPageVO.setPages(pages);
        recordsListPageVO.setSize(records.size());
        recordsListPageVO.setCurrent(pageNo);
        recordsListPageVO.setRecords(records);
        return recordsListPageVO;
    }
    /**
     * 毫秒转换
     */
    public static String timeCon(Object time){
        if(time == null){
            return "";
        }
        BigDecimal bigDecimal = new BigDecimal((Long) time);
        BigDecimal hh = bigDecimal
                .divide(BigDecimal.valueOf(3600000), 0, RoundingMode.DOWN);
        BigDecimal mm = bigDecimal
                .subtract(hh.multiply(BigDecimal.valueOf(3600000)))
                .divide(BigDecimal.valueOf(60000), 0, RoundingMode.DOWN);
        BigDecimal ss = bigDecimal
                .subtract(
                        hh
                          .multiply(BigDecimal.valueOf(3600000))
                          .add(mm.multiply(BigDecimal.valueOf(60000)))
                )
                .divide(BigDecimal.valueOf(1000), 0, RoundingMode.HALF_UP);
        BigDecimal dd = hh
                .divide(BigDecimal.valueOf(24),0,RoundingMode.DOWN);
        return dd + "天" + hh + "时" + mm + "分" + ss + "秒";
    }


    /**
     * 输入字节流转byte[]数组
     * @param is 输入字节流
     * @return
     * @throws IOException
     */
    public static byte[] toByteArray(InputStream is) throws IOException{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] bs = new byte[1024];
        int len = -1;
        while ((len = is.read(bs)) != -1) {
            bos.write(bs, 0, len);
        }
        bos.close();
        byte b[] = bos.toByteArray();
        return b;
    }

    /**
     * 解析bpmn获取formkey列表,并存入指定集合
     * @param is 输入流
     * @param map 参数集合
     * @return
     */
    public static void readModelFromStream(InputStream is,Map<String,String> map){
       BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(is);
        Collection<Process> modelElementsByType = bpmnModelInstance.getModelElementsByType(Process.class);
        String id = "";
        for (Process p:modelElementsByType){
            id = p.getId();
        }
        Collection<UserTask> userTasks = bpmnModelInstance.getModelElementsByType(UserTask.class);
       for (UserTask userTask:userTasks){
           if (userTask.getCamundaAssignee()==null){
               continue;
           }
           //避免覆盖
           if (map.get(id)==null){
               map.put(id,userTask.getCamundaFormKey());
           }
       }
    }
    /**
     * 获取系统当前时间计算耗时
     * @parm startTime 起始时间
     */
    public static String  timeConsuming(Date startTime){
        SimpleDateFormat sdf = new SimpleDateFormat();
        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        long T = (date.getTime()-startTime.getTime())/1000;
        long dd=T/(24*3600);
        long hh=T%(24*3600)/3600;
        long mm=T%3600/60;
        long ss=T%60;
        return dd + "天" + hh + "时" + mm + "分" + ss + "秒";
    }

    /**
     * 通过bpmn获取 前边 所有 节点定义id
     * @param processDefinition 流程定义对象
     * @param currentNode 当前节点定义id
     * @return
     * @throws IOException
     */
    public static List<FlowNode> getAllProNode(String processDefinition, List<String> currentNode){
        List<FlowNode> currentList = new ArrayList<>();
        List<FlowNode> resultList = new ArrayList<>();
        BpmnModelInstance bpmnModelInstance  = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService()
                .getBpmnModelInstance(processDefinition);
        Collection<FlowNode> list = bpmnModelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode flowNode:list){
            for (String current:currentNode){
                if (flowNode.getId().equals(current)){
                    currentList.add(flowNode);
                }
            }
        }
        while ((currentList = getProNode(currentList)).size()!=0){
            resultList.addAll(currentList);
        }



        return resultList;
    }

    /**
     * 通过bpmn获取上一个节点
     * @param flowNode
     * @return
     */
    public static List<FlowNode> getProNode(List<FlowNode> flowNode){
        List<FlowNode> resultList = new ArrayList<>();
        for (FlowNode tempNode:flowNode){
            resultList.addAll(tempNode.getPreviousNodes().list());
        }
        return resultList;
    }

    /**
     * 判断流程定义是否能走通
     * @param processDefinitionId
     * @return
     */
    public static Result judgeFlow(String processDefinitionId) {
        BpmnModelInstance bpmnModelInstance = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService()
                .getBpmnModelInstance(processDefinitionId);
        Collection<EndEvent> endEvents = bpmnModelInstance.getModelElementsByType(EndEvent.class);
        //判断是否存在结束节点
        if (endEvents.isEmpty()){
            return Result.error(500,"无结束节点");
        }
        for (EndEvent endEvent:endEvents){
            List<String> list = new ArrayList<>();
            list.add(endEvent.getId());
            List<FlowNode> allProNode = getAllProNode(processDefinitionId, list);
            if (allProNode.size()==1){
                return Result.error(500,"流程定义不合理1，请修改后保存");
            }
            for (FlowNode flowNode:allProNode){
                if (flowNode.getElementType().getTypeName().equals("startEvent")){
                    return Result.OK();
                }
            }
        }
        return Result.error(500,"流程定义不合理2,请修改后保存");
    }

    /**
     * 判读流程定义的用户节点是否有参数
     * @param processDefinitionId
     * @return
     * @throws Exception
     */
    public static Result judgeForm(String processDefinitionId){
        BpmnModelInstance bpmnModelInstance = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService()
                .getBpmnModelInstance(processDefinitionId);
        Collection<UserTask> modelElementsByType = bpmnModelInstance.getModelElementsByType(UserTask.class);
        if(modelElementsByType.isEmpty()){
            return Result.error(500,"至少包含一个申请节点");
        }
        //申请节点数量
        int count = 0;
        for (UserTask userTask: modelElementsByType){
            //获取formkey
            String camundaFormKey = userTask.getCamundaFormKey();
            //获取内置表单参数
            Collection<CamundaFormData> childElementsByType = userTask.getExtensionElements().getChildElementsByType(CamundaFormData.class);
            int formFieldNumber = 0;
            for (CamundaFormData camundaFormData:childElementsByType){
                formFieldNumber += camundaFormData.getCamundaFormFields().isEmpty()?0:1;
            }
            //获取代理人、委托组、委托人
            if (userTask.getCamundaAssignee()==null&&userTask.getCamundaCandidateGroups()==null
                    &&userTask.getCamundaCandidateUsers()==null){
                return Result.error(500,"用户节点无代理人或委托人/组");
            }
            //判断开始节点之后是审批节点情况
            ExtensionElements extensionElements = userTask.getExtensionElements();

            Collection<CamundaProperties> camundaProperties = extensionElements.getChildElementsByType(CamundaProperties.class);


            for(CamundaProperties properties : camundaProperties){
                Collection<CamundaProperty> camundaProperties1 = properties.getCamundaProperties();
                for(CamundaProperty camundaProperty : camundaProperties1){
                    if("role".equals(camundaProperty.getCamundaName())){
                        //如果是审批节点
                        if (camundaProperty.getCamundaValue().equals("审批人")){
                            List<FlowNode> list = userTask.getPreviousNodes().list();
                            for (FlowNode flowNode:list){
                                if (flowNode.getElementType().getTypeName().equals("startEvent")){
                                    return Result.error(500,"开始节点后不能接审批节点");
                                }
                            }
                        }
                        //如果是申请节点
                        else {
                            count++;
                            //如存在无参数用户节点
                            if ((camundaFormKey==null)&&formFieldNumber==0){
                                return Result.error(500,"存在无参数申请节点");
                            }
                            //如果包含会签，拦截部署
                            Collection<MultiInstanceLoopCharacteristics> childElementsByType1 = userTask.getChildElementsByType(MultiInstanceLoopCharacteristics.class);
                            if (!childElementsByType1.isEmpty()){
                                return Result.error(500,"申请节点不可设置会签属性");
                            }
                        }
                    }
                }
            }
        }
        if (count<=0){
            return Result.error(500,"至少包含一个申请节点");
        }
        return Result.OK();
    }


    /**
     * 获取抄送给我流程实例id列表
     */
    public static Set<String> getUserCopyList(String userId){
        Set<String> result = new HashSet<>();
        //获取服务类
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        List<HistoricVariableInstance> copy = historyService.createHistoricVariableInstanceQuery().variableName("copy").list();
        for (HistoricVariableInstance historicProcessInstance:copy){
            if (historicProcessInstance.getValue() instanceof List){
                List<String> users = (List) historicProcessInstance.getValue();
                for (String user:users){
                    if (user.equals(userId)){
                        result.add(historicProcessInstance.getProcessInstanceId());
                    }
                }
                continue;
            }else if(historicProcessInstance.getValue() instanceof String[]){
                String[] users = (String[])historicProcessInstance.getValue();
                for (String user:users){
                    if (user.equals(userId)){
                        result.add(historicProcessInstance.getProcessInstanceId());
                    }
                }
                continue;
            }
            continue;
        }
        return result;
    }
    /**
     * 获取历史实体类
     */
    public static HistoryProcessVO getHistoryProcessVO(HistoricProcessInstance historicProcessInstance){
        //将数据进行封装
        HistoryProcessVO hiProcessVo = new HistoryProcessVO();
        //获得业务标题
        hiProcessVo.setBusinessTitle(historicProcessInstance.getBusinessKey());
        //获得流程名称
        historicProcessInstance.getProcessDefinitionName();
        //获得流程实例ID
        hiProcessVo.setProcessInstanceId(historicProcessInstance.getId());
        //获取流程定义ID
        hiProcessVo.setProcessDefinitionId(historicProcessInstance.getProcessDefinitionId());
        //获得发起人ID
        hiProcessVo.setStartUserId(historicProcessInstance.getStartUserId());
        //获得流程发起时间
        hiProcessVo.setStartTime(historicProcessInstance.getStartTime());
        //获得流程结束时间
        hiProcessVo.setEndTime(historicProcessInstance.getEndTime());
        //获得该流程的耗时时间（以毫秒为单位）
        hiProcessVo.setDuration(UtilTools.timeCon(historicProcessInstance.getDurationInMillis()));
        return hiProcessVo;
    }

    /**
     *
     * 获取配置属性的信息
     */
    public static Map<String ,String > getExtensions(String procdefId,String taskId){
        //获取服务类
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //获取任务实例
        HistoricTaskInstance taskInstance = historyService
                .createHistoricTaskInstanceQuery()
                .taskId(taskId).singleResult();

        Map<String ,String > extension = new HashMap<>();

        String activityId = taskInstance.getTaskDefinitionKey();

        //根据流程定义id读取bomn文件
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
        //查询<bpmn:userTask >标签所在位置
        ModelElementInstance modelElementInstance = bpmnModelInstance.getModelElementById(activityId);

        //获取拓展属性
        UserTask userTask = (UserTask) modelElementInstance;
        ExtensionElements extensionElements = userTask.getExtensionElements();

        Collection<CamundaProperties> childElementsByType =extensionElements
                .getChildElementsByType(CamundaProperties.class);


        for(CamundaProperties camundaProperties : childElementsByType){
            Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
            for(CamundaProperty camundaProperty : camundaProperties1){

              extension.put(camundaProperty.getCamundaName(),camundaProperty.getCamundaValue());

            }
        }
        return  extension;
    }

    /**
     * @Description: 获取扩展属性中key对应的value
     *
     * @param procdefId ：流程定义id
     * @param activityId ：活动id
     * @param key ：扩展属性中的key
     * @Return : java.lang.String
     * @Author : LC @DXC.Technology
     * @Time: 2021/8/30 15:15
    */
    public static String getExtValByKey(String procdefId, String activityId, String key){
        //获取服务类
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        String result = null;

        //获取bpmn配置信息
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
        ModelElementInstance modelElementById = bpmnModelInstance.getModelElementById(activityId);

        UserTask userTask = (UserTask) modelElementById;
        ExtensionElements extensionElements = userTask.getExtensionElements();
        Collection<CamundaProperties> childElementsByType = extensionElements.getChildElementsByType(CamundaProperties.class);

        //取出key对应value
        for(CamundaProperties camundaProperties : childElementsByType){
            Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
            for(CamundaProperty camundaProperty : camundaProperties1){
                if(key.equals(camundaProperty.getCamundaName())){
                    result = camundaProperty.getCamundaValue();
                }
            }
        }

        return result;
    }
    /**
     *
     * 获取所有用户节点配置属性的信息
     */
    public static Map<String, String> getAllExtensions(String procdefId) {
        //获取服务类
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //获取模型
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
        Collection<UserTask> modelElementsByType = bpmnModelInstance.getModelElementsByType(UserTask.class);
        Map<String,String> result = new HashMap<>();
        for (UserTask userTask : modelElementsByType) {
            Collection<CamundaProperties> childElementsByType = userTask
                    .getExtensionElements()
                    .getChildElementsByType(CamundaProperties.class);
            for (CamundaProperties camundaProperties : childElementsByType) {
                Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
                for (CamundaProperty camundaProperty : camundaProperties1) {
                    result.put(camundaProperty.getCamundaName(),camundaProperty.getCamundaValue());
                }
            }
        }
        return result;
    }

    /**
     * 根据用户名称差用户id
     */
    public static String getUserId(String userName){

        //获取服务类
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();

        String id = identityService.createUserQuery().userFirstName(userName).singleResult().getId();
        return id;

    }
    /**
     *根据用户id查姓名
     */
    public static String getUserName(String userId){
        //获取服务类
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();

        String firstName = identityService.createUserQuery().userId(userId).singleResult().getFirstName();
        return firstName;
    }

    /**
     * 根据流程定义id获取申请节点数组和每个节点必要配置信息
     */
    public static ConfInfoVO getExaminesByProcedefId(String procdefId){
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        ConfInfoVO confInfoVO = new ConfInfoVO();
        List<ExtensionVO> extensionVOS = new ArrayList<>();
        List<String> examines = new ArrayList<>();

        //获取流程xml配置文件中用户节点申请节点数组和每个节点必要配置信息
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(procdefId);
        Collection<UserTask> modelElementsByType = bpmnModelInstance.getModelElementsByType(UserTask.class);
        for(UserTask userTask : modelElementsByType){
            ExtensionVO extensionVO = new ExtensionVO();
            extensionVO.setActivityId(userTask.getId());

            ExtensionElements extensionElements = userTask.getExtensionElements();
            Collection<CamundaProperties> childElementsByType = extensionElements.getChildElementsByType(CamundaProperties.class);
            for(CamundaProperties camundaProperties : childElementsByType){
                Collection<CamundaProperty> camundaProperties1 = camundaProperties.getCamundaProperties();
                for(CamundaProperty camundaProperty : camundaProperties1){
                    if("role".equals(camundaProperty.getCamundaName())){
                        extensionVO.setRole(camundaProperty.getCamundaValue());
                        if("申请人".equals(camundaProperty.getCamundaValue())){
                            examines.add(userTask.getId());
                        }
                    }else if("examine".equals(camundaProperty.getCamundaName())){
                        extensionVO.setExamine(camundaProperty.getCamundaValue());
                    }
                }
            }

            extensionVOS.add(extensionVO);
        }

        confInfoVO.setExtensionVOS(extensionVOS);
        confInfoVO.setExamines(examines);

        return confInfoVO;
    }
}

