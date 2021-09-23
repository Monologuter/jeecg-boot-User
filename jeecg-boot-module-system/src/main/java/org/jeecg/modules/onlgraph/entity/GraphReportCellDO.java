package org.jeecg.modules.onlgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import org.jeecg.modules.form.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
* <p>
*
* </p>
*
* @author system
* @since 2021-09-09
*/
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("graph_report_cell")
@ApiModel(value="GraphReportCell对象", description="")
public class GraphReportCellDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;

    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

    @ApiModelProperty(value = "字体大小")
    private String fontSize;

    @ApiModelProperty(value = "合并列数")
    private Integer colSpan;

    @ApiModelProperty(value = "合并行数")
    private int rowSpan;

    @ApiModelProperty(value = "背景颜色")
    private String backColor;

    @ApiModelProperty(value = "单元格的值")
    private String cellValue;

    @ApiModelProperty(value = "单元格横坐标")
    private Integer coordsx;

    @ApiModelProperty(value = "单元格纵坐标")
    private Integer coordsy;

    @ApiModelProperty(value = "逻辑删除 0未删除 1已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "数据集名称")
    private String datasetName;

    @ApiModelProperty(value = "是否字体加粗 0否 1是")
    private Integer isBold;

    @ApiModelProperty(value = "单元格扩展方向 1不扩展 2纵向扩展 2横向扩展")
    private Integer cellExtend;

    @ApiModelProperty(value = "sheet页id")
    private Integer sheetId;

    @ApiModelProperty(value = "字体颜色")
    private String fontColor;

    @ApiModelProperty(value = "报表id")
    private String reportId;

    @ApiModelProperty(value = "是否是合并单元格 0否 1是")
    private Integer isMerge;

    @ApiModelProperty(value = "函数类型 1求和 2求平均值 3最大值 4最小值")
    private Integer cellFunction;

    @ApiModelProperty(value = "单元格内容类型 1固定值 2变量")
    private Integer cellValueType;

    @ApiModelProperty(value = "单元格样式")
    private String cellClassname;

    @ApiModelProperty(value = "聚合类型")
    private String aggregateType;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;


}
