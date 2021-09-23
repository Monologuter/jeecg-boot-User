package org.jeecg.modules.form.dto;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormMeetingScheduleConstant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description: 以占用的会议开始和结束时间
 * @author: LiKun
 * @date: 2021年09月17日 11:24
 */
@Data
public class FormMeetingStartEndDTO {
    private Date start;
    private Date end;
}
