package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 表单与表单样式关联表DO
 *
 * @Author: HuangSn
 * @Date: 2021/5/7 20:06
 */
@Data
@TableName(value = "fd_form_style_mapping")
public class FormCSSDO {
    @ApiModelProperty(value = "主键id")
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    @ApiModelProperty(value = "样式id")
    private String styleId;

    @ApiModelProperty(value = "表单id")
    private String formId;
}
