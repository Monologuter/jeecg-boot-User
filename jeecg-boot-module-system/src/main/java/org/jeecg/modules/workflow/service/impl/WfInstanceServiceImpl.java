package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WfAllHistoryMapper;
import org.jeecg.modules.workflow.service.WfInstanceService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import javax.annotation.Resource;
import java.util.*;

/**
 * 工作流服务实现类
 * @author bin,ghz
 * @company DXC.technology
 * @data 2021-03-16 14:41
 */
@Service
@Slf4j
public class WfInstanceServiceImpl implements WfInstanceService {

    @Resource
    private WfAllHistoryMapper wfAllHistoryMapper;

    @Override
    public Result<RecordsListPageVO> getProcessInstanceList(ProcessInstanceDTO processInstanceDTO) {
        //获取pageNo、pageSize、initiator、processInstanceId、name
        Integer pageNo = processInstanceDTO.getPageNo();
        Integer pageSize = processInstanceDTO.getPageSize();
        String initiator = processInstanceDTO.getInitiator();
        String processInstanceId = processInstanceDTO.getProcessInstanceId();
        String name = processInstanceDTO.getName();
        //获取之前页的数据条数
        int firstResult = (pageNo-1)*pageSize;
        long total;
        List<ProcessInstanceDTO> processInstanceList = wfAllHistoryMapper.getProcessInstanceListPage(firstResult,
                                                                                                    pageSize,
                                                                                                    name,
                                                                                                    processInstanceId,
                                                                                                    initiator);
        processInstanceList.forEach(processInstance -> {

            if(processInstance.getInitiator() == null || "".equals(processInstance.getInitiator())){
                processInstance.setInitiator("该用户未命名或已被删除");
            }

            processInstance.setDueDate(UtilTools.timeConsuming(processInstance.getStartTime()));

                processInstance.setSuspensionState(processInstance.getState() == 2);
        });

        total = wfAllHistoryMapper.getProcessInstanceList(name,
                processInstanceId,
                initiator);

        return Result.OK("运行流程",
                UtilTools.getListPage(total,pageNo,pageSize,processInstanceList));
    }

    /**
     * 挂起流程实例：
     * 1、入参校验
     * 2、获取RuntimeService单例
     * 3、createProcessInstanceQuery().processInstanceId()查询需要挂起的流程实例是否存在和当前的挂起状态
     * 4、suspendProcessInstanceById()挂起
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @Override
    public Result suspendProcessInstance(String processInstanceId) {

        // 初始化返回对象
        Result result = new Result();
        // 1、入参校验
        if (StringUtils.isEmpty(processInstanceId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }

        // 2、获取RuntimeService单例
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();

        // 3、createProcessInstanceQuery().processInstanceId()查询需要挂起的流程实例是否存在
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        if (null == processInstance) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"不存在正在执行的流程实例");
        }

        //判断当前挂起状态
        if (processInstance.isSuspended()){
            result.success("流程实例无需重复挂起");
            return result;
        }

        // 4、suspendProcessInstanceById()挂起
        runtimeService.suspendProcessInstanceById(processInstanceId);
        ProcessInstance processInstanceVft = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        // 判断是否挂起成功
        if (processInstanceVft.isSuspended()){
            result.success("流程实例挂起成功");
        }else {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"流程实例挂起失败");
        }
        return result;
    }

    /**
     * 激活流程实例：
     * 1、入参校验
     * 2、获取RuntimeService单例
     * 3、createProcessInstanceQuery().processInstanceId()查询需要挂起的流程实例是否存在和当前的挂起状态
     * 4、activateProcessInstanceById()挂起
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @Override
    public Result activateProcessInstance(String processInstanceId) {

        // 初始化返回对象
        Result result = new Result();
        // 1、入参校验
        if (StringUtils.isEmpty(processInstanceId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }

        // 2、获取RuntimeService单例
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();

        // 3、createProcessInstanceQuery().processInstanceId()查询需要挂起的流程实例是否存在
        ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().
                processInstanceId(processInstanceId).
                singleResult();
        if (null == processInstance) {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"不存在正在执行的流程实例");
        }

        //判断当前挂起状态
        if (!processInstance.isSuspended()){
            result.success("流程实例无需重复激活");
            return result;
        }

        // 4、suspendProcessInstanceById()挂起
        runtimeService.activateProcessInstanceById(processInstanceId);
        ProcessInstance processInstanceVft = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        // 判断是否挂起成功
        if (!processInstanceVft.isSuspended()){
            result.success("流程实例激活成功");
        }else {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"流程实例激活失败");
        }

        return result;
    }

    /**
     * 重载了原本的跳转的方法，考虑到
     * @param processInstanceId 流程实例id
     * @param toUserTaskId 跳转的用户任务id
     */
    @Override
    public Result jumpEvent(String processInstanceId, String toUserTaskId) {
        Map<String,Object> map = new HashMap(16);
        return jumpEvent(processInstanceId,toUserTaskId,map);
    }

