package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;

/**
 * @description : 任务委派业务层
 * @author : lb
 * @date : 2021-4-14
 */
public interface WfdelegateTaskService {

    /**
     * 任务委派
     * @param taskId  任务id
     * @param userId  被委派人id
     * @return
     */
    Result delegateTask(String taskId, String userId);

}
