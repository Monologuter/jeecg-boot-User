package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
public class Comment implements Serializable {

    /**讨论ID*/
    @ApiModelProperty(value = "讨论ID")
    private Integer commentId;

    /**评论者ID*/
    @ApiModelProperty(value = "评论者ID")
    private String userId;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String procInsId;

    /**被回复人ID*/
    @ApiModelProperty(value = "被回复人ID")
    private String replyUserId;

    /**父级ID*/
    @ApiModelProperty(value = "父级ID")
    private Integer pid;

    /**评论消息*/
    @ApiModelProperty(value = "评论消息")
    private String commentMsg;

    /**评论创建时间*/
    @ApiModelProperty(value = "评论创建时间")
    private String creatTime;

    /**头像*/
    @ApiModelProperty(value = "头像")
    private String avatar;

    private static final long serialVersionUID = 1L;
    }