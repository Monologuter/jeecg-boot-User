package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author qjc
 * 启动流程实例返回参数
 */
@Data
public class StartProcessInstanceByIdVO {

    /**流程定义id*/
    @ApiModelProperty("流程定义id")
    private String procdefId;

    /**流程实例id*/
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**当前任务id*/
    @ApiModelProperty("当前任务id")
    private String taskId;
}
