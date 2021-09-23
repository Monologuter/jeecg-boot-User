package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfInstanceService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 工作流前端控制器
 * @author bin
 * @company DXC.technology
 * @data 2021-03-16 14:38
 */
@RestController
@RequestMapping("/workflow/process-instance")
@Api(tags="工作流")
@Slf4j
public class WfInstanceController {

    @Resource
    private WfInstanceService wfInstanceService;

    /**
     * 获取流程实例列表
     * @param processInstanceDTO
     * 提供pageNo、pageSize（这两个必要参数）name、processInstanceId、initiator（非必要参数）
     * @return Result<List<ProcessInstanceVo>>
     */
    @AutoLog(value = "流程实例列表")
    @ApiOperation(value = "流程实例列表",notes = "流程实例列表")
    @PostMapping("/get-list")
    public Result<RecordsListPageVO> getProcessInstanceList(@RequestBody ProcessInstanceDTO processInstanceDTO){

        return wfInstanceService.getProcessInstanceList(processInstanceDTO);

    }

    /**
     * 挂起流程实例
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @AutoLog(value = "挂起流程实例")
    @ApiOperation(value = "挂起流程实例",notes = "挂起流程实例")
    @GetMapping("/suspend/{processInstanceId}")
    public Result suspendProcessInstance(@PathVariable("processInstanceId") String processInstanceId){

        return wfInstanceService.suspendProcessInstance(processInstanceId);

    }

    /**
     * 激活流程实例
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @AutoLog(value = "激活流程实例")
    @ApiOperation(value = "激活流程实例",notes = "激活流程实例")
    @GetMapping("/activate/{processInstanceId}")
    public Result activateProcessInstance(@PathVariable("processInstanceId") String processInstanceId){

        return wfInstanceService.activateProcessInstance(processInstanceId);

    }


    /**
     * 流程实例任务节点跳转
     * @param processInstanceId 流程实例id
     * @param toUserTaskId 要跳转的热门节点id
     * @return Result
     */
    @AutoLog(value = "流程实例任务节点跳转")
    @ApiOperation(value = "流程实例任务节点跳转",notes = "流程实例任务节点跳转")
    @GetMapping("/jump-event")
    public Result jumpEvent(String processInstanceId,String toUserTaskId){

        return wfInstanceService.jumpEvent(processInstanceId,toUserTaskId);

    }

    /**
     * 流程实例关闭
     * @param processInstanceId 流程实例id
     * @return Result
     */
    @AutoLog(value = "流程实例关闭")
    @ApiOperation(value = "流程实例关闭",notes = "流程实例关闭")
    @GetMapping("/close")
    public Result closeProcessInstance(String processInstanceId,String userId){

        return wfInstanceService.closeProcessInstance(processInstanceId,userId);

    }

}
