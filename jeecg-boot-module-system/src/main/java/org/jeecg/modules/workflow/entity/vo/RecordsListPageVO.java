package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author 权计超
 * @company DXC.technology
 * @date 2021-04-09
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RecordsListPageVO {

    /**响应：分页属性*/
    @ApiModelProperty("响应：总条数")
    private Long total;

    /**当前页的条数*/
    @ApiModelProperty("响应：当前页的条数")
    private Integer size;

    /**当前页数*/
    @ApiModelProperty("响应：当前页数")
    private Integer current;

    /**总页数*/
    @ApiModelProperty("响应：总页数")
    private Integer pages;

    /**自定义DTO类数据*/
    @ApiModelProperty("数据")
    List records;


}
