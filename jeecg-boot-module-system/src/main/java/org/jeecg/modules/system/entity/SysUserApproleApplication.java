package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户角色应用 中间表
 * 用于给应用下的用户分配角色
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysUserApproleApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 角色id
     */
    private String approleId;

    /**
     * 应用id
     */
    private String applicationId;

    public SysUserApproleApplication(){
    }

    public SysUserApproleApplication(String userId, String approleId, String applicationId) {
        this.userId = userId;
        this.approleId = approleId;
        this.applicationId = applicationId;
    }

}
