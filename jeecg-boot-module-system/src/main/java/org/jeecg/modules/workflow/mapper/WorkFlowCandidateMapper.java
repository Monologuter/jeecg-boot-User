package org.jeecg.modules.workflow.mapper;


import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 候选人相关
 * @author: SunBoWen
 * @date: 2021年06月01日 10:50
 */
@Mapper
public interface WorkFlowCandidateMapper {

    /**
     * 根据流程定义id获取拥有人
     * */
    String getOwner(String procInstId);
}
