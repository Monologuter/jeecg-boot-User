package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.var;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.constant.CacheConstant;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysApplicationPermission;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysPermissionDataRule;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.model.SysApplicationWithPermissionId;
import org.jeecg.modules.system.model.SysPermissionTree;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.ISysPermissionDataRuleService;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 菜单权限表 服务实现类
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Service
public class SysPermissionServiceImpl extends ServiceImpl<SysPermissionMapper, SysPermission> implements ISysPermissionService {
    @Resource
    private SysPermissionMapper sysPermissionMapper;

    @Resource
    private ISysPermissionDataRuleService permissionDataRuleService;

    @Resource
    private SysRolePermissionMapper sysRolePermissionMapper;

    @Resource
    private SysDepartPermissionMapper sysDepartPermissionMapper;

    @Resource
    private SysDepartRolePermissionMapper sysDepartRolePermissionMapper;

    //菜单输出是删除应用菜单表数据
    @Resource
    private SysApplicationPermissionMapper sysApplicationPermissionMapper;

    @Resource
    private SysApplicationMapper sysApplicationMapper;

    @Resource
    private SysRoleMapper sysRoleMapper;

    @Resource
    private SysDepartMapper sysDepartMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Override
    public List<TreeModel> queryListByParentId(String parentId) {
        return sysPermissionMapper.queryListByParentId(parentId);
    }


    //lazy load
    @SuppressWarnings("unchecked")
    public IPage<SysPermission> findSystemRoots(int page, int size) {
        var query = new QueryWrapper<SysPermission>().lambda()
                .eq(SysPermission::getCreateWays, PermissionProperties.CREATE_WAYS_SYSTEM)
                .isNull(SysPermission::getParentId)
                .eq(SysPermission::getDelFlag, 0)
                .orderByAsc(SysPermission::getSortNo, SysPermission::getId);
        return sysPermissionMapper
                .selectPage(new Page<>(page, size), query);

    }

    public List<SysPermission> findChildren(String id) {
        return sysPermissionMapper.findChildren(id);
    }

    // lazy end

