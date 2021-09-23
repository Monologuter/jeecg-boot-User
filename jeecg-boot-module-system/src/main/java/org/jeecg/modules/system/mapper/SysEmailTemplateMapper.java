package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.system.entity.SysEmailTemplate;
import org.springframework.web.bind.annotation.RequestParam;

@Mapper
public interface SysEmailTemplateMapper extends BaseMapper<SysEmailTemplate>{

}
