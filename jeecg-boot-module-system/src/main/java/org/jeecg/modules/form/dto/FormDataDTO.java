package org.jeecg.modules.form.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormDataDO;

/**
 * 表单数据DTO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/18 9:28
 */
@ToString(callSuper=true)
@EqualsAndHashCode(callSuper = true)
@Data
public class FormDataDTO extends FormDataDO {
    private FormDO formDO;
}
