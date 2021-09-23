package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 *  <p>
 *     应用菜单表
 *  </p>
 * @author Zhouhonghan
 */

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysApplicationPermission implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 应用id
     */
    private String applicationId;

    /**
     * 菜单id
     */
    private String permissionId;

    public SysApplicationPermission() {
    }

    public SysApplicationPermission(String applicationId, String permissionId) {
        this.applicationId = applicationId;
        this.permissionId = permissionId;
    }



}
