package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description: 节点办理人信息
 * @author: ghz
 * @date: 2021年04月12日 11:55
 */
@Data
public class ActAssMsgVO {

    /**节点ID  */
    @ApiModelProperty(value = "节点ID ")
    private String actId;

    /**任务名称  */
    @ApiModelProperty(value = "任务名称 ")
    private String taskName;

    /**委托人  */
    @ApiModelProperty(value = "委托人")
    private String assignee;

    /**开始时间  */
    @ApiModelProperty(value = "开始时间 ")
    private String startTime;

    /**结束时间  */
    @ApiModelProperty(value = "结束时间 ")
    private String endTime;

    /**耗时  */
    @ApiModelProperty(value = "耗时 ")
    private String duration;

    /**处理意见 */
    @ApiModelProperty(value = "处理意见")
    private String opinion;

    /**
     * 按照时间排序
     * @param o
     * @return
     */
    public int compareTo(Object o) {
        if(o instanceof ActAssMsgVO){
            ActAssMsgVO actAssMsgVO = (ActAssMsgVO) o;
            String time = null;
            if(actAssMsgVO.getEndTime() != null){
                time = actAssMsgVO.getEndTime();
            }else{
                time = actAssMsgVO.getStartTime();
            }
            String compareTime = "";
            if(this.endTime != null){
                compareTime = endTime;
            }else{
                compareTime = startTime;
            }
            return compareTime.compareTo(time);
        }
        throw new ClassCastException("不能转换为ActAssMsgVO类型的对象");
    }
}
