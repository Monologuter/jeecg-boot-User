package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.entity.SysUserApproleApplication;

import java.util.List;

public interface SysUserApproleApplicationMapper extends BaseMapper<SysUserApproleApplication> {
    //判断是否删除成功
    void deleteUserRoleApplication(
            @Param("applicationId") String applicationid,
            @Param("id")String id,
            @Param("appRoleId")String appRoleId,
            @Param("selectFlag")Integer selectFlag);


    /**
     * 根据应用id和应用查询用户
     * @param applicationId
     * @param appRoleId
     * @return
     */
    List<SysUser> queryUserByAppRoleAndApplication(String applicationId, String appRoleId);

    //查询显示应用角色信息
    String queryRoleByApplicationId(@Param("applicationId") String applicationId);

    //查询应用角色用户信息
    List<SysUser> queryApplicationUsers(@Param("applicationId") String applicationId);

}
