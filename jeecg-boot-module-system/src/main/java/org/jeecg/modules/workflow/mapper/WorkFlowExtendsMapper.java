package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;

import java.util.List;

/**
 * 扩展信息表控制
 * @author 权计超
 * @company DXC
 * @date 2021/5/31 10:06
 */
@Mapper
public interface WorkFlowExtendsMapper extends BaseMapper<ProcessDevelopmentVO> {
    /**
     * 根据流程id获取扩展信息表
     * */
    List<ProcessDevelopmentVO> getExtendsByProcKey(@Param("recentUse") List<String> recentUse);
}
