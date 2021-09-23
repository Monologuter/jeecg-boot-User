package org.jeecg.modules.workflow.strategy.impl;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.Comment;
import org.jeecg.modules.workflow.entity.vo.ActHiTaskinstVO;
import org.jeecg.modules.workflow.entity.vo.CommentVO;
import org.jeecg.modules.workflow.strategy.MappingStrategy;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName CommentVOStrategy
 * @CreateTime 2021-08-27 17:38
 * @Version 1.0
 * @Description: List<ActHiTaskinstVO> 转 List<CommentVO>
 */
public class CommentVOStrategy implements MappingStrategy {

    @Override
    public Object executeMapping(Object original) {
        List<ActHiTaskinstVO> list = (List<ActHiTaskinstVO>) original;
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<CommentVO> commentVOS = new ArrayList<>();

        for(ActHiTaskinstVO taskInstance : list){
            String taskId = taskInstance.getTaskId();
            CommentVO commentVO = new CommentVO();
            //自定义实体类设置任务名称
            commentVO.setTaskName(taskInstance.getName());
            //自定义实体类设置开始时间
            commentVO.setStartTime(sf.format(taskInstance.getStartTime()));
            //自定义实体类设置结束时间
            if(taskInstance.getEndTime() != null){
                commentVO.setEndTime(sf.format(taskInstance.getEndTime()));
            }
            //自定义实体类设置办理人信息
            if(taskInstance.getAssignee() != null ){
                User user = identityService.createUserQuery()
                        .userId(taskInstance.getAssignee())
                        .singleResult();
                if(user != null){
                    commentVO.setAssignessId(user.getId());
                    commentVO.setAssigneeName(user.getLastName());
                }
            }
            //获取扩展属性中role对应的value
            String role = UtilTools.getExtValByKey(taskInstance.getProcDefId(), taskInstance.getTaskDefKey(), "role");
            if(role != null){
                commentVO.setRole(role);
            }
            //自定义实体类设置状态
            if (taskInstance.getDeleteReason()==null){
                commentVO.setState("处理中");
            }
            else {
                switch (taskInstance.getDeleteReason()){
                    case "deleted":
                        commentVO.setState("已拒绝");
                        break;
                    case "completed":
                        commentVO.setState("已完成");
                        break;
                    case "主动作废":
                        commentVO.setState("主动作废");
                        break;
                    default:
                        break;
                }
            }
            if(taskInstance.getAssignee()==null){
                commentVO.setState("待认领");
            }
            //自定义实体类设置评论
            List<Comment> taskComments = taskService.getTaskComments(taskId);
            commentVO.setComments(taskComments);
            commentVOS.add(commentVO);
        }
        return commentVOS;
    }
}
