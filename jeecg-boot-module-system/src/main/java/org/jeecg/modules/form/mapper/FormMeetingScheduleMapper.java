package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.form.dto.FormMeetingStartEndDTO;
import org.jeecg.modules.form.entity.FormMeetingScheduleDO;
import org.jeecg.modules.form.vo.SaveFormMeetingScheduleVO;

import java.util.Date;
import java.util.List;

/**
 * @Description: 表单会议日程
 * @author: LiKun
 * @date: 2021年09月10日 17:37
 */
@Mapper
public interface FormMeetingScheduleMapper extends BaseMapper<FormMeetingScheduleDO> {
    /**
     * @Description: 查询会议预定时间是否是空窗期
     * @param team: 与会项目组
     * @param place: 会议室
     * @param start: 预定的会议开始时间
     * @param end: 预定的会议结束时间
     * @return org.jeecg.modules.form.dto.FormMeetingStartEndDTO 以确定占用的开始结束时间
     * @author LiKun
     * @Date 2021/9/17 14:47
     */
    FormMeetingStartEndDTO judgeMeetingTimeByTeamAndPlace(String team, String place, Date start, Date end);
}
