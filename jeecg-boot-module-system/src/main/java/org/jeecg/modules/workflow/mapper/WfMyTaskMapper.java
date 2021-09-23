package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.MyTaskVO;

import java.util.List;

@Mapper
public interface WfMyTaskMapper {

    /**
     * 我的任务
     * */
    List<MyTaskVO> queryMyTaskPage(Integer firstResult,
                                   Integer pageSize,
                                   String assignee,
                                   String procInstName,
                                   String startUserId);

    /**
     * 查询我的任务
     * */
    List<MyTaskVO> queryMyTask(String assignee,
                               String procInstName,
                               String startUserId);

    /**
     * 查询判断是否存在组任务
     */
    Integer queryGroupTask(String taskId);

}
