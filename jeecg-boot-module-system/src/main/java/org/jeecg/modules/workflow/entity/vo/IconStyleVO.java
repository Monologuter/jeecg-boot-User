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
@TableName("act_proc_icon_style")
@NoArgsConstructor
@AllArgsConstructor
public class IconStyleVO {

    /**icon样式*/
    @ApiModelProperty(value = "icon样式")
    @TableId(value = "ICON_STYLE_",type = IdType.INPUT)
    private String iconStyle;
}
