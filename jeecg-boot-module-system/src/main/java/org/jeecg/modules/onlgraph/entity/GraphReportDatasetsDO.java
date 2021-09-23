package org.jeecg.modules.onlgraph.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

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
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@TableName("graph_report_dataset")
@ApiModel(value="GraphReportDatasets对象", description="")
public class GraphReportDatasetsDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "逻辑删除 0未删除 1已删除")
    private Integer delFlag;

    @ApiModelProperty(value = "数据源id")
    private String datasourceId;

    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;

    @ApiModelProperty(value = "数据集名称")
    private String datasetName;

    @ApiModelProperty(value = "是否分页 0否 1是")
    private Integer isPagination;

    @ApiModelProperty(value = "sql语句")
    private String reportData;

    @ApiModelProperty(value = "参数")
    private String reportParam;

    @ApiModelProperty(value = "报表id")
    private String reportId;

    @ApiModelProperty(value = "类型 1sql 2api")
    private Integer dataType;

    @ApiModelProperty(value = "每页显示条数")
    private Integer pageCount;

    @ApiModelProperty(value = "创建人")
    private String createBy;

    @ApiModelProperty(value = "创建时间")
    private Date createTime;

    @ApiModelProperty(value = "更新人")
    private String updateBy;

    @ApiModelProperty(value = "更新时间")
    private Date updateTime;
}
