package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.GroupVO;

import java.util.List;

/**
 * @Description: 组任务
 * @author: lz
 * @date: 2021年08月30日 15:00
 */
public interface WorkFlowGroupTaskMapper {

    /**
     * @Description:
     * 查询组任务
     * @param pageSize 当前页
     * @param offset 当前页数据
     * @param assignee 办理人
     * @param startUserName 流程发起人
     * @param groupName 组名称
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 15:22
    */
    List<GroupVO> queryGroupTask(@Param("pageSize") Integer pageSize,
                                 @Param("offset") Integer offset,
                                 @Param("assignee") String assignee,
                                 @Param("startUserName") String startUserName,
                                 @Param("groupName") String groupName);

    /**
     * @Description:
     * 查询组任务数据条数
     * @param assignee 办理人
     * @param startUserName 流程发起人
     * @param groupName 组名称
     * @Return : java.lang.Long
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 15:25
    */
    Long queryGroupTaskCount(@Param("assignee") String assignee,
                             @Param("startUserName") String startUserName,
                             @Param("groupName") String groupName);
}
