package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.model.SysPermissionTree;
import org.jeecg.modules.system.properties.PermissionProperties;
import org.jeecg.modules.system.service.*;
import org.jeecg.modules.system.service.impl.SysApplicationServiceImpl;
import org.jeecg.modules.system.util.CheckAttrUtil;
import org.jeecg.modules.system.vo.SysApplicationCategoryVo;
import org.jeecg.modules.system.vo.SysApplicationDepartRoleVO;
import org.jeecg.modules.system.vo.SysEditApplicationPermissionVO;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.jeecg.modules.system.util.ResultUtil.badRequest;
import static org.jeecg.modules.system.util.ResultUtil.ok;

/**
 * <p>
 * 应用表 前端控制器
 * </p>
 *
 * @Author Zhouhonghan
 */
@RestController
@Api(tags = "应用管理")
@RequestMapping("/sys/application")
@Slf4j

public class SysApplicationController {

    @Autowired
    private ISysApplicationService sysApplicationService;

    @Autowired
    private SysApplicationServiceImpl sysApplicationServiceImpl;

    @Autowired
    private ISysRoleApplicationService sysRoleApplicationService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private ISysApplicationPermissionService sysApplicationPermissionService;

    @Autowired
    private ISysCategoryService sysCategoryService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private ISysAppRoleService sysAppRoleService;

    @Value("${jeecg.path.upload}")
    private String upLoadPath;

