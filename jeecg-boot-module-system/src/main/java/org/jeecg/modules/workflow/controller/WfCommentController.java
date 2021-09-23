package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.TaskCommentVO;
import org.jeecg.modules.workflow.service.WfCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Description: 意见处理层
 * @author: liuchun
 * @date: 2021年03月29日 11:43
 */
@RestController
@RequestMapping("/workflow/workflow-Comment")
@Api(tags="工作流")
@Slf4j
public class WfCommentController {

    /**
     * 自动注入评论业务层
     */
    @Autowired
    private WfCommentService wfCommentService;

    /**
     * 展示评论 ： 根据流程实例id
     * @param processInstId : 流程实例id
     */
    @GetMapping("/show-comment")
    @ApiOperation(value = "展示评论", notes = "展示评论")
    public Result<TaskCommentVO> showComment(@ApiParam("流程实例id") @RequestParam String processInstId){
        TaskCommentVO taskCommentVO = wfCommentService.showComment(processInstId);
        return Result.OK(taskCommentVO);
    }

}
