package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: chun
 * @author: liuchun
 * @date: 2021年07月28日 11:46
 */
@Data
public class ExtensionVO {

    /**活动节点id*/
    @ApiModelProperty(value = "活动节点id")
    private String activityId;

    /**任务节点角色*/
    @ApiModelProperty(value = "任务节点角色")
    private String role;

    /**审批表单的活动id*/
    @ApiModelProperty(value = "审批表单的活动id")
    private String examine;
}
