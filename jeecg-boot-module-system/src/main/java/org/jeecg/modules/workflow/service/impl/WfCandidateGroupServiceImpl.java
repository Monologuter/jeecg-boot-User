package org.jeecg.modules.workflow.service.impl;

import org.camunda.bpm.engine.TaskService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.WfCandidateGroupService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

/**
 * @Description : 候选组相关业务层
 * @author : lb
 * @Date : 2021-5-7 17:51
 */
public class WfCandidateGroupServiceImpl implements WfCandidateGroupService {
    /**
     * 添加候选组
     * @param taskId    任务ID
     * @param groupId   组ID
     * @return
     */
    @Override
    public Result addCandidateGroup(String taskId, String groupId) {
        //入参校验
        if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(groupId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数不能为空");
        }
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        taskService.addCandidateGroup(taskId, groupId);
        return Result.OK("添加候选组成功");
    }

    /**
     * 删除候选组
     * @param taskId     任务ID
     * @param groupId    组ID
     * @return
     */
    @Override
    public Result deleteCandidateGroup(String taskId, String groupId) {
        if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(groupId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数不能为空");
        }
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        taskService.deleteCandidateGroup(taskId, groupId);
        return Result.OK("删除候选组成功");
    }
}
