package org.jeecg.modules.form.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.system.entity.SysPermission;

/**
 * 表单设计器和相关联的权限的数据DTO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 14:17
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class FormSysPermissionDTO extends FormSysPermissionDO {
    private SysPermission sysPermission;
    private FormDO formDO;
}
