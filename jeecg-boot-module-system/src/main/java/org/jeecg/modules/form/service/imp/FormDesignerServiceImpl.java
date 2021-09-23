package org.jeecg.modules.form.service.imp;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.constant.OperationErrorMessageConstant;
import org.jeecg.modules.common.util.BeanCheckUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormDataErrorMessageConstant;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.dto.FormDTO;
import org.jeecg.modules.form.dto.FormSysPermissionDTO;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.mapper.FormMapper;
import org.jeecg.modules.form.service.FormAdapterService;
import org.jeecg.modules.form.service.FormDataService;
import org.jeecg.modules.form.service.FormDesignerService;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.jeecg.modules.system.service.ISysRoleService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表单设计Service层实现类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/3 13:35
 */
@Service
@Slf4j
public class FormDesignerServiceImpl extends ServiceImpl<FormMapper, FormDO> implements FormDesignerService {
    private final ISysPermissionService sysPermissionService;
    private final ISysRolePermissionService sysRolePermissionService;
    private final ISysRoleService sysRoleService;
    private final FormSysPermissionService formSysPermissionService;
    private final FormDataService formDataService;
    private final CommonAPI commonAPI;
    private final FormAdapterService formAdapterService;

    @Autowired
    public FormDesignerServiceImpl(ISysPermissionService sysPermissionService, ISysRolePermissionService sysRolePermissionService, ISysRoleService sysRoleService, FormSysPermissionService formSysPermissionService, FormDataService formDataService, CommonAPI commonAPI, FormAdapterService formAdapterService) {
        this.sysPermissionService = sysPermissionService;
        this.sysRolePermissionService = sysRolePermissionService;
        this.sysRoleService = sysRoleService;
        this.formSysPermissionService = formSysPermissionService;
        this.formDataService = formDataService;
        this.commonAPI = commonAPI;
        this.formAdapterService = formAdapterService;
    }

    /**
     * 保存表单设计器数据和路由关联数据
     *
     * @param formDTO 表单设计器对象DTO
     * @return org.jeecg.modules.form.dto.FormDTO 返回保存后的表单设计器对象DTO
     */
    @Override
    public FormDTO saveFormDTO(FormDTO formDTO) {
        ServiceUtils.throwIfFailed(() -> StringUtils.isNotBlank(formDTO.getName()) && StringUtils.isNotBlank(formDTO.getCode()),
                FormErrorMessageConstant.FORM_SAVE_FAILED);
        ServiceUtils.throwIfFailed(() -> this.save(formDTO), FormErrorMessageConstant.FORM_SAVE_FAILED);
        return formDTO;
    }

    /**
     * 构建系统菜单授权对象
     *
     * @return org.jeecg.modules.system.entity.SysPermission 返回构建的系统菜单构建的对象
     */
    private SysPermission buildParentSysPermission() {
        SysPermission parentPermission = new SysPermission();
        parentPermission.setName("表单管理");
        parentPermission.setParentId("");
        parentPermission.setUrl("/form/manage");
        parentPermission.setComponent("layouts/RouteView");
        parentPermission.setMenuType(0);
        parentPermission.setPermsType("1");
        parentPermission.setSortNo(99.9);
        parentPermission.setLeaf(false);
        parentPermission.setAlwaysShow(false);
        parentPermission.setIcon("form");
        parentPermission.setRoute(true);
        parentPermission.setKeepAlive(false);
        parentPermission.setHidden(false);
        parentPermission.setCreateWays(1);
        parentPermission.setStatus("1");
        return parentPermission;
    }

