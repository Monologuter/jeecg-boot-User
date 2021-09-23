package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Set;

/**
 * @Description: 节点流程高亮
 * @author: ghz
 * @date: 2021年03月24日 14:54
 */
@Data
public class ActivitiHighLineDTO {

    /**活动节点 id */
    @ApiModelProperty(value = "已完成节点")
    private Set<String> highPoint;

    /**活动节点 id */
    @ApiModelProperty(value = "待完成节点")
    private Set<String> waitingToDo;
}
