package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.ImportExcelUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.mapper.FormMapper;
import org.jeecg.modules.form.mapper.FormSysPermissionMapper;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import org.jeecg.modules.onlgraph.mapper.GraphBigscreenCardMapper;
import org.jeecg.modules.onlgraph.mapper.GraphBigscreenMapper;
import org.jeecg.modules.onlgraph.mapper.OnlCfgraphFieldMapper;
import org.jeecg.modules.onlgraph.mapper.OnlCfgraphHeadMapper;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.ISysApplicationService;
import org.jeecg.modules.system.util.RandomCodeUtil;
import org.jeecg.modules.system.vo.SysApplicationDepartRoleVO;
import org.jeecg.modules.workflow.service.WfDefinitionService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 应用表 服务实现类
 * </p>
 *
 * @Author Zhouhonghan
 */
@Service
public class SysApplicationServiceImpl extends ServiceImpl<SysApplicationMapper, SysApplication> implements ISysApplicationService {

    @Autowired
    SysApplicationMapper sysApplicationMapper;

    @Autowired
    SysRoleApplicationMapper sysRoleApplicationMapper;

    @Autowired
    SysApplicationPermissionMapper sysApplicationPermissionMapper;

    @Autowired
    SysPermissionMapper sysPermissionMapper;

    //图表组四个
    @Autowired
    GraphBigscreenMapper graphBigscreenMapper;

    @Autowired
    GraphBigscreenCardMapper graphBigscreenCardMapper;

    @Autowired
    OnlCfgraphFieldMapper onlCfgraphFieldMapper;

    @Autowired
    OnlCfgraphHeadMapper onlCfgraphHeadMapper;

    //表单组两个
    @Autowired
    FormSysPermissionMapper formSysPermissionMapper;

    @Autowired
    FormMapper formMapper;

    @Autowired
    SysUserApproleApplicationMapper sysUserAppRoleApplicationMapper;

    @Autowired
    SysRoleMapper sysRoleMapper;

    @Autowired
    SysUserMapper sysUserMapper;

    @Autowired
    SysDepartApproleApplicationMapper sysDepartAppRoleApplicationMapper;

    @Autowired
    SysRoleApproleApplicationMapper sysRoleAppRoleApplicationMapper;

    @Autowired
    SysDepartMapper sysDepartMapper;

    @Autowired
    SysUserRoleMapper sysUserRoleMapper;

    @Autowired
    SysUserDepartMapper sysUserDepartMapper;

    @Autowired
    SysAppRoleMapper sysAppRoleMapper;

    @Autowired
    SysRolePermissionMapper sysRolePermissionMapper;

    @Autowired
    SysDepartPermissionMapper sysDepartPermissionMapper;

    @Autowired
    WfDefinitionService wfDefinitionService;

