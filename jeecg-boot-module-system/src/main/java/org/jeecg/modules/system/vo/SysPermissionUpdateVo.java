package org.jeecg.modules.system.vo;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.properties.PermissionProperties;

import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

@Slf4j
public class SysPermissionUpdateVo {
    public final String applicationId;

    public final SysPermission permission;

    public transient final boolean isValid;

    public SysPermissionUpdateVo(String applicationId, SysPermission permission) {
        this.applicationId = applicationId;
        this.permission = permission;
        //null or not blank string
        if (Stream.of(permission.getParentId(),
                        permission.getPerms(), permission.getPermsType(), permission.getIcon(),
                        permission.getComponentName(),
                        permission.getRedirect(), permission.getDescription(), applicationId).
                anyMatch(s -> s != null && s.trim().isEmpty())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: null or not blank string");
            return;
        }
        // not null and not blank string
        if (!Stream.of(permission.getId(), permission.getName(), permission.getComponent(),
                permission.getUrl()).allMatch(s -> s != null && !s.trim().isEmpty())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: not null and not blank string");
            return;
        }
        if (!Arrays.asList(PermissionProperties.MENU_TYPE_DIRECTORY, PermissionProperties.MENU_TYPE_FORM,
                PermissionProperties.MENU_TYPE_GRAPH, PermissionProperties.MENU_TYPE_WORKFLOW).contains(permission.getMenuType())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: menu type simple validation");
            return;
        }
        if (!Arrays.asList(PermissionProperties.CREATE_WAYS_SYSTEM, PermissionProperties.CREATE_WAYS_APPLICATION).contains(permission.getCreateWays())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: create ways simple validation");
            return;
        }
        if (!Arrays.asList(0, 1).contains(permission.getDelFlag())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: del flag simple validation");
            return;
        }
        if (Objects.equals(permission.getId(), permission.getParentId())) {
            isValid = false;
            log.warn("SysPermissionUpdateVo simple validation failed! reason: id 不能等于parentId");
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
