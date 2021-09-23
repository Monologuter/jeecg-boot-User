package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 中间表
 * 角色表 应用角色表 应用表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysRoleApproleApplication implements Serializable {
    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 角色id
     */
    private String roleId;

    /**
     * 应用角色id
     */
    private String approleId;

    /**
     * 应用id
     */
    private String applicationId;

    public SysRoleApproleApplication() {
    }

    public SysRoleApproleApplication(String roleId, String approleId, String applicationId) {
        this.roleId = roleId;
        this.approleId = approleId;
        this.applicationId = applicationId;
    }
}
