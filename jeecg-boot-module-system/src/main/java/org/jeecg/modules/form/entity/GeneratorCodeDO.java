package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 代码生成器实体类
 *
 * @author: HuQi
 * @date: 2021年08月01日 10:02
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("generator_code")
public class GeneratorCodeDO extends BaseEntity{

    @ApiModelProperty(value = "模板ID")
    private String templateId;

    @ApiModelProperty(value = "表名称")
    private String tableName;

    @ApiModelProperty(value = "表名称")
    private String tableNamePlus;

    @ApiModelProperty(value = "是否生成代码")
    private Integer isGeneratorCode;

    @ApiModelProperty(value = "代码存放路径")
    private String codePath;

    @ApiModelProperty(value = "是否加载")
    private Integer isGeneratorLoad;

    @ApiModelProperty(value = "所有字段信息")
    private String fields;

    @ApiModelProperty(value = "是否启用接口")
    private Integer isEnableUrl;
}
