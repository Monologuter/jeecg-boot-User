package org.jeecg.modules.workflow.entity.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年06月02日 9:40
 */
@Data
@TableName("act_proc_classification")
@NoArgsConstructor
@AllArgsConstructor
public class ProcClassVO {

    /**流程定义分组*/
    @ApiModelProperty(value = "流程定义分组")
    @TableId(value = "PROC_CLASS_",type = IdType.INPUT)
    private String procClass;
}
