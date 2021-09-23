package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: TODO
 * @author: liuchun
 * @date: 2021年08月20日 21:57
 */
@Data
public class ActHiTaskinstVO {

    /**任务id*/
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**任务定义key即活动定义id*/
    @ApiModelProperty(value = "任务定义key即活动定义id")
    private String taskDefKey;

    /**流程定义key*/
    @ApiModelProperty(value = "流程定义key")
    private String procDefKey;

    /**流程定义id*/
    @ApiModelProperty(value = "流程定义id")
    private String procDefId;

    /**流程实例根id*/
    @ApiModelProperty(value = "流程实例根id")
    private String rootProcInstId;

    /**流程实例id*/
    @ApiModelProperty(value = "流程实例id")
    private String procInstId;

    /**执行id*/
    @ApiModelProperty(value = "执行id")
    private String executionId;

    /**活动实例id*/
    @ApiModelProperty(value = "活动实例id")
    private String actInstId;

    /**任务名称*/
    @ApiModelProperty(value = "任务名称")
    private String name;

    /**执行人id*/
    @ApiModelProperty(value = "执行人id")
    private String assignee;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**结束时间*/
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**耗时*/
    @ApiModelProperty(value = "耗时")
    private String duration;

    /**删除原因*/
    @ApiModelProperty(value = "删除原因")
    private String deleteReason;
}
