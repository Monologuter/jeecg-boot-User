package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;

public interface WfCandidateAboutService {

    /**
     * 候选人获取可选择的任务
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param userId 当前登录用户的用户名
     * @param procDefId 流程定义ID
     * @param procDefName 流程名称
     * @return Result<List<MyTaskVO>>
     */
    Result candidateTaskViewFind(Integer pageNo, Integer pageSize, String userId, String procDefId, String procDefName);

    /**
     * 候选人领取候选任务
     * @param userId  委派人id
     * @param taskId  任务id
     * @return
     **/
    Result candidateClaimTask(String taskId, String userId);

    /**
     * 候选人退回认领的任务
     * @param taskId  任务id
     * @param userId  候选人id
     * @param procInstId 流程定义id
     * @return
     **/
    Result candidateSendBackTask(String taskId, String userId,String procInstId);


}
