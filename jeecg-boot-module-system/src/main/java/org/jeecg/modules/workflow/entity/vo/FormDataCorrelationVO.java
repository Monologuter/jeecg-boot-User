package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Map;

/**
 * @Description: 提交表单相关数据
 * @author: liuchun
 * @date: 2021年05月08日 13:38
 */
@Data
public class FormDataCorrelationVO {

    /**表单相关属性*/
    @ApiModelProperty(value = "表单相关属性")
    private Map<String,Object> formMap;

    /**任务id*/
    @ApiModelProperty(value = "任务id")
    private String taskId;

    /**流程实例id*/
    @ApiModelProperty(value = "流程实例id")
    private String processInstanceId;

    /**评论信息*/
    @ApiModelProperty(value = "评论信息")
    private String message;
}