    @Override
    public Result importExcelCheckApplicationCode(MultipartFile file, ImportParams params) throws Exception {
        List<Object> listSysApplications = ExcelImportUtil.importExcel(file.getInputStream(),
                SysApplication.class, params);
        int totalCount = listSysApplications.size();
        List<String> errorStrs = new ArrayList<>();

        /**
         // 去除 listSysApplications 中重复的数据
         for (int i = 0; i < listSysApplications.size(); i++) {
         String roleCodeI =((SysRole)listSysApplications.get(i)).getRoleCode();
         for (int j = i + 1; j < listSysApplications.size(); j++) {
         String roleCodeJ =((SysRole)listSysApplications.get(j)).getRoleCode();
         // 发现重复数据
         if (roleCodeI.equals(roleCodeJ)) {
         errorStrs.add("第 " + (j + 1) + " 行的 roleCode 值：" + roleCodeI + " 已存在，忽略导入");
         listSysApplications.remove(j);
         break;
         }
         }
         }
         */

        // 使用Hashset筛选重复数据,删除ApplicationCode为空的数据
        List<SysApplication> SysApplications = new ArrayList<SysApplication>();
        HashSet<String> set = new HashSet<>();
        for (int i = 0; i < listSysApplications.size(); i++) {
            // 获得当前行的ApplicationId
            String applicationId = ((SysApplication) listSysApplications.get(i)).getApplicationCode();
            if (applicationId == null) {
                SysApplications.add((SysApplication) listSysApplications.get(i));
                continue;
            }
            // 发现重复数据
            if (!set.add(applicationId)) {
                errorStrs.add("第 " + (i) + " 行的 roleCode 值：" + applicationId + " 已存在，忽略导入");
                SysApplications.add((SysApplication) listSysApplications.get(i));
            }
        }
        listSysApplications.removeAll(SysApplications);


        // 去掉 sql 中的重复数据
        Integer errorLines = 0;
        Integer successLines = 0;
        List<String> list = ImportExcelUtil.importDateSave(listSysApplications,
                ISysApplicationService.class, errorStrs, CommonConstant.SQL_INDEX_UNIQ_APPLICATION_CODE);
        errorLines += list.size();
        successLines += (listSysApplications.size() - errorLines);
        return ImportExcelUtil.imporReturnRes(errorLines, successLines, list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteApplication(String applicationId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("application_id", applicationId);
        //删除应用与角色的关系
        sysRoleApplicationMapper.deleteByMap(map);
        //删除应用与部门的关系
        //删除应用 应用角色 角色关系表
        sysRoleAppRoleApplicationMapper.deleteByMap(map);
        //删除应用 应用角色 用户关系表
        sysUserAppRoleApplicationMapper.deleteByMap(map);
        //删除应用 应用角色 部门关系表
        sysDepartAppRoleApplicationMapper.deleteByMap(map);
        //解除应用与菜单关系
        sysApplicationPermissionMapper.deleteByMap(map);
        //删除应用
        this.removeById(applicationId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteBatchApplication(List<String> sysApplicationIds) {
        for (String sysApplicationId : sysApplicationIds) {
            this.deleteApplication(sysApplicationId);
        }
        return true;
    }

    @Override
    public boolean queryCodeOnly(String applicationCode) {
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_code", applicationCode);
        if (oConvertUtils.isEmpty(this.getOne(queryWrapper))) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional
    public SysApplication copyApplicationById(String applicationId, Integer template) {
        //复制id，替换id即可,applicationCode要重新生成
        SysApplication application = this.getById(applicationId);
        String value = application.getApplicationName() + "-copy";
        List<SysApplication> sysApplications = sysApplicationMapper.selectList(new LambdaQueryWrapper<SysApplication>()
                .like(SysApplication::getApplicationName, value));
        //application名字的决定
        int count = 1;
        if (sysApplications.size() != 0) {
            List<Integer> numbers = sysApplications.stream()
                    .map(SysApplication::getApplicationName)
                    .map(s -> s.substring(s.lastIndexOf("-copy") + 5))
                    .map(Integer::parseInt)
                    .sorted().collect(Collectors.toList());
            for (int number : numbers) {
                if (number != count) {
                    break;
                }
                count++;
            }
        }
        String newName = value + String.valueOf(count);
        application.setApplicationName(newName);

        //id 自动生成 这里设置成null
        application.setId(null);
        //唯一验证码 不能重复 自动生成
        application.setApplicationCode(getApplicationCode());
        //创建时间
        application.setCreateTime(new Date());
        //获取修改人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //更新修改人
        application.setCreateBy(user.getUsername());
        //更新人
        application.setUpdateBy(null);
        //更新时间
        application.setUpdateTime(null);
        //状态 默认为1 未发布
        application.setStatus(1);
        //模板
        application.setTemplate(template);
        this.save(application);

        //根据应用id复制中间表
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionMapper
                .selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId));
        //复制菜单
        //所有复制后的菜单
        List<SysPermission> sysPermissions = new ArrayList<SysPermission>();

        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            String permissionId = sysApplicationPermission.getPermissionId();
            sysPermissions.addAll(copyMenuByIdWithFeature(permissionId, null));
        }
        //一级菜单Id
        Set<SysPermission> sysOnePermissions = new HashSet<SysPermission>();
        for (SysPermission sysPermission : sysPermissions) {
            if (sysPermission.getParentId() == null) {
                sysOnePermissions.add(sysPermission);
            }
        }

        //复制中间表
        for (SysPermission sysPermission : sysOnePermissions) {
            sysApplicationPermissionMapper.insert(new SysApplicationPermission(application.getId(), sysPermission.getId()));
        }
        return application;
    }

    @Override
    @Transactional
    public SysApplication saveTemplate(SysApplication sysApplication) {
        String applicationId = sysApplication.getId();
        //id置空
        sysApplication.setId(null);
        //code重新生成
        sysApplication.setApplicationCode(getApplicationCode());
        //创建人 创建时间 更新人 更新时间
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysApplication.setUpdateBy(null);
        sysApplication.setUpdateTime(null);
        sysApplication.setCreateBy(user.getUsername());
        sysApplication.setCreateTime(new Date());
        //修改为模板
        sysApplication.setTemplate(CommonConstant.APPLICATION_TEMPLATE_1);
        //保存模板
        this.save(sysApplication);
        //根据应用id复制中间表
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionMapper
                .selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                        .eq(SysApplicationPermission::getApplicationId, applicationId));
        //复制菜单
        //所有复制后的菜单
        List<SysPermission> sysPermissions = new ArrayList<SysPermission>();

        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            String permissionId = sysApplicationPermission.getPermissionId();
            sysPermissions.addAll(copyMenuByIdWithOutFeature(permissionId, null));
        }
        //一级菜单Id
        Set<SysPermission> sysOnePermissions = new HashSet<SysPermission>();
        for (SysPermission sysPermission : sysPermissions) {
            if (sysPermission.getParentId() == null) {
                sysOnePermissions.add(sysPermission);
            }
        }

        //复制中间表
        for (SysPermission sysPermission : sysOnePermissions) {
            sysApplicationPermissionMapper.insert(new SysApplicationPermission(sysApplication.getId(), sysPermission.getId()));
        }
        return sysApplication;
    }

    @Override
    public SysUser selectApplicationCreator(String applicationId) {
        SysApplication sysApplication = sysApplicationMapper.selectById(applicationId);
        List<SysUser> sysUsers = sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, sysApplication.getCreateBy()));
