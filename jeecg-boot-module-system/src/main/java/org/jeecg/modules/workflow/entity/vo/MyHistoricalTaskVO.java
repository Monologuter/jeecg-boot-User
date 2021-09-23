package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 我的历史任务
 * @author: lz
 * @date: 2021年04月09日 14:24
 */
@Data
public class MyHistoricalTaskVO {

    /**流程编号*/
    @ApiModelProperty(value = "流程编号")
    private String processDefinitionId;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**业务标题*/
    @ApiModelProperty(value = "业务标题")
    private String BusinessTitle;

    /**任务ID*/
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**任务名称*/
    @ApiModelProperty(value = "任务名称")
    private String name;

    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String startUserId;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**结束时间*/
    @ApiModelProperty(value = "结束时间")
    private Date endTime;

    /**状态*/
    @ApiModelProperty(value = "状态")
    private String state;

    /**耗时*/
    @ApiModelProperty(value = "耗时")
    private String duration;
}
