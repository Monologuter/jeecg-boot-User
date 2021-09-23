package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.*;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * @author SunBowen
 */
@Mapper
public interface CommentMapper {
    /**
     * 删除流程的评论
     * */
    int deleteByPrimaryKey(String id);

    /**
     * 新增流程的评论
     * */
    int insertComment(Comment record);

    /**
     * 获取此流程实例下所有的父级评论Id
     * @param procInsId
     * @return java.util.List<java.lang.Long>
     */
    List<String> getCommentIdByprocInsId(String procInsId);


    /**
     * 获取腹肌评论Id
     * */
    List<ReplyVO> selectByPid(Integer pid);

    /**
     * 获取评论详情
     * @param id
     * @return
     */
    CommentMainVO selectCommentById(String id);
}
