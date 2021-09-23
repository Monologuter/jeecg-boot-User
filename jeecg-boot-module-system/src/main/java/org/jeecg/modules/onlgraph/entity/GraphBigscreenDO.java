package org.jeecg.modules.onlgraph.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.jeecgframework.poi.excel.annotation.ExcelVerify;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author wangjiahao, wangshuang
 * @Description: 大屏
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Data
@TableName("graph_bigscreen")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "graph_bigscreen对象", description = "大屏")
public class GraphBigscreenDO implements Serializable {
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
     * 大屏名称
     */
    @Excel(name = "大屏名称", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏名称")
    private String graphBigscreenName;
    /**
     * 大屏描述
     */
//    @Excel(name = "大屏描述", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏描述")
    private String graphBigscreenDescription;
    /**
     * 大屏编码
     */
    @Excel(name = "大屏编码", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏编码")
    @ExcelVerify(notNull = true, interHandler = true)
    private String graphBigscreenCode;
    /**
     * 大屏高度
     */
//    @Excel(name = "大屏高度", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏高度")
    private String graphScreenHeight;
    /**
     * 大屏宽度
     */
//    @Excel(name = "大屏宽度", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏宽度")
    private String graphScreenWidth;
    /**
     * 大屏简介
     */
//    @Excel(name = "大屏简介", width = 15, needMerge = true)
    @ApiModelProperty(value = "大屏简介")
    private String graphScreenIntroduction;
    /**
     * 背景颜色
     */
//    @Excel(name = "背景颜色", width = 15, needMerge = true)
    @ApiModelProperty(value = "背景颜色")
    private String graphScreenBackgroundColor;
    /**
     * 背景图片
     */
//    @Excel(name = "背景图片", width = 15, needMerge = true)
    @ApiModelProperty(value = "背景图片")
    private String graphScreenBackgroundPicture;
    /**
     * 是否生成模板
     */
    @Excel(name = "是否生成模板", width = 15, dicCode = "yn", needMerge = true)
    @ApiModelProperty(value = "是否生成模板")
    private Integer graphScreenIsTemplate;
    /**
     * 逻辑删除
     */
//    @Excel(name = "逻辑删除", width = 15, dicCode = "yn", needMerge = true)
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 大屏菜单是否配置状态控制
     */
    @Excel(name = "大屏菜单是否配置状态控制", width = 15, dicCode = "yn", needMerge = true)
    @ApiModelProperty(value = "大屏菜单是否配置状态控制")
    private Integer permissionStatus;
    /**
     * 全局样式
     */
    @Excel(name = "全局样式", width = 15, needMerge = true)
    @ApiModelProperty(value = "全局样式")
    private String config;

}
