package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.mapper.WorkFlowCandidateMapper;
import org.jeecg.modules.workflow.service.WfCandidateAboutService;
import org.jeecg.modules.workflow.strategy.Context;
import org.jeecg.modules.workflow.strategy.impl.CandidateAboutVoStrategy;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author: sbw，qjc
 * @date: 2021年05月12日 19:30
 */
@Service
@Slf4j
public class WfCandidateAboutServiceImpl implements WfCandidateAboutService {

    @Autowired
    public WorkFlowCandidateMapper workFlowCandidateMapper;

    /**
     * 候选人获取可选择的任务
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param userId 当前登录用户的用户名
     * @param procDefId 流程定义ID
     * @param procDefName 流程名称
     * @return Result<List<MyTaskVO>>
     */
    @Override
    public Result candidateTaskViewFind(Integer pageNo, Integer pageSize, String userId, String procDefId, String procDefName) {
        // 入参校验
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(userId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        // 1、拿到taskService、repositoryService、historyService单例
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        // 2、调用createProcessInstanceQuery()得到所有运行中流程实例
        // 3、调用listPage()进行数据分页传输
        int firstResult = (pageNo-1)*pageSize; //获取之前页的数据条数
        //task任务相关获取
        //查询表数据所用集合taskList
        //自定义实体类集合myTaskVoList
        List<Task> taskList;
        List<CandidateAboutVO> candidateAboutVoList = new ArrayList<>();

        TaskQuery taskQuery = taskService
                .createTaskQuery()
                .withoutCandidateGroups()
                .taskCandidateUser(userId);
        if (!StringUtils.isEmpty(procDefId)){
            taskQuery = taskQuery.processDefinitionId(procDefId);
        }
        if(!StringUtils.isEmpty(procDefName)){
            taskQuery.processDefinitionName(procDefName);
        }
        taskList = taskQuery
                .orderByTaskCreateTime()
                .asc()
                .list();
        //映射策略：List<Task> 转 List<CandidateAboutVO>
        Context context = new Context(new CandidateAboutVoStrategy());
        candidateAboutVoList = (List<CandidateAboutVO>)context.executeMapping(taskList);


        long total = candidateAboutVoList.size();
        List<CandidateAboutVO> reasultList = null;
        if ((pageSize+firstResult) < candidateAboutVoList.size()){
            reasultList = candidateAboutVoList.subList(firstResult, pageSize + firstResult);
        }else {
            reasultList = candidateAboutVoList.subList(firstResult, candidateAboutVoList.size());
        }
        return Result.OK("候选人可以领取的任务查询成功", UtilTools.getListPage(total,pageNo,pageSize,reasultList));
    }

    /**
     * 候选人领取候选任务
     * @param userId  委派人id
     * @param taskId  任务id
     * @return
     **/
    @Override
    public Result candidateClaimTask(String taskId,String userId) {
        //查询候选人的代办任务
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskCandidateUser(userId)
                .singleResult();
        if (task.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"流程已被挂起，暂时无法操作");
        }
        taskService.claim(task.getId(),userId);
        return Result.OK("任务领取成功");
    }

    /**
     * 候选人任务退回
     * @param taskId 任务id
     * @param userId 候选人id
     * @param procInstId 流程实例ID
     * @return
     */
    @Override
    public Result candidateSendBackTask(String taskId, String userId,String procInstId) {
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        //查询候选人的代办任务
        Task task = taskService.createTaskQuery()
                .taskId(taskId)
                .taskAssignee(userId)
                .singleResult();
        if (task.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"流程已被挂起，暂时无法操作");
        }
        String listOwner = workFlowCandidateMapper.getOwner(procInstId);
        if (listOwner != null){
            return Result.Error("此任务是委派任务，不可以退回！");
        }else {
            taskService.setAssignee(task.getId(), null);
            return Result.OK("任务返还完成");
        }

    }
}
