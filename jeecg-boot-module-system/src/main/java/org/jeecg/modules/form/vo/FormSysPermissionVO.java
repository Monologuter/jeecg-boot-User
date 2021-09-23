package org.jeecg.modules.form.vo;

import lombok.Data;
import org.jeecg.modules.system.entity.SysPermission;

/**
 * 表单菜单VO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 14:20
 */
@Data
public class FormSysPermissionVO {
    private String formId;
    private SysPermission sysPermission;
}
