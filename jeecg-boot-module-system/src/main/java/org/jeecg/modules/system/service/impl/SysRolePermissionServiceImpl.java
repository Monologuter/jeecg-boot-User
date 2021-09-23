package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.util.IPUtils;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.mapper.SysRolePermissionMapper;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
@Slf4j
public class SysRolePermissionServiceImpl extends ServiceImpl<SysRolePermissionMapper, SysRolePermission> implements ISysRolePermissionService {

    private static final int SYSTEM_PERMISSION = PermissionProperties.CREATE_WAYS_SYSTEM;


    private final SysRolePermissionMapper sysRolePermissionMapper;

    private final SysPermissionMapper sysPermissionMapper;

    public SysRolePermissionServiceImpl(SysRolePermissionMapper sysRolePermissionMapper, SysPermissionMapper sysPermissionMapper) {
        this.sysRolePermissionMapper = sysRolePermissionMapper;
        this.sysPermissionMapper = sysPermissionMapper;
    }

    @Override
    public void saveRolePermission(String roleId, String permissionIds) {
        String ip = getIp();
        LambdaQueryWrapper<SysRolePermission> query = new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId);
        this.remove(query);
        List<SysRolePermission> list = new ArrayList<SysRolePermission>();
        String[] arr = permissionIds.split(",");
        for (String p : arr) {
            if (oConvertUtils.isNotEmpty(p)) {
                SysRolePermission rolepms = new SysRolePermission(roleId, p);
                rolepms.setOperateDate(new Date());
                rolepms.setOperateIp(ip);
                list.add(rolepms);
            }
        }
        this.saveBatch(list);
    }

    @Override
    public void saveRolePermission(String roleId, String permissionIds, String lastPermissionIds) {
        String ip = getIp();
        List<String> add = getDiff(lastPermissionIds, permissionIds);
        if (add != null && add.size() > 0) {
            List<SysRolePermission> list = new ArrayList<SysRolePermission>();
            for (String p : add) {
                if (oConvertUtils.isNotEmpty(p)) {
                    SysRolePermission rolepms = new SysRolePermission(roleId, p);
                    rolepms.setOperateDate(new Date());
                    rolepms.setOperateIp(ip);
                    list.add(rolepms);
                }
            }
            this.saveBatch(list);
        }

        List<String> delete = getDiff(permissionIds, lastPermissionIds);
        if (delete != null && delete.size() > 0) {
            for (String permissionId : delete) {
                this.remove(new QueryWrapper<SysRolePermission>().lambda().eq(SysRolePermission::getRoleId, roleId).eq(SysRolePermission::getPermissionId, permissionId));
            }
        }
    }

    private String getIp() {
        String ip = "";
        try {
            //获取request
            HttpServletRequest request = SpringContextUtils.getHttpServletRequest();
            //获取IP地址
            ip = IPUtils.getIpAddr(request);
        } catch (Exception e) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    @Transactional
    public boolean updateSysRolePermissionByRoleId(String roleId, Set<String> oldPermissionIds, Set<String> newPermissionIds) {
        //默认首页菜单保持不变
        final String INDEX_PID = sysPermissionMapper.selectOne(new QueryWrapper<SysPermission>().lambda()
                .eq(SysPermission::getName, PermissionProperties.PERMISSION_NAME_INDEX)
                .eq(SysPermission::getCreateWays, PermissionProperties.CREATE_WAYS_SYSTEM)).getId();
        oldPermissionIds.add(INDEX_PID);
        newPermissionIds.add(INDEX_PID);
        //旧的pid与数据库保持一致
        Map<String, String> oldSysRolePermissionPidToId = sysRolePermissionMapper
                .findSysRolePermissionByRoleIdAndCreateWays(roleId, SYSTEM_PERMISSION)
                .stream()
                .collect(Collectors
                        .toMap(SysRolePermission::getPermissionId, SysRolePermission::getId));
        if (!oldPermissionIds.equals(oldSysRolePermissionPidToId.keySet())) {
            log.warn("旧的pid与数据库保持一致");
            return false;
        }
        //新pid必须都存在于原有菜单表中
        Set<String> newPIdsFind = sysPermissionMapper.selectBatchIds(newPermissionIds)
                .stream()
                .filter(sysPermission -> sysPermission.getCreateWays() == SYSTEM_PERMISSION
                        && sysPermission.getDelFlag() == 0)
                .map(SysPermission::getId)
                .collect(Collectors.toSet());
        if (!(newPermissionIds.size() == newPIdsFind.size())) {
            log.warn("新pid必须都存在于原有菜单表中");
            return false;
        }
        /*
         *
         *          old     new
         *          0       1      insert
         *          1       0      delete
         *          1       1      update
         *
         */
        Set<String> delete = new HashSet<>(oldPermissionIds);
        oldPermissionIds.removeAll(newPermissionIds);
        sysRolePermissionMapper.deleteBatchIds(delete.stream()
                .map(oldSysRolePermissionPidToId::get)
                .collect(Collectors.toSet()));
        //如果sys_role_permission有这个菜单，则使用旧的id，如果没有这个菜单，则生成id
        final String ip = getIp();
        final Date currentDate = new Date();
        List<SysRolePermission> insertOrUpdate = newPermissionIds
                .stream()
                .map(pid -> {
                    SysRolePermission sysRolePermission = new SysRolePermission();
                    String id = oldSysRolePermissionPidToId.getOrDefault(pid, IdWorker.getIdStr());
                    sysRolePermission.setId(id);
                    sysRolePermission.setPermissionId(pid);
                    sysRolePermission.setRoleId(roleId);
                    sysRolePermission.setOperateIp(ip);
                    sysRolePermission.setOperateDate(currentDate);
                    return sysRolePermission;
                }).collect(Collectors.toList());
        log.info(insertOrUpdate.toString());
        sysRolePermissionMapper.insertOrUpdateBatch(insertOrUpdate);
        return true;
    }


    @Override
    public List<String> queryLeafSysRolePermission(String roleId) {
        return sysRolePermissionMapper.queryLeafSysRolePermission(roleId);
    }

    /**
     * 从diff中找出main中没有的元素
     *
     * @param main
     * @param diff
     * @return
     */
    private List<String> getDiff(String main, String diff) {
        if (oConvertUtils.isEmpty(diff)) {
            return null;
        }
        if (oConvertUtils.isEmpty(main)) {
            return Arrays.asList(diff.split(","));
        }

        String[] mainArr = main.split(",");
        String[] diffArr = diff.split(",");
        Map<String, Integer> map = new HashMap<>();
        for (String string : mainArr) {
            map.put(string, 1);
        }
        List<String> res = new ArrayList<String>();
        for (String key : diffArr) {
            if (oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }

}