    /**
     * 获取表单菜单的父菜单
     *
     * @param formId        表单id
     * @param sysPermission 表单菜单对象
     * @return 父菜单对象
     */
    private SysPermission getParentPermission(String formId, SysPermission sysPermission) {
        return Optional.ofNullable(sysPermissionService.getById(sysPermission.getParentId()))
                // 如果根据parentId查不到父菜单,则尝试根据条件查找父菜单
                .orElseGet(() -> Optional.ofNullable(sysPermissionService.getOne(Wrappers.lambdaQuery(SysPermission.class)
                                .eq(SysPermission::getName, "表单管理")
                                .eq(SysPermission::getMenuType, 0))).orElseGet(() -> {
                            // 如果父菜单不存在,则新建一个测试表单菜单作为父菜单
                            log.info("父菜单不存在,新建父菜单");
                            SysPermission parentSysPermission = buildParentSysPermission();
                            autoSavePermission(parentSysPermission);
                            return parentSysPermission;
                        })
                );
    }

    /**
     * 保存菜单对象,并根据角色授权
     *
     * @param sysPermission 菜单对象
     */
    private void autoSavePermission(SysPermission sysPermission) {
        try {
            sysPermissionService.addPermission(sysPermission);
        } catch (JeecgBootException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, FormErrorMessageConstant.PERMISSION_SAVE_FAILED);
        }

