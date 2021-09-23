package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

import java.util.Map;

/**
 * 工作流服务类
 * @author bin,ghz
 * @company DXC.technology
 * @data 2021-03-16 14:37
 */
public interface WfInstanceService {

    /**
     * 获取流程实例列表
     * @param processInstanceDTO
     * 提供pageNo、pageSize（这两个必要参数）name、processInstanceId、initiator（非必要参数）
     * @return RecordsListPageVO
     */
    Result<RecordsListPageVO> getProcessInstanceList(ProcessInstanceDTO processInstanceDTO);

    /**
     * 挂起流程实例
     * @param processInstanceId 流程实例id
     * @return Result
     */
    Result suspendProcessInstance(String processInstanceId);

    /**
     * 激活流程实例
     * @param processInstanceId 流程实例id
     * @return Result
     */
    Result activateProcessInstance(String processInstanceId);

    /**
     * 跳转节点
     * @param processInstanceId 流程实例id
     * @param toUserTaskId 跳转的用户任务id
     * @return Result
     */
    Result jumpEvent(String processInstanceId, String toUserTaskId);

    /**
     * 跳转节点
     * @param processInstanceId 流程实例id
     * @param toUserTaskId 跳转的用户任务id
     * @param map 节点表单参数
     * @return Result
     */
    Result jumpEvent(String processInstanceId, String toUserTaskId , Map<String,Object> map);

    /**
     * 流程实例关闭
     * @param processInstanceId 流程实例id
     * @return Result
     */
    Result closeProcessInstance(String processInstanceId, String userId);
}
