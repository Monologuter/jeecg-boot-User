package org.jeecg.modules.workflow.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: liu chun
 * @author: liuchun
 * @date: 2021年06月21日 9:18
 */
@Data
public class FormRole {

    /**有值的话说明外置表单，空的话动态表单*/
    @ApiModelProperty(value = "有值的话说明外置表单，空的话动态表单")
    private String formKey;

    /**用户任务角色*/
    @ApiModelProperty(value = "用户任务角色")
    private String role;
}
