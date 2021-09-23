package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.service.WfProcessDevelopmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年05月31日 9:44
 */
@RestController
@RequestMapping("workflow/workflow-processdevelopment")
@Api(tags = "工作流")
@Slf4j
public class WfProcessDevelopmentController {

    @Autowired
    private WfProcessDevelopmentService wfProcessDevelopmentService;

    /** 获取拓展页相关信息 **/
    @GetMapping("/processDevelopmentFindAll")
    @ApiOperation(value = "获取拓展页相关信息", notes = "获取拓展页相关信息")
    public Result processDevelopmentFindAll(@ApiParam(value = "流程定义Key", required = true) @RequestParam String procKey){
        return wfProcessDevelopmentService.processDevelopmentFindAll(procKey);
    }

    /** 拓展属性修改 **/
    @PostMapping("/processDevelopmentExpand")
    @ApiOperation(value = "拓展属性修改", notes = "获取拓展修改")
    public Result processDevelopmentUpDate(
           @RequestBody ProcessDevelopmentVO processDevelopmentVO){

        return wfProcessDevelopmentService.processDevelopmentUpDate(processDevelopmentVO);
    }

    /** 获取流程定义分类 **/
    @GetMapping("/processDevelopmentFindProcClass")
    @ApiOperation(value = "获取流程定义分类", notes = "获取流程定义分类")
    public Result<List<String>> processDevelopmentFindProcClass(@ApiParam(value = "执行人", required = true) @RequestParam String userId){
        return wfProcessDevelopmentService.processDevelopmentFindProcClass(userId);
    }


    /** 获取icon颜色 **/
    @GetMapping("/processDevelopmentFindIconColour")
    @ApiOperation(value = "获取icon颜色", notes = "获取icon颜色")
    public Result<List<String>> processDevelopmentFindIconColour(@ApiParam(value = "执行人", required = true) @RequestParam String userId){
        return wfProcessDevelopmentService.processDevelopmentFindIconColour(userId);
    }

    /** 获取icon样式 **/
    @GetMapping("/processDevelopmentFindIconStyle")
    @ApiOperation(value = "获取icon样式", notes = "获取icon样式")
    public Result<List<String>> processDevelopmentFindIconStyle(@ApiParam(value = "执行人", required = true) @RequestParam String userId){
        return wfProcessDevelopmentService.processDevelopmentFindIconStyle(userId);
    }

    /** 通过formKey获取流程定义列表 **/
    @GetMapping("/get-procdef-by-formkey")
    @ApiOperation(value = "通过formKey获取流程定义列表", notes = "通过formKey获取流程定义列表")
    public Result<List<ProcessDefinitionDTO>> getProceDefByFormKey(@ApiParam(value = "表单id", required = true) @RequestParam String formKey){
        return wfProcessDevelopmentService.getProceDefByFormKey(formKey);
    }

}
