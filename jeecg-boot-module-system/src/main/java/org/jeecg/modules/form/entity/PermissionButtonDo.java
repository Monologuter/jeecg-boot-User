package org.jeecg.modules.form.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import org.jeecg.modules.form.typehandler.JsonbTypeHandler;

@Data
@Builder
@TableName("fd_permission_button")
public class PermissionButtonDo {
    private String id;
    private String permissionId;
    private Integer type;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private JSONObject info;
    private String icon;
    private String name;
    private String color;
}
