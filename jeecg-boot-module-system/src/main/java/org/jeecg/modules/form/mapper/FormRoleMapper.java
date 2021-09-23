package org.jeecg.modules.form.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.form.entity.FormRoleDo;

/**
 * 表单角色持久层接口
 *
 * @author XuDeQing
 * @create 2021-08-26 13:22
 * @modify 2021-08-26 13:22
 */
@DS("form")
@InterceptorIgnore(tenantLine = "true")
public interface FormRoleMapper extends BaseMapper<FormRoleDo> {
}
