package org.jeecg.modules.workflow.service;

import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

/**
 * @Description: 组任务
 * @author: lz
 * @date: 2021年05月13日 11:49
 */
public interface WfGroupTaskService {

    /**
     * @Description:
     * 获取我的组任务
     * @param pageNo 当前页
     * @param pageSize 每页数据条数
     * @param assignee 用户id
     * @param startUserName 流程实例发起人名称
     * @param groupName 组名称
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:58
    */
    Result<RecordsListPageVO> queryGroupTask(Integer pageNo,
                                             Integer pageSize,
                                             String assignee,
                                             String startUserName,
                                             String groupName
                                            );


}
