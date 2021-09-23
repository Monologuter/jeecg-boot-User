package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @Description: chun
 * @author: liuchun
 * @date: 2021年07月28日 11:52
 */
@Data
public class ConfInfoVO {

    /**整个流程所有任务节点扩展属性信息*/
    @ApiModelProperty(value = "整个流程所有任务节点扩展属性信息")
    private List<ExtensionVO> extensionVOS;

    /**所有角色为申请人的任务节点id*/
    @ApiModelProperty(value = "所有角色为申请人的任务节点id")
    private List<String> examines;
}
