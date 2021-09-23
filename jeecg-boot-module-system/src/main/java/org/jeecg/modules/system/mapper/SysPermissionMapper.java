package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.model.SysApplicationWithPermissionId;
import org.jeecg.modules.system.model.TreeModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface SysPermissionMapper extends BaseMapper<SysPermission> {
    /**
     * 通过父菜单ID查询子菜单
     *
     * @param parentId
     * @return
     */
    List<TreeModel> queryListByParentId(@Param("parentId") String parentId);

    /**
     * 根据用户查询用户权限
     */
    List<SysPermission> queryByUser(@Param("username") String username);

    /**
     * 根据用户查询用户应用权限
     */
    List<SysApplication> queryByUserApplication(@Param("username") String username);

    /**
     * 修改菜单状态字段： 是否子节点
     */
    @Update("update sys_permission set is_leaf=#{leaf} where id = #{id}")
    int setMenuLeaf(@Param("id") String id, @Param("leaf") int leaf);

    /**
     * 获取模糊匹配规则的数据权限URL
     */
    @Select("SELECT url FROM sys_permission WHERE del_flag = 0 and menu_type = 2 and url like '%*%'")
    List<String> queryPermissionUrlWithStar();


    /**
     * 根据用户账号查询菜单权限
     *
     * @param sysPermission
     * @param username
     * @return
     */
    int queryCountByUsername(@Param("username") String username, @Param("permission") SysPermission sysPermission);


    @MapKey("pid")
    Map<String, SysApplicationWithPermissionId> findApplicationsByPermissionId(@Param("pids") List<String> pids);
//	/**
//	 * 查找指定节点的所有后代节点
//	 *
//	 * @param id 要查找树的根节点id
//	 * @return 指定节点的所有后代节点
//	 */
//	@Select("with recursive descendants as ( " +
//			"select * from sys_permission where parent_id = #{id}  and del_flag = 0" +
//			"union " +
//			"select s.* from descendants d, sys_permission s where s.parent_id = d.id" +
//			") " +
//			"select * from descendants")


    /**
     * 查找指定节点的所有后代节点
     *
     * @param ids 要查找树的节点id
     * @return 指定节点的所有后代节点
     */
    List<SysPermission> findDescendants(List<String> ids);


    /**
     * 查找指定节点的所有祖先节点
     *
     * @param ids 要查找的节点id
     * @return 指定节点的所有祖先节点
     */
    List<SysPermission> findAncestors(List<String> ids);

    /**
     * 查找指定节点的根节点
     *
     * @param ids 要查找的节点id
     * @return 指定节点的所有根节点
     */
    List<SysPermission> findRoots(List<String> ids);

    default List<SysPermission> findDescendants(String id) {
        return findDescendants(Collections.singletonList(id));
    }

    default List<SysPermission> findAncestors(String id) {
        return findAncestors(Collections.singletonList(id));
    }

    default SysPermission findRoot(String id) {
        return findRoots(Collections.singletonList(id)).get(0);
    }


    List<SysPermission> findAll();

    @Select("select * from sys_application a " +
            "join sys_application_permission ap on a.id = ap.application_id where ap.permission_id = #{pid}")
    SysApplication findApplicationByPermissionId(@Param("pid") String pid);


    List<SysPermission> findChildren(String id);

    /**
     * 根据用户id查询所有菜单
     *
     * @param userId
     * @return
     */
    List<SysPermission> queryPermissionsByUserId(String userId);

    /**
     * 查询当前角色在应用下拥有的菜单
     *
     * @param roleId
     * @param applicationId
     * @return
     */
    List<SysPermission> findPermissionsByRoleIdAndApplicationId(String roleId, String applicationId);

    /**
     * 查询当前部门在应用下拥有的菜单
     *
     * @param departId
     * @param applicationId
     * @return
     */
    List<SysPermission> findPermissionsByDepartIdIdAndApplicationId(String departId, String applicationId);

    /**
     * 查询子代菜单包括自己
     *
     * @param ids 菜单ids
     * @return
     */
    List<SysPermission> findDescendantsWithSelf(List<String> ids);


    /**
     * 根据角色id和创建途径查询菜单集合
     *
     * @param roleId     角色id
     * @param createWays 创建途径 0应用菜单 1系统菜单
     * @return
     */
    List<SysPermission> findSysPermissionByRoleIdAndCreateWays(String roleId, int createWays);

    /**
     * 根据应用id和创建途径查询菜单集合
     *
     * @param departId   部门id
     * @param createWays 创建途径 0应用菜单 1系统菜单
     * @return
     */
    List<SysPermission> findSysPermissionByDepartIdAndCreateWays(String departId, int createWays);

    /**
     * 全量更新 sysPermission
     *
     * @param sysPermission 要修改的sysPermission，必须包含id
     */
    void fullUpdateById(SysPermission sysPermission);


}