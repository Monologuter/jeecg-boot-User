package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.form.entity.FormSysPermissionDO;

/**
 * 表单菜单持久层接口
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 13:39
 */
@InterceptorIgnore(tenantLine = "true")
public interface FormSysPermissionMapper extends BaseMapper<FormSysPermissionDO> {
}
