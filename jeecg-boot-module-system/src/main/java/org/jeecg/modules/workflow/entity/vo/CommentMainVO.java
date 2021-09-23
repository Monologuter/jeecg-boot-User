package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.util.List;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年07月26日 15:21
 */

@Data
public class CommentMainVO {

    /**评论ID*/
    @ApiModelProperty(value = "评论ID")
    private Integer commentId;

    /**评论人ID*/
    @ApiModelProperty(value = "评论人ID")
    private String userId;

    /**评论人名称*/
    @ApiModelProperty(value = "评论人名称")
    private String userName;

    /**头像*/
    @ApiModelProperty(value = "头像")
    private String avatar;

    /**评论创建时间*/
    @ApiModelProperty(value = "评论创建时间")
    private String creatTime;

    /**评论信息*/
    @ApiModelProperty(value = "评论信息")
    private String commentMsg;

    /**子评论集*/
    @ApiModelProperty(value = "子评论集")
    private List<ReplyVO> replyVO;

}
