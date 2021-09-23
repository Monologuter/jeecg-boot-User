package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.entity.SysDepartApproleApplication;

import java.util.List;

/**
 * SysDepartRoleApplication DAO层
 */
public interface SysDepartApproleApplicationMapper extends BaseMapper<SysDepartApproleApplication> {

    /**
     * 根据应用id 角色id 查询部门
     * @param applicationId
     * @param appRoleId
     * @return
     */
    List<SysDepart> queryDepartByAppRoleAndApplication(String applicationId,String appRoleId);

}
