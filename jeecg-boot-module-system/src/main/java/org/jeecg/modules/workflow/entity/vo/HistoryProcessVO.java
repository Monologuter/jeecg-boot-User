package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 历史信息
 * @author: ghz
 * @date: 2021年04月14日 15:08
 */
@Data
public class HistoryProcessVO {
    /**当前任务ID*/
    @ApiModelProperty(value = "当前任务ID")
    private String taskId;

    /**当前任务的角色*/
    @ApiModelProperty(value = "当前任务的角色")
    private boolean taskRole;

    /**当前任务名称*/
    @ApiModelProperty(value = "当前任务名称")
    private String taskName;

    /**业务标题*/
    @ApiModelProperty(value = "业务标题")
    private String businessTitle;

    /**流程名称*/
    @ApiModelProperty(value = "流程名称")
    private String  name;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**流程定义ID*/
    @ApiModelProperty(value = "流程定义ID")
    private String processDefinitionId;

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
