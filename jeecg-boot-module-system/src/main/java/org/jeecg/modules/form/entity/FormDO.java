package org.jeecg.modules.form.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jeecg.modules.form.typehandler.JsonbTypeHandler;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * 表单DO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/3 13:35
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "fd_form",autoResultMap = true)
public class FormDO extends BaseEntity{
    @Excel(name = "模板编码")
    private String code;
    @Excel(name = "模板名称")
    private String name;
    @Excel(name = "模板结构")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private JSONObject json;
    private String department;
    private String dynamicDataSource;
    private String autoCountCollection;
    private Boolean isTemplate;
}
