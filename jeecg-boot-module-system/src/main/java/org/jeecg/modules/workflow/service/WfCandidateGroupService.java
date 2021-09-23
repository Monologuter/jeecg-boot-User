package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;

/**
 * @Description : 候选组业务层
 * @author : lb
 * @date : 2021-5-7 17:45
 */
public interface WfCandidateGroupService {
    /**
     * 添加候选组
     * @param taskId    任务ID
     * @param groupId   组ID
     * @return
     */
    Result addCandidateGroup(String taskId, String groupId);

    /**
     * 删除候选组
     * @param taskId     任务ID
     * @param groupId    组ID
     * @return
     */
    Result deleteCandidateGroup(String taskId, String groupId);
}
