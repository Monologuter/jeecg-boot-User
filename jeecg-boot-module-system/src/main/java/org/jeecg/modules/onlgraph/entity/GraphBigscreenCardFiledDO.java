package org.jeecg.modules.onlgraph.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author wangjiahao, wangshuang
 * @Description: 卡片高级属性
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Data
@TableName("graph_bigscreen_card_filed")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "graph_bigscreen_card_filed对象", description = "大屏-卡片-其他属性")
public class GraphBigscreenCardFiledDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 创建人
     */
    @ApiModelProperty(value = "创建人")
    private String createBy;
    /**
     * 创建日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "创建日期")
    private Date createTime;
    /**
     * 更新人
     */
    @ApiModelProperty(value = "更新人")
    private String updateBy;
    /**
     * 更新日期
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
    /**
     * 所属部门
     */
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
    /**
     * 卡片id
     */
    @Excel(name = "卡片id", width = 15)
    @ApiModelProperty(value = "卡片id")
    private String cardId;
    /**
     * 字段名
     */
    @Excel(name = "字段名", width = 15)
    @ApiModelProperty(value = "字段名")
    private String fieldName;
    /**
     * 是否显示
     */
//    @Excel(name = "是否显示", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "是否显示")
    private Integer fieldDisplay;
    /**
     * 是否为查询字段
     */
//    @Excel(name = "是否为查询字段", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "是否为查询字段")
    private Integer fieldQuery;
    /**
     * 字段描述
     */
//    @Excel(name = "字段描述", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "字段描述")
    private String fieldDescription;
    /**
     * 逻辑删除
     */
//    @Excel(name = "逻辑删除", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
}
