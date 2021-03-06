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
 * ????????? ???????????????
 * </p>
 *
 * @Author Zhouhonghan
 */
@RestController
@Api(tags = "????????????")
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
     * ?????????????????? --????????????????????????
     *
     * @param application
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation("??????????????????")
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
     * ????????????????????????
     *
     * @param applicationId
     * @param pageNo
     * @param pageSize
     * @return
     */
    @ApiOperation("????????????????????????")
    @RequestMapping(value = "/menuList", method = RequestMethod.GET)
    public Result<IPage<SysPermission>> displayMenu(String applicationId,
                                                    @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                    @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        Result<IPage<SysPermission>> result = new Result<IPage<SysPermission>>();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }

        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("??????id?????????");
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
     * ??????
     *
     * @param application
     * @return
     */
    @ApiOperation("??????")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysApplication> add(@RequestBody SysApplication application) {
        Result<SysApplication> result = new Result<SysApplication>();
        //????????????????????? applicationType applicationName applicationCode avatarShape avatarColor
        if (CheckAttrUtil.isNull(application, "applicationType",
                "applicationName", "applicationCode", "avatarColor", "avatarShape")) {
            result.setMessage("???????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (CheckAttrUtil.includeChineseChar(application.getApplicationCode())) {
            result.setMessage("???????????????????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //?????????????????????
        if (!sysApplicationService.queryCodeOnly(application.getApplicationCode())) {
            result.setMessage("?????????????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //???????????????
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //???????????????
        application.setCreateBy(user.getUsername());
        application.setCreateTime(new Date());
        //??????????????????????????????
        application.setStatus(1);
        if (oConvertUtils.isEmpty(application.getApplicationType())) {
            application.setApplicationType("1");
        }
        boolean bool = sysApplicationService.save(application);
        if (bool) {
            LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
            qw.eq(SysApplication::getApplicationCode, application.getApplicationCode());
            SysApplication sysApplication = sysApplicationService.getOne(qw);
            result.success("???????????????");
            result.setResult(sysApplication);
        } else {
            result.error500("????????????");
        }
        return result;
    }

    /**
     * ??????
     *
     * @param application
     * @return
     */
    @ApiOperation("??????")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysApplication> edit(@RequestBody SysApplication application) {
        Result<SysApplication> result = new Result<SysApplication>();
        log.info("============================");
        //????????????????????? applicationType applicationName applicationCode avatarShape avatarColor id
        if (CheckAttrUtil.isNull(application, "applicationType",
                "applicationName", "applicationCode", "avatarColor", "avatarShape", "id")) {
            result.setMessage("???????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????id????????????
        SysApplication sysApplication = sysApplicationService.getById(application.getId());
        if (sysApplication == null) {
            return result.error500("?????????????????????");
        }

        application.setUpdateTime(new Date());
        //???????????????
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //???????????????
        application.setUpdateBy(user.getUsername());
        //??????????????????????????????
        application.setStatus(1);
        if (!oConvertUtils.isEmpty(application.getApplicationType())) {
            application.setApplicationType("1");
        }
        boolean ok = sysApplicationService.updateById(application);
        if (ok) {
            result.success("????????????!");
        }
        return result;
    }

    /**
     * ??????id????????????
     *
     * @param id
     * @return
     */
    @ApiOperation("??????id????????????")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@RequestParam(name = "id", required = true) String id) {
        SysApplication sysApplication = sysApplicationService.getById(id);
        if (sysApplication == null) {
            return Result.Error("???????????????");
        }
        boolean success = sysApplicationService.deleteApplication(id);
        return success ? Result.OK("??????????????????") : Result.Error("??????????????????");
    }

    /**
     * ????????????
     *
     * @param ids
     * @return
     */
    @ApiOperation("??????ids????????????")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<String> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        if (oConvertUtils.isEmpty(ids)) {
            return Result.Error("????????????");
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
            return Result.OK("?????????????????????!");
        } else if (sysApplicationIds.size() > 0) {
            sysApplicationService.deleteBatchApplication(sysApplicationIds);
            return Result.OK("?????????????????????!???????????????" + notExist);
        } else {
            return Result.Error("????????????id??????");
        }
    }

    /**
     * ??????id??????
     *
     * @param id
     * @return
     */
    @ApiOperation("??????id??????")
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        Result<SysApplication> result = new Result<SysApplication>();
        SysApplication sysApplication = sysApplicationService.getById(id);
        if (oConvertUtils.isEmpty(sysApplication)) {
            result.setResult(null);
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("??????????????????????????????");
            result.setSuccess(false);
        } else {
            result.setResult(sysApplication);
            result.setCode(CommonConstant.SC_OK_200);
            result.setMessage("????????????");
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * ????????????
     *
     * @return
     */
    @ApiOperation("????????????")
    @RequestMapping(value = "/queryAll", method = RequestMethod.GET)
    public Result<?> queryAll() {
        Result<List<SysApplication>> result = new Result<List<SysApplication>>();
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getTemplate, CommonConstant.APPLICATION_TEMPLATE_0);
        List<SysApplication> sysApplications = sysApplicationService.list(qw);
        if (sysApplications == null) {
            result.setResult(null);
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setMessage("????????????");
            result.setSuccess(false);
        } else {
            result.setResult(sysApplications);
            result.setCode(CommonConstant.SC_OK_200);
            result.setMessage("????????????");
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * ????????????????????????
     *
     * @param applicationCode
     * @return
     */
    @ApiOperation("????????????????????????")
    @RequestMapping(value = "/checkApplicationCode", method = RequestMethod.GET)
    public Result checkApplicationCode(@RequestParam(name = "applicationCode", required = true) String applicationCode) {
        Result result = new Result();
        QueryWrapper queryWrapper = new QueryWrapper();
        queryWrapper.eq("application_code", applicationCode);
        List<SysApplication> sysApplications = sysApplicationService.list(queryWrapper);
        if (sysApplications.size() == 1) {
            result.setMessage("???????????????");
            result.setSuccess(false);
            return result;
        }
        result.setMessage("????????????");
        result.setSuccess(true);
        return result;
    }

    /**
     * ??????excel
     *
     * @param request
     */
    @ApiOperation(value = "??????excel", produces = "application/octet-stream")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(SysApplication sysApplication, HttpServletRequest request) {
        // Step.1 ??????????????????
        QueryWrapper<SysApplication> queryWrapper = QueryGenerator.initQueryWrapper(sysApplication, request.getParameterMap());
        //Step.2 AutoPoi ??????Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        //update-begin--Author:kangxiaolin  Date:20180825 for???[03]?????????????????????????????????????????????????????????--------------------
        String selections = request.getParameter("selections");
        if (!oConvertUtils.isEmpty(selections)) {
            queryWrapper.in("id", selections.split(","));
        }
        //update-end--Author:kangxiaolin  Date:20180825 for???[03]?????????????????????????????????????????????????????????----------------------
        List<SysApplication> pageList = sysApplicationService.list(queryWrapper);
        //??????????????????
        mv.addObject(NormalExcelConstants.FILE_NAME, "????????????");
        mv.addObject(NormalExcelConstants.CLASS, SysApplication.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        ExportParams exportParams = new ExportParams("??????????????????", "?????????:" + user.getRealname(), "????????????");
        exportParams.setImageBasePath(upLoadPath);
        mv.addObject(NormalExcelConstants.PARAMS, exportParams);
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * ??????excel????????????
     *
     * @param request
     * @param response
     * @return
     */
    @ApiOperation("??????excel????????????")
    @RequestMapping(value = "/importExcel")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
//        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        // ?????????????????????????????????????????????
        MultipartHttpServletRequest multipartRequest = new StandardMultipartHttpServletRequest(request, false);
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        if (fileMap.size() == 0) {
            return Result.OK("??????????????????");
        }
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            MultipartFile file = entity.getValue();// ????????????????????????
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            try {
                return sysApplicationService.importExcelCheckApplicationCode(file, params);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return Result.error("??????????????????:" + e.getMessage());
            } finally {
                try {
                    file.getInputStream().close();
                } catch (IOException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return Result.error("?????????????????????");
    }

    /**
     * ???????????????????????????
     * ?????????????????????????????????????????????????????????
     *
     * @param applicationId
     * @return
     */
    @ApiOperation("???????????????????????????")
    @RequestMapping(value = "/addPermission", method = RequestMethod.POST)
    public Result<String> addPermission(String applicationId, String ids) {
        Result<String> result = new Result<String>();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????applicationId????????????
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????ids????????????
        if (oConvertUtils.isEmpty(ids)) {
            result.setMessage("ids????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //???ids?????????List
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));

        //?????????????????????????????????ids?????????????????????
        List<SysPermission> sysPermissions = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
                .in(SysPermission::getId, permissionIds));

        if (sysPermissions.size() != permissionIds.size()) {
            result.setMessage("????????????????????????");
            result.setSuccess(false);
            return result;
        }
        for (SysPermission sysPermission : sysPermissions) {
            if (sysPermission.getParentId() != null) {
                result.setMessage("?????????????????????");
                result.setSuccess(false);
                return result;
            }
            if (sysPermission.getCreateWays() == PermissionProperties.CREATE_WAYS_APPLICATION) {
                result.setMessage("????????????????????????");
                result.setSuccess(false);
                return result;
            }
        }

        List<SysApplicationPermission> list = sysApplicationPermissionService.list(new LambdaQueryWrapper<SysApplicationPermission>()
                .in(SysApplicationPermission::getPermissionId, permissionIds)
                .eq(SysApplicationPermission::getApplicationId, applicationId));
        if (list.size() != 0) {
            result.setMessage("???????????????????????????");
            result.setSuccess(false);
            return result;
        }
        sysApplicationPermissionService.saveApplicationPermission(applicationId, ids);
        result.setMessage("????????????");
        result.setSuccess(true);
        return result;
    }

    /**
     * ??????id????????????????????????
     *
     * @param applicationId
     * @return
     */
    @ApiOperation("??????id??????????????????????????????")
    @RequestMapping(value = "/findPermissionById", method = RequestMethod.GET)
    public Result<?> findPermissionById(String applicationId) {
        if (oConvertUtils.isEmpty(applicationId)) {
            return Result.error(500, "id??????");
        }
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getId, applicationId);
        SysApplication sysApplication = sysApplicationService.getOne(qw);
        if (oConvertUtils.isEmpty(sysApplication)) {
            return Result.error(500, "???????????????");
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
            result.success("??????");
        } catch (Exception e) {
            log.info("?????????????????????");
            result.error(200, "?????????????????????");
        }
        return result;
    }

    @ApiOperation("???????????????????????????")
    @RequestMapping(value = "/deletePermissionByIds", method = RequestMethod.DELETE)
    public Result<String> deletePermissionByIds(String ids) {
        boolean success = sysPermissionService.deletePermissionByIds(ids);
        return success ? ok() : badRequest();
    }

    @ApiOperation("?????????????????????")
    @RequestMapping(value = "/deletePermissionById", method = RequestMethod.DELETE)
    public Result<String> deletePermissionById(String id) {
        sysPermissionService.deletePermission(id);
        return ok();
    }

    @ApiOperation("?????????????????????????????????")
    @RequestMapping(value = "/findOtherPermissionById", method = RequestMethod.GET)
    public Result<?> findOtherPermissionById(String applicationId) {
        if (oConvertUtils.isEmpty(applicationId)) {
            return Result.error(500, "id??????");
        }
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getId, applicationId);
        SysApplication sysApplication = sysApplicationService.getOne(qw);
        if (oConvertUtils.isEmpty(sysApplication)) {
            return Result.error(500, "???????????????");
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
        result.setMessage("????????????");
        return result;
    }

    @ApiOperation("???????????????????????????")
    @RequestMapping(value = "/findResiduePermission", method = RequestMethod.GET)
    public Result<?> findOtherPermissionById() {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        // ????????????????????????????????????Id??????
        List<String> usedPermissionIds = sysApplicationPermissionService.list().stream()
                .map(sap -> sap.getPermissionId())
                .collect(Collectors.toList());
        // ???????????????????????????
        List<SysPermission> permissions = sysPermissionService.list().stream().filter(
                permission -> !usedPermissionIds.contains(permission.getId())
        ).collect(Collectors.toList());
        result.setResult(permissions);
        result.setSuccess(true);
        result.setMessage("????????????");
        return result;
    }


    @ApiOperation("????????????????????????")
    @RequestMapping(value = "/updatePermissionById", method = RequestMethod.GET)
    public Result updatePermissionById(String applicationId, String ids) {
        Result result = new Result();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????applocationId????????????
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if ("null".equals(ids)) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (ids == null) {
            ids = "";
        }
        Boolean success = sysApplicationService.updateApplictionPermission(applicationId, ids);
        if (success) {
            //??????
            result.setMessage("????????????");
            result.setSuccess(true);
            return result;
        }
        result.setMessage("????????????");
        result.setSuccess(false);
        return result;
    }


    /**
     * ?????????????????????????????????????????????
     *
     * @param applicationId ????????????id
     * @return
     */
    @ApiOperation("?????????????????????????????????????????????")
    @RequestMapping(value = "/usedPermissionById", method = RequestMethod.GET)
    public Result<?> usedPermissionById(String applicationId) {
        Result<List<SysPermission>> result = new Result<List<SysPermission>>();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????applocationId????????????
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        //??????????????? ????????????????????????????????????
        LambdaQueryWrapper<SysApplicationPermission> queryWrapper = new LambdaQueryWrapper<SysApplicationPermission>();
        queryWrapper.ne(SysApplicationPermission::getApplicationId, applicationId);
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list(queryWrapper);
        //?????????????????????
        List<SysPermission> sysPermissions = new ArrayList<SysPermission>();
        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            sysPermissions.add(sysPermissionService.getById(sysApplicationPermission.getPermissionId()));
        }
        //????????????
        result.setResult(sysPermissions);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    /**
     * ??????????????????
     *
     * @return
     */
    @ApiOperation("??????????????????")
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
     * ????????????????????????
     *
     * @return
     */
    @ApiOperation("????????????????????????")
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
            return result.error("????????????");
        }
    }

    /**
     * ??????????????????
     */
    @ApiOperation("??????????????????")
    @GetMapping("/applicationTemplate")
    public Result<SysApplicationCategoryVo> applicationTemplate() {
        SysApplicationCategoryVo sysApplicationCategoryVo = new SysApplicationCategoryVo();
        Result<SysApplicationCategoryVo> result = new Result<SysApplicationCategoryVo>();

        //??????????????????
        List<SysApplication> applications = new ArrayList<SysApplication>();
        //??????????????????
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
            sysCategory.setName("??????");
            sysCategories.add(sysCategory);
            sysApplicationCategoryVo.setSysApplication(applications);
            sysApplicationCategoryVo.setSysCategory(sysCategories.stream().distinct().collect(Collectors.toList()));
            result.setResult(sysApplicationCategoryVo);
        }
        //????????????
        else{
            result.setMessage("??????????????????");
        }
        return result;
    }

    /**
     * ?????????????????? ????????????
     */
    @ApiOperation("??????????????????????????????")
    @GetMapping("/queryLevelOnePermission")
    public Result<Map<String, Object>> queryLevelOnePermission(String applicationId) {
        Result<Map<String, Object>> result = new Result<Map<String, Object>>();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (sysApplicationService.getById(applicationId) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        List<SysApplicationPermission> sysApplicationPermissions = sysApplicationPermissionService.list();
        //???????????????
        List<String> selecteds = new ArrayList<String>();
        //???????????????????????????
        List<String> otherSelecteds = new ArrayList<String>();
        //?????????
        List<String> notSelected = new ArrayList<String>();

        for (SysApplicationPermission sysApplicationPermission : sysApplicationPermissions) {
            if (sysApplicationPermission.getApplicationId().equals(applicationId)) {
                selecteds.add(sysApplicationPermission.getPermissionId());
            } else {
                otherSelecteds.add(sysApplicationPermission.getPermissionId());
            }
        }

        //?????????????????? createWays???0 ?????????????????????
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
     * ??????????????? ??????????????? ???????????????
     *
     * @param applicationId
     * @param template
     * @return
     */
    @ApiOperation("????????????")
    @GetMapping("/copyApplication")
    public Result<SysApplication> copyApplication(String applicationId, int template) {
        Result<SysApplication> result = new Result<SysApplication>();
        //??????applicationId????????????
        if (oConvertUtils.isEmpty(applicationId)) {
            result.setMessage("??????id????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        if (!(template == 1 || template == 0)) {
            result.setMessage("???????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            return result;
        }
        result.setCode(CommonConstant.SC_OK_200);
        SysApplication sysApplication = sysApplicationService.copyApplicationById(applicationId, template);
        //????????????
        result.setResult(sysApplication);
        return result;
    }

    /**
     * ????????????
     *
     * @param jsonObject
     * @return
     */
    @ApiOperation("????????????")
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
     * ??????????????????
     *
     * @param ApplicationId
     * @param status
     * @return
     */
    @ApiOperation("??????????????????")
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
            result.OK("??????");
        } else {
            result.error500("??????");
        }
        return result;
    }

    /**
     * ?????????????????????????????????
     *
     * @param ApplicationName
     * @param applicationType
     * @return
     */
    @ApiOperation("?????????????????????????????????")
    @GetMapping("/queryMenuApplication")
    public Result<List<SysApplication>> queryMenuApplication(String ApplicationName, String applicationType, String template) {
        Result<List<SysApplication>> result = new Result<List<SysApplication>>();
        if (oConvertUtils.isEmpty(ApplicationName)) {
            result = result.OK();
            return result;
        }
        //??????????????????
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //????????????
        LambdaQueryWrapper<SysApplication> qw = new LambdaQueryWrapper<SysApplication>();
        qw.eq(SysApplication::getApplicationType, applicationType);
        qw.eq(SysApplication::getTemplate, template);
        qw.like(SysApplication::getApplicationName, ApplicationName);
        List<SysApplication> sysApplications = sysApplicationService.list(qw);
        if (sysApplications.size() <= 0) {
            result.error500("??????????????????");
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
                result.error500("??????");
                return result;
            }

        }
        //??????????????????
        result.setResult(sysApplications);
        result.setCode(200);

        return result;
    }

    /**
     * ??????applicationCode
     * ??????14776336??????????????????
     */
    @ApiOperation("????????????applicationCode")
    @GetMapping("/getApplicationCode")
    public Result<String> getApplicationCode() {
        Result<String> result = new Result<String>();
        result.success("????????????");
        result.setResult(sysApplicationService.getApplicationCode());
        return result;
    }


    @ApiOperation("????????????????????????")
    @GetMapping("/queryApplicationApprole")
    public Result<List<SysApplicationDepartRoleVO>> queryApplicationApprole(@RequestParam("applicationId") String applicationId) {
        Result<List<SysApplicationDepartRoleVO>> result = new Result<List<SysApplicationDepartRoleVO>>();
        List<SysApplicationDepartRoleVO> memberManagementMessages = sysApplicationService.getMemberManagementMessage(applicationId);
        result.setCode(CommonConstant.SC_OK_200);
        result.setResult(memberManagementMessages);
        return result;
    }


    /**
     * ????????????????????????
     *
     * @param applicationId
     * @param appRoleId
     * @param id
     * @param selectFlag
     * @return
     */
    @ApiOperation("????????????????????????")
    @DeleteMapping("/deleteApplicationApprole")
    public Result<String> deleteApplicationApprole(
            //??????????????????????????????selectFlag????????????????????????id
            @RequestParam("applicationId") String applicationId,
            @RequestParam("appRoleId") String appRoleId,
            @RequestParam("id") String id,
            @RequestParam("selectFlag") Integer selectFlag) {
        int result = sysApplicationService.deleteMemberManagementMessage(applicationId, appRoleId, id, selectFlag);
        if (result > 0) {
            return Result.OK("????????????");
        }
        if (result == -1) {
            return Result.Error("selectFlag?????????");
        }
        return Result.Error("????????????");
    }

    /**
     * ?????? ????????????????????????
     *
     * @param applicationId ????????????
     * @param appRoleId     ???????????????
     * @param selectFlag    ??????????????? 0?????? 1?????? 2??????
     * @return
     */
    @ApiOperation("?????? ????????????????????????")
    @GetMapping("/queryRoleDistribution")
    public Result<Object> queryRoleDistribution(@RequestParam(name = "applicationId", required = true) String applicationId,
                                                @RequestParam(name = "appRoleId", required = true) String appRoleId,
                                                @RequestParam(name = "selectFlag", required = true) Integer selectFlag) {
        //?????????
        Result<Object> result = new Result<Object>();
        //??????applicationId????????????
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //??????appRoleId????????????
        if (sysAppRoleService.getOne(new LambdaQueryWrapper<SysApprole>().eq(SysApprole::getId, appRoleId)) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //??????selectFlag?????????
        if (!(selectFlag == 1 || selectFlag == 0 || selectFlag == 2)) {
            result.setMessage("selectFlag??????");
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
     * ????????????????????????
     *
     * @return
     */
    @ApiOperation("????????????????????????")
    @PostMapping("/updateRoleDistribution")
    public Result updateRoleDistribution(@RequestBody JSONObject json) {
        String applicationId = json.getString("applicationId");
        String appRoleId = json.getString("appRoleId");
        String ids = json.getString("ids");
        Integer selectFlag = json.getInteger("selectFlag");
        //?????????
        Result result = new Result();
        //??????applicationId????????????
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //??????appRoleId????????????
        if (sysAppRoleService.getOne(new LambdaQueryWrapper<SysApprole>().eq(SysApprole::getId, appRoleId)) == null) {
            result.setMessage("??????id?????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        //??????selectFlag?????????
        if (!(selectFlag == 1 || selectFlag == 0 || selectFlag == 2)) {
            result.setMessage("selectFlag??????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }

        Boolean success = sysApplicationService.updateRoleDistribution(applicationId, appRoleId, ids, selectFlag);
        if (!success) {
            result.setMessage("????????????");
            result.setCode(CommonConstant.SC_INTERNAL_SERVER_ERROR_500);
            result.setSuccess(false);
            return result;
        }
        result.setMessage("????????????");
        result.setCode(CommonConstant.SC_OK_200);
        result.setSuccess(true);
        return result;
    }

    @ApiOperation("??????????????????")
    @GetMapping("/queryAppRole")
    public Result<Set<String>> queryAppRole(@RequestParam(name = "applicationId", required = true) String applicationId) {
        Result<Set<String>> result = new Result<Set<String>>();
        //??????applicationId????????????
        if (sysApplicationService.getOne(new LambdaQueryWrapper<SysApplication>().eq(SysApplication::getId, applicationId)) == null) {
            result.setMessage("??????id?????????");
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
     * ????????????????????????
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
     * ????????????????????????????????????
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

    @ApiOperation("??????????????????")
    @PostMapping("/editApplicationPermission")
    public Result<String> editApplicationPermission(@RequestBody SysEditApplicationPermissionVO sysEditApplicationPermissionVO) {
        String applicationId = sysEditApplicationPermissionVO.getApplicationId();
        List<SysPermissionTree> sysPermissionTreeList = sysEditApplicationPermissionVO.getSysPermissionTreeList();
        Result<String> result = new Result<String>();
        //????????????????????????
        List<SysPermission> sysPermissionLists = getPermissionList(sysPermissionTreeList);
        List<SysPermission> sysPermissionList = new ArrayList<SysPermission>();
        //?????????list????????????
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
            return result.error500("????????????????????????");
        } else {
            //?????????????????????
            List<SysPermission> PermissionList = new ArrayList<SysPermission>();
            List<SysPermission> permissions = new ArrayList<SysPermission>();
            for (SysApplicationPermission sysApplicationPermission : Permissions) {
                PermissionList.addAll(sysPermissionLeaf(sysApplicationPermission.getPermissionId()));
            }
            PermissionList = PermissionList.stream().distinct().collect(Collectors.toList());
            //?????????list????????????
            for (SysPermission s : PermissionList) {
                if (oConvertUtils.isEmpty(s)) {
                    continue;
                }
                permissions.add(s);
            }
            if (permissions.size() == sysPermissionList.size()) {
                //?????????????????????id
                List<String> PermissionIds = new ArrayList<String>();
                //??????????????????????????????
                for (SysPermission permission : permissions) {
                    PermissionIds.add(permission.getId());
                }
                //??????????????????id
                List<String> sysPermissionIds = new ArrayList<String>();
                //??????????????????1?????????
                List<String> sysPermissions = new ArrayList<String>();
                //??????????????????????????????
                for (SysPermission permission : sysPermissionList) {
                    sysPermissionIds.add(permission.getId());
                    if (oConvertUtils.isEmpty(permission.getParentId())) {
                        sysPermissions.add(permission.getId());
                    }
                }
                if (sysPermissions.size() <= 0) {
                    return result.error500("?????????????????????????????????");
                }
                //??????????????????????????????
                int flag = 0;
                for (String sysId : sysPermissionIds) {
                    flag = 0;
                    for (String id : PermissionIds) {
                        if (sysId.equals(id)) {
                            flag += 1;
                        }
                    }
                    if (flag != 1) {
                        return result.error500("????????????,??????id????????????");
                    }
                }

                //???????????????
                sysPermissionService.removeByIds(PermissionIds);
                //?????????????????????
                LambdaQueryWrapper<SysApplicationPermission> qr = new LambdaQueryWrapper<SysApplicationPermission>();
                qr.eq(SysApplicationPermission::getApplicationId, applicationId);
                sysApplicationPermissionService.remove(qr);
                //????????????????????????
                sysPermissionService.saveBatch(sysPermissionList);
                //????????????????????????????????????
                List<SysApplicationPermission> sysApplicationPermissions = new ArrayList<SysApplicationPermission>();
                for (String id : sysPermissions) {
                    sysApplicationPermissions.add(new SysApplicationPermission(applicationId, id));
                }
                sysApplicationPermissionService.saveBatch(sysApplicationPermissions);
                result.setResult(applicationId);
                return result.success("????????????");
            }
            return result.error500("????????????");
        }
    }

    @ApiOperation("????????????KeyWord?????????????????????")
    @GetMapping("/queryApplicationOrTemplateByKeyWord")
    public Map<String, Set<SysApplication>> queryApplicationOrTemplateByKeyWord(@RequestParam("KeyWord") String KeyWord) {
        //?????????????????????set
        Set<SysApplication> sysApplicationSet = sysApplicationService.queryApplicationOrTemplateByKeyWord(KeyWord);
        //??????????????????set?????????????????????
        Set<SysApplication> applicationsSet = new HashSet<>();
        Set<SysApplication> templatesSet = new HashSet<>();
        Set<SysApplication> errorSet = new HashSet<>();
        //????????????hashmap???????????????????????????
        Map<String, Set<SysApplication>> map = new HashMap<>();
        //????????????Set????????????
        for (SysApplication sysApplication : sysApplicationSet) {
            //?????????????????????
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
                map.put("????????????error,???????????????????????????:", errorSet);
            }
        }
        return map;
    }

    @ApiOperation("????????????id???????????????")
    @GetMapping("/selectApplicationCreator")
    public Result<SysUser> selectApplicationCreator(@RequestParam("applicationId") String applicationId) {
        return ok(sysApplicationService.selectApplicationCreator(applicationId));
    }

    @ApiOperation("?????????????????????????????????")
    @PostMapping("/updateApplicationPermissionOfRole")
    public Result<String> updateApplicationPermissionOfRole(String applicationId, String roleId, String ids) {
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        permissionIds.remove("");
        Boolean success = sysApplicationService.updateApplicationPermissionOfRole(applicationId, roleId, permissionIds);
        return success ? ok() : badRequest();
    }

    @ApiOperation("?????????????????????????????????")
    @PostMapping("/updateApplicationPermissionOfDepart")
    public Result<Object> updateApplicationPermissionOfDepart(String applicationId, String departId, String ids) {
        List<String> permissionIds = new ArrayList<String>(Arrays.asList(ids.split(",")));
        permissionIds.remove("");
        Boolean success = sysApplicationService.updateApplicationPermissionOfDepart(applicationId, departId, permissionIds);
        return success ? ok() : badRequest();


    }

    @ApiOperation("???????????????????????????????????????")
    @GetMapping("/queryPermissionDisplayApplicationData")
    public Result<Page<SysPermission>> queryPermissionDisplayApplicationData(@RequestParam("applicationId") String applicationId,
                                                                             @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                             @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        //??????????????????????????????????????????
        List<SysPermission> sysPermissions = sysApplicationService.displayApplicationPermissions(applicationId);
        //????????????result????????????
        Result<Page<SysPermission>> result = new Result<>();
        if (Objects.isNull(sysPermissions)) {
            result.setCode(500);
        }
        //????????????????????????
        sysApplicationServiceImpl.changeUrlType(sysPermissions);
        //??????????????????
        Page<SysPermission> page = new Page<>(pageNo, pageSize);
        page.setRecords(sysPermissions);
        result.setResult(page);
        result.setCode(CommonConstant.SC_OK_200);
        return result;
    }

    @ApiOperation(value = "?????????????????????????????????", produces = "application/octet-stream")
    @GetMapping("/applicationDataOutput")
    public ModelAndView applicationDataOutput(@RequestParam("permissionId") String permissionIds) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<SysPermission> sysPermissions = sysApplicationService.queryApplicationPermissionsAndOutput(permissionIds);
        //??????????????????
        mv.addObject(NormalExcelConstants.FILE_NAME, "????????????");
        mv.addObject(NormalExcelConstants.CLASS, SysPermission.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("??????????????????", "?????????:" + user.getRealname(), "????????????"));
        mv.addObject(NormalExcelConstants.DATA_LIST, sysPermissions);
        return mv;
    }

    @ApiOperation(value = "????????????id????????????")
    @GetMapping("/getApplicationByFormId")
    public Result<SysApplication> getApplicationByFormId(String formId) {
        SysApplication sysApplication = sysApplicationService.getApplicationByFormId(formId);
        return sysApplication == null ? badRequest() : ok(sysApplication);
    }

    @ApiOperation(value = "????????????id???????????????????????????")
    @GetMapping("/getApplicationPermissionById")
    public Result<List<SysPermission>> getApplicationPermissionById(@RequestParam("applicationId") String applicationId){
        Result<List<SysPermission>> result = new Result<>();
        List<SysPermission> sysPermissions = sysApplicationService.displayApplicationPermissions(applicationId);
        result.setResult(sysPermissions);
        result.setCode(200);
        return result;
    }
}