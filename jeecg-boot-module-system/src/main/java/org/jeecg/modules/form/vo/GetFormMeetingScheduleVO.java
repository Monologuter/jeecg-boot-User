package org.jeecg.modules.form.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecg.modules.form.entity.FormMeetingScheduleDO;

/**
 * @Description:
 * @author: LiKun
 * @date: 2021年09月15日 13:10
 */
@Data
public class GetFormMeetingScheduleVO extends FormMeetingScheduleDO {
    @ApiModelProperty(value = "可选会议主题")
    private String optionalTitle;
}
