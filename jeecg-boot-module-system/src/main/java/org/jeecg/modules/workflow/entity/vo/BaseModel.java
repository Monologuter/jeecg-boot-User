package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @author bin, lc
 * @company DXC.technology
 * @data 2021-03-23 10:42
 */
@Data
public class BaseModel implements Serializable {

    /**请求：分页属性*/
    @ApiModelProperty("请求：当前页")
    private Integer pageNo;

    /**请求：每页显示的条数*/
    @ApiModelProperty("请求：每页显示的条数")
    private Integer pageSize;


}
