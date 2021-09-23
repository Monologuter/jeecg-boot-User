package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

/**
 * @Description: 我的任务业务层
 * @author: ghz
 * @date: 2021年03月18日 16:36
 */
public interface WfMyTaskService {
    /**
     * 获取我的任务
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param assignee 当前登录用户的用户名(即申请人申请的委托人)
     * @return Result<List<MyTaskVO>>
     */
    Result<RecordsListPageVO> queryMyTask(Integer pageNo,
                                          Integer pageSize,
                                          String assignee,
                                          String procInstId,
                                          String state);

}
