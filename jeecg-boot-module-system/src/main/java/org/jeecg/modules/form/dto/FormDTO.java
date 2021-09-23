package org.jeecg.modules.form.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.system.entity.SysPermission;

import java.util.List;

/**
 * 表单DTO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/11 13:41
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
public class FormDTO extends FormDO {
    private List<SysPermission> permissionList;
    private List<FormDataDO> formDataList;
}
