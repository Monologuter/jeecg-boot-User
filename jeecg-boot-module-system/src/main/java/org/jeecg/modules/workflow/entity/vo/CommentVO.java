package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.camunda.bpm.engine.task.Comment;

import java.io.Serializable;
import java.util.List;

/**
 * @Description: 评论实体类
 * @author: liuchun，qjc（新加角色、和状态）
 * @date: 2021年03月30日 9:35
 */
@Data
public class CommentVO implements Comparable, Serializable {

    /**任务名*/
    @ApiModelProperty("任务名")
    private String taskName;

    /**任务开始时间*/
    @ApiModelProperty("任务开始时间")
    private String startTime;

    /**任务结束时间*/
    @ApiModelProperty("任务结束时间")
    private String endTime;

    /**执行人姓名*/
    @ApiModelProperty("执行人姓名")
    private String assigneeName;

    /**执行人ID*/
    @ApiModelProperty("执行人ID")
    private String assignessId;

    /**评论*/
    @ApiModelProperty("评论")
    private List<Comment> comments;

    /**角色（申请者、审批者）*/
    @ApiModelProperty("角色（申请者、审批者）")
    private String role;

    /**状态（发起审批、已通过、已拒绝）*/
    @ApiModelProperty("状态（发起审批、已通过、已拒绝）")
    private String state;

    @Override
    public int compareTo(Object o) {
        if(o instanceof CommentVO){
            CommentVO commentVO = (CommentVO) o;
            String time = commentVO.getStartTime();
            String compareTime = this.startTime;
            return compareTime.compareTo(time);
        }
        throw new ClassCastException("不能转换为CommentVO类型的对象");
    }
}
