package org.jeecg.modules.form.service;

import org.jeecg.modules.form.vo.GetFormMeetingScheduleVO;
import org.jeecg.modules.form.vo.SaveFormMeetingScheduleVO;
import org.jeecg.modules.form.entity.FormMeetingScheduleDO;

import java.util.List;

public interface FormMeetingScheduleService {

    /**
     * 保存会议日程记录
     * @author: LiKun
     * @param saveFormMeetingScheduleVO 会议日程记录实体类
     */
    FormMeetingScheduleDO saveFormMeetingScheduleDO(SaveFormMeetingScheduleVO saveFormMeetingScheduleVO);

    /**
     * 删除会议会议记录
     * @author: LiKun
     * @param id 会议id
     */
    int deleteFormMeetingSchedule(String id);

    /**
     * 查询所有会议日程
     * @author: LiKun
     */
    List<GetFormMeetingScheduleVO> getAllFormMeetingSchedule();

    /**
     * 根据预定者或者会议主题查询会议信息
     * @author: LiKun
     * @param condition 预定者或者会议主题
     */
    List<GetFormMeetingScheduleVO> getFormMeetingSchedule(String condition);
}
