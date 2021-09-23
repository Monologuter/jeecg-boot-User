package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;

import java.util.List;

/**
 * 拓展流程实体类
 * @Description: 集成BaseMapper
 * @author: SunBoWen
 * @date: 2021年05月31日 9:44
 */
@Mapper
public interface ProcessDevelopmentMapper extends BaseMapper<ProcessDevelopmentVO> {
    /**
     * 根据外置表单id获取流程定义信息
     * @return
     */
    List<ProcessDefinitionDTO> getProceDefByFormKey(@Param("formKey") String formKey);
}