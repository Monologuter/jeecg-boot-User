package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysApplicationPermission;
import org.jeecg.modules.system.mapper.SysApplicationPermissionMapper;
import org.jeecg.modules.system.service.ISysApplicationPermissionService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * 应用菜单表 服务实现类
 * </p>
 *
 * @Author Zhouhonghan
 */
@Service
public class SysApplicationPermissionServiceImpl extends ServiceImpl<SysApplicationPermissionMapper, SysApplicationPermission> implements ISysApplicationPermissionService {
    // 参考SysRolePermissionServiceImpl
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveApplicationPermission(String applicationId, String permissionIds) {
        //删除
        this.deleteApplicationPermission(applicationId);
        if (!oConvertUtils.isEmpty(permissionIds)){
            List<String> ids = Arrays.asList(permissionIds.split(","));
            List<SysApplicationPermission> sysApplicationPermissions = new ArrayList<SysApplicationPermission>();
            for (String id : ids) {
                sysApplicationPermissions.add(new SysApplicationPermission(applicationId,id));
            }
            this.saveBatch(sysApplicationPermissions);
        }
    }

    @Override
    public void saveApplicationPermission(String applicationId, String permissionIds, String lastPermissionIds) {

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteApplicationPermission(String applicationId){
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_id",applicationId);
        this.remove(queryWrapper);
    }


}
