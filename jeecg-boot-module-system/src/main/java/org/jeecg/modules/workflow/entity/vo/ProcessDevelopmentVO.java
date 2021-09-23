package org.jeecg.modules.workflow.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年05月31日 9:45
 */
@Data
@TableName("act_proc_expand")
@NoArgsConstructor
@AllArgsConstructor
public class ProcessDevelopmentVO implements Serializable {

    /**流程定义key*/
    @ApiModelProperty(value = "流程定义Key")
    @TableId(value = "PROC_KEY_",type = IdType.INPUT)
    private String procKey;

    /**流程定义分组*/
    @ApiModelProperty(value = "流程定义分组")
    @TableField("PROC_DEF_GROUP_")
    private String procDefGroup;

    /**icon颜色*/
    @ApiModelProperty(value = "icon颜色")
    @TableField("ICON_COLOUR_")
    private String iconColour;

    /**icon图标*/
    @ApiModelProperty(value = "icon图标")
    @TableField("ICON_STYLE_")
    private String iconStyle;


    /**流程定义描述*/
    @ApiModelProperty(value = "流程定义描述")
    @TableField("PROC_DESC_")
    private String procDesc;

    /**流程定义名称*/
    @ApiModelProperty(value = "流程定义名称")
    @TableField("PROC_NAME_")
    private String procName;

    /**流程定义绑定外置表单*/
    @ApiModelProperty(value = "流程定义绑定外置表单")
    @TableField("PROC_FORM_KEY_")
    private String procFormKey;

    /**版本数量*/
    @ApiModelProperty(value = "版本数量")
    @TableField(exist = false)
    private long versions;
}