//        if (sysUsers == null || sysUsers.size() > 1){
//            return null;
//        }
        return sysUsers.get(0);
    }

    @Override
    @Transactional
    public Boolean updateApplicationPermissionOfRole(String applicationId, String roleId, List<String> newSysPermissionIds) {
        //接口测试必要 判断roleId和applicationId是否正确
        if (sysRoleMapper.selectById(roleId) == null || sysApplicationMapper.selectById(applicationId) == null) {
            return false;
        }
        //1.查询当前角色在应用下拥有的菜单
        //1.1根据applicationId查询应用下的菜单 sysPermissionIds1
        List<String> ids = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId).collect(Collectors.toList());
        List<String> sysPermissionIds1 = sysPermissionMapper.findDescendantsWithSelf(ids)
                .stream()
                .map(SysPermission::getId)
                .collect(Collectors.toList());
        //1.2根据roleId查询角色下的菜单 sysPermissionIds2
        List<String> sysPermissionIds2 = sysRolePermissionMapper.selectList(new LambdaQueryWrapper<SysRolePermission>()
                .eq(SysRolePermission::getRoleId, roleId))
                .stream()
                .map(SysRolePermission::getPermissionId)
                .collect(Collectors.toList());
        //传来的菜单不对 sysPermissionIds1是否包含id 且 传来的id是否为“”
        if (!sysPermissionIds1.containsAll(newSysPermissionIds)) {
            return false;
        }
        sysPermissionIds1.retainAll(sysPermissionIds2);
        List<String> oldPermissionIds = sysPermissionIds1;
        //2.根据传来的菜单集合A和原有的菜单集合B进行集合操作，A-B添加 B-A删除
        List<String> temps = new ArrayList<String>();
        temps.addAll(newSysPermissionIds);
        newSysPermissionIds.removeAll(oldPermissionIds);
        oldPermissionIds.removeAll(temps);

        List<SysRolePermission> sysRolePermissions = new ArrayList<SysRolePermission>();
        for (String newPermissionId : newSysPermissionIds) {
            if (!"".equals(newPermissionId)) {
                sysRolePermissions.add(new SysRolePermission(roleId, newPermissionId));
            }
        }
        //3.批量添加和删除
        if (sysRolePermissions.size() > 0) {
            sysRolePermissionMapper.insertBatch(sysRolePermissions);
        }
        for (String oldPermissionId : oldPermissionIds) {
            sysRolePermissionMapper.delete(new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getRoleId, roleId)
                    .eq(SysRolePermission::getPermissionId, oldPermissionId));
        }
        return true;
    }

    @Override
    @Transactional
    public Boolean updateApplicationPermissionOfDepart(String applicationId, String departId, List<String> newSysPermissionIds) {
        //接口测试必要 判断roleId和applicationId是否正确
        if (sysDepartMapper.selectById(departId) == null || sysApplicationMapper.selectById(applicationId) == null) {
            return false;
        }
        //1.查询当前部门在应用下拥有的菜单
        List<String> ids = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId).collect(Collectors.toList());
        List<String> sysPermissionIds1 = sysPermissionMapper.findDescendantsWithSelf(ids)
                .stream()
                .map(SysPermission::getId)
                .collect(Collectors.toList());
        List<String> sysPermissionIds2 = sysDepartPermissionMapper.selectList(new LambdaQueryWrapper<SysDepartPermission>()
                .eq(SysDepartPermission::getDepartId, departId))
                .stream()
                .map(SysDepartPermission::getPermissionId)
                .collect(Collectors.toList());
        //传来的菜单不对
        if (!sysPermissionIds1.containsAll(newSysPermissionIds)) {
            return false;
        }
        sysPermissionIds1.retainAll(sysPermissionIds2);
        List<String> oldPermissionIds = sysPermissionIds1;
        //2.根据传来的菜单集合A和原有的菜单集合B进行集合操作，A-B添加 B-A删除
        List<String> temps = new ArrayList<String>();
        temps.addAll(newSysPermissionIds);
        newSysPermissionIds.removeAll(oldPermissionIds);
        oldPermissionIds.removeAll(temps);

        List<SysDepartPermission> sysDepartPermissions = new ArrayList<SysDepartPermission>();
        for (String newPermissionId : newSysPermissionIds) {
            if (!"".equals(newPermissionId)) {
                sysDepartPermissions.add(new SysDepartPermission(departId, newPermissionId));
            }
        }
        //3.批量添加和删除s
        if (sysDepartPermissions.size() > 0) {
            sysDepartPermissionMapper.insertBatch(sysDepartPermissions);
        }
        for (String oldPermissionId : oldPermissionIds) {
            sysDepartPermissionMapper.delete(new LambdaQueryWrapper<SysDepartPermission>()
                    .eq(SysDepartPermission::getDepartId, departId)
                    .eq(SysDepartPermission::getPermissionId, oldPermissionId));
        }
        return true;
    }

    @Override
    @Transactional
    public void deletePermissionById(String applicationId, List<String> permissionIds) {
        LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();
        for (String permissionId : permissionIds) {
            queryWrapper.eq(SysApplicationPermission::getApplicationId, applicationId)
                    .eq(SysApplicationPermission::getPermissionId, permissionId);
            sysApplicationPermissionMapper.delete(queryWrapper);
            queryWrapper.clear();
        }
    }

    /**
     * 复制应用不带下面的功能
     *
     * @param permisssionId
     * @param parentId
     * @return
     */
    public List<SysPermission> copyMenuByIdWithOutFeature(String permisssionId, String parentId) {
        List<SysPermission> sysPermissions = new ArrayList<>();
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>();
        queryWrapper.eq(SysPermission::getId, permisssionId);
        SysPermission sysPermission = sysPermissionMapper.selectOne(queryWrapper);
        //设置parentId
        sysPermission.setParentId(parentId);
        //设置createTime
        sysPermission.setCreateTime(new Date());
        //设置createBy
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysPermission.setCreateBy(user.getId());
        //设置updateId
        sysPermission.setUpdateBy(null);
        //设置updateBy
        sysPermission.setUpdateTime(null);
        //复制模板,url先置空
        sysPermission.setUrl(null);
        //id置空 insert执行后会刷新id
        sysPermission.setId(null);
        sysPermissionMapper.insert(sysPermission);
        //查询是否为叶子应用
        queryWrapper.clear();
        queryWrapper.eq(SysPermission::getParentId, permisssionId);
        List<SysPermission> sysPermissions1 = sysPermissionMapper.selectList(queryWrapper);
        sysPermissions.add(sysPermission);
        if (sysPermissions1.size() > 0) {
            for (SysPermission sysPermission1 : sysPermissions1) {
                sysPermissions.addAll(copyMenuByIdWithOutFeature(sysPermission1.getId(), sysPermission.getId()));
            }
        }
        sysPermissionMapper.updateById(sysPermission);
        return sysPermissions;
    }

    /**
     * 根据id复制菜单 以及下面的功能
     *
     * @param permisssionId
     * @param parentId
     * @return
     */
    public List<SysPermission> copyMenuByIdWithFeature(String permisssionId, String parentId) {
        List<SysPermission> sysPermissions = new ArrayList<>();
        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>();
        queryWrapper.eq(SysPermission::getId, permisssionId);
        SysPermission sysPermission = sysPermissionMapper.selectOne(queryWrapper);
        //设置parentId
        sysPermission.setParentId(parentId);
        //设置createTime
        sysPermission.setCreateTime(new Date());
        //设置createBy
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        sysPermission.setCreateBy(user.getId());
        //设置updateId
        sysPermission.setUpdateBy(null);
        //设置updateBy
        sysPermission.setUpdateTime(null);
        //id置空 insert执行后会刷新id
        sysPermission.setId(null);
        sysPermissionMapper.insert(sysPermission);
        //查询是否为叶子应用
        queryWrapper.clear();
        queryWrapper.eq(SysPermission::getParentId, permisssionId);
        List<SysPermission> sysPermissions1 = sysPermissionMapper.selectList(queryWrapper);

        //解析url
        String url = sysPermission.getUrl();
        if (url == null) {
            url = "";
        }
        //url前缀
        String prefix = url.substring(0, url.lastIndexOf("/") + 1);
        //后缀id
        String id = url.substring(url.lastIndexOf("/") + 1);
        String newUrl = sysPermission.getUrl();
        //判断菜单的url前缀来自哪里，并复制
        //来自图表 普通图表
        if ("/online/graphreport/".equals(prefix)) {
            //复制普通图表方法 返回url
            newUrl = copyGraphBigScreen(id);
            //来自图表 大屏图表
        } else if ("/online/graphBigscreen/".equals(prefix)) {
            //复制大屏图表方法
            newUrl = copyGraphOnl(id);
            //复制表单
        } else if ("/form/design/manage/".equals(prefix)) {
            //复制表单方法
            newUrl = copyForm(id, sysPermission.getId());
            // /work-flow/manage/Process_1625796925967:1:b4f36d75-e05b-11eb-8cd3-a689d1c0f08e/Process_1625796925967 => /work-flow/manage/
        } else if ("/work-flow/manage/".equals(url.substring(0, url.indexOf(":")).substring(0, url.substring(0, url.indexOf(":")).lastIndexOf("/") + 1))) {
            //复制图表组
            newUrl = copyWorkFlow(url.substring(url.lastIndexOf("_") + 1));
        } else {
            //分叶子结点 无功能
        }
        //复制失败
        if (newUrl == null) {
            newUrl = sysPermission.getUrl();
        }
        sysPermission.setUrl(newUrl);
        //复制菜单
        sysPermissionMapper.updateById(sysPermission);
        sysPermissions.add(sysPermission);

        if (sysPermissions1.size() > 0) {
            for (SysPermission sysPermission1 : sysPermissions1) {
                sysPermissions.addAll(copyMenuByIdWithFeature(sysPermission1.getId(), sysPermission.getId()));
            }
        }
        return sysPermissions;
    }

    /**
     * 复制图表组 大屏
     *
     * @param id
     * @return
     */
    public String copyGraphBigScreen(String id) {
        //获取当前操作人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //大屏图表复制
        //根据id查询出对应的图表 复制未逻辑删除且已配置菜单的图表
        GraphBigscreenDO graphBigscreenDO = graphBigscreenMapper.selectOne(
                new LambdaQueryWrapper<GraphBigscreenDO>()
                        .eq(GraphBigscreenDO::getDelFlag, 0)
                        .eq(GraphBigscreenDO::getPermissionStatus, 1)
                        .eq(GraphBigscreenDO::getId, id));
        //没有查询到 无法进行下一步 退出
        if (oConvertUtils.isEmpty(graphBigscreenDO)) {
            return null;
        }
        //id置空
        graphBigscreenDO.setId(null);
        //从表复制人
        graphBigscreenDO.setCreateBy(user.getUsername());
        //从表复制时间
        graphBigscreenDO.setCreateTime(new Date());
        //从表更新人置空
        graphBigscreenDO.setUpdateBy(null);
        //从表更新时间置空
        graphBigscreenDO.setUpdateTime(null);
        //配置大屏编码
        graphBigscreenDO.setGraphBigscreenCode(UUID.randomUUID().toString());
        //插入图表数据
        graphBigscreenMapper.insert(graphBigscreenDO);
        String newId = graphBigscreenDO.getId();
        //复制从表信息
        List<GraphBigscreenCardDO> graphBigscreenCardDOS = graphBigscreenCardMapper.selectList(
                new LambdaQueryWrapper<GraphBigscreenCardDO>()
                        .eq(GraphBigscreenCardDO::getBiggreenId, id));
        for (GraphBigscreenCardDO graphBigscreenCardDO : graphBigscreenCardDOS) {
            //从表id 置空
            graphBigscreenCardDO.setId(null);
            //从表复制人
            graphBigscreenCardDO.setCreateBy(user.getUsername());
            //从表复制时间
            graphBigscreenCardDO.setCreateTime(new Date());
            //从表更新人置空
            graphBigscreenCardDO.setUpdateBy(null);
            //从表更新时间置空
            graphBigscreenCardDO.setUpdateTime(null);
            //从表设置主表信息
            graphBigscreenCardDO.setBiggreenId(newId);
            //插入从表信息
            graphBigscreenCardMapper.insert(graphBigscreenCardDO);
        }
        return "/online/graphBigscreen/" + newId;
    }

    /**
     * 复制图表组 普通图表
     *
     * @param id
     * @return
     */
    public String copyGraphOnl(String id) {
        //获取当前操作人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据id查询出对应的图表 复制未逻辑删除且已配置菜单的图表
        OnlCfgraphHeadDO onlCfgraphHeadDO = onlCfgraphHeadMapper.selectOne(
                new LambdaQueryWrapper<OnlCfgraphHeadDO>()
                        .eq(OnlCfgraphHeadDO::getDelFlag, 0)
                        .eq(OnlCfgraphHeadDO::getGraphPermissionStatus, 1)
                        .eq(OnlCfgraphHeadDO::getId, id));
        //没有查询到 无法进行下一步 退出
        if (oConvertUtils.isEmpty(onlCfgraphHeadDO)) {
            return null;
        }
        //id置空
        onlCfgraphHeadDO.setId(null);
        //创建人 即复制人
        onlCfgraphHeadDO.setCreateBy(user.getUsername());
        //创建时间 复制时间
        onlCfgraphHeadDO.setCreateTime(new Date());
        //更新人置空
        onlCfgraphHeadDO.setUpdateBy(null);
        //更新时间置空
        onlCfgraphHeadDO.setUpdateTime(null);
        //图表编码 暂时uuid代替
        onlCfgraphHeadDO.setGraphCode(UUID.randomUUID().toString());
        //插入一条新数据
        onlCfgraphHeadMapper.insert(onlCfgraphHeadDO);
        String newId = onlCfgraphHeadDO.getId();
        //复制从表信息
        List<OnlCfgraphFieldDO> onlCfgraphFieldDOS = onlCfgraphFieldMapper.selectList(new LambdaQueryWrapper<OnlCfgraphFieldDO>()
                .eq(OnlCfgraphFieldDO::getOnlCfgraphId, id));
        for (OnlCfgraphFieldDO onlCfgraphFieldDO : onlCfgraphFieldDOS) {
            //从表id 置空
            onlCfgraphFieldDO.setId(null);
            //从表复制人
            onlCfgraphFieldDO.setCreateBy(user.getUsername());
            //从表复制时间
            onlCfgraphFieldDO.setCreateTime(new Date());
            //从表更新人置空
            onlCfgraphFieldDO.setUpdateBy(null);
            //从表更新时间置空
            onlCfgraphFieldDO.setUpdateTime(null);
            //从表设置主表信息
            onlCfgraphFieldDO.setOnlCfgraphId(newId);
            //插入从表信息
            onlCfgraphFieldMapper.insert(onlCfgraphFieldDO);
        }
        return "/online/graphreport/" + newId;
    }

    /**
     * 复制表单
     *
     * @param id
     * @return
     */
    public String copyForm(String id, String newPermissionId) {
        //获取当前操作人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //根据id查询表单
        FormDO formDO = formMapper.selectById(id);
        //没有查询到 无法进行下一步 退出
        if (oConvertUtils.isEmpty(formDO)) {
            return null;
        }
        //id置空
        formDO.setId(null);
        //随机生成
        formDO.setCode(UUID.randomUUID().toString());
        //从表复制人
        formDO.setCreateBy(user.getUsername());
        //从表复制时间
        formDO.setCreateTime(new Date());
        //从表更新人置空
        formDO.setUpdateBy(null);
        //从表更新时间置空
        formDO.setUpdateTime(null);
        //插入数据
        formMapper.insert(formDO);
        //获取新表单id
        String newFormId = formDO.getId();
        //复制中间表
        FormSysPermissionDO formSysPermissionDO = new FormSysPermissionDO();
        formSysPermissionDO.setFormId(newFormId);
        formSysPermissionDO.setSysPermissionId(newPermissionId);
        formSysPermissionDO.setCreateBy(user.getId());
        formSysPermissionDO.setCreateTime(new Date());
        formSysPermissionMapper.insert(formSysPermissionDO);
        return "/form/design/manage/" + newFormId;
    }

    /**
     * 复制工作流 /work-flow/manage/Process_1625796925967:1:b4f36d75-e05b-11eb-8cd3-a689d1c0f08e/Process_1625796925967
     *
     * @param id
     * @return
     */
    public String copyWorkFlow(String id) {
        Result<String> result = wfDefinitionService.copyProcessDefinition(id);
        StringBuilder s = new StringBuilder("/work-flow/manage/Process_");
        s.append(result.getResult());
        s.append(":1:");
        s.append(UUID.randomUUID().toString());
        s.append("/Process_");
        s.append(result.getResult());
        return s.toString();
    }

    @Override
    public String getApplicationCode() {
        String str = RandomCodeUtil.applicationCode();
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getApplicationCode, str);
        SysApplication sysApplication = this.getOne(qw);
        if (oConvertUtils.isEmpty(sysApplication)) {
            return str;
        } else {
            return getApplicationCode();
        }
    }

    @Override
    public Object queryRoleDistribution(String applicationId, String appRoleId, Integer selectFlag) {
        //判断下拉框选择的选项
        if (selectFlag == 0) {
            //查询当前应用下 已选择的应用角色下 的用户
            List<SysUser> sysUsers = sysUserAppRoleApplicationMapper.queryUserByAppRoleAndApplication(applicationId, appRoleId);
            return sysUsers;
        } else if (selectFlag == 1) {
            //查询当前应用下 已选择的应用角色下 的部门
            List<SysDepart> sysDeparts = sysDepartAppRoleApplicationMapper.queryDepartByAppRoleAndApplication(applicationId, appRoleId);
            return sysDeparts;
        } else {
            //查询当前应用下 已选择的应用角色下 的角色
            List<SysRole> sysRoles = sysRoleAppRoleApplicationMapper.queryRoleByAppRoleAndApplication(applicationId, appRoleId);
            return sysRoles;

        }
    }

    @Override
    @Transactional
    public Boolean updateApplictionPermission(String applicationId, String ids) {
        //如果ids为""则不需要以，隔开
        List<String> permissionIds = new ArrayList<String>();
        if (!"".equals(ids)) {
            permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        }
        //对permissionIds进行验证 必须为一级菜单且是应用菜单
        for (String permissionId : permissionIds) {
            SysPermission sysPermission = sysPermissionMapper.selectById(permissionId);
            //如果菜单不属于应用菜单或不是一级菜单 添加失败
            if (sysPermission.getCreateWays() != PermissionProperties.CREATE_WAYS_APPLICATION || sysPermission.getParentId() != null) {
                return false;
            }
        }
        //先根据应用id删除应用菜单中间表中的关系
        sysApplicationPermissionMapper.delete(new LambdaQueryWrapper<SysApplicationPermission>()
                .in(SysApplicationPermission::getApplicationId, applicationId));
        //再依次添加应用和菜单的关系
        for (String permissionId : permissionIds) {
            SysApplicationPermission sysApplicationPermission = new SysApplicationPermission();
            sysApplicationPermission.setApplicationId(applicationId);
            sysApplicationPermission.setPermissionId(permissionId);
            sysApplicationPermissionMapper.insert(sysApplicationPermission);
        }
        return true;
    }

    @Override
    public List<SysApplicationDepartRoleVO> getMemberManagementMessage(String applicationId) {
        //结果集
        List<SysApplicationDepartRoleVO> SysApplicationDepartRoleVOs = new ArrayList<SysApplicationDepartRoleVO>();
        //查找应用角色
        List<SysApprole> sysApproles = sysAppRoleMapper.selectList(null);
        //循环应用角色列表
        for (SysApprole sysAppRole : sysApproles) {
            SysApplicationDepartRoleVO sysApplicationDepartRoleVO = new SysApplicationDepartRoleVO();
            //如果应用角色是创建者
            if ("001".equals(sysAppRole.getId())) {
                //通过应用查找创建人
                SysApplication sysApplication = sysApplicationMapper.selectOne(new LambdaQueryWrapper<SysApplication>()
                        .eq(SysApplication::getId, applicationId));
                SysUser sysUser = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                        .eq(SysUser::getUsername, sysApplication.getCreateBy()));

                ArrayList<SysUser> sysUsers = new ArrayList<>();
                sysUsers.add(sysUser);
                sysApplicationDepartRoleVO.setSysApprole(sysAppRole);
                sysApplicationDepartRoleVO.setSysUsers(sysUsers);

            } else {//如果应用角色不是创建者
                sysApplicationDepartRoleVO = getSysApplicationDepartRoleVO(sysAppRole.getId(), applicationId);

            }
            SysApplicationDepartRoleVOs.add(sysApplicationDepartRoleVO);
        }


        return SysApplicationDepartRoleVOs;
    }

    @Override
    @Transactional
    public int deleteMemberManagementMessage(String applicationId, String id, String appRoleId, Integer selectFlag) {
        //如果当前删除为用户
        if (selectFlag == 0) {
            int result = sysUserAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysUserApproleApplication>()
                    .eq(SysUserApproleApplication::getApplicationId, applicationId)
                    .eq(SysUserApproleApplication::getApproleId, appRoleId)
                    .eq(SysUserApproleApplication::getUserId, id));
            return result;
        }
        //如果当前删除为部门
        else if (selectFlag == 1) {
            int result = sysDepartAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysDepartApproleApplication>()
                    .eq(SysDepartApproleApplication::getApplicationId, applicationId)
                    .eq(SysDepartApproleApplication::getApproleId, appRoleId)
                    .eq(SysDepartApproleApplication::getDepartId, id));
            return result;
        }
        //如果当前删除为角色
        else if (selectFlag == 2) {
            int result = sysRoleAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysRoleApproleApplication>()
                    .eq(SysRoleApproleApplication::getApplicationId, applicationId)
                    .eq(SysRoleApproleApplication::getApproleId, appRoleId)
                    .eq(SysRoleApproleApplication::getRoleId, id));
            return result;
        }
        return -1;
    }

    @Override
    @Transactional
    public Boolean updateRoleDistribution(String applicationId, String appRoleId, String ids, Integer selectFlag) {
        //处理ids
        List<String> id1s = (List<String>) Arrays.asList(ids.split(","));
        //添加的是用户
        if (selectFlag == 0) {
            //查询ids是否合理
//            if (!oConvertUtils.isEmpty(ids)){
//                for (String userId:id1s){
//                    if (sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
//                            .eq(SysUser::getId,userId)) == null){
//                        return false;
//                    }
//                }
//            }
            //删除原来的
            sysUserAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysUserApproleApplication>()
                    .eq(SysUserApproleApplication::getApplicationId, applicationId)
                    .eq(SysUserApproleApplication::getApproleId, appRoleId));
            if (!oConvertUtils.isEmpty(ids)) {
                for (String userId : id1s) {
                    //更新应用下 应用角色 的用户
                    sysUserAppRoleApplicationMapper.insert(new SysUserApproleApplication(userId, appRoleId, applicationId));
                }
            }
            return true;
        } else if (selectFlag == 1) {
            //查询ids是否合理
            if (!oConvertUtils.isEmpty(ids)) {
                for (String departId : id1s) {
                    if (sysDepartMapper.selectOne(new LambdaQueryWrapper<SysDepart>()
                            .eq(SysDepart::getId, departId)) == null) {
                        return false;
                    }
                }
            }
            //删除原来的
            sysDepartAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysDepartApproleApplication>()
                    .eq(SysDepartApproleApplication::getApplicationId, applicationId)
                    .eq(SysDepartApproleApplication::getApproleId, appRoleId));
            if (!oConvertUtils.isEmpty(ids)) {
                for (String departId : id1s) {
                    //更新应用下 应用角色 的部门
                    sysDepartAppRoleApplicationMapper.insert(new SysDepartApproleApplication(departId, appRoleId, applicationId));
                }
            }
            return true;
        } else {
            //查询ids是否合理
            if (!oConvertUtils.isEmpty(ids)) {
                for (String roleId : id1s) {
                    if (sysRoleMapper.selectOne(new LambdaQueryWrapper<SysRole>()
                            .eq(SysRole::getId, roleId)) == null) {
                        return false;
                    }
                }
            }
            //删除原来的
            sysRoleAppRoleApplicationMapper.delete(new LambdaQueryWrapper<SysRoleApproleApplication>()
                    .eq(SysRoleApproleApplication::getApplicationId, applicationId)
                    .eq(SysRoleApproleApplication::getApproleId, appRoleId));
            if (!oConvertUtils.isEmpty(ids)) {
                for (String roleId : id1s) {
                    //更新应用下 应用角色 的角色
                    sysRoleAppRoleApplicationMapper.insert(new SysRoleApproleApplication(roleId, appRoleId, applicationId));
                }
            }
            return true;
        }
    }

    @Override
    public Set<String> queryAppRoleByApplicationId(String applicationId) {
        //获取当前操作人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //存储集合 用set不重复
        Set<String> sysApproleIds = new HashSet<String>();
        if (sysApplicationMapper.selectById(applicationId).getCreateBy().equals(user.getUsername())) {
            sysApproleIds.add("001");
        }
        //1.查询用户 下的应用角色
        Set<String> userSet = sysUserAppRoleApplicationMapper.selectList(new LambdaQueryWrapper<SysUserApproleApplication>()
                .eq(SysUserApproleApplication::getApplicationId, applicationId)
                .eq(SysUserApproleApplication::getUserId, user.getId()))
                .stream()
                .map(SysUserApproleApplication::getApproleId)
                .collect(Collectors.toSet());
        sysApproleIds.addAll(userSet);
        //2.查询角色 下的应用角色
        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, user.getId()));
        Set<String> roleSet = new HashSet<String>();
        for (SysUserRole sysUserRole : sysUserRoles) {
            Set<String> collect = sysRoleAppRoleApplicationMapper.selectList(new LambdaQueryWrapper<SysRoleApproleApplication>()
                    .eq(SysRoleApproleApplication::getApplicationId, applicationId)
                    .eq(SysRoleApproleApplication::getRoleId, sysUserRole.getRoleId()))
                    .stream()
                    .map(SysRoleApproleApplication::getApproleId)
                    .collect(Collectors.toSet());
            roleSet.addAll(collect);
        }
        sysApproleIds.addAll(roleSet);
        //3.查询部门 下的应用角色
        List<SysUserDepart> sysUserDeparts = sysUserDepartMapper.selectList(new LambdaQueryWrapper<SysUserDepart>()
                .eq(SysUserDepart::getUserId, user.getId()));
        Set<String> departSet = new HashSet<String>();
        for (SysUserDepart sysUserDepart : sysUserDeparts) {
            Set<String> collect = sysDepartAppRoleApplicationMapper.selectList(new LambdaQueryWrapper<SysDepartApproleApplication>()
                    .eq(SysDepartApproleApplication::getApplicationId, applicationId)
                    .eq(SysDepartApproleApplication::getDepartId, sysUserDepart.getDepId()))
                    .stream()
                    .map(SysDepartApproleApplication::getApproleId)
                    .collect(Collectors.toSet());
            departSet.addAll(collect);
        }
        sysApproleIds.addAll(departSet);

        return sysApproleIds;
    }


    /**
     * 根据 应用角色id 和 应用id 查询所涉及的 应用角色 角色集合 用户集合 部门集合
     *
     * @param approleId
     * @param applicationId
     * @return
     */
    public SysApplicationDepartRoleVO getSysApplicationDepartRoleVO(String approleId, String applicationId) {
        SysApplicationDepartRoleVO sysApplicationDepartRoleVO = new SysApplicationDepartRoleVO();

        SysApprole sysApprole = sysAppRoleMapper.selectById(approleId);
        List<SysUser> sysUsers = sysUserAppRoleApplicationMapper.queryUserByAppRoleAndApplication(applicationId, approleId);
        List<SysDepart> sysDeparts = sysDepartAppRoleApplicationMapper.queryDepartByAppRoleAndApplication(applicationId, approleId);
        List<SysRole> sysRoles = sysRoleAppRoleApplicationMapper.queryRoleByAppRoleAndApplication(applicationId, approleId);

        sysApplicationDepartRoleVO.setSysApprole(sysApprole);
        sysApplicationDepartRoleVO.setSysUsers(sysUsers);
        sysApplicationDepartRoleVO.setSysDeparts(sysDeparts);
        sysApplicationDepartRoleVO.setSysRoles(sysRoles);

        return sysApplicationDepartRoleVO;
    }

    /**
     * 根据前端Key Word查询相关模板或应用
     *
     * @param KeyWord
     * @return
     */
    public Set<SysApplication> queryApplicationOrTemplateByKeyWord(String KeyWord) {
        //创建两个Set接收查询到的数据
        List<SysApplication> sysApplicationList1 = sysApplicationMapper.queryApplicationOrTemplateByAIByAppType(KeyWord);
        List<SysApplication> sysApplicationList2 = sysApplicationMapper.queryApplicationOrTemplateByAppName(KeyWord);
        //将两个Set合并
        sysApplicationList1.addAll(sysApplicationList2);
        Set<SysApplication> sysApplicationSet = new HashSet<>(sysApplicationList1);
        return sysApplicationSet;
    }

    /**
     * 将查询到的菜单数据url替换成相应的来源类型
     *
     * @param sysPermissionList
     * @return
     */
    public List<SysPermission>  changeUrlType(List<SysPermission> sysPermissionList) {
        //将url替换成相应的来源类型
        for (SysPermission sysPermission : sysPermissionList) {
            String url = sysPermission.getUrl();
            if (url.contains("graph")) {
                sysPermission.setUrl("图表");
            } else if (url.contains("form")) {
                sysPermission.setUrl("表单");
            } else if (url.contains("work")) {
                sysPermission.setUrl("工作流");
            } else {
                sysPermission.setUrl("其他");
            }
        }
        return sysPermissionList;
    }

    /**
     * 展示应用下菜单数据
     *
     * @param applicationId
     * @return
     */
    @Override
    @Transactional
    public List<SysPermission> displayApplicationPermissions(String applicationId) {
        //判断一下前端所传应用id是否存在
        if (this.getById(applicationId) == null) {
            return null;
        }
        //首先查询应用权限中间表找出与应用关联的一级菜单的id
        List<String> ids = sysApplicationPermissionMapper.selectList(new LambdaQueryWrapper<SysApplicationPermission>()
                .eq(SysApplicationPermission::getApplicationId, applicationId))
                .stream()
                .map(SysApplicationPermission::getPermissionId)
                .collect(Collectors.toList());
        List<SysPermission> sysPermissions = new ArrayList<SysPermission>();
        if (ids.isEmpty()) {
            return sysPermissions;
        }
        sysPermissions = sysPermissionMapper.findDescendantsWithSelf(ids);
        return sysPermissions;
    }

    /**
     * 查询应用数据输出信息
     *
     * @param permissionIds
     * @return
     */
    @Override
    @Transactional
    public List<SysPermission> queryApplicationPermissionsAndOutput(String permissionIds) {
        //创建一个List接收前端的permissionIds
        List<String> permissionIdList = new ArrayList<>(Arrays.asList(permissionIds.split(",")));
        //创建一个空列表用来接收结果
        List<SysPermission> sysPermissions = new ArrayList<>();
        //将所得id列表进行遍历
        for (String permissionId : permissionIdList) {
            //查询所需菜单数据
            SysPermission sysPermission = sysPermissionMapper.selectOne(new LambdaQueryWrapper<SysPermission>()
                    .eq(SysPermission::getId, permissionId));
            sysPermissions.add(sysPermission);
        }
        changeUrlType(sysPermissions);
        return sysPermissions;
    }

    @Override
    public SysApplication getApplicationByFormId(String formId) {
        if (formMapper.selectById(formId) == null) {
            return null;
        }
        //根据formId查询其绑定的菜单
        String sysPermissionId = formSysPermissionMapper.selectOne(new LambdaQueryWrapper<FormSysPermissionDO>()
                .eq(FormSysPermissionDO::getFormId, formId)).getSysPermissionId();
        //根据菜单id查询其顶级父亲
        SysPermission rootPermission = sysPermissionMapper.findRoot(sysPermissionId);
        //根据顶级菜单获取应用
        String applicationId = sysApplicationPermissionMapper.selectOne(new LambdaQueryWrapper<SysApplicationPermission>()
                .eq(SysApplicationPermission::getPermissionId, rootPermission.getId()))
                .getApplicationId();
        SysApplication sysApplication = sysApplicationMapper.selectById(applicationId);
        return sysApplication;
    }
}
