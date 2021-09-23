package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表单样式DO
 *
 * @Author: HuangSn
 * @Date: 2021/5/6 21:53
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "fd_form_style")
public class FormStyleDO extends BaseEntity{
    private String name;
    private String content;
    private String code;
    private String type;

}
