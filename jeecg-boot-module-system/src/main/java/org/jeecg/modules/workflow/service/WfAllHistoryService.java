package org.jeecg.modules.workflow.service;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

@Mapper
public interface WfAllHistoryService {
    /**
     * 所有历史任务
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return
     */
    Result<RecordsListPageVO> queryAllHistoricalTask(Integer pageNo,
                                                     Integer pageSize,
                                                     String taskId,
                                                     String taskName);

    /**
     * 所有历史流程
     * @param pageNo 当前页码
     * @param pageSize 每页显示多少条数据
     * @return
     */
    Result<RecordsListPageVO> queryAllHistoricalProcess(Integer pageNo,
                                                        Integer pageSize,
                                                        String procInstId,
                                                        String state);

    /**
     * 跳转到上一个
     * @param
     * @return
     */
    Result<String> jumpStartEvent(String processInstanceId);
}
