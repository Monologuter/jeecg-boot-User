package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.service.ISysDepartPermissionService;
import org.jeecg.modules.system.vo.SysDepartPermissionVo;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 部门权限表
 * @Author: jeecg-boot
 * @Date:   2020-02-11
 * @Version: V1.0
 */
@Service
public class SysDepartPermissionServiceImpl extends ServiceImpl<SysDepartPermissionMapper, SysDepartPermission> implements ISysDepartPermissionService {
    @Resource
    private SysPermissionDataRuleMapper ruleMapper;

    @Resource
    private SysDepartRoleMapper sysDepartRoleMapper;

    @Resource
    private SysDepartRolePermissionMapper departRolePermissionMapper;

    @Resource
    private SysDepartMapper sysDepartMapper;

    @Resource
    private SysDepartPermissionMapper sysDepartPermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveDepartPermission(SysDepartPermissionVo sysDepartPermissionVo) {
        Set<String> oldPermissionIds = sysDepartPermissionVo.oldPermissionIds();
        Set<String> newPermissionIds = sysDepartPermissionVo.newPermissionIds();
        String departId = sysDepartPermissionVo.getDepartId();
        List<String> add = getDiff(oldPermissionIds,newPermissionIds);
        if(add!=null && add.size()>0) {
            List<SysDepartPermission> list = new ArrayList<SysDepartPermission>();
            for (String p : add) {
                if(oConvertUtils.isNotEmpty(p)) {
                    SysDepartPermission rolepms = new SysDepartPermission(departId, p);
                    list.add(rolepms);
                }
            }
            this.saveBatch(list);
        }
        List<String> delete = getDiff(newPermissionIds,oldPermissionIds);
        if(delete!=null && delete.size()>0) {
            deleteChildrenPermission(departId,delete);
            for (String permissionId : delete) {
                this.remove(new QueryWrapper<SysDepartPermission>().lambda().eq(SysDepartPermission::getDepartId, departId).eq(SysDepartPermission::getPermissionId, permissionId));
                //删除部门权限时，删除部门角色中已授权的权限
                List<SysDepartRole> sysDepartRoleList = sysDepartRoleMapper.selectList(new LambdaQueryWrapper<SysDepartRole>().eq(SysDepartRole::getDepartId,departId));
                List<String> roleIds = sysDepartRoleList.stream().map(SysDepartRole::getId).collect(Collectors.toList());
                if(roleIds != null && roleIds.size()>0){
                    departRolePermissionMapper.delete(new LambdaQueryWrapper<SysDepartRolePermission>().eq(SysDepartRolePermission::getPermissionId,permissionId));
                }
            }
        }
    }

    @Override
    public List<SysPermissionDataRule> getPermRuleListByDeptIdAndPermId(String departId, String permissionId) {
        SysDepartPermission departPermission = this.getOne(new QueryWrapper<SysDepartPermission>().lambda().eq(SysDepartPermission::getDepartId, departId).eq(SysDepartPermission::getPermissionId, permissionId));
        if(departPermission != null){
            LambdaQueryWrapper<SysPermissionDataRule> query = new LambdaQueryWrapper<SysPermissionDataRule>();
            query.in(SysPermissionDataRule::getId, Arrays.asList(departPermission.getDataRuleIds().split(",")));
            query.orderByDesc(SysPermissionDataRule::getCreateTime);
            List<SysPermissionDataRule> permRuleList = this.ruleMapper.selectList(query);
            return permRuleList;
        }else{
            return null;
        }
    }

    /**
     * 从diff中找出main中没有的元素
     * @param main
     * @param diff
     * @return
     */
    private List<String> getDiff(Set<String> main,Set<String> diff){
        if(oConvertUtils.isEmpty(diff)) {
            return null;
        }
        if(oConvertUtils.isEmpty(main)) {
            return new ArrayList<>(diff);
        }
        Map<String, Integer> map = new HashMap<>();
        for (String string : main) {
            map.put(string, 1);
        }
        List<String> res = new ArrayList<String>();
        for (String key : diff) {
            if(oConvertUtils.isNotEmpty(key) && !map.containsKey(key)) {
                res.add(key);
            }
        }
        return res;
    }

    /**
     * 删除子部门管理权限
     * @param departId
     * @param permissionIds
     */
    private void deleteChildrenPermission(String departId,List<String> permissionIds){
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepart::getParentId,departId);
        List<SysDepart> sysDepartList = sysDepartMapper.selectList(queryWrapper);
        if (sysDepartList.size() > 0) {
            for (SysDepart sysDepart : sysDepartList) {
                sysDepartPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>()
                        .eq(SysDepartPermission::getDepartId,sysDepart.getId())
                        .in(SysDepartPermission::getPermissionId,permissionIds));
                deleteChildrenPermission(sysDepart.getId(),permissionIds);
            }
        }
    }
}