    /**
     * 1、参数校验
     * 2、获取RuntimeService单例和TaskService单例
     * 3、通过processInstanceId拿到ActivityInstance 是一棵流程走向树，用来在跳转的时候去取消之前正在执行的任务
     * 4、通过runtimeService.createProcessInstanceModification可以去操作节点的跳转与取消
     * 5、通过taskService获取实例当前正在执行的任务节点key值来确定跳转是否成功
     * @param processInstanceId 流程实例id
     * @param toUserTaskId 跳转的用户任务id
     */
    @Override
    public Result jumpEvent(String processInstanceId, String toUserTaskId , Map<String,Object> map){

        // 初始化返回对象
        Result result = new Result();
        // 1、入参校验
        if (StringUtils.isEmpty(processInstanceId) || StringUtils.isEmpty(toUserTaskId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }

        // 2、获取RuntimeService单例和TaskService单例
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();

        //判断流程实例是否被挂起
        boolean suspended = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .isSuspended();

        if(suspended){
            return result.error500("流程实例已被挂起");
        }
        // 3、通过processInstanceId拿到ActivityInstance 是一棵流程走向树，用来在跳转的时候去取消之前正在执行的任务
        ActivityInstance tree = runtimeService.getActivityInstance(processInstanceId);



        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        /**
         * 1到4主要是为了查询流程任务节点循序放入集合中，因为下标的原因通过下标来判断是否需要表单
         */
        //1.获取流程定义ID
        String processDefinitionId = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getProcessDefinitionId();

        //2.读取bpmn文件流
        List<String> actList = new ArrayList<>();

        BpmnModelInstance bpmnModelInstance = repositoryService
                .getBpmnModelInstance(processDefinitionId);

        //3.再遍历任务节点中所有的任务节点信息，并且将数据集合放入到list中
        Collection<UserTask> userTasks = bpmnModelInstance
                .getModelElementsByType(UserTask.class);

        //4.循环将数据放入list集合
        userTasks.forEach(userTask -> actList.add(userTask.getId()));


        //5.查出是否为第一个节点来判断流程是否可以被追回
        List<HistoricActivityInstance> list = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInstanceId)
                .finished()
                .orderByHistoricActivityInstanceEndTime()
                .desc()
                .list();

        if(list.size() == 0){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"任务节点跳转失败");
        }

        int preIndex = -1;
        //获取节点下标
        for (int index = 0 ; index < actList.size() ; index++ ){
            if(actList.get(index).equals(toUserTaskId)){
                preIndex = index;
                break;
            }
        }

        //如果回到申请人节点
        if(preIndex == 0){
            List<HistoricVariableInstance> historicVariableInstances = historyService
                    .createHistoricVariableInstanceQuery()
                    .processInstanceId(processInstanceId)
                    .list();
            historicVariableInstances.forEach(historicVariableInstance -> {
                map.put(historicVariableInstance.getName(),null);
            });
        }


        // 4、通过runtimeService.createProcessInstanceModification可以去操作节点的跳转与取消
        runtimeService.createProcessInstanceModification(processInstanceId)
                .cancelActivityInstance(tree.getId())
                //启动目标活动节点
                .startBeforeActivity(toUserTaskId)
                .setVariables(map)
                .execute();
        // 5、通过taskService获取实例当前正在执行的任务节点key值来确定跳转是否成功
        Task task = taskService.createTaskQuery()
                .processInstanceId(processInstanceId)
                .active()
                .singleResult();
        if(task.getTaskDefinitionKey().equals(toUserTaskId)){
            result.success("任务节点跳转成功！");
        }else {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"无前置用户节点，无法跳转");
        }
        return result;
    }

    /**
     * 1、入参校验
     * 2、获取RuntimeService单例
     * 3、结束该流程实例（这之前需要判断要结束的流程实例是否存在）
     * 4、判断该流程实例是否已结束
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @Override
    public Result closeProcessInstance(String processInstanceId, String userId) {

        //创建历史服务单例用以验证是否为发起人，来区分内部终止与外部终止
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        String startUserId = historyService.createHistoricProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult()
                .getStartUserId();

        // 初始化返回对象
        Result result = new Result();
        // 1、入参校验
        if (StringUtils.isEmpty(processInstanceId) && StringUtils.isEmpty(userId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }

        // 2、获取RuntimeService单例
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        // 判断该流程实例是否存在
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (processInstance == null){
            return Result.OK(200,"该流程已结束");
        }

        // 3、结束该流程实例
        if (userId.equals(startUserId)){
            runtimeService.deleteProcessInstance(processInstanceId,"INTERNALLY_TERMINATED",false,false);
        }else {
            runtimeService.deleteProcessInstance(processInstanceId,"EXTERNALLY_TERMINATED",false,true);
        }


        // 4、判断该流程实例是否已结束
        ProcessInstance instanceResult = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId).singleResult();
        if (instanceResult == null){
            result.success("流程实例关闭成功！");
        }else {
            throw new HttpServerErrorException(
                    HttpStatus.INTERNAL_SERVER_ERROR,"流程实例关闭失败！");
        }

        return result;
    }
}
