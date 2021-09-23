package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.MD5Util;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.service.FormDesignerService;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.SysApplicationWithPermissionId;
import org.jeecg.modules.system.model.SysPermissionTree;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.util.CheckAttrUtil;
import org.jeecg.modules.system.util.PermissionDataUtil;
import org.jeecg.modules.system.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.modules.system.util.ResultUtil.*;

/**
 * <p>
 * 菜单权限表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Api(tags = "菜单管理")
@Slf4j
@RestController
@RequestMapping("/sys/permission")
public class SysPermissionController {

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysPermissionDataRuleService sysPermissionDataRuleService;

    @Autowired
    private ISysDepartPermissionService sysDepartPermissionService;

    @Autowired
    private ISysApplicationPermissionService sysApplicationPermissionService;

    @Autowired
    private ISysApplicationService sysApplicationService;

    @Autowired
    private FormDesignerService formDesignerService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysCategoryService sysCategoryService;

    @Autowired
    FormSysPermissionService formSysPermissionService;


    //lazy start
    @ApiOperation("分页返回根节点")
    @GetMapping(path = "/roots", params = "withApplication=true")
    public Result<IPage<SystemPermissionVo>> findSystemRoots(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        IPage<SysPermission> systemRoots = sysPermissionService.findSystemRoots(pageNo, pageSize);
        return ok(systemRoots.convert(SystemPermissionVo::new));
    }


    @ApiOperation("返回对应节点的子节点")
    @GetMapping("/{id}/children")
    public Result<List<SysPermission>> findChildren(@PathVariable("id") String id) {
        List<SysPermission> children = sysPermissionService.findChildren(id);
        return children.size() == 0 ? notFound() : ok(children);
    }

    //lazy end

    /**
     * 加载数据节点
     *
     * @return
     */
    @ApiOperation(value = "加载所有菜单按钮", notes = "加载所有菜单按钮")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> list() {
        long start = System.currentTimeMillis();
        Result<List<SysPermissionTree>> result = new Result<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            List<SysPermissionTree> treeList = new ArrayList<>();
            getTreeList(treeList, list, null);
            SysApplication sysApplication = new SysApplication();
            sysApplication.setId(CommonConstant.APPLICATION_ID);
            sysApplication.setApplicationName("默认应用");
            //插入菜单的应用信息
            List<String> pids = treeList.stream()
                    .map(SysPermissionTree::getId)
                    .collect(Collectors.toList());
            Map<String, SysApplicationWithPermissionId> applications = sysPermissionService.findApplicationByPid(pids);
            treeList.forEach(tree -> {
                SysApplicationWithPermissionId application = applications.get(tree.getId());
                if (application != null) {
                    tree.setApplication(application.sysApplication);
                } else {
                    tree.setApplication(sysApplication);
                }
            });

//            for(SysPermissionTree sysPermissiontree : treeList ){
//                if(Objects.equals(sysPermissiontree.getMenuType(), CommonConstant.MENU_TYPE_0)){
//                    LambdaQueryWrapper<SysApplicationPermission> qw = new LambdaQueryWrapper<SysApplicationPermission>();
//                    qw.eq(SysApplicationPermission::getPermissionId,sysPermissiontree.getId());
//                    List<SysApplicationPermission> sysApplicationPermission = sysApplicationPermissionService.list(qw);
//                    if(sysApplicationPermission.size() > 0){
//                        LambdaQueryWrapper<SysApplication> qe = new LambdaQueryWrapper<SysApplication>();
//                        qe.eq(SysApplication::getId,sysApplicationPermission.get(0).getApplicationId());
//                        SysApplication  sysApplicationOne= sysApplicationService.getOne(qe);
//                        sysPermissiontree.setApplication(sysApplicationOne);
//                    }else{
//                        sysPermissiontree.setApplication(sysApplication);
//                    }
//
//                }
//            }
            result.setResult(treeList);
            result.setSuccess(true);
            log.info("======获取全部菜单数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }

    /*update_begin author:wuxianquan date:20190908 for:先查询一级菜单，当用户点击展开菜单时加载子菜单 */

    /**
     * 系统菜单列表(一级菜单)
     *
     * @return
     */
    @ApiOperation(value = "加载一级菜单", notes = "加载一级菜单")
    @RequestMapping(value = "/getSystemMenuList", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> getSystemMenuList() {
        long start = System.currentTimeMillis();
        Result<List<SysPermissionTree>> result = new Result<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_0);
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            List<SysPermissionTree> sysPermissionTreeList = new ArrayList<SysPermissionTree>();
            for (SysPermission sysPermission : list) {

                //一级菜单插入应用信息
                LambdaQueryWrapper<SysApplicationPermission> qw = new LambdaQueryWrapper<SysApplicationPermission>();
                qw.eq(SysApplicationPermission::getPermissionId, sysPermission.getId());
                List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list(qw);
                SysApplication sysApplication = new SysApplication();
                if (sysApplicationPermissions.size() > 0) {
                    LambdaQueryWrapper<SysApplication> queryWrapper = new LambdaQueryWrapper<SysApplication>();
                    queryWrapper.eq(SysApplication::getId, sysApplicationPermissions.get(0).getApplicationId());
                    sysApplication = sysApplicationService.getOne(queryWrapper);
                } else {
                    sysApplication.setApplicationName("默认菜单");
                }

                SysPermissionTree sysPermissionTree = new SysPermissionTree(sysPermission);
                sysPermissionTree.setApplication(sysApplication);
                sysPermissionTreeList.add(sysPermissionTree);
            }
            result.setResult(sysPermissionTreeList);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        log.info("======获取一级菜单数据=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
        return result;
    }

    /**
     * 查询子菜单
     *
     * @param parentId
     * @return
     */
    @ApiOperation(value = "根据父id查询子菜单", notes = "根据父id查询子菜单")
    @RequestMapping(value = "/getSystemSubmenu", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> getSystemSubmenu(@RequestParam("parentId") String parentId) {
        Result<List<SysPermissionTree>> result = new Result<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getParentId, parentId);
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            List<SysPermissionTree> sysPermissionTreeList = new ArrayList<SysPermissionTree>();
            for (SysPermission sysPermission : list) {
                SysPermissionTree sysPermissionTree = new SysPermissionTree(sysPermission);
                sysPermissionTreeList.add(sysPermissionTree);
            }
            if (sysPermissionTreeList.size() == 0) {
                result.error500("没有子菜单");
            } else {
                //不知道为什么，调用返回为ok时，状态码为0
                result.setResult(sysPermissionTreeList);
                result.setSuccess(true);
                result.setCode(200);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }
    /*update_end author:wuxianquan date:20190908 for:先查询一级菜单，当用户点击展开菜单时加载子菜单 */

    // update_begin author:sunjianlei date:20200108 for: 新增批量根据父ID查询子级菜单的接口 -------------

    /**
     * 查询子菜单
     *
     * @param parentIds 父ID（多个采用半角逗号分割）
     * @return 返回 key-value 的 Map
     */
    @ApiOperation(value = "根据父id查询子菜单（批量）", notes = "根据父id查询子菜单（批量）")
    @GetMapping("/getSystemSubmenuBatch")
    public Result getSystemSubmenuBatch(@RequestParam("parentIds") String parentIds) {
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<>();
            List<String> parentIdList = Arrays.asList(parentIds.split(","));
            query.in(SysPermission::getParentId, parentIdList);
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            Map<String, List<SysPermissionTree>> listMap = new HashMap<>();
            for (SysPermission item : list) {
                String pid = item.getParentId();
                if (parentIdList.contains(pid)) {
                    List<SysPermissionTree> mapList = listMap.get(pid);
                    if (mapList == null) {
                        mapList = new ArrayList<>();
                    }
                    mapList.add(new SysPermissionTree(item));
                    listMap.put(pid, mapList);
                }
            }
            if (listMap.size() == 0) {
                return Result.error("子菜单不存在");
            } else {
                return Result.ok(listMap);
            }

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return Result.error("批量查询子菜单失败：" + e.getMessage());
        }
    }
    // update_end author:sunjianlei date:20200108 for: 新增批量根据父ID查询子级菜单的接口 -------------

//	/**
//	 * 查询用户拥有的菜单权限和按钮权限（根据用户账号）
//	 * 
//	 * @return
//	 */
//	@RequestMapping(value = "/queryByUser", method = RequestMethod.GET)
//	public Result<JSONArray> queryByUser(HttpServletRequest req) {
//		Result<JSONArray> result = new Result<>();
//		try {
//			String username = req.getParameter("username");
//			List<SysPermission> metaList = sysPermissionService.queryByUser(username);
//			JSONArray jsonArray = new JSONArray();
//			this.getPermissionJsonArray(jsonArray, metaList, null);
//			result.setResult(jsonArray);
//			result.success("查询成功");
//		} catch (Exception e) {
//			result.error500("查询失败:" + e.getMessage());
//			log.error(e.getMessage(), e);
//		}
//		return result;
//	}

    /**
     * 查询用户拥有的菜单权限和按钮权限
     *
     * @return
     */
    @ApiOperation(value = "查询用户拥有的菜单权限和按钮权限", notes = "查询用户拥有的菜单权限和按钮权限")
    @RequestMapping(value = "/getUserPermissionByToken", method = RequestMethod.GET)
    public Result<?> getUserPermissionByToken() {
        Result<JSONObject> result = new Result<JSONObject>();
        try {
            //直接获取当前用户不适用前端token
            LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
            if (oConvertUtils.isEmpty(loginUser)) {
                return Result.error("请登录系统！");
            }
            //获取用户权限部门
            List<SysPermission> metaList = sysPermissionService.queryByUser(loginUser.getUsername());
            //添加首页路由
            //update-begin-author:taoyan date:20200211 for: TASK #3368 【路由缓存】首页的缓存设置有问题，需要根据后台的路由配置来实现是否缓存
            if (!PermissionDataUtil.hasIndexPage(metaList)) {
                SysPermission indexMenu = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>().eq(SysPermission::getName, "首页")).get(0);
                metaList.add(0, indexMenu);
            }
            //update-end-author:taoyan date:20200211 for: TASK #3368 【路由缓存】首页的缓存设置有问题，需要根据后台的路由配置来实现是否缓存
            JSONObject json = new JSONObject();
            JSONArray menujsonArray = new JSONArray();
            this.getPermissionJsonArray(menujsonArray, metaList, null);
            JSONArray authjsonArray = new JSONArray();
            this.getAuthJsonArray(authjsonArray, metaList);
            //查询所有的权限
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.eq(SysPermission::getMenuType, CommonConstant.MENU_TYPE_2);
            //query.eq(SysPermission::getStatus, "1");
            List<SysPermission> allAuthList = sysPermissionService.list(query);
            JSONArray allauthjsonArray = new JSONArray();
            this.getAllAuthJsonArray(allauthjsonArray, allAuthList);

            //过滤空的JSONObject
            JSONArray normalMenujsonArray = new JSONArray();
            for (int i = 0; i < menujsonArray.size(); i++) {
                JSONObject jsonObject = menujsonArray.getJSONObject(i);
                if (!jsonObject.toJSONString().equals("{}")) {
                    normalMenujsonArray.add(jsonObject);
                }
            }
            //路由菜单
            json.put("menu", normalMenujsonArray);

//			json.put("menu", menujsonArray);
            //按钮权限（用户拥有的权限集合）
            json.put("auth", authjsonArray);
            //全部权限配置集合（按钮权限，访问权限）
            json.put("allAuth", allauthjsonArray);
            result.setResult(json);
            result.success("查询成功");
        } catch (Exception e) {
            result.error500("查询失败:" + e.getMessage());
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 查询用户拥有的应用
     *
     * @return
     */
    @ApiOperation(value = "查询用户拥有的应用", notes = "查询用户拥有的应用")
    @RequestMapping(value = "/userApplication", method = RequestMethod.GET)
    public Result<?> userApplication() {
        Result<SysApplicationCategoryVo> result = new Result<SysApplicationCategoryVo>();
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        if (oConvertUtils.isEmpty(loginUser)) {
            return Result.Error("请登录系统！");
        }
        //获取用户应用
        List<SysApplication> sysApplicationList = sysPermissionService.queryByUserApplication(loginUser.getUsername());
        //获得用户应用分类
        List<SysCategory> sysCategoryList = new ArrayList<SysCategory>();
        for (SysApplication application : sysApplicationList) {
            LambdaQueryWrapper<SysCategory> qw = new LambdaQueryWrapper<SysCategory>();
            qw.eq(SysCategory::getId, application.getApplicationType());
            SysCategory sysCategory = sysCategoryService.getOne(qw);
            if (!oConvertUtils.isEmpty(sysCategory)) {
                sysCategoryList.add(sysCategory);
            } else {
                application.setApplicationType("1");
                LambdaQueryWrapper<SysApplication> qe = new LambdaQueryWrapper<SysApplication>();
                qe.eq(SysApplication::getId, application.getId());
                sysApplicationService.saveOrUpdate(application, qe);
            }
        }
        SysCategory sysCategory = new SysCategory();
        sysCategory.setId("1");
        sysCategory.setName("其他");
        sysCategoryList.add(sysCategory);
        SysApplicationCategoryVo sysApplicationCategoryVo = new SysApplicationCategoryVo();
        sysApplicationCategoryVo.setSysApplication(sysApplicationList);
        sysApplicationCategoryVo.setSysCategory(sysCategoryList.stream().distinct().collect(Collectors.toList()));
        result.setResult(sysApplicationCategoryVo);
        return result;
    }


    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "添加菜单", notes = "添加菜单")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysPermission> add(@RequestBody SysPermissionCreateVo sysPermissionCreateVo) {
        if (!sysPermissionCreateVo.isValid) {
            log.warn("SysPermissionUpdateVo simple validation failed!");
            return badRequest();
        }
        PermissionDataUtil.intelligentProcessData(sysPermissionCreateVo.permission);
        boolean success = sysPermissionService.create(sysPermissionCreateVo.applicationId, sysPermissionCreateVo.permission);
        return success ? ok(sysPermissionCreateVo.permission) : badRequest();
//        Result<SysPermission> result = new Result<SysPermission>();
//        SysPermission permission = new SysPermission();
//        if (permissionVo.getPermission() != null
//                && permissionVo.getPermission().getParentId() != null
//                && permissionVo.getPermission().getParentId().trim().isEmpty()) {
//            permissionVo.getPermission().setParentId(null);
//
//        }
//        SysApplicationPermission sysApplicationPermission = new SysApplicationPermission();
//        try {
//            if (permissionVo.getApplicationId() != null) {
//                LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
//                qw.eq(SysApplication::getId, permissionVo.getApplicationId());
//                SysApplication sysApplication = sysApplicationService.getOne(qw);
//                if (sysApplication == null) {
//                    result.error500("应用不存在");
//                    return result;
//                } else {
//                    permission = PermissionDataUtil.intelligentProcessData(permissionVo.getPermission());
//                    sysPermissionService.addPermission(permission);
//                    result.success("添加成功！");
//                    result.setResult(permission);
//                    sysApplicationPermission.setApplicationId(permissionVo.getApplicationId());
//                    sysApplicationPermission.setPermissionId(permissionVo.getPermission().getId());
//                    LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();
//                    sysApplicationPermissionService.save(sysApplicationPermission);
//                }
//            } else {
//                permission = PermissionDataUtil.intelligentProcessData(permissionVo.getPermission());
//                sysPermissionService.addPermission(permission);
//                result.success("添加成功！");
//                result.setResult(permission);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            result.error500("操作失败");
//        }
//        return result;
    }


    @ApiOperation("编辑菜单信息接口")
    @PutMapping("/edit")
    public Result<SysPermission> edit(@RequestBody SysPermissionUpdateVo permissionVo) {
        if (!permissionVo.isValid) {
            log.warn("SysPermissionUpdateVo simple validation failed!");
            return badRequest();
        }
        PermissionDataUtil.intelligentProcessData(permissionVo.permission);
        boolean success = sysPermissionService.edit(permissionVo.applicationId, permissionVo.permission);
        return success ? ok(permissionVo.permission) : badRequest();
    }

    /**
     * 删除菜单
     *
     * @param id
     * @return
     */
    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "删除菜单", notes = "删除菜单")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<SysPermission> delete(@RequestParam(name = "id", required = true) String id) {
        //删除fd_sys_permission表的数据 by Huangsn
        int count = formSysPermissionService.count(new LambdaQueryWrapper<FormSysPermissionDO>()
                .eq(FormSysPermissionDO::getSysPermissionId, id));
        if (count != 0) {
            formDesignerService.deleteFormSysPermission(id);
        }
        Result<SysPermission> result = new Result<>();
        try {
            sysPermissionService.deletePermission(id);
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500(e.getMessage());
        }
        return result;
    }

    /**
     * 批量删除菜单
     *
     * @param ids
     * @return
     */
    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "批量删除菜单", notes = "批量删除菜单")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysPermission> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysPermission> result = new Result<>();
        try {
            String[] arr = ids.split(",");
            for (String id : arr) {
                if (oConvertUtils.isNotEmpty(id)) {
                    sysPermissionService.deletePermission(id);
                }
            }
            result.success("删除成功!");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("删除失败!");
        }
        return result;
    }

    /**
     * 获取全部的权限树
     *
     * @return
     */
    @ApiOperation(value = "获取全部的权限树", notes = "获取全部的权限树")
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeList() {
        Result<Map<String, Object>> result = new Result<>();
        // 全部权限ids
        List<String> ids = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            for (SysPermission sysPer : list) {
                ids.add(sysPer.getId());
            }
            List<SysPermissionTree> treeList = new ArrayList<>();
            getTreeList(treeList, list, null);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("treeList", treeList); // 全部树节点数据
            resMap.put("ids", ids);// 全部树ids
            //插入应用信息进树形结构
            for (SysPermissionTree treeModel : treeList) {
                if ("".equals(treeModel.getParentId())) {
                    LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();
                    queryWrapper.eq(SysApplicationPermission::getPermissionId, treeModel.getKey());
                    List<SysApplicationPermission> sysApplicationList = sysApplicationPermissionService.list(queryWrapper);
                    SysApplication sysApplication = new SysApplication();
                    if (sysApplicationList.size() > 0) {
                        LambdaQueryWrapper<SysApplication> qW = new LambdaQueryWrapper<SysApplication>();
                        qW.eq(SysApplication::getId, sysApplicationList.get(0).getApplicationId());
                        sysApplication = sysApplicationService.getOne(qW);
                    } else {
                        sysApplication.setId(CommonConstant.APPLICATION_ID);
                        sysApplication.setApplicationName("默认应用");
                    }
                    treeModel.setApplication(sysApplication);
                }
                ;
            }
            result.setResult(resMap);
            result.setSuccess(true);
            result.setCode(200);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    /**
     * 异步加载数据节点
     *
     * @return
     */
    @ApiOperation(value = "异步加载数据节点", notes = "异步加载数据节点")
    @RequestMapping(value = "/queryListAsync", method = RequestMethod.GET)
    public Result<List<TreeModel>> queryAsync(@RequestParam(name = "pid", required = false) String parentId) {
        Result<List<TreeModel>> result = new Result<>();
        try {
            List<TreeModel> list = sysPermissionService.queryListByParentId(parentId);
            if (list == null || list.size() <= 0) {
                result.error500("未找到菜单信息");
            } else {
                result.setResult(list);
                result.setSuccess(true);
                result.setCode(200);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return result;
    }


    @ApiOperation(value = "查询系统角色授权", notes = "查询系统角色授权")
    @RequestMapping(value = "/querySysRolePermission", method = RequestMethod.GET)
    public Result<List<String>> querySysRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
        Result<List<String>> result = new Result<List<String>>();
        //roleId不能为空
        if (oConvertUtils.isEmpty(roleId)) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不能为空");
            return result;
        }
        //roleId的正确性
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getId, roleId);
        if (sysRoleService.getOne(queryWrapper) == null) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不正确");
            return result;
        }

        LambdaQueryWrapper<SysRolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionService.list(lambdaQueryWrapper);
        List<String> pids = new ArrayList<>();
        if (!sysRolePermissions.isEmpty()) {
            pids = sysPermissionService
                    .listByIds(sysRolePermissions.stream()
                            .map(SysRolePermission::getPermissionId)
                            .collect(Collectors.toList())
                    )
                    .stream()
                    .filter(sysPermission -> sysPermission.getCreateWays() == 1)
                    .map(SysPermission::getId)
                    .distinct()
                    .collect(Collectors.toList());
        }

