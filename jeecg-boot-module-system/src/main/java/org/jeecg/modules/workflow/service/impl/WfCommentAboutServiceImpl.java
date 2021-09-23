package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.*;

import org.jeecg.modules.workflow.mapper.CommentMapper;
import org.jeecg.modules.workflow.service.WfCommentAboutService;
import org.jeecg.modules.workflow.service.WfUserService;
import org.jeecg.modules.workflow.utils.TimeUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.ArrayList;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年07月26日 13:41
 */
@Service
@Slf4j
public class WfCommentAboutServiceImpl implements WfCommentAboutService {


    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private WfUserService wfUserService;

    @Override
    public boolean insertComment(Comment comment) {
        return commentMapper.insertComment(comment) == 1;
    }

    /**
     * 删除流程的评论
     * */
    @Override
    public Result<Object> deleteComment(String commentId) {
        if (commentMapper.deleteByPrimaryKey(commentId) != 1){
            return Result.Error("删除评论失败！");
        }
        commentMapper.deleteByPrimaryKey(commentId);
        return Result.OK("删除评论成功！");
    }

    /**
     * 创建流程的评论
     * */
    @Override
    public Result<Object> creatComment(CreatCommentForm form) {
        Comment comment = new Comment();
        BeanUtils.copyProperties(form, comment);
        comment.setUserId(form.getUserId());
        comment.setCreatTime(TimeUtil.getNowTime());
        comment.setPid(form.getPid());
        comment.setCommentMsg(form.getCommentMsg());
        insertComment(comment);
        return Result.OK("评论成功",comment);
    }

    /**
     * 获取流程的评论
     * */
    @Override
    public Result getAllCommentByprocInsId(String procInsId) {
        List<CommentMainVO> result = new ArrayList<>();
        // 查找流程下所有的父级评论
        List<String> commentIdList = commentMapper.getCommentIdByprocInsId(procInsId);
        if (commentIdList.isEmpty() == true){
            return Result.OK("该流程下暂无评论!",result);
        }
        for (String commentId : commentIdList) {
            CommentMainVO vo = commentMapper.selectCommentById(commentId);
            //查找所有父级评论下的子评论
            List<ReplyVO> vos = commentMapper.selectByPid(vo.getCommentId());
            List<ReplyVO> reply = new ArrayList<>();
            for (ReplyVO replyVO : vos) {
                UserDTO user = wfUserService.selectByUserId(replyVO.getReplyUserId());
                replyVO.setReplyUserName(user.getFirstName());
                reply.add(replyVO);
            }
            vo.setReplyVO(reply);
            result.add(vo);
        }
        return Result.OK("该流程查询评论成功!",result);
    }
}
