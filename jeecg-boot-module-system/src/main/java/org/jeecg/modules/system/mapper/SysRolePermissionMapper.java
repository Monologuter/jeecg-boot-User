package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysRolePermission;

import java.util.List;

/**
 * <p>
 * 角色权限表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysRolePermissionMapper extends BaseMapper<SysRolePermission> {

    List<String> queryLeafSysRolePermission(String roleId);

    /**
     * 批量添加角色菜单关系
     *
     * @param sysRolePermissions
     */
    void insertBatch(List<SysRolePermission> sysRolePermissions);

    /**
     * 根据角色和创建途径查询角色菜单集合
     *
     * @param roleId     角色id
     * @param createWays 创建途径 0应用菜单 1系统菜单
     * @return
     */
    List<SysRolePermission> findSysRolePermissionByRoleIdAndCreateWays(String roleId, int createWays);


    void insertOrUpdateBatch(List<SysRolePermission> sysRolePermissions);

}
