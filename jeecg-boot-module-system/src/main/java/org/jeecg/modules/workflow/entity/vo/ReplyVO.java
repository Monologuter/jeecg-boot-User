package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author hobo
 * @description：评论相关
 */
@Data
public class ReplyVO {

    /**评论ID*/
    @ApiModelProperty("评论ID")
    private Integer commentId;

    /**评论人ID*/
    @ApiModelProperty("评论人ID")
    private String userId;

    /**评论人用户名*/
    @ApiModelProperty("评论人用户名")
    private String userName;

    /**评论人头像*/
    @ApiModelProperty("评论人头像")
    private String avatar;

    /**回复人ID*/
    @ApiModelProperty("回复人ID")
    private String replyUserId;

    /**回复人用户名*/
    @ApiModelProperty("回复人用户名")
    private String replyUserName;

    /**创建时间*/
    @ApiModelProperty("创建时间")
    private String creatTime;

    /**评论内容*/
    @ApiModelProperty("评论内容")
    private String commentMsg;

}
