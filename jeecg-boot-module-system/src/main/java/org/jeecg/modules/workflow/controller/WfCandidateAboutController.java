package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.CandidateAboutVO;
import org.jeecg.modules.workflow.service.WfCandidateAboutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @author: sbw
 * @date: 2021年05月12日 19:27
 */
@RestController
@RequestMapping("workflow/workflow-candidate")
@Api(tags = "工作流")
@Slf4j
public class WfCandidateAboutController {

    @Autowired
    private WfCandidateAboutService wfCandidateAboutService;

    /**
     * 候选人获取可选择的任务
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param userId 当前登录用户的用户名
     * @param procDefId 流程定义ID
     * @param procDefName 流程名称
     * @return Result<List<MyTaskVO>>
     */
    @GetMapping("/candidateTaskViewFind")
    @ApiOperation(value = "候选人获取可选择的任务", notes = "候选人获取可选择的任务")
    public Result<CandidateAboutVO> candidateTaskViewFind(@ApiParam(value = "当前页" ,required = true) @RequestParam Integer pageNo ,
                                    @ApiParam(value = "显示多少条",required = true) @RequestParam Integer pageSize ,
                                    @ApiParam(value = "执行人", required = true) @RequestParam String userId ,
                                    @ApiParam("流程定义ID") @RequestParam(required = false) String procDefId ,
                                    @ApiParam("流程名称") @RequestParam(required = false) String procDefName){
        return wfCandidateAboutService.candidateTaskViewFind(pageNo,pageSize,userId,procDefId,procDefName);
    }

    /**
     * 候选人领取候选任务
     * @param userId  委派人id
     * @param taskId  任务id
     * @return
     **/
    @GetMapping("/candidateClaimTask")
    @ApiOperation(value = "候选人领取候选任务", notes = "候选人领取候选任务")
    public Result<CandidateAboutVO> claimTask(
            @ApiParam(value = "任务id", required = true) @RequestParam String taskId,
            @ApiParam(value = "候选人id", required = true) @RequestParam String userId){
        return wfCandidateAboutService.candidateClaimTask(taskId,userId);
    }

    /**
     * 候选人退回认领的任务
     * @param taskId  任务id
     * @param userId  候选人id
     * @return
     **/

    @GetMapping("/candidateSendBackTask")
    @ApiOperation(value = "候选人退回认领的任务", notes = "候选人退还认领的任务")
    public Result<CandidateAboutVO> sendBackTask(@ApiParam(value = "任务id", required = true)
                               @RequestParam String taskId,
                               @ApiParam(value = "候选人id", required = true)
                               @RequestParam String userId,
                               @ApiParam(value = "流程实例id", required = true)
                               @RequestParam String procInstId){
        return wfCandidateAboutService.candidateSendBackTask(taskId,userId,procInstId);
    }
}
