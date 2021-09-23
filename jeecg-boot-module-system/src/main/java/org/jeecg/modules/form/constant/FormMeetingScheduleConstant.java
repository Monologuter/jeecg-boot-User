package org.jeecg.modules.form.constant;

import com.alibaba.druid.util.StringUtils;
import org.jeecg.modules.form.dto.FormMeetingStartEndDTO;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Description:
 * @author: LiKun
 * @date: 2021年09月13日 11:01
 */
public class FormMeetingScheduleConstant {
    private FormMeetingScheduleConstant() {
    }

    public static final String MESSAGE_INFO_SAVE_LACK_FILED = "请填写必填字段[会议主题、会议状态、会议地点、会议开始时间、会议结束时间、预定者、预定项目组]信息！";

    public static final String MESSAGE_ERROR_UPDATE = "会议日程记录数据修改失败！";
    public static final String MESSAGE_ERROR_INSERT = "会议日程记录数据添加失败！";
    public static final String MESSAGE_ERROR_START_RATHER_END = "会议开始时间不能大于结束时间！";
    public static final String MESSAGE_ERROR_START_EQUALS_END = "会议开始时间不能等于结束时间！";
    public static final String MESSAGE_ERROR_TIME_FIELD = "请填写正确的会议开始和结束时间！";
    public static final String MESSAGE_ERROR_DELETE = "会议日程记录删除失败！！！";
    public static final String MESSAGE_ERROR_PLACE = "会议地点和会议地点资源不一致！！！";

    public static final String MESSAGE_SUCCESS_URL_PARSE = "完成！";
    public static final String MEETING_STATUS_UNDER_APPROVAL = "unBooked";
    public static final String MEETING_STATUS_UNDER_APPROVAL_COLOR = "type-blue";
    public static final String MEETING_STATUS_BOOKED = "booked";
    public static final String MEETING_STATUS_BOOKED_COLOR = "type-green";

    /**
     * @Description: 拼接会议非空窗期返回信息
     * @param team: 与会项目组
     * @param place: 会议室
     * @param isMeeting: 以占用的会议开始和结束时间
     * @return java.lang.String 具体占用时间段
     * @author LiKun
     * @Date 2021/9/17 15:01
     */
    public static String meetingTimeConflict(String team, String place, FormMeetingStartEndDTO isMeeting){
        StringBuilder stringBuilder = new StringBuilder();
        String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(isMeeting.getStart());
        String endTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(isMeeting.getEnd());
        stringBuilder.append(startTime).append("~").append(endTime).append("有会议正在进行，");
        if(!StringUtils.isEmpty(place)){
            return stringBuilder.append(team).append("组在").append(place).append("预定会议失败").toString();
        }
        return stringBuilder.append(team).append("时间冲突，预定会议失败").toString();
    }
}
