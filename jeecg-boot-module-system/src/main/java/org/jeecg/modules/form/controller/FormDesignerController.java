package org.jeecg.modules.form.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.form.annotation.DataRebuild;
import org.jeecg.modules.form.dto.FormDTO;
import org.jeecg.modules.form.dto.FormSysPermissionDTO;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.service.FormDesignerService;
import org.jeecg.modules.form.vo.FormSysPermissionVO;
import org.jeecg.modules.form.vo.FormVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 表单控制器
 *
 * @author XuDeQing
 * @create 2021-04-01 13:35
 * @modify 2021-04-07 09:42
 */
@Slf4j
@RestController
@RequestMapping("/form/")
@Api(tags = "表单设计器")
public class FormDesignerController extends JeecgController<FormDO, FormDesignerService> {
    private final FormDesignerService formService;
    private final BeanCopier formCopier = BeanCopier.create(FormVO.class, FormDTO.class, false);
    private final BeanCopier formSysPermissionCopier = BeanCopier.create(FormSysPermissionVO.class, FormSysPermissionDTO.class, false);

    @Autowired
    public FormDesignerController(FormDesignerService formService) {
        this.formService = formService;
    }

    /**
     * 保存表单数据
     *
     * @param form 封装好的表单实体类,如需生成路由及授权信息,则传入SysPermission对象并填入相关信息
     * @return 保存是否成功
     */
    @PostMapping("/save")
    @ApiOperation("保存表单数据")
    public Result<FormDTO> saveForm(@RequestBody FormVO form) {
        FormDTO formDTO = new FormDTO();
        formCopier.copy(form, formDTO, null);
        return Result.OK(formService.saveFormDTO(formDTO));
    }

    /**
     * 通过id查询表单数据,包括相关联的菜单列表
     *
     * @param id 表单id
     * @return 查询结果
     */
    @GetMapping("/get")
    @ApiOperation("通过id查询表单数据")
    public Result<Object> getFormById(@RequestParam(defaultValue = "") String id) {
        return Result.OK(formService.getFormDTOById(id));
    }

    /**
     * 获取表单列表
     *
     * @param isTemplate 是否为模板
     * @param pageNo     当前页数
     * @param pageSize   页容量
     * @return 表单列表
     */
    @GetMapping("/list")
    @ApiOperation("列出模板或非模板表单数据")
    public Result<IPage<FormDTO>> getFormList(
            @ApiParam("是否为模板，0为否，1为是,可选，默认为0") @RequestParam(defaultValue = "0") Integer isTemplate,
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("表单名，可选，默认为空") @RequestParam(defaultValue = "") String name,
            @ApiParam("表单编码，可选，默认为空") @RequestParam(defaultValue = "") String code) {
        Page<FormDTO> formPage = new Page<>(pageNo, pageSize);
        formService.getFormList(formPage, isTemplate != 0, name, code);
        return Result.OK(formPage);
    }

    /**
     * 获取表单列表
     *
     * @param pageNo   当前页数
     * @param pageSize 页容量
     * @return 表单列表
     */
    @GetMapping("/list-template")
    @ApiOperation("列出模板表单数据,根据表单名称模糊查询")
    public Result<IPage<FormDO>> getFormTemplateList(
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("表单名称，可选，默认为空") @RequestParam(required = false) String name) {
        LambdaQueryWrapper<FormDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.likeRight(FormDO::getName, name);
        // IsTemplate字段必须True才能返回
        queryWrapper.eq(FormDO::getIsTemplate, true);
        Page<FormDO> formPage = new Page<>(pageNo, pageSize);
        formService.page(formPage, queryWrapper);
        return Result.OK(formPage);
    }

