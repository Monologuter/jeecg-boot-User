package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 应用表
 * </p>
 *
 * @Author Zhouhonghan
 */
@Data
@EqualsAndHashCode
@Accessors
public class SysApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 应用名称
     */
    @Excel(name="应用名",width=50)
    private String applicationName;

    /**
     * 应用编码
     */
    @Excel(name="应用编码",width=50)
    private String applicationCode;

    /**
     * 描述
     */
    @Excel(name="描述",width=50)
    private String description;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新人
     */
    private String updateBy;

    /**
     * 更新时间
     */
    @JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    /**
     * 图标形状
     */
    @Excel(name = "图标形状", width = 50)
    private String avatarShape;

    /**
     * 图标颜色
     */
    @Excel(name = "图标颜色", width = 50)
    private String avatarColor;

    /**
     * 状态码
     * 0:已停用 1:未发布 2:已上线
     */
    @Excel(name = "状态码{0:已停用 1:未发布 2:已上线}", width = 100)
    private Integer status;

    /**
     * 是否为模板
     * 0:非模板 1:模板
     */
    @Excel(name = "是否为模板{0:非模板 1:模板}", width = 50)
    private Integer template;

    /**
     * 应用分类
     */
    @Excel(name = "应用分类", width = 50)
    private String applicationType;

    /**
     * 是否显示工作台。0：不显示工作台 1：显示工作台
     */
    private int displayWorkbench;

    /**
     * 图像
     */
    private String applicationImage;

    /**
     * 多租户字段
     */
    private Integer tenantId;

    public SysApplication() {
    }
}
