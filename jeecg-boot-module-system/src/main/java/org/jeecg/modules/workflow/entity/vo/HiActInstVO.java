package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


/**
 * @Description: 历史流程跟踪表
 * @author: ghz
 * @date: 2021年03月29日 9:54
 */
@Data
public class HiActInstVO implements Comparable<Object> {

    /**业务标题*/
    @ApiModelProperty(value = "业务标题")
    private String businessTitle;

    /**流程实例ID*/
    @ApiModelProperty(value = "流程实例ID")
    private String processInstanceId;

    /**开始时间*/
    @ApiModelProperty(value = "开始时间")
    private String startTime;

    /**结束时间*/
    @ApiModelProperty(value = "结束时间")
    private String endTime;

    /**负责人*/
    @ApiModelProperty(value = "负责人")
    private String assignee;

    /**处理结果*/
    @ApiModelProperty(value = "处理结果")
    private String results;

    /**处理意见*/
    @ApiModelProperty(value = "处理意见")
    private String opinion;

    /**按照时间排序*/
    @Override
    public int compareTo(Object o) {
        if(o instanceof HiActInstVO){
            HiActInstVO hiActInstVO = (HiActInstVO) o;
            String time = "";
            if(hiActInstVO.getEndTime() != null){
                time = hiActInstVO.getEndTime();
            }else{
                time = hiActInstVO.getStartTime();
            }
            String compareTime = "";
            if(this.endTime != null){
                compareTime = endTime;
            }else{
                compareTime = startTime;
            }
            return compareTime.compareTo(time);
        }
        throw new ClassCastException("不能转换为HiActInstVO类型的对象");
    }

}

