package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Description: 我的任务
 * @author: ghz
 * @date: 2021年03月18日 16:36
 */
@Data
public class MyTaskVO {

    /**业务标题*/
    @ApiModelProperty(value = "业务标题")
    private String businessTitle;

    /**流程编号*/
    @ApiModelProperty(value = "流程编号")
    private String processDefinitionId;

    /**任务ID*/
    @ApiModelProperty(value = "任务ID")
    private String taskId;

    /**流程实例名称*/
    @ApiModelProperty(value = "流程实例名称")
    private String processName;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**发起人*/
    @ApiModelProperty(value = "发起人")
    private String startUserId;

    /**
     * 执行人
     */
    @ApiModelProperty(value = "执行人")
    private String assignee;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private Date startTime;

    /**当前任务*/
    @ApiModelProperty(value = "当前任务")
    private String currentTask;

    /**判断当前任务*/
    @ApiModelProperty(value = "是否可退回")
    private boolean claim;
}
