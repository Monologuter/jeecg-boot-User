package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@TableName("fd_form_button")
public class FormButtonDo {
    private String id;
    private String formId;
    private Integer type;
    private String info;
    private String icon;
    private String color;
}
