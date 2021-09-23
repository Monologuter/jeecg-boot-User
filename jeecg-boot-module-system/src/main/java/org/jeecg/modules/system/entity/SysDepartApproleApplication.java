package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 中间表
 * 部门 角色 应用表
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysDepartApproleApplication implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 部门id
     */
    private String departId;

    /**
     * 角色id
     */
    private String approleId;

    /**
     * 应用id
     */
    private String applicationId;

    public SysDepartApproleApplication() {
    }

    public SysDepartApproleApplication(String departId, String approleId, String applicationId) {
        this.departId = departId;
        this.approleId = approleId;
        this.applicationId = applicationId;
    }
}
