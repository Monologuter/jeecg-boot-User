package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * @author SunBowen
 */
@Data
public class CreatCommentForm {

    /**流程实例Id*/
    @ApiModelProperty("流程实例Id")
    @NotNull(message = "流程实例Id不能为空")
    private String procInsId;

    /**评论内容 */
    @ApiModelProperty("评论内容")
    @NotNull(message = "评论内容不能为空")
    private String commentMsg;

    /**被回复人*/
    @ApiModelProperty("被回复人")
    private String replyUserId;

    /**父级评论Id 若为首级评论则为0*/
    @ApiModelProperty("父级评论Id 若为首级评论则为0")
    @NotNull(message = "父级评论Id不能为空")
    private Integer pid;

    /**用户id*/
    @ApiModelProperty("用户id")
    @NotNull(message = "用户id")
    private String userId;
}

