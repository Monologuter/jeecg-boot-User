package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;

import java.util.List;

/**
 * 最新版本流程定义
 * @author sch
 * @company DXC
 * @date 2021-08-17
 */
@Mapper
public interface WorkFlowProcessDefinitionMapper {

    /**
     * 查询最新版本流程定义
     * */
    List<ProcessDefinitionDTO> ProcessDefinitionQuery(@Param("pageSize") Integer pageSize,
                                                      @Param("offset") Integer offset,
                                                      @Param("definitionKey") String definitionKey,
                                                      @Param("definitionName") String definitionName);

    /**
     * 查询最新版本流程定义总数
     * */
    Long ProcessDefinitionCount(@Param("definitionKey") String definitionKey,
                                @Param("definitionName") String definitionName);
}
