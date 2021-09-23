package org.jeecg.modules.system.vo;

import lombok.Data;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
public class SysDepartPermissionVo {

    private String departId;

    private String permissionIds;

    private String lastpermissionIds;

    private boolean isValid;

    public SysDepartPermissionVo(String departId, String permissionIds, String lastpermissionIds) {
        this.departId = departId;
        this.permissionIds = permissionIds;
        this.lastpermissionIds = lastpermissionIds;
        isValid  = Stream.of(departId).noneMatch(s -> s == null || s.trim().isEmpty());
    }

    public Set<String> oldPermissionIds(){
        return Arrays.stream(lastpermissionIds.split(",")).collect(Collectors.toSet());
    }
    public Set<String> newPermissionIds(){
        return Arrays.stream(permissionIds.split(",")).collect(Collectors.toSet());
    }
}
