package org.jeecg.modules.form.controller;

import com.alibaba.fastjson.JSONException;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.common.util.BeanCheckUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.annotation.DataRebuild;
import org.jeecg.modules.form.annotation.OperationLog;
import org.jeecg.modules.form.constant.FormDataErrorMessageConstant;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.service.FormDataService;
import org.jeecg.modules.form.service.FormDesignerService;
import org.jeecg.modules.form.vo.FormDataGraphVO;
import org.jeecg.modules.form.vo.FormDataToWorkflowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 表单数据控制器
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/17 17:30
 */
@Slf4j
@RestController
@RequestMapping("/form/data")
@Api(tags = "表单数据")
public class FormDataController {

    private final FormDataService formDataService;
    private final FormDesignerService formService;

    @Autowired
    public FormDataController(FormDataService formDataService, FormDesignerService formService) {
        this.formDataService = formDataService;
        this.formService = formService;
    }

    /**
     * 保存表单数据
     *
     * @param formDataDO 表单数据DO对象
     * @param formCode   表单数据code
     * @return 保存结果
     */
    @PostMapping("/save")
    @ApiOperation("保存表单数据")
    @OperationLog(value = "表单数据日志")
    public Result<FormDataDO> saveFormData(@RequestBody FormDataDO formDataDO,
                                           @ApiParam("表单code") @RequestParam(required = false) String formCode) {
        if (formDataDO.getFormId() == null && formCode != null) {
            LambdaQueryWrapper<FormDO> queryWrapper = Wrappers.lambdaQuery();
            queryWrapper.eq(FormDO::getCode, formCode).eq(FormDO::getIsTemplate, false);
            FormDO rs = formService.getOne(queryWrapper);
            ServiceUtils.throwIfFailed(rs != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
            formDataDO.setFormId(rs.getId());
        } else {
            ServiceUtils.throwIfFailed(formService.getById(formDataDO.getFormId()) != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
        }
        ServiceUtils.throwIfFailed(formDataService.save(formDataDO), FormDataErrorMessageConstant.FORM_DATA_SAVE_FAILED);
        return Result.OK(formDataDO);
    }

    /**
     * 根据id删除表单数据
     *
     * @param id 表单数据id
     * @return 删除结果
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除表单数据")
    @OperationLog(value = "表单数据日志")
    public Result<Object> deleteFormData(@RequestParam String id) {
        ServiceUtils.throwIfFailed(formDataService.removeById(id), FormDataErrorMessageConstant.FORM_DATA_DELETE_FAILED);
        return Result.OK();
    }

    /**
     * 根据id批量删除表单数据
     *
     * @param ids 表单数据id数组
     * @return 删除结果
     */
    @DeleteMapping("/delete-batch")
    @ApiOperation("批量删除表单数据")
    @OperationLog(value = "表单数据日志")
    public Result<Object> deleteFormDataBatch(@RequestParam List<String> ids) {
        ServiceUtils.throwIfFailed(formDataService.removeByIds(ids), FormDataErrorMessageConstant.FORM_DATA_DELETE_FAILED);
        return Result.OK();
    }

    /**
     * 根据id更新表单数据
     *
     * @param formDataDO 表单数据DO对象
     * @return 保存结果
     */
    @PutMapping("/update")
    @ApiOperation("更新表单数据")
    @OperationLog(value = "表单数据日志")
    public Result<Object> updateFormData(@RequestBody FormDataDO formDataDO) {
        LambdaUpdateWrapper<FormDataDO> updateWrapper = Wrappers.lambdaUpdate();
        BeanCheckUtils.beanIsAllEmpty(formDataDO, FormDataDO::getRowData);
        updateWrapper.set(formDataDO.getRowData() != null, FormDataDO::getRowData, formDataDO.getRowData())
                .eq(FormDataDO::getId, formDataDO.getId());
        ServiceUtils.throwIfFailed(formDataService.update(new FormDataDO(), updateWrapper), FormDataErrorMessageConstant.FORM_DATA_UPDATE_FAILED);
        return Result.OK();
    }

    /**
     * 根据id获取表单数据
     *
     * @param id 表单数据id
     * @return 表单数据对象
     */
    @GetMapping("/get")
    @ApiOperation("获取表单数据")
    @OperationLog(value = "表单数据日志")
    public Result<FormDataDO> getFormData(@RequestParam String id) {
        FormDataDO formData = formDataService.getById(id);
        ServiceUtils.throwIfFailed(formData != null, FormDataErrorMessageConstant.FORM_DATA_NOT_EXISTS);
        return Result.OK(formData);
    }

    /**
     * 根据参数分页列出表单id指定的表单对象列表
     *
     * @param formId   表单id
     * @param pageNo   当前页数
     * @param pageSize 页容量
     * @return 当前页的表单数据数组，时间降序排列
     */
    @PostMapping("/list")
    @ApiOperation("列出表单数据")
    public Result<IPage<FormDataDO>> getFormDataList(
            @ApiParam("表单id") @RequestParam(defaultValue = "") String formId,
            @ApiParam("菜单id") @RequestParam(required = false) String permissionId,
            @RequestParam(defaultValue = "") List<String> orderBy,
            @RequestBody List<FormRoleDo> searchRules,
            @RequestParam(defaultValue = "true") Boolean isDesc,
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<FormDataDO> page = new Page<>(pageNo, pageSize);
        searchRules.forEach(e -> e.setRuleValue("%" + e.getRuleValue() + "%"));
        log.info(orderBy.toString());
        formDataService.getFormDataList(page, formId,permissionId, searchRules, orderBy, isDesc);
        return Result.OK(page);
    }

    /**
     * 根据时间区间和表单id查询表单数据
     *
     * @param formId:
     * @param fromTime:
     * @param toTime:
     * @param pageNo:
     * @param pageSize:
     * @return 返回保存时间在区间内的表单数据列表, 时间降序排列
     */
    @GetMapping("/list-by-time")
    @ApiOperation("列出保存时间在时间(fromTime,toTime)区间内的表单数据")
    public Result<IPage<FormDataDO>> getFormDataListByTime(
            @ApiParam("表单id") @RequestParam(defaultValue = "") String formId,
            @ApiParam("起始时间") @RequestParam(defaultValue = "1999-6-10 00:00:00") Date fromTime,
            @ApiParam("截至时间") @RequestParam(defaultValue = "2999-6-10 00:00:00") Date toTime,
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize) {
        ServiceUtils.throwIfFailed(() -> formService.getById(formId) != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
        LambdaQueryWrapper<FormDataDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FormDataDO::getFormId, formId)
                .between(FormDataDO::getCreateTime, fromTime, toTime)
                .orderByDesc(FormDataDO::getCreateTime);
        Page<FormDataDO> page = new Page<>(pageNo, pageSize);
        formDataService.page(page, queryWrapper);
        return Result.OK(page);
    }

    /**
     * 通过表单模板code获取详细信息
     *
     * @param formCode 表单code
     * @return 返回对应id的json字符串
     */
    @ApiOperation("通过表单模板code获取表单模板中的所有输入框列表")
    @GetMapping("/getJsonByCode")
    public Result<FormDataToWorkflowVO> getFormModelByID(@ApiParam(value = "表单code", required = true) @RequestParam() String formCode) {
        LambdaQueryWrapper<FormDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(FormDO::getCode, formCode);
        FormDO formDO = formService.getBaseMapper().selectOne(queryWrapper);
        ServiceUtils.throwIfFailed(() -> formDO != null, FormErrorMessageConstant.FORM_NOT_EXISTS);
        FormDataToWorkflowVO toWorkflowVO;
        try {
            toWorkflowVO = new FormDataToWorkflowVO(formDO.getCode(), formDO.getName(), formDataService.getDataJsonToWorkflow(formDO.getJson().toJSONString()));
        } catch (JSONException e) {
            return Result.Error("表单数据不存在");
        }
        return Result.OK(toWorkflowVO);
    }

    /**
     * 通过表单名称模糊查询表单模板列表
     *
     * @param formName: 表单名称
     * @param pageNo:   返回页码
     * @param pageSize: 返回大小
     * @return 表单列表
     */
    @ApiOperation("模糊查询表单模板列表")
    @GetMapping("/getFormList")
    public Result<IPage<FormDO>> getListTemplate(
            @ApiParam(value = "表单名称") @RequestParam(defaultValue = "") String formName,
            @ApiParam("当前页,可选,默认1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量,可选,默认10") @RequestParam(defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<FormDO> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.like(FormDO::getName, formName);
        queryWrapper.eq(FormDO::getIsTemplate, false);
        Page<FormDO> formPage = new Page<>(pageNo, pageSize);
        formService.page(formPage, queryWrapper);
        return Result.OK(formPage);
    }

    @PostMapping("/count")
    @ApiOperation(value = "根据规则统计")
    public Result<Object> getFormDataCount(@RequestParam String formId,
                                           @RequestBody List<FormRoleDo> rules) {
        return Result.OK(formDataService.getFormDataCount(formId, rules));
    }

    @GetMapping("/getGraphNeedData")
    @ApiOperation(value ="返回图表组数据",notes = "图表组需要的一个接口")
    public Result<List<Map<String,Integer>>> getFormDataGraph(){
        List list=new ArrayList();
        list.add(formDataService.getGraphData());
        return Result.OK(list);
    }

}
