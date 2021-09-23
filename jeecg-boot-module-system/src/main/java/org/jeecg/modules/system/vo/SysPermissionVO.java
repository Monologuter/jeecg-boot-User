package org.jeecg.modules.system.vo;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.Value;
import org.jeecg.modules.system.entity.SysPermission;

@Data
public class SysPermissionVO {

    @NotNull
    private String applicationId;

    private SysPermission permission;
}
