package org.jeecg.modules.system.vo;


import lombok.Data;
import org.jeecg.modules.system.entity.SysApprole;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysUser;
import java.util.List;
/**
 * 应用权限中角色管理所需对象列表
 * */
@Data
public class SysApplicationDepartRoleVO {

    /**
     * 应用角色
     */
    private SysApprole sysApprole;

    /**
     * 用户对象列表
     */
    private List<SysUser> SysUsers;
    /**
     * 部门对象列表
     */
    private List<SysDepart> SysDeparts;

    /**
     * 角色对象列表
     */
    private List<SysRole> SysRoles;

    public SysApplicationDepartRoleVO(){};

    public SysApplicationDepartRoleVO(SysApprole sysApprole, List<SysUser> sysUsers, List<SysDepart> sysDeparts, List<SysRole> sysRoles) {
        this.sysApprole = sysApprole;
        SysUsers = sysUsers;
        SysDeparts = sysDeparts;
        SysRoles = sysRoles;
    }
}