    /**
     * 真实删除
     */
    @Override
    @Transactional
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void deletePermission(String id) throws JeecgBootException {
        SysPermission sysPermission = this.getById(id);
        if (sysPermission == null) {
            /**
             *@Description: 去掉抛出异常，改为直接打印异常信息，让其事务不会被回滚，
             *               解决删除删除所有表单管理后无法删除单个表单设计的问题
             *@Author: HuQi
             *@Date: 2021-06-18 09:40
             **/
            log.error("未找到菜单信息");
//			throw new JeecgBootException("未找到菜单信息");
            return;
        }
        String pid = sysPermission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
            if (count == 1) {
                //若父节点无其他子节点，则该父节点是叶子节点
                this.sysPermissionMapper.setMenuLeaf(pid, 1);
            }
        }
        sysPermissionMapper.deleteById(id);
        // 该节点可能是子节点但也可能是其它节点的父节点,所以需要级联删除
        this.removeChildrenBy(sysPermission.getId());
        //关联删除
        Map map = new HashMap<>();
        map.put("permission_id", id);
        //删除数据规则
        this.deletePermRuleByPermId(id);
        //删除角色授权表
        sysRolePermissionMapper.deleteByMap(map);
        //删除部门权限表
        sysDepartPermissionMapper.deleteByMap(map);
        //删除部门角色授权
        sysDepartRolePermissionMapper.deleteByMap(map);
        //删除应用菜单表
        sysApplicationPermissionMapper.deleteByMap(map);
    }

    /**
     * 根据父id删除其关联的子节点数据
     *
     * @return
     */
    public void removeChildrenBy(String parentId) {
        LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
        // 封装查询条件parentId为主键,
        query.eq(SysPermission::getParentId, parentId);
        // 查出该主键下的所有子级
        List<SysPermission> permissionList = this.list(query);
        if (permissionList != null && permissionList.size() > 0) {
            String id = ""; // id
            int num = 0; // 查出的子级数量
            // 如果查出的集合不为空, 则先删除所有
            this.remove(query);
            // 再遍历刚才查出的集合, 根据每个对象,查找其是否仍有子级
            for (int i = 0, len = permissionList.size(); i < len; i++) {
                id = permissionList.get(i).getId();
                Map map = new HashMap<>();
                map.put("permission_id", id);
                //删除数据规则
                this.deletePermRuleByPermId(id);
                //删除角色授权表
                sysRolePermissionMapper.deleteByMap(map);
                //删除部门权限表
                sysDepartPermissionMapper.deleteByMap(map);
                //删除部门角色授权
                sysDepartRolePermissionMapper.deleteByMap(map);
                num = this.count(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getParentId, id));
                // 如果有, 则递归
                if (num > 0) {
                    this.removeChildrenBy(id);
                }
            }
        }
    }

    /**
     * 逻辑删除
     */
    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    //@CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE,allEntries=true,condition="#sysPermission.menuType==2")
    public void deletePermissionLogical(String id) throws JeecgBootException {
        SysPermission sysPermission = this.getById(id);
        if (sysPermission == null) {
            throw new JeecgBootException("未找到菜单信息");
        }
        String pid = sysPermission.getParentId();
        int count = this.count(new QueryWrapper<SysPermission>().lambda().eq(SysPermission::getParentId, pid));
        if (count == 1) {
            //若父节点无其他子节点，则该父节点是叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 1);
        }
        sysPermission.setDelFlag(1);
        this.updateById(sysPermission);
    }

    @Override
    @CacheEvict(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE, allEntries = true)
    public void addPermission(SysPermission sysPermission) throws JeecgBootException {
        //----------------------------------------------------------------------
        //判断是否是一级菜单，是的话清空父菜单
//        if (CommonConstant.MENU_TYPE_0.equals(sysPermission.getMenuType())) {
//            sysPermission.setParentId(null);
//        }
        //----------------------------------------------------------------------
        String pid = sysPermission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 0);
        }
        sysPermission.setDelFlag(0);
        sysPermission.setLeaf(true);
        this.save(sysPermission);
    }

    @Override
    @Transactional
    public boolean create(String applicationId, SysPermission sysPermission) {
        if (applicationId != null) {
            boolean applicationExists = sysApplicationMapper
                    .selectCount(new QueryWrapper<SysApplication>().lambda().eq(SysApplication::getId, applicationId)) == 1;
            if (!applicationExists) {
                log.warn("SysPermissionCreateVo database validation failed! reason: application doesn't exist");
                return false;
            }
        }


        //parent_id 若存在则必须存在于数据库中
        if (sysPermission.getParentId() != null) {
            int parentCount = sysPermissionMapper.selectCount(
                    new QueryWrapper<SysPermission>()
                            .lambda()
                            .eq(SysPermission::getId, sysPermission.getParentId()));
            if (parentCount == 0) {
                log.warn("SysPermissionCreateVo database validation failed! reason: parent permission doesn't exist");
                return false;
            }
        }
        //若parent原本无孩子，则将parent变成非叶节点
        if (sysPermission.getParentId() != null) {
            int childrenSize = sysPermissionMapper.selectCount(
                    new QueryWrapper<SysPermission>()
                            .lambda()
                            .eq(SysPermission::getParentId, sysPermission.getParentId()));
            if (childrenSize == 0)
                sysPermissionMapper.setMenuLeaf(sysPermission.getParentId(), 0);
        }
        sysPermissionMapper.insert(sysPermission);
        if (applicationId != null) {
            SysApplicationPermission sysApplicationPermission = new SysApplicationPermission();
            sysApplicationPermission.setPermissionId(sysPermission.getId());
            sysApplicationPermission.setApplicationId(applicationId);
            sysApplicationPermissionMapper.insert(sysApplicationPermission);
        }
        return true;
    }


    @Override
    @Transactional
    @SuppressWarnings("UnnecessaryLocalVariable")
    public boolean edit(String applicationId, SysPermission sysPermission) {
        SysPermission newSelf = sysPermission;
        //id 必须存在于数据库中
        SysPermission oldSelf = sysPermissionMapper.selectOne(
                new QueryWrapper<SysPermission>()
                        .lambda()
                        .eq(SysPermission::getId, newSelf.getId()));
        if (oldSelf == null) {
            log.warn("SysPermissionUpdateVo database validation failed! reason: permission doesn't exist");
            return false;
        }
        if (oldSelf.getCreateWays() != newSelf.getCreateWays()) {
            log.warn("SysPermissionUpdateVo database validation failed! reason: permission's createWays property can't be updated'");
            return false;
        }
        if (!Objects.equals(oldSelf.getMenuType(), newSelf.getMenuType())) {
            log.warn("SysPermissionUpdateVo database validation failed! reason: permission's menuType property can't be updated'");
            return false;
        }
        if (oldSelf.isLeaf() != newSelf.isLeaf()) {
            log.warn("SysPermissionUpdateVo database validation failed! reason: permission's leaf property doesn't match database'");
            return false;
        }
        if (applicationId != null) {
            boolean applicationExists = sysApplicationMapper
                    .selectCount(new QueryWrapper<SysApplication>().lambda().eq(SysApplication::getId, applicationId)) == 1;
            if (!applicationExists) {
                log.warn("SysPermissionUpdateVo database validation failed! reason: application doesn't exist");
                return false;
            }
            String rootId = sysPermissionMapper.findRoot(newSelf.getId()).getId();
            //sys_application_permission 只存根节点菜单，所以要找到对应根节点，再进行比较
            boolean applicationPermissionExists = sysApplicationPermissionMapper.selectCount(new QueryWrapper<SysApplicationPermission>().lambda().eq(SysApplicationPermission::getApplicationId, applicationId)
                    .eq(SysApplicationPermission::getPermissionId, rootId)) == 1;
            if (!applicationPermissionExists) {
                log.warn("SysPermissionUpdateVo database validation failed! reason: application permission doesn't exist");
                return false;
            }
        }

        //parent_id 若存在则必须存在于数据库中
        if (newSelf.getParentId() != null) {
            int parentCount = sysPermissionMapper.selectCount(
                    new QueryWrapper<SysPermission>()
                            .lambda()
                            .eq(SysPermission::getId, newSelf.getParentId()));
            if (parentCount == 0) {
                log.warn("SysPermissionUpdateVo database validation failed! reason: parent permission doesn't exist");
                return false;
            }
        }


        //若改变父节点
        if (!Objects.equals(oldSelf.getParentId(), newSelf.getParentId())) {
            //for parent
            if (oldSelf.getParentId() != null) {
                //去掉节点后，原父亲无孩子，则原父亲is_leaf = true
                int oldChildrenSize = sysPermissionMapper.selectCount(
                        new QueryWrapper<SysPermission>()
                                .lambda()
                                .eq(SysPermission::getParentId, oldSelf.getParentId()));
                if (oldChildrenSize == 1)
                    sysPermissionMapper.setMenuLeaf(oldSelf.getParentId(), 1);
            }
            if (newSelf.getParentId() != null) {
                //新父亲原本无孩子，则新父亲is_leaf = false
                int newChildrenSize = sysPermissionMapper.selectCount(
                        new QueryWrapper<SysPermission>()
                                .lambda()
                                .eq(SysPermission::getParentId, newSelf.getParentId()));
                if (newChildrenSize == 0)
                    sysPermissionMapper.setMenuLeaf(newSelf.getParentId(), 0);

            }
        }
        sysPermissionMapper.fullUpdateById(sysPermission);
        return true;

    }

    @Override
    @Transactional
    public boolean deletePermissionByIds(String ids) {
        if ("".equals(ids)) {
            return true;
        }
        List<String> sysPermissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        for (String sysPermissionId : sysPermissionIds) {
            this.deletePermission(sysPermissionId);
        }
        return true;
    }

    @Override
    public List<SysPermission> queryByUser(String username) {
        return this.sysPermissionMapper.queryByUser(username);
    }

    @Override
    public List<SysApplication> queryByUserApplication(String username) {
        return this.sysPermissionMapper.queryByUserApplication(username);
    }

    /**
     * 根据permissionId删除其关联的SysPermissionDataRule表中的数据
     */
    @Override
    public void deletePermRuleByPermId(String id) {
        LambdaQueryWrapper<SysPermissionDataRule> query = new LambdaQueryWrapper<>();
        query.eq(SysPermissionDataRule::getPermissionId, id);
        int countValue = this.permissionDataRuleService.count(query);
        if (countValue > 0) {
            this.permissionDataRuleService.remove(query);
        }
    }

    /**
     * 获取模糊匹配规则的数据权限URL
     */
    @Override
    @Cacheable(value = CacheConstant.SYS_DATA_PERMISSIONS_CACHE)
    public List<String> queryPermissionUrlWithStar() {
        return this.baseMapper.queryPermissionUrlWithStar();
    }

    @Override
    public boolean hasPermission(String username, SysPermission sysPermission) {
        int count = baseMapper.queryCountByUsername(username, sysPermission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean hasPermission(String username, String url) {
        SysPermission sysPermission = new SysPermission();
        sysPermission.setUrl(url);
        int count = baseMapper.queryCountByUsername(username, sysPermission);
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<SysPermissionTree> findAllTrees() {
        return buildTree(sysPermissionMapper.findAll());
    }

    public List<SysPermissionTree> buildTree(List<SysPermission> sysPermissions) {
        List<SysPermissionTree> sysPermissionTrees = sysPermissions
                .stream()
                .map(SysPermissionTree::new)
                .collect(Collectors.toList());
        HashMap<String, Integer> idMappings = new HashMap<>(sysPermissionTrees.size());
        for (int i = 0; i < sysPermissionTrees.size(); i++) {
            idMappings.put(sysPermissionTrees.get(i).getId(), i);
        }
        List<SysPermissionTree> root = new ArrayList<>();
        for (SysPermissionTree sysPermissionTree : sysPermissionTrees) {
            if (sysPermissionTree.getParentId() == null || sysPermissionTree.getParentId().equals("")) {
                root.add(sysPermissionTree);
                continue;
            }
            Integer index = idMappings.get(sysPermissionTree.getParentId());
            if (index != null) {
                SysPermissionTree parent = sysPermissionTrees.get(index);
                parent.getChildren().add(sysPermissionTree);
            }
        }
        return root;
    }

    @Override
    public Map<String, SysApplicationWithPermissionId> findApplicationByPid(List<String> pids) {
        return sysPermissionMapper.findApplicationsByPermissionId(pids);
    }

    @Override
    public List<SysPermissionTree> queryTreeByApplicationId(String applicationId) {
        if (sysApplicationMapper.selectById(applicationId) == null) {
            return null;
        }
        //1 查询当前登陆人拥有的菜单
        //1.1获取当前登录人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        List<SysPermission> sysPermissions = sysPermissionMapper.queryPermissionsByUserId(user.getId());
        //1.3根据应用id应用菜单表查询所有一级菜单id
        Set<String> sysPermissionSets = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId)
                .collect(Collectors.toSet());
        List<SysPermission> sysPermissionList = sysPermissionMapper.findDescendantsWithSelf(new ArrayList<String>(sysPermissionSets));
        sysPermissionList.retainAll(sysPermissions);
        //3 讲菜单集合封装成树返回
        List<SysPermissionTree> sysPermissionTrees = buildTree(new ArrayList<SysPermission>(sysPermissionList));
        return sysPermissionTrees;
    }

    @Override
    public List<SysPermission> queryUserOfApplicationPermission(String userId, String applicationId) {
        if (sysUserMapper.selectById(userId) == null || sysApplicationMapper.selectById(applicationId) == null) {
            return null;
        }
        List<SysPermission> sysPermissions = sysPermissionMapper.queryPermissionsByUserId(userId);
        //根据应用id应用菜单表查询所有一级菜单id
        Set<String> sysPermissionSets = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId)
                .collect(Collectors.toSet());
        List<SysPermission> sysPermissionList = sysPermissionMapper.findDescendantsWithSelf(new ArrayList<String>(sysPermissionSets));
        sysPermissionList.retainAll(sysPermissions);
        //3 菜单集合返回
        return new ArrayList<SysPermission>(sysPermissionList);
    }

    @Override
    public List<SysPermission> queryRoleOfApplicationPermission(String roleId, String applicationId) {
        //接口测试必要 判断roleId和applicationId是否正确
        if (sysRoleMapper.selectById(roleId) == null || sysApplicationMapper.selectById(applicationId) == null) {
            return null;
        }
        //1.根据角色id查询其拥有的应用菜单 list1
        List<SysPermission> list1 = sysPermissionMapper.
                findSysPermissionByRoleIdAndCreateWays(roleId, PermissionProperties.CREATE_WAYS_APPLICATION);
        //2.查询应用下拥有的应用菜单 list2
        //2.1查询应用下的一级菜单集合
        List<String> ids = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId)
                .collect(Collectors.toList());
        //2.2根据一级菜单集合查询其拥有的所有菜单
        List<SysPermission> list2 = sysPermissionMapper.findDescendantsWithSelf(ids);
        //3.list1与list2的交集
        if (list2 == null) {
            list2 = new ArrayList<SysPermission>();
        }
        list2.retainAll(list1);
        return list2;
    }

    @Override
    public List<SysPermission> queryDepartOfApplicationPermission(String departId, String applicationId) {
        //接口测试必要 判断roleId和applicationId是否正确
        if (sysDepartMapper.selectById(departId) == null || sysApplicationMapper.selectById(applicationId) == null) {
            return null;
        }
        //1.根据部门id查询其拥有的应用菜单 list1
        List<SysPermission> list1 = sysPermissionMapper.
                findSysPermissionByDepartIdAndCreateWays(departId, PermissionProperties.CREATE_WAYS_APPLICATION);
        //2.查询应用下拥有的应用菜单 list2
        //2.1查询应用下的一级菜单集合
        List<String> ids = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId)
                .collect(Collectors.toList());
        //2.2根据一级菜单集合查询其拥有的所有菜单
        List<SysPermission> list2 = sysPermissionMapper.findDescendantsWithSelf(ids);
        //3.list1与list2的交集
        if (list2 == null) {
            list2 = new ArrayList<SysPermission>();
        }
        list2.retainAll(list1);
        return list2;
    }

    /**
     * 获取顶级菜单
     *
     * @param sysPermission
     * @return
     */
    public SysPermission getSuperPermission(SysPermission sysPermission) {
        try {
            if (sysPermission.getParentId() == null) {
                return sysPermission;
            } else {
                SysPermission sysPermission1 = sysPermissionMapper.selectById(sysPermission.getParentId());
                return getSuperPermission(sysPermission1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SysApplication findApplicationByPid(String pid) {
        return sysPermissionMapper.findApplicationByPermissionId(pid);
    }

}
