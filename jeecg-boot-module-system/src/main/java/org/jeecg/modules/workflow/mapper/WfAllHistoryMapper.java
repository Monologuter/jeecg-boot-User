package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.HistoryProcessVO;
import org.jeecg.modules.workflow.entity.vo.HistoryTaskVO;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;

import java.util.List;

@Mapper
public interface WfAllHistoryMapper {
    /**
     * 所有历史任务
     * */
    List<HistoryTaskVO> queryAllHistoricalTaskPage(Integer firstResult,
                                               Integer pageSize,
                                               String taskId,
                                               String taskName);

    /**
     * 查询所有历史任务
     * */
    Integer queryAllHistoricalTask(String taskId,
                                               String taskName);

    /**
     * 分页查询所有历史流程
     * */
    List<HistoryProcessVO> queryAllHistoricalProcessPage(Integer firstResult,
                                                     Integer pageSize,
                                                     String procInstId,
                                                     String state);

    /**
     * 所有历史流程
     * */
    Integer queryAllHistoricalProcess(String procInstId,
                                                     String state);

    /**
     * 分页查询运行流程
     * */
    List<ProcessInstanceDTO> getProcessInstanceListPage(Integer firstResult,
                                                    Integer pageSize,
                                                    String processName,
                                                    String processInstanceId,
                                                    String initiator);

    /**
     * 查询所有运行流程条数
     * */
    Integer getProcessInstanceList(String processName,
                                                    String processInstanceId,
                                                    String initiator);

}
