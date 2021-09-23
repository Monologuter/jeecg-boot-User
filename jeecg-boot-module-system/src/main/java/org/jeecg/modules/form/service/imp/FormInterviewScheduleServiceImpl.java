package org.jeecg.modules.form.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormInterviewScheduleConstant;
import org.jeecg.modules.form.entity.FormInterviewScheduleDO;
import org.jeecg.modules.form.mapper.FormInterviewScheduleMapper;
import org.jeecg.modules.form.service.FormInterviewScheduleService;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 面试日程ServiceImpl层接口实现类
 *
 * @Author: HuQi
 * @Date: 2021年08月31日 10:29
 */
@Service
@Slf4j
public class FormInterviewScheduleServiceImpl extends ServiceImpl<FormInterviewScheduleMapper, FormInterviewScheduleDO> implements FormInterviewScheduleService {

    @Autowired
    private FormInterviewScheduleMapper formInterviewScheduleMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    /**
     * SimpleDateFormat 时间格式化
     */
    private SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 保存面试日程记录，包括记录的新增和修改，修改时需要传id、新增时无需传id
     *
     * @param formInterviewScheduleDO 面试日程记录实体类
     * @return org.jeecg.modules.form.entity.FormInterviewScheduleDO 返回最终保存的数据，包含生成的id
     * @Author HuQi
     * @create 2021-08-31 11:18
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public FormInterviewScheduleDO saveFormInterviewScheduleDO(FormInterviewScheduleDO formInterviewScheduleDO) {
        SysUser sysUser = sysUserMapper.selectById(formInterviewScheduleDO.getInterviewer());
        if (sysUser == null){
            ServiceUtils.throwException(FormInterviewScheduleConstant.MESSAGE_ERROR_INTERVIEWER_NOT_FOUNT);
        }else {
            formInterviewScheduleDO.setTitle(sysUser.getRealname() + " 面试 " + formInterviewScheduleDO.getInterviewee());
        }
        ServiceUtils.throwIfFailed(() -> StringUtils.isNotBlank(formInterviewScheduleDO.getInterviewer())
                        && StringUtils.isNotBlank(formInterviewScheduleDO.getType())
                        && StringUtils.isNotBlank(formInterviewScheduleDO.getInterviewee()),
                FormInterviewScheduleConstant.MESSAGE_INFO_SAVE_LACK_FILED);
        Date end = getStringToDate(formInterviewScheduleDO.getEnd());
        Date start = getStringToDate(formInterviewScheduleDO.getStart());
        if (start.getTime() - end.getTime() > 0){
            ServiceUtils.throwException(FormInterviewScheduleConstant.MESSAGE_ERROR_START_RATHER_END);
        }
        if (StringUtils.isNotBlank(formInterviewScheduleDO.getId())){
            ServiceUtils.throwIfFailed(() -> this.updateById(formInterviewScheduleDO),
                    FormInterviewScheduleConstant.MESSAGE_ERROR_UPDATE);
        }else {
            ServiceUtils.throwIfFailed(() -> this.save(formInterviewScheduleDO),
                    FormInterviewScheduleConstant.MESSAGE_ERROR_INSERT);
        }
        return formInterviewScheduleDO;
    }

    /**
     * 根据面试日程Id删除面试日程记录
     *
     * @param id 面试日程ID
     * @return int 返回删除成功标识
     * @author: LiKun
     * @Return: int
    */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteFormInterviewSchedule(String id) {
        return formInterviewScheduleMapper.deleteById(id);
    }

    /**
     * 方法描述:当前用户部门为综合岗时,查询所有的面试信息
     *
     * @return : java.util.List<org.jeecg.modules.form.entity.FormInterviewScheduleDO>
     * @author : lxk
     * @create :2021/8/31 17:13
    */
    @Override
    public List<FormInterviewScheduleDO> getAllFormInterviewSchedule() {
        return formInterviewScheduleMapper.selectList(null);
    }

    /**
     * 方法描述:当前用户不是综合岗,则查询当前用户（面试官）需要进行面试日程impl
     *
     * @param userId 用户（面试官）ID
     * @return : java.util.List<org.jeecg.modules.form.entity.FormInterviewScheduleDO>
     * @author : lxk
     * @create :2021/8/31 17:13
    */
    @Override
    public List<FormInterviewScheduleDO> getByInterviewerFormInterviewSchedule(String userId) {
        LambdaQueryWrapper<FormInterviewScheduleDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FormInterviewScheduleDO::getInterviewer, userId)
                .orderByAsc(FormInterviewScheduleDO::getStart);
        return formInterviewScheduleMapper.selectList(queryWrapper);
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
            ServiceUtils.throwException(FormInterviewScheduleConstant.MESSAGE_ERROR_TIME_FIELD);
        }
        Date date = new Date();
        try {
            date = sf.parse(time);
        } catch (Exception e) {
            ServiceUtils.throwException(FormInterviewScheduleConstant.MESSAGE_ERROR_TIME_FIELD);
        }
        return date;
    }

}
