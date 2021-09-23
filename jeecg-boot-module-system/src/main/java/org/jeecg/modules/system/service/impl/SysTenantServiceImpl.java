package org.jeecg.modules.system.service.impl;

import cn.hutool.core.date.DateTime;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.util.PasswordUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.SysTenantMapper;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
public class SysTenantServiceImpl extends ServiceImpl<SysTenantMapper, SysTenant> implements ISysTenantService {

    @Autowired
    ISysPermissionService sysPermissionService;

    @Autowired
    ISysUserService sysUserService;

    @Autowired
    ISysRoleService sysRoleService;

    @Autowired
    ISysUserRoleService sysUserRoleService;

    @Autowired
    ISysRolePermissionService sysRolePermissionService;

    @Override
    public List<SysTenant> queryEffectiveTenant(Collection<String> idList) {
        LambdaQueryWrapper<SysTenant> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SysTenant::getId, idList);
        //此处查询忽略时间条件
        List<SysTenant> list = super.list(queryWrapper);
        for (SysTenant sysTenant : list) {
            if (sysTenant.getEndDate().before(new DateTime())) {
                //过期了
                sysTenant.setStatus(0);
                super.updateById(sysTenant);
            }
        }
        queryWrapper.eq(SysTenant::getStatus, CommonConstant.STATUS_1);
        return super.list(queryWrapper);
    }

    @Override
    public int countUserLinkTenant(String id) {
        // 查找出已被关联的用户数量
        List<SysUser> sysUsers = sysUserService.list(null);
        int count = 0;
        for (SysUser sysUser : sysUsers) {
            String relTenantIds = sysUser.getRelTenantIds();
            List<String> ids = new ArrayList<>(Arrays.asList(relTenantIds.split(",")));
            if (ids.contains(id)) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean removeTenantById(String id) {
        // 查找出已被关联的用户数量
        int userCount = this.countUserLinkTenant(id);
        if (userCount > 0) {
            throw new JeecgBootException("该租户已被引用，无法删除！");
        }
        return super.removeById(id);
    }

    /**
     * 获取基础菜单列表
     *
     * @return
     */
    private List<SysPermission> getPermissionList() {
        String jsonPath = "static/system/baseRoute.json";
        ClassPathResource classPathResource = new ClassPathResource(jsonPath);
        byte[] bytes = new byte[0];
        try {
            bytes = FileCopyUtils.copyToByteArray(classPathResource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String json = new String(bytes);
        JSONArray array = JSON.parseArray(json);
        List<SysPermission> ls = array.toJavaList(SysPermission.class);
        return ls;
    }

    /**
     * 保存租户 1.在租户下添加基础菜单 2.给租户下添加默认角色 3.给默认角色添加菜单权限
     *
     * @param sysTenant
     */
    @Override
    @Transactional
    public void saveSysTenant(SysTenant sysTenant) {
        this.save(sysTenant);
        int tenantId = sysTenant.getId();
        List<SysPermission> ls = getPermissionList();
        Collection<String> menuIds = setPermissionTenant(ls, tenantId);
        sysPermissionService.saveBatch(ls);

        //生成改租户下的默认管理员用户
        SysUser user = new SysUser();
        String username = "admin_" + tenantId;//设置用户名
        String password = "123456";//设置初始密码
        user.setCreateTime(new Date());// 设置创建时间
        String salt = oConvertUtils.randomGen(8);
        String passwordEncode = PasswordUtil.encrypt(username, password, salt);
        user.setSalt(salt);
        user.setUsername(username);
        user.setRealname(username);
        user.setPassword(passwordEncode);
        user.setStatus(CommonConstant.USER_UNFREEZE);
        user.setDelFlag(CommonConstant.DEL_FLAG_0);
        user.setActivitiSync(CommonConstant.ACT_SYNC_0);
        user.setRelTenantIds(String.valueOf(tenantId));
        sysUserService.save(user);

        // 添加admin角色
        SysRole role = new SysRole();
        role.setRoleCode("admin" + tenantId);
        role.setRoleName("管理员");
        role.setTenantId(tenantId);
        sysRoleService.save(role);

        // 添加角色 用户关系
        SysUserRole sysUserRole = new SysUserRole();
        // TODO is ok？
        sysUserRole.setRoleId(role.getId());
        sysUserRole.setUserId(user.getId());
        sysUserRoleService.save(sysUserRole);

        // 添加角色 菜单关系
        List<SysRolePermission> list = new ArrayList<>();
        for (String menuId : menuIds) {
            SysRolePermission sp = new SysRolePermission();
            sp.setPermissionId(menuId);
            sp.setRoleId(role.getId());
            list.add(sp);
        }
        sysRolePermissionService.saveBatch(list);
    }

    private String randomId() {
        long id = IdWorker.getId();
        return String.valueOf(id);
    }

    private Collection<String> setPermissionTenant(List<SysPermission> ls, int tenantId) {
        // 循环两次 第一次设置ID和tenantId 第二次设置pid
        Map<String, String> map = new HashMap<>();
        for (SysPermission p : ls) {
            String oldId = p.getId();
            String newId = randomId();
            map.put(oldId, newId);
            p.setId(newId);
            p.setTenantId(tenantId);
            p.setMenuType(PermissionProperties.MENU_TYPE_DIRECTORY);
            p.setCreateWays(PermissionProperties.CREATE_WAYS_SYSTEM);
            p.setCreateBy(null);
            p.setCreateTime(null);
            p.setUpdateBy(null);
            p.setUpdateTime(null);
        }
        for (SysPermission p : ls) {
            String oldPid = p.getParentId();
            if (oConvertUtils.isNotEmpty(oldPid)) {
                String newPid = map.get(oldPid);
                if (oConvertUtils.isNotEmpty(newPid)) {
                    p.setParentId(newPid);
                } else {
                    // TODO 一般情况下这个newPid是肯定有值的  如果没有值 说明当前节点的父节点 没有设置为基础路由  那么 需要递归获取 所有父级节点 挨个设置一下即可
                }
            }
        }
        return map.values();
    }
}
