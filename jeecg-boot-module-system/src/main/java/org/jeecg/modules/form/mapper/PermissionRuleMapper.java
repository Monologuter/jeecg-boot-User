package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.form.entity.PermissionRuleDo;

@InterceptorIgnore(tenantLine = "true")
public interface PermissionRuleMapper extends BaseMapper<PermissionRuleDo> {
}