//		List<String> sysPermissionStrs = new ArrayList<>();
//		for (SysRolePermission sysRolePermission : sysRolePermissions){
//			sysPermissionStrs.add(sysRolePermission.getPermissionId());
//		}
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(pids);
        return result;
    }

    @ApiOperation(value = "查询系统角色授权叶子节点", notes = "查询系统角色授权叶子节点")
    @RequestMapping(value = "/queryLeafSysRolePermission", method = RequestMethod.GET)
    public Result<List<String>> queryLeafSysRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
        Result<List<String>> result = new Result<List<String>>();
        //roleId不能为空
        if (oConvertUtils.isEmpty(roleId)) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不能为空");
            return result;
        }
        //roleId的正确性
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getId, roleId);
        if (sysRoleService.getOne(queryWrapper) == null) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不正确");
            return result;
        }

        List<String> leafSysRolePermission = sysRolePermissionService.queryLeafSysRolePermission(roleId);
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(leafSysRolePermission);
        return result;
    }

    /**
     * 查询角色授权
     *
     * @return
     */
    @ApiOperation(value = "查询角色授权", notes = "查询角色授权")
    @RequestMapping(value = "/queryRolePermission", method = RequestMethod.GET)
    public Result<List<String>> queryRolePermission(@RequestParam(name = "roleId", required = true) String roleId) {
        Result<List<String>> result = new Result<List<String>>();
        //roleId不能为空
        if (oConvertUtils.isEmpty(roleId)) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不能为空");
            return result;
        }
        //roleId的正确性
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<SysRole>();
        queryWrapper.eq(SysRole::getId, roleId);
        if (sysRoleService.getOne(queryWrapper) == null) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("roleId不正确");
            return result;
        }

        LambdaQueryWrapper<SysRolePermission> lambdaQueryWrapper = new LambdaQueryWrapper<SysRolePermission>();
        lambdaQueryWrapper.eq(SysRolePermission::getRoleId, roleId);
        List<SysRolePermission> sysRolePermissions = sysRolePermissionService.list(lambdaQueryWrapper);
        List<String> sysPermissionStrs = new ArrayList<String>();
        for (SysRolePermission sysRolePermission : sysRolePermissions) {
            sysPermissionStrs.add(sysRolePermission.getPermissionId());
        }
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(sysPermissionStrs);
        return result;
    }

    /**
     * 保存角色授权
     *
     * @return
     */
    @ApiOperation(value = "保存角色授权", notes = "保存角色授权")
    @RequestMapping(value = "/saveRolePermission", method = RequestMethod.POST)
    //@RequiresRoles({ "admin" })
    public Result<String> saveRolePermission(@RequestBody JSONObject json) {
        long start = System.currentTimeMillis();
        Result<String> result = new Result<>();
        String roleId = json.getString("roleId");
        String permissionIds = json.getString("permissionIds");
        String lastPermissionIds = json.getString("lastpermissionIds");
//		System.out.println("===>"+roleId);
//		System.out.println("=====>"+permissionIds);
//		System.out.println("=======>"+lastPermissionIds);
//		if (oConvertUtils.isEmpty(roleId) || permissionIds == null || lastPermissionIds == null){
//			result.setMessage("数据为空");
//			result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
//			return result;
//		}
//		//变成集合
        List<String> permissionIdList = Arrays.asList(permissionIds.split(","));
        List<String> lastPermissionIdList = Arrays.asList(lastPermissionIds.split(","));
        Set<String> permissionIdSet = new HashSet<String>();
        Set<String> lastPermissionIdSet = new HashSet<String>();

        if (!"".equals(permissionIds)) {
            LambdaQueryWrapper<SysPermission> qw = new LambdaQueryWrapper<SysPermission>();
            for (String sysPermissionId : permissionIdList) {
                permissionIdSet.add(sysPermissionId);
                qw.eq(SysPermission::getId, sysPermissionId);
                if (sysPermissionService.getOne(qw) == null) {
                    result.setMessage("插入数据有误！");
                    result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                    return result;
                }
                qw.clear();
            }
            if (permissionIdList.size() != permissionIdSet.size()) {
                result.setMessage("重复数据！");
                result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                return result;
            }
        }
        int size = sysRolePermissionService.list(new LambdaQueryWrapper<SysRolePermission>().eq(SysRolePermission::getRoleId, roleId)).size();
        if (!("".equals(lastPermissionIds) && size == 0)) {
            LambdaQueryWrapper<SysRolePermission> queryWrapper = new LambdaQueryWrapper<SysRolePermission>();
            queryWrapper.eq(SysRolePermission::getRoleId, roleId);
            List<SysRolePermission> SysRolePermissions = sysRolePermissionService.list(queryWrapper);
            queryWrapper.clear();
            if (SysRolePermissions.size() != lastPermissionIdList.size()) {
                result.setMessage("原数据有误！");
                result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                return result;
            }
            for (String sysPermissionId : lastPermissionIdList) {
                lastPermissionIdSet.add(sysPermissionId);
                queryWrapper.eq(SysRolePermission::getRoleId, roleId);
                queryWrapper.eq(SysRolePermission::getPermissionId, sysPermissionId);
                if (sysRolePermissionService.getOne(queryWrapper) == null) {
                    result.setMessage("原数据有误！");
                    result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                    return result;
                }
                queryWrapper.clear();
            }
            if (lastPermissionIdList.size() != lastPermissionIdSet.size()) {
                result.setMessage("重复数据！");
                result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                return result;
            }
        }


        //从数据库查询之前选中的菜单
//		Set<String> ids = sysRolePermissionService.list(new LambdaQueryWrapper<SysRolePermission>()
//				.eq(SysRolePermission::getRoleId, roleId))
//				.stream().map(SysRolePermission::getPermissionId).collect(Collectors.toSet());
//		String lastPermissionIds = "";
//		if (ids.size() != 0){
//			StringBuilder stringBuilder = new StringBuilder();
//			for (String permissionId:ids){
//				stringBuilder.append(",");
//				stringBuilder.append(permissionId);
//			}
//			lastPermissionIds = stringBuilder.toString().substring(1);
//		}

        this.sysRolePermissionService.saveRolePermission(roleId, permissionIds, lastPermissionIds);
        result.success("保存成功！");
        log.info("======角色授权成功=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");

        return result;
    }


    @ApiOperation("修改角色系统授权")
    @PutMapping("/saveRoleSysPermission")
    public Result<Void> saveRoleSysPermission(@RequestBody SysRolePermissionUpdateVo sysRolePermissionUpdateVo) {
        if (!sysRolePermissionUpdateVo.isValid) {
            log.warn("SysRolePermissionUpdateVo simple update failed! ");
            return badRequest();
        }
        log.info(String.valueOf(sysRolePermissionUpdateVo));
        boolean success = sysRolePermissionService.updateSysRolePermissionByRoleId(sysRolePermissionUpdateVo.roleId,
                sysRolePermissionUpdateVo.oldPermissionIds(),
                sysRolePermissionUpdateVo.newPermissionIds());
        return success ? ok() : badRequest();
    }

    private void getTreeList(List<SysPermissionTree> treeList, List<SysPermission> metaList, SysPermissionTree temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            SysPermissionTree tree = new SysPermissionTree(permission);
            if (temp == null && oConvertUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getId())) {
                temp.getChildren().add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeList(treeList, metaList, tree);
                }
            }

        }
    }

    private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission);
            if (temp == null && oConvertUtils.isEmpty(tempPid)) {
                treeList.add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            } else if (temp != null && tempPid != null && tempPid.equals(temp.getKey())) {
                temp.getChildren().add(tree);
                if (!tree.getIsLeaf()) {
                    getTreeModelList(treeList, metaList, tree);
                }
            }

        }
    }

    /**
     * 获取权限JSON数组
     *
     * @param jsonArray
     * @param allList
     */
    private void getAllAuthJsonArray(JSONArray jsonArray, List<SysPermission> allList) {
        JSONObject json = null;
        for (SysPermission permission : allList) {
            json = new JSONObject();
            json.put("action", permission.getPerms());
            json.put("status", permission.getStatus());
            //1显示2禁用
            json.put("type", permission.getPermsType());
            json.put("describe", permission.getName());
            jsonArray.add(json);
        }
    }

    /**
     * 获取权限JSON数组
     *
     * @param jsonArray
     * @param metaList
     */
    private void getAuthJsonArray(JSONArray jsonArray, List<SysPermission> metaList) {
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            JSONObject json = null;
            if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2) && CommonConstant.STATUS_1.equals(permission.getStatus())) {
                json = new JSONObject();
                json.put("action", permission.getPerms());
                json.put("type", permission.getPermsType());
                json.put("describe", permission.getName());
                jsonArray.add(json);
            }
        }
    }

    /**
     * 获取菜单JSON数组
     *
     * @param jsonArray
     * @param metaList
     * @param parentJson
     */
    private void getPermissionJsonArray(JSONArray jsonArray, List<SysPermission> metaList, JSONObject parentJson) {
        for (SysPermission permission : metaList) {
            if (permission.getMenuType() == null) {
                continue;
            }
            String tempPid = permission.getParentId();
            JSONObject json = getPermissionJsonObject(permission);
            if (json == null) {
                continue;
            }
            if (parentJson == null && oConvertUtils.isEmpty(tempPid)) {
                jsonArray.add(json);
                if (!permission.isLeaf()) {
                    getPermissionJsonArray(jsonArray, metaList, json);
                }
            } else if (parentJson != null && oConvertUtils.isNotEmpty(tempPid) && tempPid.equals(parentJson.getString("id"))) {
                // 类型( 0：一级菜单 1：子菜单 2：按钮 )
                if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_1)) {
                    JSONObject metaJson = parentJson.getJSONObject("meta");
                    if (metaJson.containsKey("permissionList")) {
                        metaJson.getJSONArray("permissionList").add(json);
                    } else {
                        JSONArray permissionList = new JSONArray();
                        permissionList.add(json);
                        metaJson.put("permissionList", permissionList);
                    }
                    // 类型( 0：一级菜单 1：子菜单 2：按钮 )
                } else if (permission.getMenuType().equals(PermissionProperties.MENU_TYPE_DIRECTORY) ||
                        permission.getMenuType().equals(PermissionProperties.MENU_TYPE_FORM) ||
                        permission.getMenuType().equals(PermissionProperties.MENU_TYPE_GRAPH) ||
                        permission.getMenuType().equals(PermissionProperties.MENU_TYPE_WORKFLOW)
                ) {

                    if (parentJson.containsKey("children")) {
                        parentJson.getJSONArray("children").add(json);
                    } else {
                        JSONArray children = new JSONArray();
                        children.add(json);
                        parentJson.put("children", children);
                    }
                    System.out.println(permission);
                    if (!permission.isLeaf()) {
                        getPermissionJsonArray(jsonArray, metaList, json);
                    }
                }
            }

        }
    }

    /**
     * 根据菜单配置生成路由json
     *
     * @param permission
     * @return
     */
    private JSONObject getPermissionJsonObject(SysPermission permission) {
        JSONObject json = new JSONObject();
        // 类型(0：一级菜单 1：子菜单 2：按钮)
        //插入按钮之后才需要加这个权限
//        if (permission.getMenuType().equals(CommonConstant.MENU_TYPE_2)) {
//            //json.put("action", permission.getPerms());
//            //json.put("type", permission.getPermsType());
//            //json.put("describe", permission.getName());
//            return null;
//        } else
        if (permission.getMenuType().equals(PermissionProperties.MENU_TYPE_DIRECTORY) ||
                permission.getMenuType().equals(PermissionProperties.MENU_TYPE_FORM) ||
                permission.getMenuType().equals(PermissionProperties.MENU_TYPE_GRAPH) ||
                permission.getMenuType().equals(PermissionProperties.MENU_TYPE_WORKFLOW)) {
            json.put("id", permission.getId());
            if (permission.isRoute()) {
                json.put("route", "1");// 表示生成路由
            } else {
                json.put("route", "0");// 表示不生成路由
            }

            if (isWWWHttpUrl(permission.getUrl())) {
                json.put("path", MD5Util.MD5Encode(permission.getUrl(), "utf-8"));
            } else {
                json.put("path", permission.getUrl());
            }

            // 重要规则：路由name (通过URL生成路由name,路由name供前端开发，页面跳转使用)
            if (oConvertUtils.isNotEmpty(permission.getComponentName())) {
                json.put("name", permission.getComponentName());
            } else {
                json.put("name", urlToRouteName(permission.getUrl()));
            }

            // 是否隐藏路由，默认都是显示的
            if (permission.isHidden()) {
                json.put("hidden", true);
            }
            // 聚合路由
            if (permission.isAlwaysShow()) {
                json.put("alwaysShow", true);
            }
            json.put("component", permission.getComponent());
            JSONObject meta = new JSONObject();
            // 由用户设置是否缓存页面 用布尔值
            if (permission.isKeepAlive()) {
                meta.put("keepAlive", true);
            } else {
                meta.put("keepAlive", false);
            }

            /*update_begin author:wuxianquan date:20190908 for:往菜单信息里添加外链菜单打开方式 */
            //外链菜单打开方式
            if (permission.isInternalOrExternal()) {
                meta.put("internalOrExternal", true);
            } else {
                meta.put("internalOrExternal", false);
            }
            /* update_end author:wuxianquan date:20190908 for: 往菜单信息里添加外链菜单打开方式*/

            meta.put("title", permission.getName());

            //update-begin--Author:scott  Date:20201015 for：路由缓存问题，关闭了tab页时再打开就不刷新 #842
            String component = permission.getComponent();
            if (oConvertUtils.isNotEmpty(permission.getComponentName()) || oConvertUtils.isNotEmpty(component)) {
                meta.put("componentName", oConvertUtils.getString(permission.getComponentName(), component.substring(component.lastIndexOf("/") + 1)));
            }
            //update-end--Author:scott  Date:20201015 for：路由缓存问题，关闭了tab页时再打开就不刷新 #842

            if (oConvertUtils.isEmpty(permission.getParentId())) {
                // 一级菜单跳转地址
                json.put("redirect", permission.getRedirect());
                if (oConvertUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            } else {
                if (oConvertUtils.isNotEmpty(permission.getIcon())) {
                    meta.put("icon", permission.getIcon());
                }
            }
            if (isWWWHttpUrl(permission.getUrl())) {
                meta.put("url", permission.getUrl());
            }
            json.put("meta", meta);
        }

        return json;
    }

    /**
     * 判断是否外网URL 例如： http://localhost:8080/jeecg-boot/swagger-ui.html#/ 支持特殊格式： {{
     * window._CONFIG['domianURL'] }}/druid/ {{ JS代码片段 }}，前台解析会自动执行JS代码片段
     *
     * @return
     */
    private boolean isWWWHttpUrl(String url) {
        if (url != null && (url.startsWith("http://") || url.startsWith("https://") || url.startsWith("{{"))) {
            return true;
        }
        return false;
    }

    /**
     * 通过URL生成路由name（去掉URL前缀斜杠，替换内容中的斜杠‘/’为-） 举例： URL = /isystem/role RouteName =
     * isystem-role
     *
     * @return
     */
    private String urlToRouteName(String url) {
        if (oConvertUtils.isNotEmpty(url)) {
            if (url.startsWith("/")) {
                url = url.substring(1);
            }
            url = url.replace("/", "-");

            // 特殊标记
            url = url.replace(":", "@");
            return url;
        } else {
            return null;
        }
    }

    /**
     * 根据菜单id来获取其对应的权限数据
     *
     * @param sysPermissionDataRule
     * @return
     */
    @ApiOperation(value = "根据菜单id来获取其对应的权限数据", notes = "根据菜单id来获取其对应的权限数据")
    @RequestMapping(value = "/getPermRuleListByPermId", method = RequestMethod.GET)
    public Result<List<SysPermissionDataRule>> getPermRuleListByPermId(SysPermissionDataRule sysPermissionDataRule) {
        Result<List<SysPermissionDataRule>> result = new Result<>();
        if (oConvertUtils.isEmpty(sysPermissionDataRule)) {
            result.error500("参数为空");
            return result;
        }

        LambdaQueryWrapper<SysPermission> queryWrapper = new LambdaQueryWrapper<SysPermission>();
        queryWrapper.eq(SysPermission::getId, sysPermissionDataRule.getPermissionId());
        SysPermission sysPermission = sysPermissionService.getOne(queryWrapper);
        if (oConvertUtils.isEmpty(sysPermission)) {
            result.error500("菜单不存在");
            return result;
        }
        List<SysPermissionDataRule> permRuleList = sysPermissionDataRuleService.getPermRuleListByPermId(sysPermissionDataRule.getPermissionId());
        if (sysPermissionDataRule.getPermissionId() == null) {
            result.error500("参数为空");
            return result;
        }
        result.setSuccess(true);
        result.setResult(permRuleList);
        result.setCode(200);
        return result;
    }

    /**
     * 添加菜单权限数据
     *
     * @param sysPermissionDataRule
     * @return
     */
    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "添加菜单权限数据", notes = "添加菜单权限数据")
    @RequestMapping(value = "/addPermissionRule", method = RequestMethod.POST)
    public Result<SysPermissionDataRule> addPermissionRule(@RequestBody SysPermissionDataRule sysPermissionDataRule) {
        Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
        //判断数据有效性 permissionId ruleColumn ruleConditions ruleName ruleValue status
        if (oConvertUtils.isEmpty(sysPermissionDataRule) || CheckAttrUtil.isNull(sysPermissionDataRule,
                "permissionId", "ruleColumn", "ruleConditions", "ruleName", "ruleValue", "status")) {
            result.setMessage("数据为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断菜单id的正确性
        if (sysPermissionService.getById(sysPermissionDataRule.getPermissionId()) == null) {
            result.setMessage("菜单id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //添加数据
        try {
            sysPermissionDataRule.setCreateTime(new Date());
            sysPermissionDataRuleService.savePermissionDataRule(sysPermissionDataRule);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "修改菜单权限数据", notes = "修改菜单权限数据")
    @RequestMapping(value = "/editPermissionRule", method = {RequestMethod.PUT, RequestMethod.POST})
    public Result<SysPermissionDataRule> editPermissionRule(@RequestBody SysPermissionDataRule sysPermissionDataRule) {
        Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
        //检验权限菜单
        LambdaQueryWrapper<SysPermissionDataRule> qw = new LambdaQueryWrapper<SysPermissionDataRule>();
        qw.eq(SysPermissionDataRule::getId, sysPermissionDataRule.getId());
        SysPermissionDataRule permissonDataRule = sysPermissionDataRuleService.getOne(qw);
        if (oConvertUtils.isEmpty(permissonDataRule)) {
            return result.error500("菜单权限数据不存在");
        }
        //检验菜单
        LambdaQueryWrapper<SysPermission> qe = new LambdaQueryWrapper<SysPermission>();
        qe.eq(SysPermission::getId, sysPermissionDataRule.getPermissionId());
        SysPermission permisson = sysPermissionService.getOne(qe);
        if (oConvertUtils.isEmpty(permisson)) {
            return result.error500("菜单不存在");
        }
        try {
            sysPermissionDataRuleService.saveOrUpdate(sysPermissionDataRule);
            result.success("更新成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 删除菜单权限数据
     * 传入的是数据规则id
     *
     * @param id
     * @return
     */
    //@RequiresRoles({ "admin" })
    @ApiOperation(value = "删除菜单权限数据", notes = "删除菜单权限数据")
    @RequestMapping(value = "/deletePermissionRule", method = RequestMethod.DELETE)
    public Result<SysPermissionDataRule> deletePermissionRule(@RequestParam(name = "id", required = true) String id) {
        Result<SysPermissionDataRule> result = new Result<SysPermissionDataRule>();
        //判断菜单id是否为空
        if (oConvertUtils.isEmpty(id)) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("菜单id不能为空");
            return result;
        }
        //判断菜单id的正确性
        if (sysPermissionDataRuleService.getById(id) == null) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("数据规则id不正确");
            return result;
        }
        try {
            sysPermissionDataRuleService.deletePermissionDataRule(id);
            result.success("删除成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 查询菜单权限数据
     *
     * @param sysPermissionDataRule
     * @return
     */
    @ApiOperation(value = "查询菜单权限数据", notes = "查询菜单权限数据")
    @RequestMapping(value = "/queryPermissionRule", method = RequestMethod.GET)
    public Result<List<SysPermissionDataRule>> queryPermissionRule(SysPermissionDataRule sysPermissionDataRule) {
        Result<List<SysPermissionDataRule>> result = new Result<>();
        try {
            List<SysPermissionDataRule> permRuleList = sysPermissionDataRuleService.queryPermissionRule(sysPermissionDataRule);
            if (permRuleList.size() == 0) {
                result.error500("菜单不存在");
            } else {
                result.setResult(permRuleList);
                result.success("查询成功！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 部门权限表
     *
     * @param departId
     * @return
     */
    @ApiOperation(value = "部门权限表", notes = "部门权限表")
    @RequestMapping(value = "/queryDepartPermission", method = RequestMethod.GET)
    public Result<List<String>> queryDepartPermission(@RequestParam(name = "departId", required = true) String departId) {
        Result<List<String>> result = new Result<>();

        if (oConvertUtils.isEmpty(departId)) {
            result.setMessage("部门id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        LambdaQueryWrapper<SysDepart> queryWrapper = new LambdaQueryWrapper<SysDepart>();
        queryWrapper.eq(SysDepart::getId, departId);
        if (sysDepartService.getOne(queryWrapper) == null) {
            result.setMessage("部门id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }

        List<SysDepartPermission> sysDepartPermissions = sysDepartPermissionService.list(new QueryWrapper<SysDepartPermission>().lambda().eq(SysDepartPermission::getDepartId, departId));
        List<String> sysPermissionStrs = new ArrayList<String>();
        for (SysDepartPermission sysDepartPermission : sysDepartPermissions) {
            sysPermissionStrs.add(sysDepartPermission.getPermissionId());
        }
        if (sysPermissionStrs.size() <= 0) {
            result.setMessage("该部门没有菜单");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        result.setResult(sysPermissionStrs);
        result.setSuccess(true);
        result.setCode(200);

        return result;
    }

    /**
     * 保存部门授权
     *
     * @return
     */
    @ApiOperation(value = "保存部门授权", notes = "保存部门授权")
    @RequestMapping(value = "/saveDepartPermission", method = RequestMethod.POST)
    public Result<String> saveDepartPermission(@RequestBody SysDepartPermissionVo sysDepartPermissionVo) {
        //计时
        log.info(String.valueOf(sysDepartPermissionVo));
        long start = System.currentTimeMillis();
        if (!sysDepartPermissionVo.isValid()) {
            log.warn("sysDepartPermissionVo simple update failed! ");
            return badRequest();
        }
        Result<String> result = new Result<>();
        if (oConvertUtils.isEmpty(sysDepartPermissionVo.getDepartId())
                || sysDepartPermissionVo.getPermissionIds() == null
                || sysDepartPermissionVo.getLastpermissionIds() == null) {
            result.setMessage("数据为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (!"".equals(sysDepartPermissionVo.getPermissionIds())) {
            LambdaQueryWrapper<SysPermission> qw = new LambdaQueryWrapper<SysPermission>();
            for (String sysPermissionId : sysDepartPermissionVo.newPermissionIds()) {
                qw.eq(SysPermission::getId, sysPermissionId);
                if (sysPermissionService.getOne(qw) == null) {
                    result.setMessage("插入数据有误！");
                    result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                    return result;
                }
                qw.clear();
            }
        }
        int size = sysDepartPermissionService
                .list(new LambdaQueryWrapper<SysDepartPermission>()
                .eq(SysDepartPermission::getDepartId, sysDepartPermissionVo.getDepartId())).size();
        if (!("".equals(sysDepartPermissionVo.getLastpermissionIds()) && size == 0)) {
            LambdaQueryWrapper<SysDepartPermission> queryWrapper = new LambdaQueryWrapper<SysDepartPermission>();
            queryWrapper.eq(SysDepartPermission::getDepartId, sysDepartPermissionVo.getDepartId());
            List<SysDepartPermission> sysDepartPermissions = sysDepartPermissionService.list(queryWrapper);
            queryWrapper.clear();
            int a = sysDepartPermissionVo.oldPermissionIds().size();
            if (sysDepartPermissions.size() != sysDepartPermissionVo.oldPermissionIds().size()) {
                result.setMessage("传入数据条目有误！");
                result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                return result;
            }
            for (String sysPermissionId : sysDepartPermissionVo.oldPermissionIds()) {
                queryWrapper.eq(SysDepartPermission::getDepartId, sysDepartPermissionVo.getDepartId());
                queryWrapper.eq(SysDepartPermission::getPermissionId, sysPermissionId);
                if (sysDepartPermissionService.getOne(queryWrapper) == null) {
                    result.setMessage("传入数据有误！");
                    result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
                    return result;
                }
                queryWrapper.clear();
            }
        }
        this.sysDepartPermissionService.saveDepartPermission(sysDepartPermissionVo);
        result.success("保存成功！");
        log.info("======部门授权成功=====耗时:" + (System.currentTimeMillis() - start) + "毫秒");
        return result;
    }

    /**
     * 查询全部应用
     *
     * @return
     */
    @ApiOperation(value = "查询全部应用", notes = "查询全部应用")
    @RequestMapping(value = "/queryApplication", method = RequestMethod.GET)
    public Result<List<SysApplication>> queryApplication() {
        LambdaQueryWrapper<SysApplication> queryWrapper = new LambdaQueryWrapper<SysApplication>();
        List<SysApplication> list = sysApplicationService.list(queryWrapper);
        SysApplication sysApplication = new SysApplication();
        sysApplication.setId(CommonConstant.APPLICATION_ID);
        sysApplication.setApplicationName("默认应用");
        list.add(sysApplication);
        Result<List<SysApplication>> result = new Result<>();
        result.setResult(list);
        result.success("查询成功");
        return result;
    }


    //一级菜单和下边的所有菜单
    private List<SysPermission> sysPermissionLeaf(String sysPermissionId) {
        List<SysPermission> sysPermissionList = new ArrayList<>();
        LambdaQueryWrapper<SysPermission> qw = new LambdaQueryWrapper<SysPermission>();
        qw.eq(SysPermission::getId, sysPermissionId);
        qw.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
        SysPermission sysPermission = sysPermissionService.getOne(qw);
        sysPermissionList.add(sysPermission);

        LambdaQueryWrapper<SysPermission> qe = new LambdaQueryWrapper<SysPermission>();
        qe.eq(SysPermission::getParentId, sysPermissionId);
        qw.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
        List<SysPermission> sysPermissions = sysPermissionService.list(qe);
        if (sysPermissions.size() > 0) {
            for (SysPermission permission : sysPermissions) {
                sysPermissionList.addAll(sysPermissionLeaf(permission.getId()));
            }
        }
        return sysPermissionList;
    }

    @ApiOperation(value = "根据应用id，查询应用下的菜单", notes = "根据应用id，查询应用菜单")
    @RequestMapping(value = "/queryApplicationPermission", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> queryApplicationPermission(@RequestParam(name = "applicationId", required = true) String applicationId) {
        Result<List<SysPermissionTree>> result = new Result<List<SysPermissionTree>>();
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("没有该应用");
            return result;
        }
        //queryWrapper:应用菜单中间表条件
        LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();

        queryWrapper.eq(SysApplicationPermission::getApplicationId, applicationId);

        List<SysApplicationPermission> sysApplicationPermissionList = sysApplicationPermissionService.list(queryWrapper);

        List<SysPermission> permissionArrayList = new ArrayList<>();
        List<SysPermission> permissionArrays = new ArrayList<>();
        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissionList) {
            permissionArrayList.addAll(sysPermissionLeaf(sysApplicationPermission.getPermissionId()));
            permissionArrayList = permissionArrayList.stream().distinct().collect(Collectors.toList());
            permissionArrayList.remove(null);
        }


        if (permissionArrayList.size() <= 0 || oConvertUtils.isEmpty(permissionArrayList)) {
            result.error500("该应用下没有菜单");
            return result;
        }
        List<SysPermissionTree> treeList = new ArrayList<SysPermissionTree>();
        getTreeList(treeList, permissionArrayList, null);
        result.setResult(treeList);
        result.setSuccess(true);
        return result;
    }

    /**
     * 批量查询
     */

    @Deprecated
    @ApiOperation(value = "根据应用ids，查询应用菜单", notes = "根据应用ids，查询应用菜单")
    @RequestMapping(value = "/queryApplicationPermissionBatch", method = RequestMethod.GET)
    public Result<List<List<SysPermissionTree>>> queryApplicationPermissionBatch(@RequestParam(name = "applicationIds", required = true) String applicationIds) {
        String[] ids = applicationIds.split(",");
        List<List<SysPermissionTree>> list = new ArrayList<List<SysPermissionTree>>();
        int flag = 0;
        for (String id : ids) {

            //queryWrapper:应用菜单中间表条件
            LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();

            queryWrapper.eq(SysApplicationPermission::getApplicationId, id);

            List<SysApplicationPermission> sysApplicationPermissionList = sysApplicationPermissionService.list(queryWrapper);

            List<SysPermission> permissionArrayList = new ArrayList<>();

            for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissionList) {
                permissionArrayList.addAll(sysPermissionLeaf(sysApplicationPermission.getPermissionId()));
            }

            //菜单是一个list：permissionArrayList-
            //生成树出错
            List<SysPermissionTree> treeList = new ArrayList<SysPermissionTree>();
            getTreeList(treeList, permissionArrayList, null);
            list.add(treeList);
        }
        Result<List<List<SysPermissionTree>>> result = new Result<List<List<SysPermissionTree>>>();
        if (flag <= 0) {
            result.error500("该应用没有功能");
            return result;
        }
        result.setResult(list);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation("查看工作台")
    @RequestMapping(value = "/queryWorkbench", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> queryWorkbench() {
        Result<List<SysPermissionTree>> result = new Result<List<SysPermissionTree>>();
        List<SysPermission> permissionArrayList = new ArrayList<>();
        SysPermission sysPermission = sysPermissionService.getOne(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getName, "工作台")
                .eq(SysPermission::getCreateWays, PermissionProperties.CREATE_WAYS_SYSTEM));
        permissionArrayList.addAll(sysPermissionLeaf(sysPermission.getId()));
        List<SysPermissionTree> treeList = new ArrayList<SysPermissionTree>();
        getTreeList(treeList, permissionArrayList, null);
        result.setResult(treeList);
        return result;
    }

    @ApiOperation("查询当前用户在应用下拥有的菜单")
    @RequestMapping(value = "/queryPermissionsByApplicationId", method = RequestMethod.GET)
    public Result<List<SysPermissionTree>> queryPermissionsByApplicationId(String applicationId) {
        List<SysPermissionTree> sysPermissionTrees = sysPermissionService.queryTreeByApplicationId(applicationId);
        return sysPermissionTrees == null ? badRequest() : ok(sysPermissionTrees);
    }

    @ApiOperation("查询用户在应用下拥有的菜单")
    @RequestMapping(value = "/queryUserOfApplicationPermission", method = RequestMethod.GET)
    public Result<List<SysPermission>> queryUserOfApplicationPermission(String userId, String applicationId) {
        List<SysPermission> sysPermissions = sysPermissionService.queryUserOfApplicationPermission(userId, applicationId);
        return sysPermissions == null ? badRequest() : ok(sysPermissions);
    }

    @ApiOperation("查询角色在应用下拥有的菜单")
    @RequestMapping(value = "/queryRoleOfApplicationPermission", method = RequestMethod.GET)
    public Result<List<SysPermission>> queryRoleOfApplicationPermission(String roleId, String applicationId) {
        List<SysPermission> sysPermissions = sysPermissionService.queryRoleOfApplicationPermission(roleId, applicationId);
        return sysPermissions == null ? badRequest() : ok(sysPermissions);
    }

    @ApiOperation("查询部门在应用下拥有的菜单")
    @RequestMapping(value = "/queryDepartOfApplicationPermission", method = RequestMethod.GET)
    public Result<List<SysPermission>> queryDepartOfApplicationPermission(String departId, String applicationId) {
        List<SysPermission> sysPermissions = sysPermissionService.queryDepartOfApplicationPermission(departId, applicationId);
        return sysPermissions == null ? badRequest() : ok(sysPermissions);
    }

    @ApiOperation("获取所有系统菜单")
    @RequestMapping(value = "/getAllPermissions", method = RequestMethod.GET)
    public Result<List<SysPermission>> getAllPermissions() {
        List<SysPermission> list = sysPermissionService.list();
        return ok(list);
    }
}
