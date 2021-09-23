package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.form.typehandler.StringListTypeHandler;

import java.util.List;

/**
 * 表单菜单DO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 13:35
 */
@TableName("fd_form_sys_permission")
@Data
@EqualsAndHashCode(callSuper = true)
public class FormSysPermissionDO extends BaseEntity {
    private String formId;
    private String sysPermissionId;
    private String fields;
    private String searches;
}
