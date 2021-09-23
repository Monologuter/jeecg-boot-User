package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfMyHistoricalTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 我的历史任务
 * @author: lz
 * @date: 2021年04月12日 15:15
 */
@RestController
@RequestMapping("workflow/workflow-myHistoricalTask")
@Api(tags="工作流")
@Slf4j
public class WfMyHistoricalTaskController {

    @Autowired
    private WfMyHistoricalTaskService wfMyHistoricalTaskService;

    /**
     * @Description:
     * 我办理的所有的任务
     * @param pageNo 当前页
     * @param pageSize 当前页显示数据条数
     * @param assignee 办理人
     * @param processInstanceId 流程实例id
     * @param startUserName 流程发起人名称
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 16:13
    */
    @AutoLog(value = "我的历史任务")
    @ApiOperation(value = "我的历史任务",notes = "我的历史任务")
    @GetMapping("/get-myHistoricalTask")
    public Result<RecordsListPageVO> queryMyHistoricalTask(@ApiParam(value = "当前页" ,required = true)
                                                               @RequestParam Integer pageNo,
                                                           @ApiParam(value = "显示多少条",required = true)
                                                               @RequestParam Integer pageSize,
                                                           @ApiParam(value = "执行人", required = true)
                                                               @RequestParam String assignee,
                                                           @ApiParam("流程实例ID")
                                                               @RequestParam(required = false)String processInstanceId,
                                                           @ApiParam("流程实例发起人名称")
                                                               @RequestParam(required = false) String startUserName) {
        return wfMyHistoricalTaskService.queryMyHistoricalTask( pageNo, pageSize, assignee ,
                processInstanceId, startUserName);
    }
}
