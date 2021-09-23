package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.workflow.entity.vo.HistoryProcessVO;
import java.util.List;

/**
 * @Author:LZ
 * @Description:我发起的流程
 * @Date:2021-8-17
 */
@Mapper
public interface WorkFlowMyStartProcessMapper {

    /**
     * @Description:
     * 由我发起的正在运行中的流程 sql查询
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key（业务标题）
     * @param startUserId 流程发起人id
     * @Return : java.util.List<org.jeecg.modules.workflow.entity.vo.HistoryProcessVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 11:41
    */
    List<HistoryProcessVO> myStartProcessList(@Param("proDefName")String  proDefName,
                                              @Param("proDefKey")String  proDefKey,
                                              @Param("startUserId")String  startUserId);

    /**
     * @Description:
     * 由我发起的所有流程 sql查询
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key（业务标题）
     * @param startUserId 流程发起人id
     * @param proState 流程状态
     * @Return : java.util.List<org.jeecg.modules.workflow.entity.vo.HistoryProcessVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 11:43
    */
    List<HistoryProcessVO> myHistoricalStartProcessList(@Param("proDefName")String  proDefName,
                                                        @Param("proDefKey")String  proDefKey,
                                                        @Param("startUserId")String  startUserId,
                                                        @Param("proState")String  proState);

}
