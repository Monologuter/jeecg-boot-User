package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Collection;

/**
 * @author qjc
 * 用于节点信息展示
 */
@Data
public class TaskCommentVO {

    /**用于节点信息展示*/
    @ApiModelProperty("用于节点信息展示")
    private Collection<CommentListVO> commentListVO;

    /**用于节点评论展示*/
    @ApiModelProperty("用于节点评论展示")
    private Collection<CommentVO> commentVO;
}
