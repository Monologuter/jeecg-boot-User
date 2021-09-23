package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.vo.SysApplicationDepartRoleVO;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 应用表 服务类
 * </p>
 *
 * @Author Zhouhonghan
 */
public interface ISysApplicationService extends IService<SysApplication> {
    /**
     * 导入 excel ，检查 ApplicationCode 的唯一性
     *
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcelCheckApplicationCode(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 删除应用
     *
     * @param applicationId
     * @return
     */
    boolean deleteApplication(String applicationId);

    /**
     * 批量删除应用
     *
     * @param applicationIds
     * @return
     */
    boolean deleteBatchApplication(List<String> applicationIds);

    /**
     * 较验码是否唯一
     *
     * @param applicationCode
     * @return
     */
    boolean queryCodeOnly(String applicationCode);


    /**
     * 通过id复制应用
     *
     * @param application
     * @return
     */
    SysApplication copyApplicationById(String application, Integer template);

    /**
     * 通过应用id删除菜单
     *
     * @param applicationId
     * @param permissionIds
     */
    void deletePermissionById(String applicationId, List<String> permissionIds);

    /**
     * 获取应用编码
     *
     * @return
     */
    String getApplicationCode();

    /**
     * 查询角色分配页面
     *
     * @param applicationId
     * @param appRoleId
     * @return
     */
    Object queryRoleDistribution(String applicationId, String appRoleId, Integer selectFlag);

    /**
     * 更新应用下的菜单
     *
     * @param applicationId
     * @param ids
     * @return
     */
    Boolean updateApplictionPermission(String applicationId, String ids);

    /**
     * 获取成员管理信息
     *
     * @param applicationId
     * @return
     */
    List<SysApplicationDepartRoleVO> getMemberManagementMessage(String applicationId);

    /**
     * 删除成员管理信息
     *
     * @param applicationId
     * @param id
     * @param appRoleId
     * @param selectFlag
     * @return
     */
    int deleteMemberManagementMessage(String applicationId, String id, String appRoleId, Integer selectFlag);

    /**
     * 添加角色分配
     *
     * @param applicationId
     * @param roleId
     * @param ids
     * @param selectFlag
     * @return
     */
    Boolean updateRoleDistribution(String applicationId, String roleId, String ids, Integer selectFlag);


    /**
     * 根据应用id查询应用角色
     *
     * @param applicationId
     * @return
     */
    Set<String> queryAppRoleByApplicationId(String applicationId);


    /**
     * 根据AI机器人传来的语句查询应用或模板
     *
     * @param KeyWord
     * @return
     */
    Set<SysApplication> queryApplicationOrTemplateByKeyWord(String KeyWord);

    /**
     * 保存模板
     *
     * @param sysApplication
     * @return
     */
    SysApplication saveTemplate(SysApplication sysApplication);

    /**
     * 获取应用的创建者
     *
     * @param applicationId
     * @return
     */
    SysUser selectApplicationCreator(String applicationId);

    /**
     * 更新应用下角色配置菜单
     *
     * @param applicationId
     * @param roleId
     * @param newSysPermissionIds
     */
    Boolean updateApplicationPermissionOfRole(String applicationId, String roleId, List<String> newSysPermissionIds);

    /**
     * 更新应用下部门配置菜单
     *
     * @param applicationId
     * @param departId
     * @param newSysPermissionIds
     */
    Boolean updateApplicationPermissionOfDepart(String applicationId, String departId, List<String> newSysPermissionIds);

    /**
     * 展示应用下菜单信息
     *
     * @param applicationId
     * @return
     */
    List<SysPermission> displayApplicationPermissions(String applicationId);

    /**
     * 查询应用数据输出信息
     *
     * @param permissionId
     * @return
     */
    List<SysPermission> queryApplicationPermissionsAndOutput(String permissionId);

    /**
     * 根据表单id查询其属于哪个应用下
     *
     * @param formId 表单id
     * @return
     */
    SysApplication getApplicationByFormId(String formId);
}
