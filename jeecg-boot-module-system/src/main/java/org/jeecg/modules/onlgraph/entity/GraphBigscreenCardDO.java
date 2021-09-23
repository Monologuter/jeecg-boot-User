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
 * @Description: 大屏-卡片
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Data
@TableName("graph_bigscreen_card")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "graph_bigscreen_card对象", description = "大屏-卡片")
public class GraphBigscreenCardDO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
    /**
     * 大屏ID
     */
    @ApiModelProperty(value = "大屏ID")
    private String biggreenId;
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
     * 卡片类型 1柱状、2折线图、3饼状图
     */
//    @Excel(name = "卡片类型 1柱状、2折线图、3饼状图", width = 15, dicCode = "graph_type")
    @ApiModelProperty(value = "卡片类型 1柱状、2折线图、3饼状图")
    private Integer cardGraphType;
    /**
     * 数据类型 1json、2sql、3api
     */
    @Excel(name = "数据类型 1json、2sql、3api", width = 15, dicCode = "graph_data_type")
    @ApiModelProperty(value = "数据类型 1json、2sql、3api")
    private Integer cardDataType;
    /**
     * 图表展示模板 1单排、2多排、3tab
     * 通过数据字典进行转换
     */
//    @Excel(name = "图表展示模板", width = 15, dicCode = "graph_show_type")
    @ApiModelProperty(value = "图表展示模板")
    private Integer cardShowType;
    /**
     * 卡片数据（json）
     */
    @Excel(name = "卡片数据（json）", width = 15)
    @ApiModelProperty(value = "卡片数据（json）")
    private String cardData;
    /**
     * 卡片编码
     */
    //    @Excel(name = "卡片编码", width = 15)
    @ApiModelProperty(value = "卡片编码")
    private String cardCode;
    /**
     * 卡片自定义css样式
     */
    //    @Excel(name = "卡片自定义css样式", width = 15)
    @ApiModelProperty(value = "卡片自定义css样式")
    private String cssStyle;
    /**
     * 卡片样式属性（json）
     */
    @Excel(name = "卡片样式属性（json）", width = 15)
    @ApiModelProperty(value = "卡片样式属性（json）")
    private String cardSpecialFiled;
    /**
     * 逻辑删除
     */
//    @Excel(name = "逻辑删除", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "逻辑删除")
    private Integer delFlag;
    /**
     * 是否显示
     */
//    @Excel(name = "是否显示", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "是否显示")
    private Integer isShow;
}
