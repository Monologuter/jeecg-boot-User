package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

/**
 * @Description: 我的流程相关
 * @author: lz
 * @date: 2021年04月13日 9:56
 */
public interface WfHistoricalProcessService {

    /**
     * @Description:
     * 由我发起的正在运行中的流程
     * @param pageNo 当前页码
     * @param pageSize 当前页码显示数据条数
     * @param startUserId 流程发起人id
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key（业务标题）
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 10:57
    */
    Result<RecordsListPageVO> queryMyProcess(Integer pageNo,
                                             Integer pageSize,
                                             String  startUserId,
                                             String  proDefName,
                                             String  proDefKey);

    /**
     * @Description:
     * 由我发起的所有流程
     * @param pageNo 当前页码
     * @param pageSize 当前页显示数据条数
     * @param startUserId 流程发起人id
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key （业务标题）
     * @param proState 流程的状态
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 10:58
    */
    Result<RecordsListPageVO> queryHistoricalProcess(Integer pageNo,
                                                     Integer pageSize,
                                                     String  startUserId,
                                                     String  proDefName,
                                                     String  proDefKey,
                                                     String  proState);

}
