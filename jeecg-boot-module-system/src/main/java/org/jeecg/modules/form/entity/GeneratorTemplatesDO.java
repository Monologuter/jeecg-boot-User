package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成器模板实体类
 *
 * @author: HuQi
 * @date: 2021年07月29日 07:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("generator_templates")
public class GeneratorTemplatesDO extends BaseEntity{

    @ApiModelProperty(value = "模板名称")
    private String templateName;

    @ApiModelProperty(value = "是否使用Controller模板")
    private Integer isUseController;

    @ApiModelProperty(value = "Controller内容")
    private String controllerContent;

    @ApiModelProperty(value = "是否使用Entity模板")
    private Integer isUseEntity;

    @ApiModelProperty(value = "Entity内容")
    private String entityContent;

    @ApiModelProperty(value = "是否使用Mapper模板")
    private Integer isUseMapper;

    @ApiModelProperty(value = "Mapper内容")
    private String mapperContent;

    @ApiModelProperty(value = "是否使用Service模板")
    private Integer isUseService;

    @ApiModelProperty(value = "Service内容")
    private String serviceContent;

    @ApiModelProperty(value = "是否使用ServiceImpl模板")
    private Integer isUseServiceImpl;

    @ApiModelProperty(value = "ServiceImpl内容")
    private String serviceImplContent;

    @ApiModelProperty(value = "是否使用自定义变量")
    private Integer isUseTemplatesVar;

    @ApiModelProperty(value = "自定义变量内容")
    private String templatesVarJson;

    @ApiModelProperty(value = "是否公开")
    private Integer isPublic;
}
