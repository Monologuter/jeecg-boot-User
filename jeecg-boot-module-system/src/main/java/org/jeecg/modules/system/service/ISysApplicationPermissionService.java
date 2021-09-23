package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysApplicationPermission;
/**
 * <p>
 * 应用菜单表 服务类
 * </p>
 *
 * @Author Zhouhonghan
 */
public interface ISysApplicationPermissionService extends IService<SysApplicationPermission> {
    /**
     * 保存菜单
     * @param applicationId
     * @param permissionIds
     */
    void saveApplicationPermission(String applicationId,String permissionIds);

    /**
     * 保存授权 将上次的权限和这次作比较 差异处理提高效率
     * @param applicationId
     * @param permissionIds
     * @param lastPermissionIds
     */
    void saveApplicationPermission(String applicationId,String permissionIds,String lastPermissionIds);

    /**
     * 根据应用id删除应用和菜单的关系
     * @param applicationId
     */
    void deleteApplicationPermission(String applicationId);
}
