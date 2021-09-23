package org.jeecg.modules.onlgraph.entity;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.NoArgsConstructor;
import org.jeecgframework.poi.excel.annotation.ExcelVerify;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_head图表配置表汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Data
@TableName("onl_cfgraph_head")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value = "onl_cfgraph_head对象", description = "onl图表配置表汇总表")
@AllArgsConstructor
@NoArgsConstructor
public class OnlCfgraphHeadDO implements Serializable {
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
     * 图表名称
     */
    @Excel(name = "图表名称", width = 15)
    @ApiModelProperty(value = "图表名称")
    @ExcelVerify(notNull = true)
    private String graphName;
    /**
     * 图表描述
     */
    @Excel(name = "图表描述", width = 15)
    @ApiModelProperty(value = "图表描述")
    private String graphDescription;

    /**
     * sql多数据源
     */
    @ApiModelProperty(value = "sql多数据源")
    private String dbKey;

    /**
     * 数据类型 1json、2sql、3api
     * 通过数据字典进行转换
     */
    @Excel(name = "数据类型", width = 15, dicCode = "graph_data_type")
    @ApiModelProperty(value = "数据类型")
    @ExcelVerify(notNull = true)
    private Integer graphDataType;

    /**
     * 图表类型 1柱状、2折线图、3饼状图
     * 通过数据字典进行转换
     */
    @Excel(name = "图表类型", width = 15, dicCode = "graph_type")
    @ApiModelProperty(value = "图表类型")
    @ExcelVerify(notNull = true)
    private String graphType;

    /**
     * 图表展示模板 1单排、2多排、3tab
     * 通过数据字典进行转换
     */
    @Excel(name = "图表展示模板", width = 15, dicCode = "graph_show_type")
    @ApiModelProperty(value = "图表展示模板")
    private Integer graphShowType;

    /**
     * 数据
     */
    @Excel(name = "数据", width = 15)
    @ApiModelProperty(value = "数据")
    @ExcelVerify(notNull = true)
    private String graphData;

    /**
     * 图表x轴字段名
     */
    @Excel(name = "图表x轴字段名", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "图表x轴字段名")
    @ExcelVerify(notNull = true)
    private String graphX;

    /**
     * 图表y轴字段名
     */
    @Excel(name = "图表y轴字段名", width = 15)
    @ApiModelProperty(value = "图表y轴字段名")
    @ExcelVerify(notNull = true)
    private String graphY;

    /**
     * 图表编码
     */
    @Excel(name = "图表编码", width = 15)
    @ApiModelProperty(value = "图表编码")
    @ExcelVerify(notNull = true, interHandler = true)
    private String graphCode;

    /**
     * 图表菜单是否生成状态控制
     */
    @Excel(name = "是否生成菜单", width = 15, dicCode = "yn")
    @ApiModelProperty(value = "是否生成菜单")
    @ExcelVerify(notNull = true)
    private Integer graphPermissionStatus;

    /**
     * 表单数据ID
     */
    @Excel(name = "表单数据ID", width = 15)
    @ApiModelProperty(value = "表单数据ID")
    private String formId;

    /**
     * 图标菜单删除标记
     */
    @ApiModelProperty(value = "是否已删除")
    private Integer delFlag;

}
