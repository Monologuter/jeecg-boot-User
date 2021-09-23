package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 活动类
 * @author: liuchun
 * @date: 2021年03月19日 13:54
 */
@Data
public class ActivityVO {

    /**活动节点 id */
    @ApiModelProperty(value = "节点id(编号)")
    private String id;

    /**活动节点 名称 */
    @ApiModelProperty(value = "节点名称")
    private String name;

    /**活动表单地址 */
    @ApiModelProperty(value = "外置表单地址")
    private String formKey;

    /**执行人 */
    @ApiModelProperty(value = "执行人")
    private String assignee;
}
