package org.jeecg.modules.form.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormMeetingScheduleConstant;

import org.jeecg.modules.form.dto.FormMeetingStartEndDTO;
import org.jeecg.modules.form.vo.GetFormMeetingScheduleVO;
import org.jeecg.modules.form.vo.SaveFormMeetingScheduleVO;
import org.jeecg.modules.form.mapper.FormMeetingScheduleMapper;
import org.jeecg.modules.form.service.FormMeetingScheduleService;
import org.jeecg.modules.form.entity.FormMeetingScheduleDO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Description: 表单会议日程
 * @author: LiKun
 * @date: 2021年09月10日 17:11
 */
@Service
@Slf4j
public class FormMeetingScheduleServiceImpl extends ServiceImpl<FormMeetingScheduleMapper, FormMeetingScheduleDO> implements FormMeetingScheduleService {

    @Autowired
    private FormMeetingScheduleMapper formMeetingScheduleMapper;

    /**
     * SimpleDateFormat 时间格式化
     */
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 保存会议日程记录
     * @author: LiKun
     * @param saveFormMeetingScheduleVO 会议日程记录实体类
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormMeetingScheduleDO saveFormMeetingScheduleDO(SaveFormMeetingScheduleVO saveFormMeetingScheduleVO) {
        String team = saveFormMeetingScheduleVO.getTeam();
        String place = saveFormMeetingScheduleVO.getPlace();
        String status = saveFormMeetingScheduleVO.getStatus();
        ServiceUtils.throwIfFailed(() -> StringUtils.isNotBlank(saveFormMeetingScheduleVO.getTitle())
                        && StringUtils.isNotBlank(saveFormMeetingScheduleVO.getStatus())
                        && StringUtils.isNotBlank(saveFormMeetingScheduleVO.getStart())
                        && StringUtils.isNotBlank(saveFormMeetingScheduleVO.getEnd())
                        && StringUtils.isNotBlank(saveFormMeetingScheduleVO.getBooker())
//                目前只有线下会议室一种会议方式，即不能为空
                        && StringUtils.isNotBlank(saveFormMeetingScheduleVO.getPlace())
                        && StringUtils.isNotBlank(team),
                FormMeetingScheduleConstant.MESSAGE_INFO_SAVE_LACK_FILED);
        FormMeetingScheduleDO formMeetingScheduleDo = new FormMeetingScheduleDO();
        if(StringUtils.isNotBlank(saveFormMeetingScheduleVO.getPlace())){
            formMeetingScheduleDo.setResourceId(saveFormMeetingScheduleVO.getPlace());
        }
        Date end = getStringToDate(saveFormMeetingScheduleVO.getEnd());
        Date start = getStringToDate(saveFormMeetingScheduleVO.getStart());
        if (start.getTime() - end.getTime() > 0){
            ServiceUtils.throwException(FormMeetingScheduleConstant.MESSAGE_ERROR_START_RATHER_END);
        }
        if (start.getTime() - end.getTime() == 0){
            ServiceUtils.throwException(FormMeetingScheduleConstant.MESSAGE_ERROR_START_EQUALS_END);
        }
//        如果在会议室开会，根据”参会项目组“，”会议场地“，”预定的会议开始结束时间“来查找对应时间段是否为空窗期
//        这里再次判断是否为空，是为了后面多种会议方式（video、scene、phone）做准备
        if(StringUtils.isNotBlank(place)){
            FormMeetingStartEndDTO isMeeting = formMeetingScheduleMapper.judgeMeetingTimeByTeamAndPlace(team,place,start,end);
            if(isMeeting!=null){
                ServiceUtils.throwException(FormMeetingScheduleConstant.meetingTimeConflict(team,place,isMeeting));
            }
//            如果是电话或视频会议，则查询条件不包含”会议场地“
        }else {
            FormMeetingStartEndDTO isMeeting = formMeetingScheduleMapper.judgeMeetingTimeByTeamAndPlace(team,"",start,end);
            if(isMeeting!=null){
                ServiceUtils.throwException(FormMeetingScheduleConstant.meetingTimeConflict(team,null,isMeeting));
            }
        }
        BeanUtils.copyProperties(saveFormMeetingScheduleVO, formMeetingScheduleDo);
//        根据会议状态赋予颜色
        if((FormMeetingScheduleConstant.MEETING_STATUS_UNDER_APPROVAL).equals(status)){
            formMeetingScheduleDo.setClassNames(FormMeetingScheduleConstant.MEETING_STATUS_UNDER_APPROVAL_COLOR);
        }else if((FormMeetingScheduleConstant.MEETING_STATUS_BOOKED).equals(status)){
            formMeetingScheduleDo.setClassNames(FormMeetingScheduleConstant.MEETING_STATUS_BOOKED_COLOR);
        }
        if (StringUtils.isNotBlank(saveFormMeetingScheduleVO.getId())){
            ServiceUtils.throwIfFailed(() -> this.updateById(formMeetingScheduleDo),
                    FormMeetingScheduleConstant.MESSAGE_ERROR_UPDATE);
        }else {
            ServiceUtils.throwIfFailed(() -> this.save(formMeetingScheduleDo),
                    FormMeetingScheduleConstant.MESSAGE_ERROR_INSERT);
        }
        return formMeetingScheduleDo;
    }

    /**
     * 删除会议会议记录
     * @author: LiKun
     * @param id 会议id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFormMeetingSchedule(String id) {
        return formMeetingScheduleMapper.deleteById(id);
    }

    /**
     * 查询所有会议日程
     * @author: LiKun
     */
    @Override
    public List<GetFormMeetingScheduleVO> getAllFormMeetingSchedule(){
       List<FormMeetingScheduleDO> list = formMeetingScheduleMapper.selectList(null);
       List<GetFormMeetingScheduleVO> resList = new ArrayList<>();
        for (FormMeetingScheduleDO formMeetingScheduleDO : list) {
            GetFormMeetingScheduleVO getFormMeetingScheduleVO = new GetFormMeetingScheduleVO();
            if(formMeetingScheduleDO!=null){
                BeanUtils.copyProperties(formMeetingScheduleDO, getFormMeetingScheduleVO);
                getFormMeetingScheduleVO.setOptionalTitle(formMeetingScheduleDO.getTitle());
                resList.add(getFormMeetingScheduleVO);
            }
        }
        return resList;
    }

    /**
     * 根据预定者或者会议主题查询会议信息
     * @author: LiKun
     * @param condition 预定者或者会议主题
     */
    @Override
    public List<GetFormMeetingScheduleVO> getFormMeetingSchedule(String condition) {
//        LambdaQueryWrapper<GetFormMeetingScheduleVO> queryWrapper = Wrappers.lambdaQuery();
//        queryWrapper.eq(GetFormMeetingScheduleVO::getBooker, condition)
//                .or()
//                .eq(GetFormMeetingScheduleVO::getTitle,condition)
//                .orderByAsc(GetFormMeetingScheduleVO::getStart);
//        return formMeetingScheduleMapper.selectList(queryWrapper);
        return null;
    }

    /**
     * 将时间字符串转成Date时间格式
     *
     * @param time 时间字符串
     * @return java.util.Date
     * @Author HuQi
     * @create 2021-08-31 14:41
     */
    private Date getStringToDate(String time) {
        if (StringUtils.isEmpty(time)){
            ServiceUtils.throwException(FormMeetingScheduleConstant.MESSAGE_ERROR_TIME_FIELD);
        }
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (Exception e) {
            ServiceUtils.throwException(FormMeetingScheduleConstant.MESSAGE_ERROR_TIME_FIELD);
        }
        return date;
    }
}