        log.info("获取当前登录用户");
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Set<String> roles = commonAPI.queryUserRoles(user.getUsername());
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysRole::getRoleCode, roles).select(SysRole::getId);

        log.info("获取用户角色列表");
        List<SysRole> sysRoleList = sysRoleService.list(queryWrapper);

        log.info("保存所有角色授权");
        ServiceUtils.throwIfFailed(() -> sysRolePermissionService.saveBatch(sysRoleList.stream()
                .map(sysRole -> new SysRolePermission(sysRole.getId(), sysPermission.getId()))
                .collect(Collectors.toSet())), FormErrorMessageConstant.ROLE_PERMISSION_SAVE_FAILED);
    }

    /**
     * 根据表单ID删除所有有关联的路由关联数据
     *
     * @param id 表单ID
     */
    @Override
    @DS("form")
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormById(String id) {
        formAdapterService.deleteSysPermissionsOfForm(delForm(id));
    }

    /**
     * 根据表单ID查询所有有关联的路由关联数据
     *
     * @param id 表单ID
     * @return java.util.List<org.jeecg.modules.form.entity.FormSysPermissionDO> 返回所有有关联的系统菜单对象的列表
     */
    private List<FormSysPermissionDO> delForm(String id) {
        FormDO formDO = getById(id);
        ServiceUtils.throwIfFailed(() -> formDO != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
        LambdaQueryWrapper<FormDataDO> formDataWrapper = Wrappers
                .<FormDataDO>lambdaQuery()
                .eq(FormDataDO::getFormId, id);
        if (formDataService.count(formDataWrapper) > 0) {
            ServiceUtils.throwIfFailed(formDataService.remove(formDataWrapper),
                    FormDataErrorMessageConstant.FORM_DATA_DELETE_FAILED);
        }
        log.info("获取该表单所有关联菜单的列表");
        LambdaQueryWrapper<FormSysPermissionDO> wrapper = Wrappers.lambdaQuery(FormSysPermissionDO.class)
                .eq(FormSysPermissionDO::getFormId, id);
        List<FormSysPermissionDO> list = formSysPermissionService.list(wrapper);
        ServiceUtils.throwIfFailed(() -> removeById(id) && removeFormSysPermissions(list),
                FormErrorMessageConstant.FORM_DELETE_FAILED);
        return list;
    }

    /**
     * 根据表单系统菜单授权对象的列表进行删除对应的路由关联数据
     *
     * @param list 表单系统菜单授权对象的列表
     * @return boolean 返回是否删除成功的布尔值
     */
    private boolean removeFormSysPermissions(List<FormSysPermissionDO> list) {
        if (list.isEmpty()) {
            return true;
        } else {
            return formSysPermissionService.removeByIds(list.stream()
                    .map(FormSysPermissionDO::getId).collect(Collectors.toList()));
        }
    }

    /**
     * 根据表单ids列表删除表单路由关联数据
     *
     * @param ids 包含多个表单id
     */
    @DS("form")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormByIdBatch(List<String> ids) {
        List<FormSysPermissionDO> list = new ArrayList<>();
        ids.forEach(id -> list.addAll(delForm(id)));
        formAdapterService.deleteSysPermissionsOfForm(list);
    }

    /**
     * 根据表单授权对象添加路由关联数据
     *
     * @param formSysPermission 表单授权对象
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveFormSysPermission(FormSysPermissionDTO formSysPermission) {
        String formId = formSysPermission.getFormId();
        SysPermission sysPermission = formSysPermission.getSysPermission();
        SysPermission parentPermission = Optional.ofNullable(sysPermissionService.getById(formId))
                // 如果根据parentId查不到父菜单,则尝试根据条件查找父菜单
                .orElseGet(() -> Optional.ofNullable(sysPermissionService.getOne(Wrappers.lambdaQuery(SysPermission.class)
                                .eq(SysPermission::getName, "表单管理")
                                .eq(SysPermission::getMenuType, 0))).orElseGet(() -> {
                            // 如果父菜单不存在,则新建一个测试表单菜单作为父菜单
                            log.info("父菜单不存在,新建父菜单");
                            SysPermission parentSysPermission = buildParentSysPermission();
                            autoSavePermission(parentSysPermission);
                            return parentSysPermission;
                        })
                );
        sysPermission.setParentId(parentPermission.getId());
        autoSavePermission(sysPermission);
        formSysPermission.setSysPermissionId(sysPermission.getId());
        formAdapterService.saveFormSysPermission(formSysPermission);
    }

    /**
     * 查询所有表单设计器表单数据和路由关联数据，不包含模板数据
     *
     * @param page       分页参数
     * @param isTemplate 是否为模板
     * @param name       表单名称
     * @param code       表单Code
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.dto.FormDTO> 返回表单DTO对象的IPage
     */
    @Override
    public IPage<FormDTO> getFormList(Page<FormDTO> page, Boolean isTemplate, String name, String code) {
        IPage<FormDTO> rsPage = getBaseMapper().getFormDTOListByIsTemplate(page, isTemplate, "%" + name + "%", "%" + code + "%");
        rsPage.getRecords().forEach(this::getSysPermissionsOfForm);
        return rsPage;
    }

    /**
     * 导出（下载）表单模板数据到XLS文件
     *
     * @param ids 表单ID列表
     * @return org.springframework.web.servlet.ModelAndView
     */
    @Override
    public ModelAndView exportTemplateToXls(List<String> ids) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<FormDO> pageList = lambdaQuery()
                .eq(FormDO::getIsTemplate, true)
                .in(FormDO::getId, ids)
                .list();
        //导出文件名称
        mv.addObject(NormalExcelConstants.FILE_NAME, "表单设计器模板");
        mv.addObject(NormalExcelConstants.CLASS, FormDO.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("表单设计器模板数据", "导出人:"
                + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
        return mv;
    }

    /**
     * 导入表单文件数据到数据库
     *
     * @param xlsFile 导入的文件数据信息
     * @return java.util.List<java.lang.String> 返回处理结果信息的列表
     */
    @Override
    public List<String> importXlsToTemplate(MultipartFile xlsFile) {
        ArrayList<String> rs = null;
        List<Object> list;
        try {
            ImportParams importParams = new ImportParams();
            importParams.setTitleRows(2);
            importParams.setHeadRows(1);
            list = ExcelImportUtil.importExcel(xlsFile.getInputStream(), FormDTO.class, importParams);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Excel文件读取失败!");
        }
        for (Object o : list) {
            FormDTO formDTO = (FormDTO) o;
            formDTO.setIsTemplate(true);
            try {
                saveFormDTO(formDTO);
            } catch (DuplicateKeyException e) {
                if (rs == null) {
                    rs = new ArrayList<>();
                }
                rs.add(String.format("编码为%s的模板插入失败,模板编码已存在!", formDTO.getCode()));
            }
        }
        return rs;
    }

    /**
     * 从已有的表单中复制一个副本表单出来
     *
     * @param code   表单Code
     * @param name   表单名称
     * @param formId 表单ID
     * @return org.jeecg.modules.form.entity.FormDO 返回新增的副本表单DO对象
     */
    @Override
    public FormDO copyForm(String code, String name, String formId) {
        //code唯一性校验
        if (getBaseMapper().ifCOdeIsUsed(code) > 0) {
            ServiceUtils.throwException("code不可用！");
        }
        //根据formId找到json串
        JSONObject json = getBaseMapper().selectJsonById(formId);
        //新建一个复制的表单
        FormDO formDO = new FormDO();
        formDO.setCode(code);
        formDO.setName(name);
        formDO.setJson(json);
        this.save(formDO);
        return formDO;
    }

    /**
     * 根据表单ID查询表单信息及其相关的路由关联数据
     *
     * @param id 表单ID
     * @return org.jeecg.modules.form.dto.FormDTO 返回表单DTO对象
     */
    @Override
    public FormDTO getFormDTOById(String id) {
        FormDTO formDTO = getBaseMapper().getFormDTOById(id);
        ServiceUtils.throwIfFailed(() -> formDTO != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
        getSysPermissionsOfForm(formDTO);
        return formDTO;
    }

    /**
     * 更新表单设计器数据及其相应的路由关联数据
     *
     * @param formDTO 更新表单设计器数据对象DTO
     */
    @Override
    @DS("form")
    @Transactional(rollbackFor = Exception.class)
    public void updateFormDTOById(FormDTO formDTO) {
        ServiceUtils.throwIfFailed(() -> {
            Optional.ofNullable(formDTO.getName())
                    .ifPresent(name -> ServiceUtils.throwIfFailed(() -> StringUtils.isNotBlank(name),
                            FormErrorMessageConstant.FORM_NAME_ALL_SPACE));
            LambdaUpdateWrapper<FormDO> updateWrapper = Wrappers.lambdaUpdate();
            BeanCheckUtils.beanIsAllEmpty(formDTO, FormDTO::getName, FormDTO::getJson, FormDTO::getIsTemplate);
            // 更新json,name,isTemplate字段
            updateWrapper.set(null != formDTO.getJson(), FormDO::getJson, formDTO.getJson())
                    .set(StringUtils.isNotBlank(formDTO.getName()), FormDO::getName, formDTO.getName())
                    .set(formDTO.getIsTemplate() != null, FormDO::getIsTemplate, formDTO.getIsTemplate())
                    .set(StringUtils.isNotBlank(formDTO.getAutoCountCollection()), FormDO::getAutoCountCollection, formDTO.getAutoCountCollection())
                    .set(StringUtils.isNotBlank(formDTO.getDynamicDataSource()), FormDO::getDynamicDataSource, formDTO.getDynamicDataSource())
                    .eq(FormDO::getId, formDTO.getId());
            return update(new FormDTO(), updateWrapper);
        }, FormErrorMessageConstant.FORM_UPDATE_FAILED);
    }


    /**
     * 删除路由关联数据
     *
     * @param id 菜单id
     */
    @Override
    @DS("form")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteFormSysPermission(String id) {
        LambdaQueryWrapper<FormSysPermissionDO> wrapper = Wrappers
                .lambdaQuery(FormSysPermissionDO.class)
                .eq(FormSysPermissionDO::getSysPermissionId, id);
        ServiceUtils.throwIfFailed(() -> formSysPermissionService.remove(wrapper),
                OperationErrorMessageConstant.DELETE_FAILED);
    }

    /**
     * 根据表单ID获取表单路由关联数据，同时设置form
     *
     * @param form 表单数据DTO对象，包含表单设计器数据和表单路由关联数据
     */
    private void getSysPermissionsOfForm(FormDTO form) {
        List<String> list = getBaseMapper()
                .getFormSysPermissionByFormId(form.getId())
                .stream()
                .map(FormSysPermissionDO::getSysPermissionId)
                .collect(Collectors.toList());
        if (!list.isEmpty()) {
            form.setPermissionList(sysPermissionService.listByIds(list));
        }
    }
}
