package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.WfWorkbenchDisplayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年06月03日 14:11
 */

@RestController
@RequestMapping("workflow/workflow-workbenchdisplay")
@Api(tags = "工作流")
@Slf4j
public class WfWorkbenchDisplayController {

    @Autowired
    private WfWorkbenchDisplayService wfWorkbenchDisplayService;

    /**
     * 工作台页面展示
     *
     * @return*/

    @GetMapping("/workbenchDisplayFindAll")
    @ApiOperation(value = "工作台页面展示", notes = "工作台页面展示")
    public Result<Object> workbenchDisplayFindAll(
            @ApiParam(value = "用户id", required = true) @RequestParam String userId) {
        Result<Object> procGroupVOList = wfWorkbenchDisplayService.workbenchDisplayFindAll(userId);
        return Result.OK("查询成功",procGroupVOList);
    }
}

