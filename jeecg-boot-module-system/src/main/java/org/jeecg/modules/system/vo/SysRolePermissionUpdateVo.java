package org.jeecg.modules.system.vo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SysRolePermissionUpdateVo {


    public final String roleId;
    //该角色更新拥有的菜单的id

    public final String permissionIds;
    //原该角色拥有的菜单的id

    public final String lastpermissionIds;

    public transient final boolean isValid;


    public SysRolePermissionUpdateVo(String roleId, String permissionIds, String lastpermissionIds) {
        this.roleId = roleId;
        this.permissionIds = permissionIds;
        this.lastpermissionIds = lastpermissionIds;
        isValid  = Stream.of(roleId, permissionIds, lastpermissionIds).noneMatch(s -> s == null || s.trim().isEmpty());
    }


    public Set<String> oldPermissionIds(){
        return Arrays.stream(lastpermissionIds.split(",")).collect(Collectors.toSet());
    }
    public Set<String> newPermissionIds(){
        return Arrays.stream(permissionIds.split(",")).collect(Collectors.toSet());
    }

//    //进行简单的与数据库无关的校验
//    private static boolean validate(){
//
//    }


    @Override
    public String toString() {
        return "SysRolePermissionUpdateVo{" +
                "roleId='" + roleId + '\'' +
                ", permissionIds='" + permissionIds + '\'' +
                ", lastpermissionIds='" + lastpermissionIds + '\'' +
                ", isValid=" + isValid +
                '}';
    }
}
