package org.jeecg.modules.form.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Data;
import org.jeecg.modules.form.entity.BaseEntity;

/**
 * @Description:
 * @author: LiKun
 * @date: 2021年09月10日 17:14
 */
@Data
public class SaveFormMeetingScheduleVO extends BaseEntity {
    @ApiModelProperty(value = "会议主题", required = true)
    @ApiParam(required = true)
    private String title;

    @ApiModelProperty(value = "会议方式", required = false, example = "目前只有线下会议室一种")
    private String type;

    @ApiModelProperty(value = "会议开始时间", required = true)
    private String start;

    @ApiModelProperty(value = "会议结束时间", required = true)
    private String end;

    @ApiModelProperty(value = "会议地点", required = false)
    private String place;

    @ApiModelProperty(value = "预定项目组", required = false)
    private String team;

    @ApiModelProperty(value = "会议备注", required = true)
    private String mark;

    @ApiModelProperty(value = "预定者", required = true)
    private String booker;

    @ApiModelProperty(value = "参会人数", required = false)
    private Integer members;

    @ApiModelProperty(value = "会议状态", required = true)
    private String status;

    @ApiModelProperty(value = "会议方式对应的颜色", required = false, example = " type-orange、type-blue、type-green")
    @TableField(value = "style_name")
    private String classNames;

    @ApiModelProperty(value = "筹备物品", required = false, example = "茶水，马克笔")
    @TableField(value = "prepare_items")
    private String prepareItems;

}
