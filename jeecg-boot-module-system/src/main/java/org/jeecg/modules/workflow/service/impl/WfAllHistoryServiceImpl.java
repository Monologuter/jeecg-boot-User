package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.*;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.mapper.WfAllHistoryMapper;
import org.jeecg.modules.workflow.service.WfAllHistoryService;
import org.jeecg.modules.workflow.service.WfInstanceService;
import org.jeecg.modules.workflow.common.ProcInstState;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 流程管理所有历史任务以及流程
 * @author: ghz
 * @date: 2021年04月14日 9:57
 */

@Slf4j
@Service
public class WfAllHistoryServiceImpl implements WfAllHistoryService {

    @Resource(name = "wfInstanceServiceImpl")
    private WfInstanceService wfInstanceService;

    @Resource
    private WfAllHistoryMapper wfAllHistoryMapper;

    /**
     * 所有历史任务
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @param taskId 任务DI
     * @param taskName 任务名称
     */
    @Override
    public Result<RecordsListPageVO> queryAllHistoricalTask(Integer pageNo,
                                                            Integer pageSize,
                                                            String taskId,
                                                            String taskName) {

        //获取之前页的数据条数
        int firstResult = (pageNo-1)*pageSize;
        long total;
        List<HistoryTaskVO> historyTaskVOS = wfAllHistoryMapper.queryAllHistoricalTaskPage(firstResult,
                                                                                       pageSize,
                                                                                       taskId,
                                                                                       taskName);

        historyTaskVOS.forEach(historyTaskVO -> {

            if(StringUtils.isEmpty(historyTaskVO.getStartUserId())){
                historyTaskVO.setStartUserId("该用户未命名或已被删除");
            }

            if (StringUtils.isEmpty(historyTaskVO.getDurationLong())){
                historyTaskVO.setDuration(UtilTools.timeCon(historyTaskVO.getDurationLong()));
            } else {
                historyTaskVO.setDuration(UtilTools.timeConsuming(historyTaskVO.getStartTime()));
            }

                historyTaskVO.setStateSignal(!StringUtils.isEmpty(historyTaskVO.getEndTime()));

            if (StringUtils.isEmpty(historyTaskVO.getState())){
                historyTaskVO.setState("未完成");
            } else {
                if (ProcInstState.valueOf(historyTaskVO.getState()).name().equals(historyTaskVO.getState())){
                    historyTaskVO.setState(ProcInstState.valueOf(historyTaskVO.getState()).getDesc());
                }
            }
        });

        total = wfAllHistoryMapper.queryAllHistoricalTask(
                taskId,
                taskName);

        return Result.OK(UtilTools.getListPage(total,pageNo,pageSize,historyTaskVOS));
    }

    /**
     * 所有历史流程
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @param procInstId 流程实例ID
     * @param state 流程状态
     */
    @Override
    public Result<RecordsListPageVO> queryAllHistoricalProcess(Integer pageNo,
                                                               Integer pageSize,
                                                               String procInstId,
                                                               String state) {

        //获取之前页的数据条数
        int firstResult = (pageNo-1)*pageSize;
        long total;

        List<HistoryProcessVO> historyProcessVOS = wfAllHistoryMapper.queryAllHistoricalProcessPage(firstResult,
                                                                                                pageSize,
                                                                                                procInstId,
                                                                                                state);


        historyProcessVOS.forEach(historyProcessVO -> {

            if(StringUtils.isEmpty(historyProcessVO.getStartUserId())){
                historyProcessVO.setStartUserId("该用户未命名或已被删除");
            }

            if (StringUtils.isEmpty(historyProcessVO.getDurationLong())){
                historyProcessVO.setDuration(UtilTools.timeCon(historyProcessVO.getDurationLong()));
            } else {
                historyProcessVO.setDuration(UtilTools.timeConsuming(historyProcessVO.getStartTime()));
            }

            historyProcessVO.setStateSignal(StringUtils.isEmpty(historyProcessVO.getEndTime()));

            if (ProcInstState.valueOf(historyProcessVO.getState()).name().equals(historyProcessVO.getState())){
                historyProcessVO.setState(ProcInstState.valueOf(historyProcessVO.getState()).getDesc());
            }

            List<HistoricTaskInstance> list = ProcessEngineUtil
                    .PROCESS_ENGINE_INSTANCE
                    .getHistoryService()
                    .createHistoricTaskInstanceQuery()
                    .processInstanceId(historyProcessVO.getProcessInstanceId())
                    .orderByHistoricActivityInstanceStartTime()
                    .desc()
                    .list();

            if (list.size() != 0){
                historyProcessVO.setTaskId(list.get(0).getId());
                historyProcessVO.setTaskName(list.get(0).getName());
            } else {
                historyProcessVO.setTaskId("该流程存在问题，建议删除流程");
                historyProcessVO.setTaskName("该流程存在问题，建议删除流程");
            }

        });

        total = wfAllHistoryMapper.queryAllHistoricalProcess(procInstId,
                state);

        return Result.OK(UtilTools.getListPage(total,pageNo,pageSize,historyProcessVOS));
    }

    /**
     * 流程追回
     * @param processInsId 流程实例ID
     */
    @Transactional
    @Override
    public Result<String> jumpStartEvent(String processInsId){

        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();

        //获取正在运行的节点，获取该流程下所有正在运行的节点
        List<String> activityInstances = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInsId)
                .unfinished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toList());

        //如果正在运行节点不唯一则不能追回
        if (activityInstances.size() != 1){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"任务节点不可被追回");
        }

        //获取流程定义ID
        String processDefinitionId = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInsId)
                .singleResult()
                .getProcessDefinitionId();

        List<FlowNode> proNode = UtilTools.getAllProNode(processDefinitionId,activityInstances);

        //如果上一个节点不唯一，则不能追回
        if (proNode.size() == 0){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"任务节点不可被追回");
        }
        System.out.println(proNode.get(0).getId());
        wfInstanceService.jumpEvent(processInsId , proNode.get(0).getId());
        return Result.OK("success");
    }
}
