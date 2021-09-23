package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.model.SysApplicationWithPermissionId;
import org.jeecg.modules.system.model.SysPermissionTree;
import org.jeecg.modules.system.model.TreeModel;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 菜单权限表 服务类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
public interface ISysPermissionService extends IService<SysPermission> {
	
	public List<TreeModel> queryListByParentId(String parentId);
	
	/**真实删除*/
	public void deletePermission(String id) throws JeecgBootException;
	/**逻辑删除*/
	public void deletePermissionLogical(String id) throws JeecgBootException;
	
	public void addPermission(SysPermission sysPermission) throws JeecgBootException;
	

	
	public List<SysPermission> queryByUser(String username);

	public List<SysApplication> queryByUserApplication(String username);
	
	/**
	 * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
	 * 
	 * @param id
	 * @return
	 */
	public void deletePermRuleByPermId(String id);
	
	/**
	  * 查询出带有特殊符号的菜单地址的集合
	 * @return
	 */
	public List<String> queryPermissionUrlWithStar();

	/**
	 * 判断用户否拥有权限
	 * @param username
	 * @param sysPermission
	 * @return
	 */
	public boolean hasPermission(String username, SysPermission sysPermission);

	/**
	 * 根据用户和请求地址判断是否有此权限
	 * @param username
	 * @param url
	 * @return
	 */
	public boolean hasPermission(String username, String url);


	List<SysPermissionTree> findAllTrees();

	Map<String, SysApplicationWithPermissionId> findApplicationByPid(List<String> pids);

	List<SysPermissionTree> buildTree(List<SysPermission> sysPermissions);

	IPage<SysPermission> findSystemRoots(int page, int size);

	List<SysPermission> findChildren(String id);

	SysApplication findApplicationByPid(String pid);

	/**
	 * 根据应用id，查询当前用户在此应用下拥有的菜单
	 *
	 * @param applicationId
	 * @return
	 */
	List<SysPermissionTree> queryTreeByApplicationId(String applicationId);

	List<SysPermission> queryUserOfApplicationPermission(String userId, String applicationId);

	/**
	 * 根据应用id，查询当前角色在此应用下拥有的菜单
	 *
	 * @param roleId
	 * @param applicationId
	 * @return
	 */
	List<SysPermission> queryRoleOfApplicationPermission(String roleId, String applicationId);

	/**
	 * 根据应用id，查询当前部门在此应用下拥有的菜单
	 *
	 * @param departId
	 * @param applicationId
	 * @return
	 */
	List<SysPermission> queryDepartOfApplicationPermission(String departId, String applicationId);

	boolean create(String applicationId, SysPermission sysPermission);


	boolean edit(String applicationId, SysPermission sysPermission);

	/**
	 * 批量删除菜单
	 *
	 * @param ids
	 * @return
	 */
	boolean deletePermissionByIds(String ids);
}
