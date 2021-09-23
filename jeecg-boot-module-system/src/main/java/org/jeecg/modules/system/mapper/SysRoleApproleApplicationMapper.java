package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRoleApproleApplication;

import java.util.List;


public interface SysRoleApproleApplicationMapper extends BaseMapper<SysRoleApproleApplication> {


    /**
     * 根据应用角色和应用查询角色
     * @param applicationId
     * @param appRoleId
     * @return
     */
    List<SysRole> queryRoleByAppRoleAndApplication(String applicationId, String appRoleId);
}
