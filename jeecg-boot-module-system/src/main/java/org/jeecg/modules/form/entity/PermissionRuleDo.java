package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.jeecg.modules.form.typehandler.StringListTypeHandler;

import java.util.List;

@Data
@Builder
@TableName("fd_permission_rule")
public class PermissionRuleDo {
    private String id;
    private String permissionId;
    @TableField(typeHandler = StringListTypeHandler.class)
    private List<String> ruleKey;
    private String ruleValue;
}
