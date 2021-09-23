package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@TableName("sys_operation_record")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysOperationRecord {
    /**
     * 操作记录id(主键)
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 记录属于哪个应用下的操作
     */
    private String applicationId;
    /**
     * 操作类型（记录为做了何种操作）
     */
    private int operationType;
    /**
     * 操作之前的原数据（用来做还原）
     */
    private String data;
    /**
     * 操作之后数据
     */
    private String currentData;
    /**
     * 执行操作的时间
     */
    @Excel(name = "更新时间", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新时间")
    private java.util.Date time;
    /**
     * 操作人
     */
    private String operator;

    public SysOperationRecord() {
    }
}
