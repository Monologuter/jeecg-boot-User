package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.CommentMainVO;
import org.jeecg.modules.workflow.entity.vo.CreatCommentForm;
import org.jeecg.modules.workflow.service.WfCommentAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年07月26日 13:40
 */

@RestController
@RequestMapping("/workflow/workflow-common")
@Api(tags="工作流")
@Slf4j
public class WfCommentAboutController {


    @Autowired
    private WfCommentAboutService wfCommentAboutService;

    @ApiOperation("评论流程")
    @PostMapping(name = "评论流程",value = "/creatComment")
    public Object creatComment(@RequestBody CreatCommentForm form) {
        return wfCommentAboutService.creatComment(form);
    }

    @ApiOperation("评论删除")
    @GetMapping(name = "评论删除",value = "/deleteComment")
    public Object deleteComment(String commentId) {
        return wfCommentAboutService.deleteComment(commentId);
    }

    @ApiOperation("获取该流程下所有的评论")
    @GetMapping(name = "获取该流程下所有的评论",value = "/getAllCommentByprocInsId")
    public Result<CommentMainVO> getAllCommentByprocInsId(String procInsId) {
        return wfCommentAboutService.getAllCommentByprocInsId(procInsId);
    }
}

