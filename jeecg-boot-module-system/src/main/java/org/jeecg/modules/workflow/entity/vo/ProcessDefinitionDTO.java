package org.jeecg.modules.workflow.entity.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author qjc
 * @company DXC.technology
 */
@Data
public class ProcessDefinitionDTO {

    /**流程定义id*/
    @ApiModelProperty(value = "流程定义id")
    private String processDefinitionId;

    /**流程定义名称*/
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    /**流程定义key*/
    @ApiModelProperty(value = "流程定义key")
    private String processDefinitionKey;

    /**流程定义发布状态*/
    @ApiModelProperty(value = "发布状态: true:已发布 false:未发布")
    private boolean processDefinitionDeployState;

    /**流程定义创建人*/
    @ApiModelProperty(value = "创建人")
    private String processDefinitionCreate;

    /**流程定义部署时间*/
    @ApiModelProperty(value = "部署时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date deploymentTime;

    /**部署id*/
    @ApiModelProperty(value = "部署id")
    private String deploymentId;

    /**流程定义描述**/
    @ApiModelProperty(value = "流程定义描述")
    private String tagVersion;
}
