package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfMyRelatedProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 我的历史任务所属流程状态
 * @author lz
 * @company DXC.technology
 * @date 2021-8-19
 */
@RestController
@RequestMapping("workflow/workflow-my-related-process")
@Api(tags = "工作流")
@Slf4j
public class WfMyRelatedProcessController {

    @Autowired
    private WfMyRelatedProcessService wfMyRelatedProcessService;

    /**
     * @Description:
     * 我已办（工作台）
     * 查询我的历史任务所属流程的相关信息
     * @param pageNo 当前页
     * @param pageSize 每页数据的条数
     * @param assignee 办理人id
     * @param proState 流程的状态
     * @param startUserId 流程发起人
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 17:05
    */
    @ApiOperation(value = "我的历史任务所属流程状态", notes = "我的历史任务所属流程状态")
    @GetMapping("/get-processByTaskAssignee")
    public Result<RecordsListPageVO> queryProcessByTaskAssignee(@ApiParam(value = "当前页", required = true)
                                                                    @RequestParam Integer pageNo,
                                                                @ApiParam(value = "显示多少条", required = true)
                                                                    @RequestParam Integer pageSize,
                                                                @ApiParam(value = "执行人", required = true)
                                                                    @RequestParam String assignee,
                                                                @ApiParam(value = "流程实例状态")
                                                                    @RequestParam(required = false)
                                                                            String proState,
                                                                @ApiParam(value = "流程实例发起人ID")
                                                                    @RequestParam(required = false)
                                                                            String startUserId) {
        return wfMyRelatedProcessService.queryProcessByTaskAssignee(
                pageNo, pageSize, assignee, proState, startUserId);
    }
}