    /**
     * 分页列表查询 --仅仅展示应用概述
     *
     * @param application
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation("分页列表查询")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysApplication>> queryPageList(SysApplication application,
                                                       @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       HttpServletRequest req) {
        Result<IPage<SysApplication>> result = new Result<IPage<SysApplication>>();
        QueryWrapper<SysApplication> queryWrapper = QueryGenerator.initQueryWrapper(application, req.getParameterMap());
        queryWrapper.eq("template", CommonConstant.APPLICATION_TEMPLATE_0);
        Page<SysApplication> page = new Page<SysApplication>(pageNo, pageSize);
        IPage<SysApplication> pageList = sysApplicationService.page(page, queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 展示应用下属菜单
     *
     * @param applicationId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("展示应用下属菜单")
    @RequestMapping(value = "/menuList", method = RequestMethod.GET)
    public Result<IPage<SysPermission>> displayMenu(String applicationId,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<SysPermission>> result = new Result<IPage<SysPermission>>();
        //判断applicationId的合法性
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }

        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }

        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_id", applicationId);
        List<SysApplicationPermission> list = sysApplicationPermissionService.list(queryWrapper);

        List<String> permissionIds = new ArrayList<String>();
        for (SysApplicationPermission sysApplicationPermission : list) {
            permissionIds.add(sysApplicationPermission.getPermissionId());
        }
        Page<SysPermission> page = new Page<SysPermission>();
        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.in("id", permissionIds);
        sysPermissionService.page(page, queryWrapper1);
        result.setResult(page);
        result.setSuccess(true);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * 新增
     *
     * @param application
     * @return
     */
    @ApiOperation("添加")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysApplication> add(@RequestBody SysApplication application) {
        Result<SysApplication> result = new Result<SysApplication>();
        //判断数据有效性 applicationType applicationName applicationCode avatarShape avatarColor
        if (CheckAttrUtil.isNull(application, "applicationType",
                "applicationName", "applicationCode", "avatarColor", "avatarShape")) {
            result.setMessage("数据为空！");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (CheckAttrUtil.includeChineseChar(application.getApplicationCode())) {
            result.setMessage("校验码不能为中文！");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //较验码是否唯一
        if (!sysApplicationService.queryCodeOnly(application.getApplicationCode())) {
            result.setMessage("校验码不唯一！");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //获取创建人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //更新创建人
        application.setCreateBy(user.getUsername());
        application.setCreateTime(new Date());
        //添加默认应用为未发布
        application.setStatus(1);
        if (oConvertUtils.isEmpty(application.getApplicationType())) {
            application.setApplicationType("1");
        }
        boolean bool = sysApplicationService.save(application);
        if (bool) {
            LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
            qw.eq(SysApplication::getApplicationCode, application.getApplicationCode());
            SysApplication sysApplication = sysApplicationService.getOne(qw);
            result.success("添加成功！");
            result.setResult(sysApplication);
        } else {
            result.error500("插入失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param application
     * @return
     */
    @ApiOperation("编辑")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysApplication> edit(@RequestBody SysApplication application) {
        Result<SysApplication> result = new Result<SysApplication>();
        log.info("============================");
        //判断数据有效性 applicationType applicationName applicationCode avatarShape avatarColor id
        if (CheckAttrUtil.isNull(application, "applicationType",
                "applicationName", "applicationCode", "avatarColor", "avatarShape", "id")) {
            result.setMessage("数据为空！");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断id的正确性
        SysApplication sysApplication = sysApplicationService.getById(application.getId());
        if (sysApplication == null) {
            return result.error500("未找到对应实体");
        }

        application.setUpdateTime(new Date());
        //获取修改人
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //更新修改人
        application.setUpdateBy(user.getUsername());
        //默认编辑应用为未发布
        application.setStatus(1);
        if (!oConvertUtils.isEmpty(application.getApplicationType())) {
            application.setApplicationType("1");
        }
        boolean ok = sysApplicationService.updateById(application);
        if (ok) {
            result.success("修改成功!");
        }
        return result;
    }

    /**
     * 通过id删除应用
     *
     * @param id
     * @return
     */
    @ApiOperation("通过id删除应用")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        SysApplication sysApplication = sysApplicationService.getById(id);
        if (sysApplication == null) {
            return Result.Error("应用不存在");
        }
        boolean success = sysApplicationService.deleteApplication(id);
        return success ? Result.OK("删除应用成功") : Result.Error("删除应用失败");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @ApiOperation("通过ids批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        if (oConvertUtils.isEmpty(ids)) {
            return Result.Error("请输入值");
        }
        List<String> sysApplicationIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        List<String> notExist = new ArrayList<String>();
        for (String sysApplicationId : sysApplicationIds) {
            if (Objects.isNull(sysApplicationService.getById(sysApplicationId))) {
                notExist.add(sysApplicationId);
            }
        }
        sysApplicationIds.removeAll(notExist);
        if (notExist.size() == 0) {
            sysApplicationService.deleteBatchApplication(sysApplicationIds);
            return Result.OK("所有应用被删除!");
        } else if (sysApplicationIds.size() > 0) {
            sysApplicationService.deleteBatchApplication(sysApplicationIds);
            return Result.OK("部分应用被删除!错误应用有" + notExist);
        } else {
            return Result.Error("所有应用id错误");
        }
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation("通过id查询")
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysApplication> result = new Result<SysApplication>();
        SysApplication sysApplication = sysApplicationService.getById(id);
        if (oConvertUtils.isEmpty(sysApplication)) {
            result.setResult(null);
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("查询失败，未找到应用");
            result.setSuccess(false);
        } else {
            result.setResult(sysApplication);
            result.setCode(CommonConstant.SC_OK_200);
            result.setMessage("查询成功");
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查询所有
     *
     * @return
     */
    @ApiOperation("查询所有")
    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    public Result<?> queryAll() {
        Result<List<SysApplication>> result = new Result<List<SysApplication>>();
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getTemplate, CommonConstant.APPLICATION_TEMPLATE_0);
        List<SysApplication> sysApplications = sysApplicationService.list(qw);
        if (sysApplications == null) {
            result.setResult(null);
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("查询失败");
            result.setSuccess(false);
        } else {
            result.setResult(sysApplications);
            result.setCode(CommonConstant.SC_OK_200);
            result.setMessage("查询成功");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 校验应用编码唯一
     *
     * @param applicationCode
     * @return
     */
    @ApiOperation("校验角色编码唯一")
    @RequestMapping(value = "/checkApplicationCode", method = RequestMethod.GET)
    public Result checkApplicationCode(@RequestParam(name = "applicationCode", required = true) String applicationCode) {
        Result result = new Result();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_code", applicationCode);
        List<SysApplication> sysApplications = sysApplicationService.list(queryWrapper);
        if (sysApplications.size() == 1) {
            result.setMessage("账号不唯一");
            result.setSuccess(false);
            return result;
        }
        result.setMessage("账号唯一");
        result.setSuccess(true);
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     */
    @ApiOperation(value = "导出excel", produces = "application/octet-stream")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysApplication sysApplication, HttpServletRequest request) {
        // Step.1 组装查询条件
        QueryWrapper<SysApplication> queryWrapper = QueryGenerator.initQueryWrapper(sysApplication, request.getParameterMap());
        //Step.2 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据--------------------
        String selections = request.getParameter("selections");
        if (!oConvertUtils.isEmpty(selections)) {
            queryWrapper.in("id", selections.split(","));
        }
        //update-end--Author:kangxiaolin  Date:20180825 for：[03]用户导出，如果选择数据则只导出相关数据----------------------
        List<SysApplication> pageList = sysApplicationService.list(queryWrapper);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "应用列表");
        mv.addObject(NormalExcelConstants.CLASS, SysApplication.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("应用列表数据", "导出人:" + user.getRealname(), "导出信息");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
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
    @RequestMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // 解决单元测试时强制类型转换失败
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request, false);
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap.size() == 0) {
            return Result.OK("导入文件为空");
        }
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// 获取上传文件对象
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                return sysApplicationService.importExcelCheckApplicationCode(file, params);
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
     * 给指定应用添加菜单
     * 这里应用只能添加一级菜单并且是应用菜单
     *
     * @param applicationId
     * @return
     */
    @ApiOperation("给指定应用添加菜单")
    @RequestMapping(value = "/addPermission", method = RequestMethod.POST)
    public Result<String> addPermission(String applicationId, String ids) {
        Result<String> result = new Result<String>();
        //判断applicationId是否为空
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断applicationId的合法性
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("应用id不规范");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断ids的合法性
        if (oConvertUtils.isEmpty(ids)) {
            result.setMessage("ids不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //将ids加工成List
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));

        //只能添加一级菜单，判断ids里是否符合规范
        List<SysPermission> sysPermissions = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permissionIds));

        if (sysPermissions.size() != permissionIds.size()) {
            result.setMessage("添加的菜单不规范");
            result.setSuccess(false);
            return result;
        }
        for (SysPermission sysPermission : sysPermissions) {
            if (sysPermission.getParentId() != null) {
                result.setMessage("请添加一级菜单");
                result.setSuccess(false);
                return result;
            }
            if (sysPermission.getCreateWays() == PermissionProperties.CREATE_WAYS_APPLICATION) {
                result.setMessage("只能添加应用菜单");
                result.setSuccess(false);
                return result;
            }
        }

        List<SysApplicationPermission> list = sysApplicationPermissionService.list(new LambdaQueryWrapper<SysApplicationPermission>()
                .in(SysApplicationPermission::getPermissionId, permissionIds)
                .eq(SysApplicationPermission::getApplicationId, applicationId));
        if (list.size() != 0) {
            result.setMessage("某些菜单已经被添加");
            result.setSuccess(false);
            return result;
        }
        sysApplicationPermissionService.saveApplicationPermission(applicationId, ids);
        result.setMessage("添加成功");
        result.setSuccess(true);
        return result;
    }

    /**
     * 通过id查询应用下的菜单
     *
     * @param applicationId
     * @return
     */
    @ApiOperation("通过id查询应用下的一级菜单")
    @RequestMapping(value = "/findPermissionById", method = RequestMethod.GET)
    public Result<?> findPermissionById(String applicationId) {
        if (oConvertUtils.isEmpty(applicationId)) {
            return Result.error(500, "id为空");
        }
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getId, applicationId);
        SysApplication sysApplication = sysApplicationService.getOne(qw);
        if (oConvertUtils.isEmpty(sysApplication)) {
            return Result.error(500, "应用不存在");
        }
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_id", applicationId);
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list(queryWrapper);

        List<String> permissionIds = new ArrayList<>();
        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            permissionIds.add(sysApplicationPermission.getPermissionId());
        }

        QueryWrapper queryWrapper1 = new QueryWrapper();
        queryWrapper1.in("id", permissionIds);
        try {
            List<SysPermission> sysPermissions = sysPermissionService.list(queryWrapper1);
            result.setResult(sysPermissions);
            result.setCode(200);
            result.success("成功");
        } catch (Exception e) {
            log.info("应用下没有菜单");
            result.error(200, "应用下没有菜单");
        }
        return result;
    }

    @ApiOperation("批量删除已有的菜单")
    @RequestMapping(value = "/deletePermissionByIds", method = RequestMethod.DELETE)
    public Result<String> deletePermissionByIds(String ids) {
        boolean success = sysPermissionService.deletePermissionByIds(ids);
        return success ? ok() : badRequest();
    }

    @ApiOperation("删除已有的菜单")
    @RequestMapping(value = "/deletePermissionById", method = RequestMethod.DELETE)
    public Result<String> deletePermissionById(String id) {
        sysPermissionService.deletePermission(id);
        return ok();
    }

    @ApiOperation("查询当前应用没有添加的")
    @RequestMapping(value = "/findOtherPermissionById", method = RequestMethod.GET)
    public Result<?> findOtherPermissionById(String applicationId) {
        if (oConvertUtils.isEmpty(applicationId)) {
            return Result.error(500, "id为空");
        }
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getId, applicationId);
        SysApplication sysApplication = sysApplicationService.getOne(qw);
        if (oConvertUtils.isEmpty(sysApplication)) {
            return Result.error(500, "应用不存在");
        }
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_id", applicationId);
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list(queryWrapper);

        List<String> sysPermissionIds = new ArrayList<String>();
        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            sysPermissionIds.add(sysApplicationPermission.getPermissionId());
        }
        List<SysPermission> SysPermissions = new ArrayList<>();
        if (sysPermissionIds.size() > 0) {
            QueryWrapper queryWrapper1 = new QueryWrapper();
            queryWrapper1.notIn("id", sysPermissionIds);
            SysPermissions.addAll(sysPermissionService.list(queryWrapper1));
        } else {
            SysPermissions.addAll(sysPermissionService.list());
        }

        result.setResult(SysPermissions);
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }

    @ApiOperation("查询应用可添加菜单")
    @RequestMapping(value = "/findResiduePermission", method = RequestMethod.GET)
    public Result<?> findOtherPermissionById() {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        // 获取已被应用使用的菜单的Id集合
        List<String> usedPermissionIds = sysApplicationPermissionService.list().stream()
                .map(sap -> sap.getPermissionId())
                .collect(Collectors.toList());
        // 获取未使用菜单集合
        List<SysPermission> permissions = sysPermissionService.list().stream().filter(
                permission -> !usedPermissionIds.contains(permission.getId())
        ).collect(Collectors.toList());
        result.setResult(permissions);
        result.setSuccess(true);
        result.setMessage("查询成功");
        return result;
    }


    @ApiOperation("更新应用下的菜单")
    @RequestMapping(value = "/updatePermissionById", method = RequestMethod.GET)
    public Result updatePermissionById(String applicationId, String ids) {
        Result result = new Result();
        //判断applicationId是否为空
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断applocationId的正确性
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if ("null".equals(ids)) {
            result.setMessage("菜单id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (ids == null) {
            ids = "";
        }
        Boolean success = sysApplicationService.updateApplictionPermission(applicationId, ids);
        if (success) {
            //存储
            result.setMessage("更新成功");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("更新失败");
        result.setSuccess(false);
        return result;
    }


    /**
     * 查询除当前应用外已被使用的菜单
     *
     * @param applicationId 当前应用id
     * @return
     */
    @ApiOperation("查询除当前应用外已被使用的菜单")
    @RequestMapping(value = "/usedPermissionById", method = RequestMethod.GET)
    public Result<?> usedPermissionById(String applicationId) {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        //判断applicationId是否为空
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //判断applocationId的正确性
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //条件构造器 其他应用已添加的一级菜单
        LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();
        queryWrapper.ne(SysApplicationPermission::getApplicationId, applicationId);
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list(queryWrapper);
        //已添加菜单集合
        List<SysPermission> sysPermissions = new ArrayList<SysPermission>();
        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            sysPermissions.add(sysPermissionService.getById(sysApplicationPermission.getPermissionId()));
        }
        //返回结果
        result.setResult(sysPermissions);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * 查询一级菜单
     *
     * @return
     */
    @ApiOperation("查询一级菜单")
    @RequestMapping(value = "/findOneMenuType", method = RequestMethod.GET)
    public Result<?> findOneMenuType() {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("menu_type", 0);
        List<SysPermission> sysPermissions = sysPermissionService.list(queryWrapper);
        result.setSuccess(true);
        result.setResult(sysPermissions);
        return result;
    }

    /**
     * 模糊查询应用名称
     *
     * @return
     */
    @ApiOperation("模糊查询应用名称")
    @GetMapping("/findAppByName")
    public Result<?> findAppByName(@RequestParam(value = "applicationName", required = false, defaultValue = "") String applicationName) {
        Result<List<SysApplication>> result = new Result<>();
        QueryWrapper wrapper = new QueryWrapper();
        wrapper.eq("template", CommonConstant.APPLICATION_TEMPLATE_0);
        if (!StringUtils.isEmpty(applicationName)) {
            wrapper.like("application_name", applicationName);
        }
        try {
            List<SysApplication> list = sysApplicationService.list(wrapper);
            result.setSuccess(true);
            result.setResult(list);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return result.error("查询失败");
        }
    }

    /**
     * 应用模板显示
     */
    @ApiOperation("应用模板显示")
    @GetMapping("/applicationTemplate")
    public Result<SysApplicationCategoryVo> applicationTemplate() {
        SysApplicationCategoryVo sysApplicationCategoryVo = new SysApplicationCategoryVo();
        Result<SysApplicationCategoryVo> result = new Result<SysApplicationCategoryVo>();

        //保存应用模板
        List<SysApplication> applications = new ArrayList<SysApplication>();
        //保存分类模板
        List<SysCategory> sysCategories = new ArrayList<SysCategory>();

        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getTemplate, CommonConstant.APPLICATION_TEMPLATE_1);
        List<SysApplication> applicationList = sysApplicationService.list(qw);
        if (applicationList.size() > 0) {
            applications.addAll(applicationList);
            applications = applications.stream().distinct().collect(Collectors.toList());
            for (SysApplication application : applications) {
                LambdaQueryWrapper<SysCategory> qe = new LambdaQueryWrapper<SysCategory>();
                qe.eq(SysCategory::getId, application.getApplicationType());
                SysCategory sysCategory = sysCategoryService.getOne(qe);
                if (!oConvertUtils.isEmpty(sysCategory)) {
                    sysCategories.add(sysCategory);
                } else {
                    application.setApplicationType("1");
                    LambdaQueryWrapper<SysApplication> qr = new LambdaQueryWrapper<SysApplication>();
                    qr.eq(SysApplication::getId, application.getId());
                    sysApplicationService.update(application, qr);
                }
            }
            SysCategory sysCategory = new SysCategory();
            sysCategory.setId("1");
            sysCategory.setName("其他");
            sysCategories.add(sysCategory);
            sysApplicationCategoryVo.setSysApplication(applications);
            sysApplicationCategoryVo.setSysCategory(sysCategories.stream().distinct().collect(Collectors.toList()));
            result.setResult(sysApplicationCategoryVo);
        }
        //前端联调
        else{
            result.setMessage("应用模板为空");
        }
        return result;
    }

    /**
     * 查询一级菜单 应用菜单
     */
    @ApiOperation("查询应用下的一级菜单")
    @GetMapping("/queryLevelOnePermission")
    public Result<Map<String, Object>> queryLevelOnePermission(String applicationId) {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        //判断applicationId的合法性
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list();
        //已经被选择
        List<String> selecteds = new ArrayList<String>();
        //已经被其他应用选择
        List<String> otherSelecteds = new ArrayList<String>();
        //未选择
        List<String> notSelected = new ArrayList<String>();

        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            if (sysApplicationPermission.getApplicationId().equals(applicationId)) {
                selecteds.add(sysApplicationPermission.getPermissionId());
            } else {
                otherSelecteds.add(sysApplicationPermission.getPermissionId());
            }
        }

        //查询一级菜单 createWays为0 只查询应用菜单
        List<SysPermission> sysPermissions = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
                .eq(SysPermission::getCreateWays, PermissionProperties.CREATE_WAYS_APPLICATION));
        List<SysPermissionTree> allLevelOnes = new ArrayList<SysPermissionTree>();

        for (SysPermission sysPermission : sysPermissions) {
            SysPermissionTree sysPermissionTree = new SysPermissionTree();
            sysPermissionTree.setId(sysPermission.getId());
            sysPermissionTree.setKey(sysPermission.getId());
            sysPermissionTree.setTitle(sysPermission.getName());
            allLevelOnes.add(sysPermissionTree);
            notSelected.add(sysPermission.getId());
        }
        notSelected.removeAll(otherSelecteds);
        notSelected.removeAll(selecteds);

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("selecteds", selecteds);
        map.put("otherSelecteds", otherSelecteds);
        map.put("allLevelOnes", allLevelOnes);
        map.put("notSelected", notSelected);

        result.setResult(map);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * 应用到应用 模板到应用 应用到模板
     *
     * @param applicationId
     * @param template
     * @return
     */
    @ApiOperation("复制应用")
    @GetMapping("/copyApplication")
    public Result<SysApplication> copyApplication(String applicationId, int template) {
        Result<SysApplication> result = new Result<SysApplication>();
        //判断applicationId的合法性
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("应用id不能为空");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (!(template == 1 || template == 0)) {
            result.setMessage("模板不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        result.setCode(CommonConstant.SC_OK_200);
        SysApplication sysApplication = sysApplicationService.copyApplicationById(applicationId, template);
        //复制应用
        result.setResult(sysApplication);
        return result;
    }

    /**
     * 保存模板
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("复制模板")
    @PostMapping("/saveTemplate")
    public Result<SysApplication> saveTemplate(@RequestBody JSONObject jsonObject) {
        Result<SysApplication> result = new Result<SysApplication>();
        SysApplication sysApplication = new SysApplication();
        sysApplication.setId(jsonObject.getString("id"));
        sysApplication.setApplicationName(jsonObject.getString("applicationName"));
        sysApplication.setAvatarColor(jsonObject.getString("avatarColor"));
        sysApplication.setAvatarShape(jsonObject.getString("avatarShape"));
        sysApplication.setApplicationImage(jsonObject.getString("applicationImage"));
        sysApplication.setStatus(Integer.parseInt(jsonObject.getString("status")));
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(sysApplicationService.saveTemplate(sysApplication));
        return result;
    }

    /**
     * 更改应用状态
     *
     * @param ApplicationId
     * @param status
     * @return
     */
    @ApiOperation("更改应用状态")
    @GetMapping("/changeStatus")
    public Result<String> changeStatus(String ApplicationId, int status) {
        Result<String> result = new Result<String>();
        SysApplication sysApplication = new SysApplication();
        sysApplication.setStatus(status);
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getId, ApplicationId);
        qw.eq(SysApplication::getTemplate, CommonConstant.APPLICATION_TEMPLATE_0);
        boolean bool = sysApplicationService.update(sysApplication, qw);
        if (bool) {
            result.OK("成功");
        } else {
            result.error500("失败");
        }
        return result;
    }

    /**
     * 根据应用名查询应用模板
     *
     * @param ApplicationName
     * @param applicationType
     * @return
     */
    @ApiOperation("根据应用名查询应用模板")
    @GetMapping("/queryMenuApplication")
    public Result<List<SysApplication>> queryMenuApplication(String ApplicationName, String applicationType, String template) {
        Result<List<SysApplication>> result = new Result<List<SysApplication>>();
        if (oConvertUtils.isEmpty(ApplicationName)) {
            result = result.OK();
            return result;
        }
        //获取用户应用
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //查询应用
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getApplicationType, applicationType);
        qw.eq(SysApplication::getTemplate, template);
        qw.like(SysApplication::getApplicationName, ApplicationName);
        List<SysApplication> sysApplications = sysApplicationService.list(qw);
        if (sysApplications.size() <= 0) {
            result.error500("未搜索到应用");
            return result;
        }

        if ("0".equals(template)) {
            List<SysApplication> sysApplicationList = sysPermissionService.queryByUserApplication(loginUser.getUsername());
            List<SysApplication> applications = new ArrayList<>();
            if (sysApplications.size() > 0 && sysApplicationList.size() > 0) {
                applications.addAll(sysApplicationList);
                sysApplicationList.removeAll(sysApplications);
                applications.removeAll(sysApplicationList);
                result.setResult(sysApplications);
                result.setCode(200);
                return result;
            } else {
                result.error500("失败");
                return result;
            }

        }
        //模糊查询模板
        result.setResult(sysApplications);
        result.setCode(200);

        return result;
    }

    /**
     * 生成applicationCode
     * 支持14776336个不重合数据
     */
    @ApiOperation("随机生成applicationCode")
    @GetMapping("/getApplicationCode")
    public Result<String> getApplicationCode() {
        Result<String> result = new Result<String>();
        result.success("生成成功");
        result.setResult(sysApplicationService.getApplicationCode());
        return result;
    }


    @ApiOperation("获取应用角色信息")
    @GetMapping("/queryApplicationApprole")
    public Result<List<SysApplicationDepartRoleVO>> queryApplicationApprole(@RequestParam("applicationId") String applicationId) {
        Result<List<SysApplicationDepartRoleVO>> result = new Result<List<SysApplicationDepartRoleVO>>();
        List<SysApplicationDepartRoleVO> memberManagementMessages = sysApplicationService.getMemberManagementMessage(applicationId);
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(memberManagementMessages);
        return result;
    }


    /**
     * 删除成员管理信息
     *
     * @param applicationId
     * @param appRoleId
     * @param id
     * @param selectFlag
     * @return
     */
    @ApiOperation("删除成员管理信息")
    @DeleteMapping("/deleteApplicationApprole")
    public Result<String> deleteApplicationApprole(
            //前端传四个参数，根据selectFlag的数值来区分所传id
            @RequestParam("applicationId") String applicationId,
            @RequestParam("appRoleId") String appRoleId,
            @RequestParam("id") String id,
            @RequestParam("selectFlag") Integer selectFlag) {
        int result = sysApplicationService.deleteMemberManagementMessage(applicationId, appRoleId, id, selectFlag);
        if (result > 0) {
            return Result.OK("删除成功");
        }
        if (result == -1) {
            return Result.Error("selectFlag不合法");
        }
        return Result.Error("删除失败");
    }

    /**
     * 查询 修改角色分配页面
     *
     * @param applicationId 当前应用
     * @param appRoleId     选中的角色
     * @param selectFlag    选择的类型 0用户 1部门 2角色
     * @return
     */
    @ApiOperation("查询 修改角色分配页面")
    @GetMapping("/queryRoleDistribution")
    public Result<Object> queryRoleDistribution(@RequestParam(name = "applicationId", required = true) String applicationId,
                                                @RequestParam(name = "appRoleId", required = true) String appRoleId,
                                                @RequestParam(name = "selectFlag", required = true) Integer selectFlag) {
        //结果集
        Result<Object> result = new Result<Object>();
        //判断applicationId是否合法
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //判断appRoleId是否合法
        if (sysAppRoleService.getOne(new LambdaQueryWrapper<SysApprole>().eq(SysApprole::getId, appRoleId)) == null) {
            result.setMessage("角色id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //判断selectFlag合法性
        if (!(selectFlag == 1 || selectFlag == 0 || selectFlag == 2)) {
            result.setMessage("selectFlag错误");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        Object o = sysApplicationService.queryRoleDistribution(applicationId, appRoleId, selectFlag);
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        result.setResult(o);
        return result;
    }

    /**
     * 添加角色分配页面
     *
     * @return
     */
    @ApiOperation("更新角色分配页面")
    @PostMapping("/updateRoleDistribution")
    public Result updateRoleDistribution(@RequestBody JSONObject json) {
        String applicationId = json.getString("applicationId");
        String appRoleId = json.getString("appRoleId");
        String ids = json.getString("ids");
        Integer selectFlag = json.getInteger("selectFlag");
        //结果集
        Result result = new Result();
        //判断applicationId是否合法
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //判断appRoleId是否合法
        if (sysAppRoleService.getOne(new LambdaQueryWrapper<SysApprole>().eq(SysApprole::getId, appRoleId)) == null) {
            result.setMessage("角色id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //判断selectFlag合法性
        if (!(selectFlag == 1 || selectFlag == 0 || selectFlag == 2)) {
            result.setMessage("selectFlag错误");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }

        Boolean success = sysApplicationService.updateRoleDistribution(applicationId, appRoleId, ids, selectFlag);
        if (!success) {
            result.setMessage("添加失败");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        result.setMessage("添加成功");
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation("查询应用角色")
    @GetMapping("/queryAppRole")
    public Result<Set<String>> queryAppRole(@RequestParam(name = "applicationId", required = true) String applicationId) {
        Result<Set<String>> result = new Result<Set<String>>();
        //判断applicationId是否合法
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("应用id不正确");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        Set<String> appRoleIds = sysApplicationService.queryAppRoleByApplicationId(applicationId);
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(appRoleIds);
        return result;
    }

    /**
     * 菜单树转化为集合
     */
    public List<SysPermission> getPermissionList(List<SysPermissionTree> sysPermissionTreeList) {
        SysPermission sysPermission;
        List<SysPermission> sysPermissionList = new ArrayList<SysPermission>();
        for (SysPermissionTree permissionTree : sysPermissionTreeList) {
            sysPermission = new SysPermission(permissionTree);
            sysPermissionList.add(sysPermission);
            if (!oConvertUtils.isEmpty(permissionTree.getChildren())) {
                sysPermissionList.addAll(getPermissionList(permissionTree.getChildren()));
            }
        }
        return sysPermissionList;
    }

    /**
     * 一级菜单和下边的所有菜单
     */
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

    @ApiOperation("应用菜单拖拽")
    @PostMapping("/editApplicationPermission")
    public Result<String> editApplicationPermission(@RequestBody SysEditApplicationPermissionVO sysEditApplicationPermissionVO) {
        String applicationId = sysEditApplicationPermissionVO.getApplicationId();
        List<SysPermissionTree> sysPermissionTreeList = sysEditApplicationPermissionVO.getSysPermissionTreeList();
        Result<String> result = new Result<String>();
        //前端传输所有菜单
        List<SysPermission> sysPermissionLists = getPermissionList(sysPermissionTreeList);
        List<SysPermission> sysPermissionList = new ArrayList<SysPermission>();
        //删除为list为空地方
        for (SysPermission s : sysPermissionLists) {
            if (oConvertUtils.isEmpty(s)) {
                continue;
            }
            if("".equals(s.getParentId())){
                s.setParentId(null);
            }
            sysPermissionList.add(s);
        }

        LambdaQueryWrapper<SysApplicationPermission> qe = new LambdaQueryWrapper<SysApplicationPermission>();
        qe.eq(SysApplicationPermission::getApplicationId, applicationId);
        List<SysApplicationPermission> Permissions = sysApplicationPermissionService.list(qe);
        if (Permissions.size() != sysPermissionList.size() && oConvertUtils.isEmpty(Permissions)) {
            return result.error500("菜单列表传输错误");
        } else {
            //数据库原有菜单
            List<SysPermission> PermissionList = new ArrayList<SysPermission>();
            List<SysPermission> permissions = new ArrayList<SysPermission>();
            for (SysApplicationPermission sysApplicationPermission : Permissions) {
                PermissionList.addAll(sysPermissionLeaf(sysApplicationPermission.getPermissionId()));
            }
            PermissionList = PermissionList.stream().distinct().collect(Collectors.toList());
            //删除为list为空地方
            for (SysPermission s : PermissionList) {
                if (oConvertUtils.isEmpty(s)) {
                    continue;
                }
                permissions.add(s);
            }
            if (permissions.size() == sysPermissionList.size()) {
                //数据库原有菜单id
                List<String> PermissionIds = new ArrayList<String>();
                //数据库原有的菜单数据
                for (SysPermission permission : permissions) {
                    PermissionIds.add(permission.getId());
                }
                //前端上传菜单id
                List<String> sysPermissionIds = new ArrayList<String>();
                //前端上传菜单1级菜单
                List<String> sysPermissions = new ArrayList<String>();
                //数据库原有的菜单数据
                for (SysPermission permission : sysPermissionList) {
                    sysPermissionIds.add(permission.getId());
                    if (oConvertUtils.isEmpty(permission.getParentId())) {
                        sysPermissions.add(permission.getId());
                    }
                }
                if (sysPermissions.size() <= 0) {
                    return result.error500("参数错误，没有一级菜单");
                }
                //判断掺入菜单数据正确
                int flag = 0;
                for (String sysId : sysPermissionIds) {
                    flag = 0;
                    for (String id : PermissionIds) {
                        if (sysId.equals(id)) {
                            flag += 1;
                        }
                    }
                    if (flag != 1) {
                        return result.error500("参数错误,菜单id错线变化");
                    }
                }

                //删除原有表
                sysPermissionService.removeByIds(PermissionIds);
                //删除中间表数据
                LambdaQueryWrapper<SysApplicationPermission> qr = new LambdaQueryWrapper<SysApplicationPermission>();
                qr.eq(SysApplicationPermission::getApplicationId, applicationId);
                sysApplicationPermissionService.remove(qr);
                //插入前端传回菜单
                sysPermissionService.saveBatch(sysPermissionList);
                //插入前端上传菜单的中间表
                List<SysApplicationPermission> sysApplicationPermissions = new ArrayList<SysApplicationPermission>();
                for (String id : sysPermissions) {
                    sysApplicationPermissions.add(new SysApplicationPermission(applicationId, id));
                }
                sysApplicationPermissionService.saveBatch(sysApplicationPermissions);
                result.setResult(applicationId);
                return result.success("移动成功");
            }
            return result.error500("参数错误");
        }
    }

    @ApiOperation("根据前端KeyWord查询应用或模板")
    @GetMapping("/queryApplicationOrTemplateByKeyWord")
    public Map<String, Set<SysApplication>> queryApplicationOrTemplateByKeyWord(@RequestParam("KeyWord") String KeyWord) {
        //接收实现类所传set
        Set<SysApplication> sysApplicationSet = sysApplicationService.queryApplicationOrTemplateByKeyWord(KeyWord);
        //再次创建三个set对结果进行区分
        Set<SysApplication> applicationsSet = new HashSet<>();
        Set<SysApplication> templatesSet = new HashSet<>();
        Set<SysApplication> errorSet = new HashSet<>();
        //创建一个hashmap对分类结果进行接收
        Map<String, Set<SysApplication>> map = new HashMap<>();
        //对得到的Set进行遍历
        for (SysApplication sysApplication : sysApplicationSet) {
            //如果对象为应用
            if (sysApplication.getTemplate() == 0) {
                applicationsSet.add(sysApplication);
                map.put("Application", applicationsSet);
            }
            if (sysApplication.getTemplate() == 1) {
                templatesSet.add(sysApplication);
                map.put("Template", templatesSet);
            }
            if (sysApplication.getTemplate() != 0 && sysApplication.getTemplate() != 1) {
                errorSet.add(sysApplication);
                map.put("模板状态error,以下为所查到的内容:", errorSet);
            }
        }
        return map;
    }

    @ApiOperation("根据应用id查询创建者")
    @GetMapping("/selectApplicationCreator")
    public Result<SysUser> selectApplicationCreator(@RequestParam("applicationId") String applicationId) {
        return ok(sysApplicationService.selectApplicationCreator(applicationId));
    }

    @ApiOperation("更新应用下角色配置菜单")
    @PostMapping("/updateApplicationPermissionOfRole")
    public Result<String> updateApplicationPermissionOfRole(String applicationId, String roleId, String ids) {
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        permissionIds.remove("");
        Boolean success = sysApplicationService.updateApplicationPermissionOfRole(applicationId, roleId, permissionIds);
        return success ? ok() : badRequest();
    }

    @ApiOperation("更新应用下部门配置菜单")
    @PostMapping("/updateApplicationPermissionOfDepart")
    public Result<Object> updateApplicationPermissionOfDepart(String applicationId, String departId, String ids) {
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        permissionIds.remove("");
        Boolean success = sysApplicationService.updateApplicationPermissionOfDepart(applicationId, departId, permissionIds);
        return success ? ok() : badRequest();


    }

    @ApiOperation("展示应用设置下所有菜单数据")
    @GetMapping("/queryPermissionDisplayApplicationData")
    public Result<Page<SysPermission>> queryPermissionDisplayApplicationData(@RequestParam("applicationId") String applicationId,
                                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        //接收实现类查询出的数据库数据
        List<SysPermission> sysPermissions = sysApplicationService.displayApplicationPermissions(applicationId);
        //创建一个result接收结果
        Result<Page<SysPermission>> result = new Result<>();
        if (Objects.isNull(sysPermissions)) {
            result.setCode(500);
        }
        //调用修改类型方法
        sysApplicationServiceImpl.changeUrlType(sysPermissions);
        //添加一个分页
        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        page.setRecords(sysPermissions);
        result.setResult(page);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @ApiOperation(value = "应用设置下菜单数据导出", produces = "application/octet-stream")
    @GetMapping("/applicationDataOutput")
    public ModelAndView applicationDataOutput(@RequestParam("permissionId") String permissionIds) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysPermission> sysPermissions = sysApplicationService.queryApplicationPermissionsAndOutput(permissionIds);
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "应用数据");
        mv.addObject(NormalExcelConstants.CLASS, SysPermission.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("应用页面数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, sysPermissions);
        return mv;
    }

    @ApiOperation(value = "根据表单id查询应用")
    @GetMapping("/getApplicationByFormId")
    public Result<SysApplication> getApplicationByFormId(String formId) {
        SysApplication sysApplication = sysApplicationService.getApplicationByFormId(formId);
        return sysApplication == null ? badRequest() : ok(sysApplication);
    }

    @ApiOperation(value = "根据应用id查询应用下所有菜单")
    @GetMapping("/getApplicationPermissionById")
    public Result<List<SysPermission>> getApplicationPermissionById(@RequestParam("applicationId") String applicationId){
        Result<List<SysPermission>> result = new Result<>();
        List<SysPermission> sysPermissions = sysApplicationService.displayApplicationPermissions(applicationId);
        result.setResult(sysPermissions);
        result.setCode(200);
        return result;
    }
}