package org.jeecg.modules.workflow.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年05月31日 13:48
 */
@Data
@TableName("act_proc_icon_colour")
@NoArgsConstructor
@AllArgsConstructor
public class IconColourVO {

    /**icon颜色*/
    @ApiModelProperty(value = "icon颜色")
    @TableId(value = "ICON_COLOUR_",type = IdType.INPUT)
    private String iconColour;
}