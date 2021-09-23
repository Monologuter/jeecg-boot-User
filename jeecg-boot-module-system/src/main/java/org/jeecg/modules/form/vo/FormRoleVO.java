package org.jeecg.modules.form.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 表单角色返回的数据
 *
 * @author XuDeQing
 * @create 2021-08-31 17:01
 * @modify 2021-08-31 17:01
 */
@Data
@Accessors(chain = true)
public class FormRoleVO {
    private String id;
    private String formId;
    private String roleId;
    private List<String> ruleKey;
    private String ruleValue;
}
