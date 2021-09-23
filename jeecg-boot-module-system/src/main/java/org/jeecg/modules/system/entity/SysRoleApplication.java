package org.jeecg.modules.system.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 角色应用表
 * </p>
 *
 * @Author Zhouhonghan
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRoleApplication implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 应用id
     */
    private String applicationId;

    public SysRoleApplication() {
    }

    public SysRoleApplication(String roleId, String applicationId) {
        this.roleId = roleId;
        this.applicationId = applicationId;
    }



}
