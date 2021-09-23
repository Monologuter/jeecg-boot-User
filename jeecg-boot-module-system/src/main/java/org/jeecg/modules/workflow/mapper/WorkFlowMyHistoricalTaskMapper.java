package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.workflow.entity.vo.ActHiTaskinstVO;
import org.jeecg.modules.workflow.entity.vo.MyHistoricalTaskVO;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 我的历史任务
 * @author LZ
 * @company DXC
 * @date 2021-08-18
 */
public interface WorkFlowMyHistoricalTaskMapper {

    /**
     * @Description:
     * 我的历史任务  sql查询
     * @param pageSize 当前页码
     * @param offset 本页开始数据
     * @param assignee 办理人id
     * @param processInstanceId 流程实例id
     * @param startUserName 流程发起人名称
     * @Return : java.util.List<org.jeecg.modules.workflow.entity.vo.MyHistoricalTaskVO>
     * @Author : LZ @DXC.Technology  
     * @Time: 2021/8/27 17:05
    */
    /**
     * 分页查询我的历史任务
     * */
    List<MyHistoricalTaskVO> queryMyHistoricalTask(@Param("pageSize") Integer pageSize,
                                                   @Param("offset") Integer offset,
                                                   @Param("assignee") String assignee,
                                                   @Param("processInstanceId") String processInstanceId,
                                                   @Param("startUserName") String startUserName);

    /**
     * @Description:
     * 查询我的历史任务总条数
     * @param assignee 办理人id
     * @param processInstanceId 流程实例id
     * @param startUserName 流程发起人名称
     * @Return : java.lang.Long
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:02
    */
    Long queryMyHistoricalTaskCount(@Param("assignee") String assignee,
                                    @Param("processInstanceId") String processInstanceId,
                                    @Param("startUserName") String startUserName);

    /**
     * @Description:
     * 通过流程实例id查询任务信息
     * @param proceInst 流程实例id
     * @Return : java.util.List<org.jeecg.modules.workflow.entity.vo.ActHiTaskinstVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:06
    */
    List<ActHiTaskinstVO> queryHistoricTaskByProceInst(@Param("proceInst") String proceInst);
}
