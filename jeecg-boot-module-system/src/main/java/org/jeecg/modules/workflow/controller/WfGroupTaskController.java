package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfGroupTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 组任务
 * @author: lz
 * @date: 2021年05月13日 14:53
 */
@RestController
@RequestMapping("workflow/workflow-groupTask")
@Api(tags="工作流")
@Slf4j
public class WfGroupTaskController{

    @Autowired
    private WfGroupTaskService wfGroupTaskService;

    /**
     * @Description:
     * 获取我的组任务
     * @param pageNo 当前页
     * @param pageSize 每页数据条数
     * @param assignee 用户id
     * @param startUserName 流程实例发起人名称
     * @param groupName 组名称
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:58
     */
    @ApiOperation(value = "组任务",notes = "组任务")
    @GetMapping("/get-groupTask")
    public Result<RecordsListPageVO> queryGroupTask(@ApiParam(value = "当前页" ,required = true)
                                                        @RequestParam Integer pageNo,
                                                    @ApiParam(value = "显示多少条",required = true)
                                                        @RequestParam Integer pageSize,
                                                    @ApiParam(value = "当前用户id",required = true)
                                                        @RequestParam String assignee,
                                                    @ApiParam("流程实例发起人名称")
                                                        @RequestParam(required = false)
                                                                String startUserName,
                                                    @ApiParam("组名称")
                                                        @RequestParam(required = false)
                                                                String groupName) {
        return wfGroupTaskService.queryGroupTask(pageNo, pageSize, assignee, startUserName, groupName);
    }


}