    /**
     * 通过id更新表单数据,可以更新路由信息
     *
     * @param form 封装好的表单实体类
     * @return 更新是否成功
     */
    @PutMapping("/update")
    @DataRebuild(value = "重构表单数据")
    @ApiOperation("通过id更新表单数据")
    public Result<Object> updateFormById(@RequestBody FormVO form) {
        FormDTO formDTO = new FormDTO();
        formCopier.copy(form, formDTO, null);
        formService.updateFormDTOById(formDTO);
        return Result.OK();
    }

    /**
     * 通过id删除表单数据,,如果表单关联了路由及相关授权信息将一并删除
     *
     * @param id 表单id
     * @return 删除是否成功
     */
    @DeleteMapping("/delete")
    @ApiOperation("通过id删除表单数据")
    public Result<Object> deleteFormById(@RequestParam(defaultValue = "") String id) {
        formService.deleteFormById(id);
        return Result.OK();
    }

    /**
     * 检查表单编码有效性
     *
     * @param code 表单编码
     * @return 表单编码是否有效
     */
    @GetMapping("/code-valid")
    @ApiOperation("验证表单编码有效性")
    public Result<Object> checkFormCodeValid(
            @ApiParam("表单编码") @RequestParam String code,
            @ApiParam("是否为模板，0为否，1为是,可选，默认为0") @RequestParam(defaultValue = "0") Integer isTemplate) {
        if (StringUtils.isEmpty(code)) { return Result.Error("编码不能为空!"); }
        LambdaQueryWrapper<FormDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FormDO::getIsTemplate, 1 == isTemplate);
        queryWrapper.eq(FormDO::getCode, code);
        FormDO one = formService.getOne(queryWrapper);
        if (one == null) { return Result.OK(); }
        return Result.Error("编码已存在!");
    }

    /**
     * 根据id批量删除表单数据,如果表单关联了路由及相关授权信息将一并删除
     *
     * @param ids id数组
     * @return 删除是否成功
     */
    @DeleteMapping("/delete-batch")
    @ApiOperation("根据id批量删除表单数据")
    public Result<Object> deleteFormByIdBatch(
            @ApiParam("id数组") @RequestParam List<String> ids) {
        formService.deleteFormByIdBatch(ids);
        return Result.OK();
    }

    /**
     * 保存表单关联菜单路由
     *
     * @param formSysPermissionVO 表单菜单VO对象
     * @return 保存结果
     */
    @PostMapping("/save-route")
    @ApiOperation("保存表单关联菜单路由")
    public Result<Object> saveFormSysPermission(@RequestBody FormSysPermissionVO formSysPermissionVO) {
        FormSysPermissionDTO formSysPermissionDTO = new FormSysPermissionDTO();
        formSysPermissionCopier.copy(formSysPermissionVO, formSysPermissionDTO, null);
        formService.saveFormSysPermission(formSysPermissionDTO);
        return Result.OK();
    }

    /**
     * @param id
     * @return
     */
    @Deprecated
    @RequestMapping(value = "/delete-route", method = RequestMethod.DELETE)
    @ApiOperation("删除表单关联菜单路由")
    public Result<Object> deleteFormSysPermission(@ApiParam(value = "表单id", required = true) @RequestParam String id) {
        formService.deleteFormSysPermission(id);
        return Result.OK();
    }

    @GetMapping("/template/export")
    @ApiOperation("导出模板数据为Excel文件")
    public ModelAndView exportTemplateToXls(@ApiParam("模板id数组") @RequestParam List<String> selections) {
        return formService.exportTemplateToXls(selections);
    }

    @PostMapping("/template/import")
    @ApiOperation("将Excel文件中的模板数据导入")
    public Result<Object> importXlsToTemplate(@ApiParam("Excel文件") MultipartFile file) {
        return Result.OK(formService.importXlsToTemplate(file));
    }

    @PostMapping("/copyForm")
    @ApiOperation("复制表单")
    public Result<FormDO> copyForm(@ApiParam(required = true) String code,
                                   @ApiParam(required = true) String name,
                                   @ApiParam(required = true) String formId) {
        return Result.OK(formService.copyForm(code, name, formId));
    }
}
