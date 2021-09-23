package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.WfCandidateGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;

/**
 * @Description : 候选组相关控制层
 * @author : lb
 * @date : 2021-5-8 9:31
 */
public class WfCandidateGroupController {
    @Autowired
    WfCandidateGroupService wfCandidateGroupService;

    /**
     * 添加候选组
     * @param taskId
     * @param groupId
     * @return
     */
    @PutMapping("/add-candidateGroup")
    @ApiOperation(value = "添加候选组", notes = "添加候选组")
    public Result addCandidateGroup(@ApiParam(value = "任务id", required = true) String taskId,
                                    @ApiParam(value = "组id", required = true) String groupId){
        return wfCandidateGroupService.addCandidateGroup(taskId, groupId);
    }

    /**
     * 删除候选组
     * @param taskId
     * @param groupId
     * @return
     */
    @DeleteMapping("/delete-candidateGroup")
    @ApiOperation(value = "删除候选组", notes = "删除候选组")
    public Result deleteCandidateGroup(@ApiParam(value = "任务id", required = true) String taskId,
                                       @ApiParam(value = "组id", required = true) String groupId){
        return wfCandidateGroupService.deleteCandidateGroup(taskId, groupId);
    }
}
