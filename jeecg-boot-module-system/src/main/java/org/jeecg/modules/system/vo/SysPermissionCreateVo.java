package org.jeecg.modules.system.vo;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.properties.PermissionProperties;

import java.util.Arrays;
import java.util.stream.Stream;

@Slf4j
public class SysPermissionCreateVo {
    public final String applicationId;

    public final SysPermission permission;

    public transient final boolean isValid;

    public SysPermissionCreateVo(String applicationId, SysPermission permission) {
        this.applicationId = applicationId;
        this.permission = permission;
        //new permission must be a leaf
        permission.setLeaf(true);

        permission.setDelFlag(0);
        // must be null
        if (permission.getId() != null) {
            isValid = false;
            log.warn("SysPermissionCreateVo simple validation failed! reason: id must be null");
            return;
        }
        //null or not blank string
        if (Stream.of(permission.getParentId(),
                        permission.getPerms(), permission.getPermsType(), permission.getIcon(),
                        permission.getComponentName(),
                        permission.getRedirect(), permission.getDescription(), applicationId).
                anyMatch(s -> s != null && s.trim().isEmpty())) {
            isValid = false;
            log.warn("SysPermissionCreateVo simple validation failed! reason: null or not blank string");
            return;
        }
        // not null and not blank string
        if (!Stream.of(permission.getName(), permission.getComponent(),
                permission.getUrl()).allMatch(s -> s != null && !s.trim().isEmpty())) {
            isValid = false;
            log.warn("SysPermissionCreateVo simple validation failed! reason: not null and not blank string");
            return;
        }
        if (!Arrays.asList(PermissionProperties.MENU_TYPE_DIRECTORY, PermissionProperties.MENU_TYPE_BUTTON, PermissionProperties.MENU_TYPE_FORM,
                PermissionProperties.MENU_TYPE_GRAPH, PermissionProperties.MENU_TYPE_WORKFLOW).contains(permission.getMenuType())) {
            isValid = false;
            log.warn("SysPermissionCreateVo simple validation failed! reason: menu type simple validation");
            return;
        }
        if (!Arrays.asList(PermissionProperties.CREATE_WAYS_SYSTEM, PermissionProperties.CREATE_WAYS_APPLICATION).contains(permission.getCreateWays())) {
            isValid = false;
            log.warn("SysPermissionCreateVo simple validation failed! reason: create ways simple validation");
            return;
        }
        isValid = applicationId == null ? systemPermissionValidation(permission) : applicationPermissionValidation(permission);
    }


    private boolean systemPermissionValidation(SysPermission sysPermission) {
        return PermissionProperties.MENU_TYPE_DIRECTORY == sysPermission.getMenuType() &&
                sysPermission.getCreateWays() == PermissionProperties.CREATE_WAYS_SYSTEM;

    }

    private boolean applicationPermissionValidation(SysPermission sysPermission) {
        return sysPermission.getCreateWays() == PermissionProperties.CREATE_WAYS_APPLICATION;
    }
}
