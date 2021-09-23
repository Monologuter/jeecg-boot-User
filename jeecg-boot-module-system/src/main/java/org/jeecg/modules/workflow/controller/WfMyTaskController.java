package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfMyTaskService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description: 我的任务Controller
 * @author: ghz
 * @date: 2021年03月21日 12:38
 */
@RestController
@RequestMapping("workflow/workflow-mytask")
@Api(tags="工作流")
@Slf4j
public class WfMyTaskController {
    @Resource
    private WfMyTaskService wfMyTaskService;

    /**
     * 获取我的任务
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param assignee 当前登录用户的用户名
     * @param procInstName 流程实例ID
     * @param startUserId 流程状态
     * @return Result<List<MyTaskVO>>
     */
    @AutoLog(value = "我的任务")
    @ApiOperation(value = "我的任务",notes = "我的任务")
    @GetMapping("/get-mytasklist")
    public Result<RecordsListPageVO> queryMyTask(@ApiParam(value = "当前页" ,required = true) @RequestParam Integer pageNo ,
                                                 @ApiParam(value = "显示多少条",required = true) @RequestParam Integer pageSize ,
                                                 @ApiParam(value = "执行人", required = true) @RequestParam String assignee ,
                                                 @ApiParam("流程实例名称") @RequestParam(required = false) String procInstName ,
                                                 @ApiParam("发起人") @RequestParam(required = false) String startUserId
                                              ){

        return wfMyTaskService.queryMyTask( pageNo, pageSize, assignee ,
                procInstName ,startUserId);

    }

}
