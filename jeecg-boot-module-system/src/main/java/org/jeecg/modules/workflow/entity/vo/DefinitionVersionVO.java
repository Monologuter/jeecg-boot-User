package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qjc
 *
 */
@Data
public class DefinitionVersionVO {

    /**流程定义id*/
    @ApiModelProperty(value = "流程定义id")
    private String definitionId;

    /**流程定义名称*/
    @ApiModelProperty(value = "流程定义名称")
    private String definitionName;

    /**流程定义key*/
    @ApiModelProperty(value = "流程定义key")
    private String definitionKey;

    /**流程定义版本*/
    @ApiModelProperty(value = "流程定义版本")
    private Integer definitionVersion;

    /**流程定义是否挂起 true:挂起 false:未挂起*/
    @ApiModelProperty(value = "流程定义是否挂起")
    private boolean suspension;
}
