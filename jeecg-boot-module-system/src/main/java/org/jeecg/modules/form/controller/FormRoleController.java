package org.jeecg.modules.form.controller;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.service.FormRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * What purpose
 *
 * @author XuDeQing
 * @create 2021-08-31 16:57
 * @modify 2021-08-31 16:57
 */
@RequestMapping("/form/role")
@Slf4j
@RestController
public class FormRoleController {
    private final FormRoleService formRoleService;

    @Autowired
    public FormRoleController(FormRoleService formRoleService) {
        this.formRoleService = formRoleService;
    }

    @PostMapping("/save")
    public Result<Object> saveFormRole(@RequestBody FormRoleDo formRoleDo) {
        log.info(formRoleDo.toString());
        formRoleService.saveFormRole(formRoleDo);
        return Result.OK(formRoleDo);
    }

    @GetMapping("/list")
    public Result<List<FormRoleDo>> listFormRole(String formId){
        return Result.OK(formRoleService.listFormRoleList(formId));
    }

    @DeleteMapping("/delete")
    public Result<Object> deleteFormRole(String id){
        formRoleService.deleteFormRole(id);
        return Result.OK();
    }
}
