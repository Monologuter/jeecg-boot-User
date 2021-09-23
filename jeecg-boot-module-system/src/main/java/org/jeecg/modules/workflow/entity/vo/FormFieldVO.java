package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description: 动态表单属性实体类
 * @author: liuchun
 * @date: 2021年03月20日 16:55
 */
@Data
public class FormFieldVO {

    /**动态表单字段 id*/
    @ApiModelProperty(value = "动态表单字段 id")
    private String id;

    /**动态表单字段 类型*/
    @ApiModelProperty(value = "动态表单字段 类型")
    private String type;

    /**动态表单字段 备注*/
    @ApiModelProperty(value = "动态表单字段 备注")
    private String label;

    /**动态表单字段 默认值*/
    @ApiModelProperty(value = "动态表单字段 默认值")
    private String defaultValue;

    /**value值*/
    @ApiModelProperty(value = "value值")
    private Object value;

    /**动态表单字段 property属性*/
    @ApiModelProperty(value = "动态表单字段 property属性")
    private Map<String,String> property;
}
