package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 所有历史任务
 * @author: ghz
 * @date: 2021年04月15日 14:20
 */
@Data
public class HistoryTaskVO {

    /**业务标题*/
    @ApiModelProperty(value = "业务标题")
    private String businessTitle;

    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String  name;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processDefinitionId;

    /**任务ID*/
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String startUserId;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**结束时间*/
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**耗时*/
    @ApiModelProperty(value = "耗时")
    private Long durationLong;

    /**耗时*/
    @ApiModelProperty(value = "耗时")
    private String duration;

    /**状态*/
    @ApiModelProperty(value = "状态")
    private String state;

    /**状态*/
    @ApiModelProperty(value = "状态信号")
    private boolean stateSignal;
}
