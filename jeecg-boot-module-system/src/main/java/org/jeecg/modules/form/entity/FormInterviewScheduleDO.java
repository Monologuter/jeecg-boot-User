package org.jeecg.modules.form.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 面试日程记录DO
 *
 * @Author: HuQi
 * @Date: 2021年08月31日 09:54
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "fd_form_interview_schedule")
public class FormInterviewScheduleDO extends BaseEntity{

    @ApiModelProperty(value = "事件名称", required = true)
    @ApiParam(required = true)
    private String title;

    @ApiModelProperty(value = "面试方式", required = true, example = "video、scene、phone")
    private String type;

    @ApiModelProperty(value = "面试方式对应的颜色", required = true, example = "type-orange、type-blue、type-green")
    @TableField(value = "style_name")
    private String classNames;

    @ApiModelProperty(value = "面试开始时间", required = true)
    private String start;

    @ApiModelProperty(value = "面试结束时间", required = true)
    private String end;

    @ApiModelProperty(value = "面试者", required = true)
    private String interviewee;

    @ApiModelProperty(value = "面试官", required = true)
    private String interviewer;

    @ApiModelProperty(value = "部门ID", required = true)
    private String depId;

    @ApiModelProperty(value = "备注")
    private String mark;

    @ApiModelProperty(value = "角色ID", required = true)
    private String roleId;

    @ApiModelProperty(value = "表单ID", required = true)
    private String formId;

}
