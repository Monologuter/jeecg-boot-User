package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.form.entity.PermissionButtonDo;

@InterceptorIgnore(tenantLine = "true")
public interface FormButtonMapper extends BaseMapper<PermissionButtonDo> {
}
