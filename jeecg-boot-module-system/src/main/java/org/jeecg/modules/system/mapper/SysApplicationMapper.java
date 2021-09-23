package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysApplication;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 应用表 Mapper 接口
 * </p>
 *
 * @Author Zhouhonghan
 */
public interface SysApplicationMapper extends BaseMapper<SysApplication> {

    // 删除角色与应用的关系
    @Delete("delete from sys_role_application where application_id = #{applicationId}")
    void deleteRoleApplicationRelation(@Param("applicationId") String applicationId);

    // 删除应用与菜单权限的关系
    @Delete("delete from sys_application_permission where application_id = #{applicationId}")
    void deleteApplicationPermissionRelation(@Param("applicationId") String applicationId);

    //根据AI语句查找应用或模板(根据应用类型)
    List<SysApplication> queryApplicationOrTemplateByAIByAppType(@Param("KeyWord") String KeyWord);

    //根据AI语句查找应用或模板(根据应用或模板名)
    List<SysApplication> queryApplicationOrTemplateByAppName(@Param("KeyWord") String KeyWord);
}
