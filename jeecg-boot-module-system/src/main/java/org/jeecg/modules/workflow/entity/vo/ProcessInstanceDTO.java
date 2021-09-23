package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.models.auth.In;
import lombok.Data;
import java.util.Date;

/**
 * @author bin, lc
 * @company DXC.technology
 * @data 2021-03-18 13:24
 */
@Data
public class ProcessInstanceDTO extends BaseModel {

    /**流程实例名称*/
    @ApiModelProperty(value = "流程实例名称")
    private String name;

    /**业务key值*/
    @ApiModelProperty(value = "业务key值")
    private String businessKey;

    /**流程定义的id*/
    @ApiModelProperty(value = "流程定义的id")
    private String processDefinitionId;

    /**流程实例的id*/
    @ApiModelProperty(value = "流程实例的id")
    private String processInstanceId;

    /**流程实例发起人*/
    @ApiModelProperty(value = "流程实例发起人")
    private String initiator;

    /**当前节点任务id*/
    @ApiModelProperty(value = "当前节点任务Id")
    private String currentTaskId;

    /**当前节点任务*/
    @ApiModelProperty(value = "当前节点任务")
    private String currentTask;

    /**当前节点任务的办理人*/
    @ApiModelProperty(value = "当前节点任务的办理人")
    private String assignee;

    /**当前节点任务的开始时间*/
    @ApiModelProperty(value = "当前节点任务的开始时间")
    private Date startTime;

    /**当前节点任务耗时*/
    @ApiModelProperty(value = "当前节点任务耗时")
    private String dueDate;

    /**挂起状态 1：激活 2：挂起*/
    @ApiModelProperty(value = "挂起状态 false：激活 true：挂起")
    private Integer State;

    /**挂起状态 1：激活 2：挂起*/
    @ApiModelProperty(value = "挂起状态 false：激活 true：挂起")
    private Boolean suspensionState;

    /**当前流程实例状态*/
    @ApiModelProperty(value = "当前流程实例状态")
    private String proState;
}
