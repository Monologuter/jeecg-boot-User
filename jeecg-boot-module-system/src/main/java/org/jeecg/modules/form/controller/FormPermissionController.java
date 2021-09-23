package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.entity.PermissionRuleDo;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.jeecg.modules.form.service.PermissionRuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "表单菜单配置")
@Slf4j
@RestController
@RequestMapping("/form/permission")
public class FormPermissionController {
    private final FormSysPermissionService formSysPermissionService;
    private final PermissionRuleService permissionRuleService;

    @Autowired
    public FormPermissionController(FormSysPermissionService formSysPermissionService, PermissionRuleService permissionRuleService) {
        this.formSysPermissionService = formSysPermissionService;
        this.permissionRuleService = permissionRuleService;
    }



    @ApiOperation("修改菜单相关绑定")
    @PutMapping("/update")
    public Result<Object> updateFormPermission(@RequestBody FormSysPermissionDO formSysPermissionDO){
        formSysPermissionService.updateFormPermission(formSysPermissionDO);
        return Result.OK();
    }

    @ApiOperation("添加菜单的表单数据筛选规则")
    @PostMapping("/rule/save")
    public Result<Object> savePermissionRule(@RequestBody PermissionRuleDo permissionRuleDo) {
        permissionRuleService.savePermissionRule(permissionRuleDo);
        return Result.OK(permissionRuleDo);
    }

    @ApiOperation("列出菜单的表单数据筛选规则")
    @GetMapping("/rule/list")
    public Result<List<PermissionRuleDo>> listPermissionRule(String permissionId){
        return Result.OK(permissionRuleService.listPermissionRule(permissionId));
    }

    @ApiOperation("删除菜单的表单数据筛选规则")
    @DeleteMapping("/rule/delete")
    public Result<Object> deletePermissionRule(String id){
        permissionRuleService.deletePermissionRule(id);
        return Result.OK();
    }
}
