package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;


/**
 * @Description: 我的历史任务服务
 * @author: lz
 * @date: 2021年04月09日 14:01
 */
public interface WfMyHistoricalTaskService {

    /**
     * @Description:
     * 我办理的所有的任务
     * @param pageNo 当前页
     * @param pageSize 当前页显示数据条数
     * @param assignee 办理人
     * @param processInstanceId 流程实例id
     * @param startUserName 流程发起人id
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 16:15
    */
    Result<RecordsListPageVO> queryMyHistoricalTask(Integer pageNo,
                                                    Integer pageSize,
                                                    String assignee,
                                                    String processInstanceId,
                                                    String startUserName);


}
