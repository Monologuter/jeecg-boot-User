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
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_field图表配置表单汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Data
@TableName("onl_cfgraph_field")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "onl_cfgraph_field对象", description = "onl图表配置表单汇总表")
public class OnlCfgraphFieldDO implements Serializable {
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
     * 所属表ID
     */
    @Excel(name = "所属表ID", width = 15)
    @ApiModelProperty(value = "所属表ID")
    private String onlCfgraphId;
    /**
     * 字段文本
     */
    @Excel(name = "字段文本", width = 15)
    @ApiModelProperty(value = "字段文本")
    private String dbFieldText;
    /**
     * 字段名称
     */
    @Excel(name = "字段名称", width = 15)
    @ApiModelProperty(value = "字段名称")
    private String dbFieldName;
    /**
     * 查询模式
     */
    @Excel(name = "查询模式", width = 15)
    @ApiModelProperty(value = "查询模式")
    private String dbFieldInquireType;
    /**
     * 排序
     */
    @Excel(name = "排序", width = 15)
    @ApiModelProperty(value = "排序")
    private Integer orderNum;
    /**
     * 字段类型
     */
    @Excel(name = "字段类型", width = 15)
    @ApiModelProperty(value = "字段类型")
    private String dbFieldType;
    /**
     * 是否显示
     */
    @Excel(name = "是否显示", width = 15)
    @ApiModelProperty(value = "是否显示")
    private Integer dbFieldDisplayable;
    /**
     * 计算总和
     */
    @Excel(name = "计算总和", width = 15)
    @ApiModelProperty(value = "计算总和")
    private Integer dbFieldSum;
    /**
     * 是否查询
     */
    @Excel(name = "是否查询", width = 15)
    @ApiModelProperty(value = "是否查询")
    private Integer dbFieldInquire;
    /**
     * 字典Code
     */
    @Excel(name = "字典Code", width = 15)
    @ApiModelProperty(value = "字典Code")
    private String dbFieldCode;
    /**
     * 操作
     */
    @Excel(name = "操作", width = 15)
    @ApiModelProperty(value = "操作")
    private String dbFieldOperate;
}
