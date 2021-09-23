package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 组的实体类
 * @author: lz
 * @date: 2021年05月26日 14:04
 */
@Data
public class GroupVO {

    //任务id
    @ApiModelProperty(value = "任务id")
    private String taskId;

    //任务名称
    @ApiModelProperty(value = "任务名称")
    private String taskName;

    //发起人
    @ApiModelProperty(value = "流程发起名称")
    private String startUserName;

    //开始时间
    @ApiModelProperty(value = "任务开始时间")
    private String startTime;

    //流程定义名称
    @ApiModelProperty(value = "流程定义名称")
    private String processDefinitionName;

    //流程实例id
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    //组id
    @ApiModelProperty(value = "组id")
    private String groupId;

    //组名称
    @ApiModelProperty(value = "组名称")
    private String groupName;
}
