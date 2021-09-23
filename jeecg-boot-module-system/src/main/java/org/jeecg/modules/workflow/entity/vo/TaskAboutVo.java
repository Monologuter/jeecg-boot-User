package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年05月08日 16:25
 */
@Data
public class TaskAboutVo {

    /**任务ID*/
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**流程定义ID*/
    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**流程发起人*/
    @ApiModelProperty(value = "流程创建人")
    private String Assignee;

    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String processDefinitionName;

}
