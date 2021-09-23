package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

public interface WfMyRelatedProcessService {

    /**
     * @Description:
     * 我已办（工作台）
     * 查询我的历史任务所属流程的相关信息
     * @param pageNo 当前页
     * @param pageSize 每页数据的条数
     * @param assignee 办理人id
     * @param proState 流程的状态
     * @param startUserId 流程发起人
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:12
    */
    Result<RecordsListPageVO> queryProcessByTaskAssignee(Integer pageNo,
                                                         Integer pageSize,
                                                         String assignee,
                                                         String proState,
                                                         String startUserId);
}
