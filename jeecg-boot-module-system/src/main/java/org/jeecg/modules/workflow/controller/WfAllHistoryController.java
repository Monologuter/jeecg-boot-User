package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfAllHistoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description: 所有历史流程
 * @author: 高华泽
 * @date: 2021年04月14日 13:38
 */
@RestController
@RequestMapping("/workflow/workflow-history")
@Api(tags="工作流")
@Slf4j
public class WfAllHistoryController {
    @Resource(name = "wfAllHistoryServiceImpl")
    WfAllHistoryService wfAllHistoryService;


    /**
     * 所有历史任务
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @param taskId 任务ID
     * @param taskName　任务名称
     */
    @GetMapping("/historyTask")
    @ApiOperation(value = "所有历史任务", notes = "所有历史任务")
    public Result<RecordsListPageVO> queryAllHistoricalTask(@ApiParam(value = "当前页" ,required = true) @RequestParam Integer pageNo ,
                                                     @ApiParam(value = "显示多少条",required = true) @RequestParam Integer pageSize,
                                                     @ApiParam(value = "任务Id") @RequestParam(required = false) String taskId,
                                                     @ApiParam(value = "任务名称") @RequestParam(name = "name",required = false) String taskName){
        return  wfAllHistoryService.queryAllHistoricalTask(pageNo,pageSize,taskId,taskName);
    }

    /**
     * 所有历史流程
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @param procInstId 流程实例ID
     * @param state 流程实例名称
     */
    @GetMapping("/historyProcess")
    @ApiOperation(value = "所有历史流程", notes = "所有历史流程")
    public Result<RecordsListPageVO> queryAllHistoricalProcess(@ApiParam(value = "当前页" ,required = true) @RequestParam Integer pageNo ,
                                                        @ApiParam(value = "显示多少条",required = true) @RequestParam Integer pageSize ,
                                                        @ApiParam(value = "流程实例Id") @RequestParam(required = false) String procInstId,
                                                        @ApiParam(value = "流程状态" )@RequestParam(name = "state",required = false) String state){
        return wfAllHistoryService.queryAllHistoricalProcess(pageNo,pageSize,procInstId,state);
    }

    /**
     * 流程追回
     * @param processInstanceId 流程实例ID
     */
    @GetMapping("/jump-form")
    @ApiOperation(value = "流程追回",notes = "流程追回")
    public Result<Object> jumpStartEvent(@ApiParam(value = "流程实例Id",required = true) @RequestParam String processInstanceId){
        wfAllHistoryService.jumpStartEvent(processInstanceId);
        return Result.OK();
    }
}
