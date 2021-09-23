package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 候选人实体类
 * @author: sbw
 * @date: 2021年05月13日 10:11
 */
@Data
public class CandidateAboutVO {

    /**流程编号*/
    @ApiModelProperty(value = "流程编号")
    private String processDefinitionId;

    /**任务ID*/
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**任务名称*/
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    /**流程实例名称*/
    @ApiModelProperty(value = "流程实例名称")
    private String processName;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String startUserId;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;
}

