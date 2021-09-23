package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.service.WfRecentUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 最近使用
 * @author 权计超
 * @date 2021-6-10 13:15
 */
@RestController
@RequestMapping("workflow/workflow-recent-use")
@Api(tags="工作流")
@Slf4j


public class WfRecentUserController {
    @Autowired
    private WfRecentUserService wfRecentUserService;

    @GetMapping("/get-recent-use")
    @ApiOperation(value = "获取最近使用",notes = "获取最近使用")
    public Result<List<ProcessDevelopmentVO>> getRecentUse(@ApiParam(value = "登录用户id",required = true) @RequestParam String userId){
        return  wfRecentUserService.getRecentUse(userId);
    }
}