package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;
import java.util.List;

/**
 * 流程抄送业务层
 * @author qjc
 * @date 2021-5-25 11：07
 */
public interface WfCopyService {
    /**
     * 获取抄送给我的流程
     * @param userId 当前用户id
     * @param pageNo 当前页
     * @param pageSize 每页显示多少条
     */
    Result<RecordsListPageVO> getCopyInstance(String  userId,
                                              Integer pageNo,
                                              Integer pageSize,
                                              String  state,
                                              String definitionId);


    /**
     * 设置抄送人
     * @param processInstanceId 流程实例id
     * @param copyArray 新增的抄送人列表
     */
    Result<String> setCopy(String processInstanceId,String[] copyArray);

    /**
     * 获取抄送人
     * @param processInstanceId 流程实例ID
     */
    Result<List<UserDTO>> getCopy(String processInstanceId);
}