package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import java.util.List;

/**
 * @Description: 我的历史任务所属流程状态
 * @author: LZ
 * @date: 2021-8-20
 */
public interface WorkFlowMyRelatedProcessMapper {

    /**
     * @Description:
     * 查询指定办理人办理的任务 所属流程的相关数据
     * @param pageSize 当前页
     * @param offset 当前页开始数据
     * @param assignee 办理人
     * @param proState 流程状态
     * @param startUserId 流程发起人
     * @Return : java.util.List<org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:13
    */
    List<ProcessInstanceDTO> queryProcessByTaskAssignee(@Param("pageSize") Integer pageSize,
                                                        @Param("offset") Integer offset,
                                                        @Param("assignee") String assignee,
                                                        @Param("proState") String proState,
                                                        @Param("startUserId") String startUserId);

    /**
     * @Description:
     * 查询数据总条数
     * @param assignee 办理人
     * @param proState 流程状态
     * @param startUserId 流程发起人
     * @Return : java.lang.Long
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:15
    */
    Long queryProcessByTaskAssigneeCount(@Param("assignee") String assignee,
                                         @Param("proState") String proState,
                                         @Param("startUserId") String startUserId);

}
