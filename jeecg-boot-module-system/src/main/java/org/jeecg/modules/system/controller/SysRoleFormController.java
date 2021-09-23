package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.system.entity.SysRoleForm;
import org.jeecg.modules.system.service.ISysRoleFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author 胡文晓
 * @Description:用户表单数据
 * @date 2021/8/1813:23
 */
@RestController
@Slf4j
@RequestMapping("/sys/sysRoleForm")
@Api(tags = "角色表单数据过滤")
public class SysRoleFormController {

    @Autowired
    private ISysRoleFormService sysRoleFormService;

    @PostMapping("/add")
    @ApiOperation(value = "添加数据-添加角色表单数据", notes="添加数据-添加角色表单数据")
    public Result<String> addSysRoleForm(SysRoleForm sysRoleForm){
        sysRoleFormService.addSysRoleForm(sysRoleForm);
        return  Result.OK("添加成功");
    }

    @GetMapping("/queryFormDataDO")
    @ApiOperation(value = "根据角色过滤条件", notes="根据角色过滤条件")
    public Result<List<FormDataDO>> queryFormDataDO(SysRoleForm sysRoleForm){
        Result<List<FormDataDO>> result = new Result<List<FormDataDO>>();
        List<FormDataDO> formDataDOS = sysRoleFormService.queryFormDataDO(sysRoleForm);
        return result.OK(formDataDOS);
    }
}