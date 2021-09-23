package org.jeecg.modules.system.controller;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.TreeModel;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.vo.SysApplicationRoleVO;
import org.jeecg.modules.workflow.service.WfUserService;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 角色表 前端控制器
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
@RestController
@Api(tags = "角色管理")
@RequestMapping("/sys/role")
@Slf4j
public class SysRoleController {
    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysPermissionDataRuleService sysPermissionDataRuleService;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysRoleApplicationService sysRoleApplicationService;

    @Autowired
    private ISysApplicationService sysApplicationService;

    @Autowired
    private ISysDepartService sysDepartService;

    @Autowired
    private ISysDepartPermissionService sysDepartPermissionService;

    @Autowired
    private WfUserService wfUserService;




    /**
     * 分页列表查询
     *
     * @param role
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation("分页列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysRole>> queryPageList(SysRole role,
                                                @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                HttpServletRequest req) {
        Result<IPage<SysRole>> result = new Result<IPage<SysRole>>();
        QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(role, req.getParameterMap());
        Page<SysRole> page = new Page<SysRole>(pageNo, pageSize);
        IPage<SysRole> pageList = sysRoleService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param role
     * @return
     */
    @ApiOperation("添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    //@RequiresRoles({"admin"})
    public Result<SysRole> add(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        try {
            role.setCreateTime(new Date());
            sysRoleService.save(role);
            String indexId = sysPermissionService.getOne(new QueryWrapper<SysPermission>().lambda()
                    .eq(SysPermission::getName, PermissionProperties.PERMISSION_NAME_INDEX)
                    .eq(SysPermission::getCreateWays, PermissionProperties.CREATE_WAYS_SYSTEM)).getId();
            sysRolePermissionService.saveRolePermission(role.getId(), indexId);
            wfUserService.addGroup(role.getId(), role.getRoleName(), null);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param role
     * @return
     */
    //@RequiresRoles({"admin"})
    @ApiOperation("编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysRole> edit(@RequestBody SysRole role) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(role.getId());
        wfUserService.deleteGroup(role.getId());
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            role.setUpdateTime(new Date());
            boolean ok = sysRoleService.updateById(role);
            wfUserService.addGroup(role.getId(), role.getRoleName(), null);

            //TODO 返回false说明什么？
            if (ok) {
                result.success("修改成功!");
            }
        }

        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    //@RequiresRoles({"admin"})
    @ApiOperation("通过id删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {

//		判断id是否存在
        if (oConvertUtils.isEmpty(sysRoleService.getById(id))) {
            return Result.error("角色不存在");
        }
        sysRoleService.deleteRole(id);
        wfUserService.deleteGroup(id);
        return Result.ok("删除角色成功");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    //@RequiresRoles({"admin"})
    @ApiOperation("批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<SysRole> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        Result<SysRole> result = new Result<SysRole>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中角色！");
        } else {
            sysRoleService.deleteBatchRole(ids.split(","));
            result.success("删除角色成功!");
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation("通过id查询")
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysRole> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysRole> result = new Result<SysRole>();
        SysRole sysrole = sysRoleService.getById(id);
        if (sysrole == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysrole);
            result.setSuccess(true);
        }
        return result;
    }

    @ApiOperation("查询所有")
    @RequestMapping(value = "/queryall", method = RequestMethod.GET)
    public Result<List<SysRole>> queryall() {
        Result<List<SysRole>> result = new Result<>();
        List<SysRole> list = sysRoleService.list();
        if (list == null || list.size() <= 0) {
            result.error500("未找到角色信息");
        } else {
            result.setResult(list);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 校验角色编码唯一
     */
    @ApiOperation("校验角色编码唯一")
    @RequestMapping(value = "/checkRoleCode", method = RequestMethod.GET)
    public Result<Boolean> checkUsername(String id, String roleCode) {
        Result<Boolean> result = new Result<>();
        result.setResult(true);//如果此参数为false则程序发生异常
        log.info("--验证角色编码是否唯一---id:" + id + "--roleCode:" + roleCode);
        try {
            SysRole role = null;
            if (oConvertUtils.isNotEmpty(id)) {
                role = sysRoleService.getById(id);
            }
            SysRole newRole = sysRoleService.getOne(new QueryWrapper<SysRole>().lambda().eq(SysRole::getRoleCode, roleCode));
            if (newRole != null) {
                //如果根据传入的roleCode查询到信息了，那么就需要做校验了。
                if (role == null) {
                    //role为空=>新增模式=>只要roleCode存在则返回false
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                } else if (!id.equals(newRole.getId())) {
                    //否则=>编辑模式=>判断两者ID是否一致-
                    result.setSuccess(false);
                    result.setMessage("角色编码已存在");
                    return result;
                }
            }
        } catch (Exception e) {
            result.setSuccess(false);
            result.setResult(false);
            result.setMessage(e.getMessage());
            return result;
        }
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @ApiOperation(value = "导出excel", produces = "application/octet-stream")
    @RequestMapping(value = "/exportXls", method = RequestMethod.GET)
    public ModelAndView exportXls(SysRole sysRole, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysRole> queryWrapper = QueryGenerator.initQueryWrapper(sysRole, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysRole> pageList = sysRoleService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "角色列表");
        mv.addObject(NormalExcelConstants.CLASS, SysRole.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("角色列表数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("通过excel导入数据")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                return sysRoleService.importExcelCheckRoleCode(file, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("文件导入失败:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("文件导入失败！");
    }

    /**
     * 查询数据规则数据
     */
    @ApiOperation("查询数据规则数据")
    @GetMapping(value = "/datarule/{permissionId}/{roleId}")
    public Result<?> loadDatarule(@PathVariable("permissionId") String permissionId, @PathVariable("roleId") String roleId) {
        List<SysPermissionDataRule> list = sysPermissionDataRuleService.getPermRuleListByPermId(permissionId);
        if (list == null || list.size() == 0) {
            return Result.error("未找到权限配置信息");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("datarule", list);
            LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getPermissionId, permissionId)
                    .isNotNull(SysRolePermission::getDataRuleIds)
                    .eq(SysRolePermission::getRoleId, roleId);
            SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                return Result.error("未找到角色菜单配置信息");
            } else {
                String drChecked = sysRolePermission.getDataRuleIds();
                if (oConvertUtils.isNotEmpty(drChecked)) {
                    map.put("drChecked", drChecked.endsWith(",") ? drChecked.substring(0, drChecked.length() - 1) : drChecked);
                }
            }
            return Result.ok(map);
            //TODO 以后按钮权限的查询也走这个请求 无非在map中多加两个key
        }
    }

    /**
     * 保存数据规则至角色菜单关联表
     */
    @ApiOperation("保存数据规则至角色菜单关联表")
    @PostMapping(value = "/datarule")
    public Result<?> saveDatarule(@RequestBody JSONObject jsonObject) {
        try {
            String permissionId = jsonObject.getString("permissionId");
            String roleId = jsonObject.getString("roleId");
            String dataRuleIds = jsonObject.getString("dataRuleIds");
            log.info("保存数据规则>>" + "菜单ID:" + permissionId + "角色ID:" + roleId + "数据权限ID:" + dataRuleIds);
            LambdaQueryWrapper<SysRolePermission> query = new LambdaQueryWrapper<SysRolePermission>()
                    .eq(SysRolePermission::getPermissionId, permissionId)
                    .eq(SysRolePermission::getRoleId, roleId);
            SysRolePermission sysRolePermission = sysRolePermissionService.getOne(query);
            if (sysRolePermission == null) {
                return Result.error("请先保存角色菜单权限!");
            } else {
                sysRolePermission.setDataRuleIds(dataRuleIds);
                this.sysRolePermissionService.updateById(sysRolePermission);
            }
        } catch (Exception e) {
            log.error("SysRoleController.saveDatarule()发生异常：" + e.getMessage(), e);
            return Result.error("保存失败");
        }
        return Result.ok("保存成功!");
    }


    @ApiOperation("查看系统菜单树")
    @RequestMapping(value = "/querySysTreeList", method = RequestMethod.GET)
    public Result<Map<String, Object>> querySysTreeList(@RequestParam(name = "departId", required = false) String departId) {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        List<String> ids = new ArrayList<>();
        Map<String, Object> resMap = new HashMap<String, Object>();
        List<TreeModel> treeList = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            if (departId != null) {
                String parentId = sysDepartService.getById(departId).getParentId();
                if (Strings.isNotBlank(parentId)) {
                    LambdaQueryWrapper<SysDepartPermission> queryWrapper = new LambdaQueryWrapper<>();
                    queryWrapper.eq(SysDepartPermission::getDepartId, parentId);
                    List<String> sysPermissionIds = sysDepartPermissionService.list(queryWrapper)
                            .stream()
                            .map(SysDepartPermission::getPermissionId)
                            .collect(Collectors.toList());
                    if (sysPermissionIds.size() == 0) {
                        resMap.put("treeList", treeList); //全部树节点数据
                        resMap.put("ids", ids);//全部树ids
                        result.setResult(resMap);
                        result.setSuccess(true);
                        return result;
                    }
                    query.in(SysPermission::getId, sysPermissionIds);
                }
            }
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query)
                    .stream()
                    .filter(sysPermission -> sysPermission.getCreateWays() == 1)
                    .collect(Collectors.toList());
            ids = list.stream().parallel().map(SysPermission::getId).collect(Collectors.toList());
            getTreeModelList(treeList, list, null);
            int n=0;
            n=getInt(treeList,n);
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            result.setResult(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private int getInt(List<TreeModel> treeList,int n){
        for (TreeModel treeModel : treeList) {
            n++;
            if (treeModel.getChildren()!=null){
                n=getInt(treeModel.getChildren(),n);
            }
        }
        return n;
    }


    /**
     * 用户角色授权功能，查询菜单权限树
     *
     * @param request
     * @return
     */
    @ApiOperation("用户角色授权功能，查询菜单权限树")
    @RequestMapping(value = "/queryTreeList", method = RequestMethod.GET)
    public Result<Map<String, Object>> queryTreeList(HttpServletRequest request) {
        Result<Map<String, Object>> result = new Result<>();
        //全部权限ids
        List<String> ids = new ArrayList<>();
        try {
            LambdaQueryWrapper<SysPermission> query = new LambdaQueryWrapper<SysPermission>();
            query.eq(SysPermission::getDelFlag, CommonConstant.DEL_FLAG_0);
            query.orderByAsc(SysPermission::getSortNo);
            List<SysPermission> list = sysPermissionService.list(query);
            for (SysPermission sysPer : list) {
                ids.add(sysPer.getId());
            }
            List<TreeModel> treeList = new ArrayList<>();
            getTreeModelList(treeList, list, null);
            Map<String, Object> resMap = new HashMap<String, Object>();
            resMap.put("treeList", treeList); //全部树节点数据
            resMap.put("ids", ids);//全部树ids
            result.setResult(resMap);
            result.setSuccess(true);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return result;
    }

    private void getTreeModelList(List<TreeModel> treeList, List<SysPermission> metaList, TreeModel temp) {
        for (SysPermission permission : metaList) {
            String tempPid = permission.getParentId();
            TreeModel tree = new TreeModel(permission.getId(), tempPid, permission.getName(), permission.getRuleFlag(), permission.isLeaf());
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
     * 通过id查找应用
     *
     * @return
     */
    @ApiOperation("通过id查找应用")
    @GetMapping(value = "/queryApplicationByid")
    public Result<List<SysApplication>> queryApplicationByid(String id) {
        Result<List<SysApplication>> result = new Result<>();
        //先判断id的正确性
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", id);
        SysRole sysRole = sysRoleService.getOne(queryWrapper);
        if (sysRole == null) {
            result.setSuccess(false);
            result.setMessage("角色id不正确");
            return result;
        }

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("role_id", id);
        List<SysRoleApplication> sysRoleApplications = sysRoleApplicationService.list(queryWrapper1);
        List<String> applicationIds = new ArrayList<String>();
        if (sysRoleApplications.size() == 0) {
            result.setSuccess(true);
            result.setMessage("角色无应用");
            return result;
        }
        for (SysRoleApplication sysRoleApplication : sysRoleApplications) {
            applicationIds.add(sysRoleApplication.getApplicationId());
        }

        QueryWrapper queryWrapper2 = new QueryWrapper();
        queryWrapper2.in("id", applicationIds);
        List<SysApplication> sysApplications = sysApplicationService.list(queryWrapper2);
        result.setSuccess(true);
        result.setResult(sysApplications);
        return result;
    }

    /**
     * 通过应用id删除角色下的应用
     *
     * @return
     */
    @ApiOperation("通过应用id删除角色下的应用")
    @DeleteMapping(value = "/deleteByApplicationIds")
    public Result deleteByApplicationIds(String roleId, String ids) {
        Result result = new Result();
        //判断roleId的正确性
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("id", roleId);
        SysRole sysRole = sysRoleService.getOne(queryWrapper1);
        System.out.println(sysRole);
        //roleId不正确
        if (sysRole == null) {
            result.setMessage("角色id不正确");
            result.setSuccess(false);
            return result;
        }
        //将ids加工成数组
        String[] applicationIds = ids.split(",");
        //条件查询器
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.in("application_id", applicationIds);
        boolean remove = sysRoleApplicationService.remove(queryWrapper);
        if (remove) {
            result.setMessage("删除成功");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("删除失败");
        result.setSuccess(false);
        return result;
    }

    /**
     * 给指定角色添加应用
     *
     * @param roleId
     * @param ids    应用id数组
     * @return
     */
    @ApiOperation("更新角色下的应用")
    @PostMapping(value = "/updateApplicationByIds")
    public Result updateApplicationByIds(String roleId, String ids) {
        Result result = new Result();
        //判断roleId的正确性
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("id", roleId);
        SysRole sysRole = sysRoleService.getOne(queryWrapper);
        System.out.println(sysRole);
        //roleId不正确
        if (sysRole == null) {
            result.setMessage("角色id不正确");
            result.setSuccess(false);
            return result;
        }
        //先删除已有的
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.eq("role_id", roleId);
        sysRoleApplicationService.remove(queryWrapper1);
        //将ids加工成List
        List<String> applicationIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        //执行保存操作
        for (String applicationId : applicationIds) {
            sysRoleApplicationService.save(new SysRoleApplication(roleId, applicationId));
        }
        result.setMessage("更新成功");
        result.setSuccess(true);
        return result;
    }

    @ApiOperation("查询所有应用")
    @GetMapping(value = "/queryApplicationRole")
    public Result<?> queryApplicationRole() {
        Result<List<SysApplicationRoleVO>> result = new Result<List<SysApplicationRoleVO>>();
        List<SysApplication> sysApplications = sysApplicationService.list(null);

        List<SysApplicationRoleVO> sysApplicationRoleVOS = new ArrayList<>();
        QueryWrapper queryWrapper = new QueryWrapper();

        for (SysApplication sysApplication : sysApplications) {
            List<String> roles = new ArrayList<String>();
            queryWrapper.eq("application_id", sysApplication.getId());
            for (SysRoleApplication sysRoleApplication : (List<SysRoleApplication>) sysRoleApplicationService.list(queryWrapper)) {
                roles.add(sysRoleApplication.getRoleId());
            }
            sysApplicationRoleVOS.add(new SysApplicationRoleVO(sysApplication, roles));
            queryWrapper.clear();
        }
        result.setResult(sysApplicationRoleVOS);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation("查询当前登陆角色创建的应用")
    @GetMapping(value = "/queryApplicationByLogin")
    public Result<List<SysApplication>> queryApplicationByLogin() {
        //封装结果集
        Result<List<SysApplication>> result = new Result<List<SysApplication>>();
        //获取当前操作人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询当前用户创建的所有应用
        List<SysApplication> sysApplications = sysApplicationService.list(new LambdaQueryWrapper<SysApplication>()
                .eq(SysApplication::getCreateBy, user.getUsername()));
        result.setResult(sysApplications);
        result.setMessage("查询成功");
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @ApiOperation("查询所有角色")
    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    public Result<List<SysRole>> queryAll() {
        Result<List<SysRole>> result = new Result<List<SysRole>>();
        List<SysRole> sysRoles = sysRoleService.list(null);
        result.setResult(sysRoles);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @ApiOperation("模糊搜索角色")
    @RequestMapping(value = "/queryByKeyword", method = RequestMethod.GET)
    public Result<List<SysRole>> queryByKeyword(String keyword) {
        Result<List<SysRole>> result = new Result<List<SysRole>>();
        List<SysRole> sysRoles = sysRoleService.list(new LambdaQueryWrapper<SysRole>()
                .like(SysRole::getRoleName, keyword));
        result.setResult(sysRoles);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }
}
