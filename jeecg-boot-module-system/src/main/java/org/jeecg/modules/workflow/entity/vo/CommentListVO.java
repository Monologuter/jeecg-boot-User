package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.camunda.bpm.engine.task.Comment;

import java.io.Serializable;
import java.util.List;

/**
 * @author qjc
 * 用于节点图展示
 */
@Data
public class CommentListVO implements Serializable,Comparable {

    /**办理人*/
    @ApiModelProperty("办理人")
    private String assignee;

    /**节点名称*/
    @ApiModelProperty("节点名称")
    private String taskName;

    /**开始时间*/
    @ApiModelProperty("开始时间")
    private String startTime;

    /**审批状态*/
    @ApiModelProperty("审批状态")
    private String state;

    /**角色*/
    @ApiModelProperty("角色")
    private String role;

    /**审批意见/备注信息*/
    @ApiModelProperty("审批意见/备注信息")
    private List<Comment> messages;

    /**头像url列表*/
    @ApiModelProperty("头像url列表")
    private List<String> avatar;

    @Override
    public int compareTo(Object o) {
        if(o instanceof CommentListVO){
            CommentListVO commentListVO = (CommentListVO) o;
            String time = commentListVO.getStartTime();
            String compareTime = this.getStartTime();
            return compareTime.compareTo(time);
        }
        throw new ClassCastException("不能转换为CommentListVO类型的对象");
    }
}