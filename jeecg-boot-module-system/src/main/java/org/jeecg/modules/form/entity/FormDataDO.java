package org.jeecg.modules.form.entity;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.form.typehandler.JsonbTypeHandler;

/**
 * 表单数据DO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/17 16:47
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName(value="fd_form_data",autoResultMap = true)
public class FormDataDO extends BaseEntity{
    private String formId;
    @TableField(typeHandler = JsonbTypeHandler.class)
    private JSONObject rowData;
}
