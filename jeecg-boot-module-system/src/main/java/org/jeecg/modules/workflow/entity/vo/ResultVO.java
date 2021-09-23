package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ResultVO
 * @Author hobo
 * @Date 19-4-22 下午3:39
 * @Description
 **/
@Data
public class ResultVO<T> {

    /**状态码*/
    @ApiModelProperty("状态码")
    private Integer code;

    /**msg*/
    @ApiModelProperty("msg")
    private String msg;

    /**数据*/
    @ApiModelProperty("数据")
    private T data;
}
