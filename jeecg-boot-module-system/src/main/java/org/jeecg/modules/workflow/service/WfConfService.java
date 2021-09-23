package org.jeecg.modules.workflow.service;


import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ConfInfoVO;
import org.jeecg.modules.workflow.entity.vo.FormFieldVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;

import java.util.List;

/**
* 任务业务层
* @author: liuchun
* @date: 2021/3/21 15:37
*/
public interface WfConfService {
    /**
     * 获取任务节点信息 : 根据Bpmn流程xml文件
     * @param procdefId : 流程定义id
     * @return ： Result<RecordsListPageVO>
     */
    Result<RecordsListPageVO> showActivityByBpmn(Integer pageNo, Integer pageSize, String procdefId);

    /**
     * 展示动态表单 ： 根据活动任务节点
     * @param procdefId : 流程定义id
     * @param activityId : 相关节点id
     * @return ：List<FormFieldVO>
     */
    List<FormFieldVO> showFormByBpmn(String procdefId, String activityId);

    /**获取扩展属性信息：根据流程定义id
     * @param procdefId : 流程定义id
     * @return : ConfInfoVO
     */
    Result<ConfInfoVO> getExtension(String procdefId);
}
