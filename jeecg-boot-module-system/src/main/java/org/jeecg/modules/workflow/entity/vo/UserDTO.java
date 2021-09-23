package org.jeecg.modules.workflow.entity.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class UserDTO extends Comment {

    /**用户id*/
    @TableId
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**姓*/
    @ApiModelProperty(value = "姓")
    private String firstName;

    /**名*/
    @ApiModelProperty(value = "名")
    private String lastName;

    /**邮箱*/
    @ApiModelProperty(value = "邮箱")
    private String email;

    /**头像*/
    @ApiModelProperty(value = "头像")
    private String avatar;

}
