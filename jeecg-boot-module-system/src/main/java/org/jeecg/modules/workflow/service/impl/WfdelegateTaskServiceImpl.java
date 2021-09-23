package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.Task;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.service.WfdelegateTaskService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

/**
 * 任务委派业务层
 * @author : lb
 * @date : 2021-4-14 10:08
 */
@Service
@Slf4j
public class WfdelegateTaskServiceImpl implements WfdelegateTaskService {

    /**
     * 任务委派
     * @param taskId 任务id
     * @param userId 被委派人id
     * @return
     */
    @Override
    public Result delegateTask(String taskId, String userId) {
        //1、入参校验
        if (StringUtils.isEmpty(taskId) || StringUtils.isEmpty(userId)) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数为空");
        }

        //2、获取taskService、identityService单例
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();

        //3、判断taskId、userId是否存在
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        User user = identityService.createUserQuery().userId(userId).singleResult();
        if (task == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "该任务不存在");
        }
        if (user == null) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "该用户不存在");
        }

        //4、调用delegateTask方法
        taskService.setAssignee(taskId,userId);

        return Result.OK("委派成功");
    }
}
