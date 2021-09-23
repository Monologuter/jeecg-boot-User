package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.entity.FormInterviewScheduleDO;

import java.util.List;

/**
 * 面试日程Service层接口类
 *
 * @author: HuQi
 * @date: 2021年08月31日 10:29
 */
public interface FormInterviewScheduleService extends IService<FormInterviewScheduleDO> {

    /**
     * 保存面试日程记录，包括记录的新增和修改，修改时需要传id、新增时无需传id
     *
     * @param formInterviewScheduleDO 面试日程记录实体类
     * @return org.jeecg.modules.form.entity.FormInterviewScheduleDO 返回最终保存的数据，包含生成的id
     * @Author HuQi
     * @create 2021-08-31 11:18
     */
    FormInterviewScheduleDO saveFormInterviewScheduleDO(FormInterviewScheduleDO formInterviewScheduleDO);

    /**
     * 根据面试日程Id删除面试日程记录
     *
     * @param id 面试日程ID
     * @return int 返回删除成功标识
     * @author: LiKun
     * @Return: int
    */
    int deleteFormInterviewSchedule(String id);

    /**
     * 方法描述:当前用户部门为综合岗时,查询所有的面试信息Service
     * 
     * @return : java.util.List<org.jeecg.modules.form.entity.FormInterviewScheduleDO>
     * @author : lxk
     * @create 2021/9/1 9:36
    */
    List<FormInterviewScheduleDO> getAllFormInterviewSchedule();

    /**
     * 方法描述:当前用户不是综合岗,则查询当前用户（面试官）需要进行面试日程Service
     *
     * @param userId 用户（面试官）ID
     * @return : java.util.List<org.jeecg.modules.form.entity.FormInterviewScheduleDO>
     * @author : lxk
     * @create :2021/9/1 9:41
    */
    List<FormInterviewScheduleDO> getByInterviewerFormInterviewSchedule(String userId);
}
