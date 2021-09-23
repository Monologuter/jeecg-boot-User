package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.WfdelegateTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 任务委派控制层
 * @author : lb
 * @date : 2021-4-14 10:32
 */
@RestController
@RequestMapping("workflow/workflow-delegate")
@Api(tags = "工作流")
@Slf4j
public class WfdelegateTaskController {
    @Autowired
    private WfdelegateTaskService wfdelegateTaskService;

    /**
     * 任务委派
     * @param taskId  任务id
     * @param userId  被委派人id
     * @return
     */

    @GetMapping("/delegate-task")
    @ApiOperation(value = "任务委派", notes = "任务委派")
    public Result delegateTask(@ApiParam(value = "任务id", required = true)
                                       @RequestParam String taskId,
                               @ApiParam(value = "委派人id", required = true)
                                       @RequestParam String userId){
        return wfdelegateTaskService.delegateTask(taskId, userId);
    }
}
