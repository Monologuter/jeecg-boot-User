package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author 权计超
 * 客户页面：流程定义分组页面
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcGroupVO implements Serializable {

    /**流程定义分组名称*/
    @ApiModelProperty(value = "流程定义分组名称")
    private String procGroupName;

    /**数量*/
    @ApiModelProperty(value = "数量")
    private Integer number;

    /**具体信息*/
    @ApiModelProperty(value = "具体信息")
    private List<ProcessDevelopmentVO> projects;
}
