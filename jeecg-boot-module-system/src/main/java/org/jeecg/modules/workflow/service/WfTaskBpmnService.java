package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ActAssMsgVO;
import org.jeecg.modules.workflow.entity.vo.ActivitiHighLineDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

import java.io.IOException;
import java.util.List;

/**
 * 任务业务层
 * @author: ghz
 * @date: 2021/3/29 9:37
 */
public interface WfTaskBpmnService {

    /**
     * 查询图片返回Bpmn
     * @param processDefinitionId
     * @return
     */
    Result<String> getBpmnXml(String processDefinitionId) throws IOException;

    /**
     * 获取历史节点跟踪
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param procInsId 流程实例ID
     * @return
     */
    Result<RecordsListPageVO> getHistoryActive(Integer pageNo,
                                               Integer pageSize,
                                               String procInsId);

    /**
     *返回已完成，执行过，未完成节点
     * @param processInsId 流程实例ID
     */
    Result<ActivitiHighLineDTO> getHigh(String processInsId) throws IOException;
    /**
     *返回节点信息
     * @param processInsId 流程实例ID
     * @return
     */
    Result<List<ActAssMsgVO>> getActMsg(String processInsId);

}
