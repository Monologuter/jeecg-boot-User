package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.PermissionButtonDo;
import org.jeecg.modules.form.service.FormButtonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 表单按钮控制器
 *
 * @author XuDeQing
 * @date 2021-09-10 15:50
 */
@Slf4j
@RestController
@Api(tags = "表单按钮")
@RequestMapping("/form/button")
public class FormButtonController {
    private final FormButtonService formButtonService;

    @Autowired
    public FormButtonController(FormButtonService formButtonService) {
        this.formButtonService = formButtonService;
    }

    /**
     * 添加或保存表单按钮
     * 当传来的数据中有id时，为修改数据
     * 若无id，则为保存表单按钮数据
     * 
     * @param button 表单按钮实体类
     */
    @ApiOperation("添加或保存表单按钮")
    @PostMapping("/save")
    public Result<Object> saveButton(@RequestBody PermissionButtonDo button) {
        formButtonService.saveButton(button);
        return Result.OK(button);
    }

    /**
     * 删除表单按钮，通过表单按钮删除对应的表单按钮数据
     *
     * @param id 表单按钮ID
     */
    @ApiOperation("删除表单按钮")
    @DeleteMapping("/delete")
    public Result<Object> deleteButton(@RequestParam String id) {
        formButtonService.deleteButton(id);
        return Result.OK();
    }

    /**
     * 通过表单ID查询该表单ID所拥有的表单按钮数据
     *
     * @param permissionId 菜单ID
     */
    @ApiOperation("列出表单按钮")
    @GetMapping("/list")
    public Result<List<PermissionButtonDo>> listButtons(@RequestParam String permissionId) {
        return Result.OK(formButtonService.listButtons(permissionId));
    }
}
