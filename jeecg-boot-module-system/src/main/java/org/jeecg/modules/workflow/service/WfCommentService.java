package org.jeecg.modules.workflow.service;

import org.jeecg.modules.workflow.entity.vo.TaskCommentVO;


/**
* 评论业务层
*@author : liuchun
* @date : 2021/3/30 9:29
*/
public interface WfCommentService {

    /**
     * 展示评论 ： 根据流程实例id
     * @param processInstId : 流程实例id
     */
    TaskCommentVO showComment(String processInstId);

}
