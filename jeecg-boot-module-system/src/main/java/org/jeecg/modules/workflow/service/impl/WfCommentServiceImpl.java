package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.task.Comment;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.ExtensionElements;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperties;
import org.camunda.bpm.model.bpmn.instance.camunda.CamundaProperty;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.mapper.WorkFlowMyHistoricalTaskMapper;
import org.jeecg.modules.workflow.service.WfCommentAboutService;
import org.jeecg.modules.workflow.service.WfCommentService;
import org.jeecg.modules.workflow.service.WfUserService;
import org.jeecg.modules.workflow.strategy.Context;
import org.jeecg.modules.workflow.strategy.MappingStrategy;
import org.jeecg.modules.workflow.strategy.impl.CommentVOStrategy;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Description: 评论相关业务层
 * @author: liuchun，qjc（新增角色、和状态属性）
 * @date: 2021年03月30日 9:30
 */
@Service
@Slf4j
public class WfCommentServiceImpl implements WfCommentService {

    @Autowired
    private WfUserService wfUserService;

    @Autowired
    private WorkFlowMyHistoricalTaskMapper wfMyHistoricalTaskMapper;
    @Autowired
    private WfCommentAboutService wfCommentAboutService;
    /**
     * 展示评论 ： 根据流程实例id
     * @param processInstId : 流程实例id
     */
    @Override
    public TaskCommentVO showComment(String processInstId) {


        //查询流程历史表中流程实例的每个任务节点
        List<ActHiTaskinstVO> list = wfMyHistoricalTaskMapper.queryHistoricTaskByProceInst(processInstId);

        //将任务节点数据存储到实体类集合中
        //加载映射策略:List<ActHiTaskinstVO> 转 List<CommentVO>
        Context context = new Context(new CommentVOStrategy());
        List<CommentVO> commentVOS = (List<CommentVO>)context.executeMapping(list);

        //评论随时间先后展示
        Collections.sort(commentVOS);

        //将并行节点合并
        HashMap<String, CommentListVO> taskMap = new HashMap<>();
        for (CommentVO commentVO: commentVOS){
            List<String> users = new ArrayList<>();
            if (taskMap.get(commentVO.getStartTime())==null){
                CommentListVO commentListVO = new CommentListVO();
                commentListVO.setAssignee(commentVO.getAssigneeName());
                commentListVO.setTaskName(commentVO.getTaskName());
                commentListVO.setStartTime(commentVO.getStartTime());
                commentListVO.setRole(commentVO.getRole());
                commentListVO.setState(commentVO.getState());
                //添加-审批意见/备注信息
                commentListVO.setMessages(commentVO.getComments());
                //增加头像信息
                users.add(commentVO.getAssignessId());
                List<String> avatars = wfUserService.queryAvatar(users);
                //解决头像显示的bug
                if (avatars.size() !=0){
                    commentListVO.setAvatar(avatars);
                }else {
                    commentListVO.setAvatar(new ArrayList<>());
                }
                taskMap.put(commentVO.getStartTime(),commentListVO);
            }

            else {//如果并行节点已经存在
                CommentListVO commentListVO = new CommentListVO();
                commentListVO.setAssignee(taskMap.get(commentVO.getStartTime()).getAssignee()+"、"+commentVO.getAssigneeName());
                commentListVO.setTaskName(taskMap.get(commentVO.getStartTime()).getTaskName()+"、"+commentVO.getTaskName());
                commentListVO.setStartTime(commentVO.getStartTime());
                commentListVO.setRole(commentVO.getRole());
                commentListVO.setState(taskMap.get(commentVO.getStartTime()).getState().equals("已完成")?"已完成":commentVO.getState());
                commentListVO.setMessages(commentVO.getComments());
                //增加头像信息
                users.add(commentVO.getAssignessId());
                List<String> avatars = wfUserService.queryAvatar(users);
                //解决头像显示的bug
                if (avatars.size() !=0){
                    commentListVO.setAvatar(avatars);
                }else {
                    commentListVO.setAvatar(new ArrayList<>());
                }
                taskMap.put(commentVO.getStartTime(),commentListVO);
            }
        }
        ArrayList<CommentListVO> commentListVOs = new ArrayList<>();
        //添加备注节点
        List<CommentMainVO> result = (List<CommentMainVO>) wfCommentAboutService.getAllCommentByprocInsId(processInstId).getResult();
        for (CommentMainVO cmv:result){
            CommentListVO commentListVO = new CommentListVO();
            commentListVO.setTaskName(cmv.getCommentMsg());
            commentListVO.setAssignee(cmv.getUserName());
            commentListVO.setRole("备注");
            commentListVO.setStartTime(cmv.getCreatTime());
            commentListVOs.add(commentListVO);
        }

        for (CommentListVO mapEntry:taskMap.values()){
            commentListVOs.add(mapEntry);
        }
        //评论随时间先后展示
        Collections.sort(commentListVOs);

        TaskCommentVO taskCommentVO = new TaskCommentVO();
        taskCommentVO.setCommentListVO(commentListVOs);
        taskCommentVO.setCommentVO(commentVOS);
        return taskCommentVO;
    }
}