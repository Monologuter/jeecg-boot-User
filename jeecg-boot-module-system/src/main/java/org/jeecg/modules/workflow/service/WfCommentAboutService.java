package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.Comment;
import org.jeecg.modules.workflow.entity.vo.CreatCommentForm;

/**
 * 评论业务层
 * @author: SunBoWen
 * @date: 2021/7/26 15:37
 */
public interface WfCommentAboutService {

    /**
     * @param comment
     * @return boolean
     */
    boolean insertComment(Comment comment);

    /***
     *
     * @param commentId
     * @return boolean
     */
    Result<Object> deleteComment(String commentId);

    /**
     * 评论
     *
     * @return
     */
    Result<Object> creatComment(CreatCommentForm form);

    /**
     * 获取文章下所有评论
     * @param procInsId
     * @return
     */
    Result getAllCommentByprocInsId(String procInsId);

}
