package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

/**
 * @author 权计超
 * 启动流程实例参数
 */
@Data
public class StartVariable implements Serializable {

    /**流程定义id或key*/
    @ApiModelProperty("流程定义id或key")
    private String processDefinitionParameter;

    /**发起人id*/
    @ApiModelProperty("发起人id")
    private String userId;

    /**抄送人id列表*/
    @ApiModelProperty("抄送人id列表")
    private String[] copy;

    /**初始化json参数*/
    @ApiModelProperty("初始化json参数")
    private String rowData;
}
