package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 表单角色DO
 * 用途：根据表单角色筛选表单数据
 *
 * @author XuDeQing
 * @create 2021-08-25 9:43
 * @modify 2021-08-25 9:43
 */
@Data
@Builder
@Accessors(chain = true)
@TableName("fd_form_role")
public class FormRoleDo {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    private String formId;
    private String roleId;
    @TableField
    private List<String> ruleKey;
    private String ruleValue;
}
