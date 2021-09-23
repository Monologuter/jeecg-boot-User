package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author 权计超
 * 设置抄送人
 */
@Data
public class CopyVO {

    /**流程实例id*/
    @ApiModelProperty("流程实例id")
    private String processInstanceId;

    /**抄送人列表*/
    @ApiModelProperty("抄送人列表")
    private String[] addCopyArray;
}
