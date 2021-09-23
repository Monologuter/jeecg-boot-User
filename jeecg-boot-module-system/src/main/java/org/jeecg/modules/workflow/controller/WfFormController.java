package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.FormDataCorrelationVO;
import org.jeecg.modules.workflow.entity.vo.FormFieldVO;
import org.jeecg.modules.workflow.entity.vo.FormRole;
import org.jeecg.modules.workflow.service.WfFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;


/**
* 工作流表单控制层
* @author: liuchun
* @date: 2021/3/18 13:38
*/
@RestController
@RequestMapping("/workflow/workflow-form")
@Api(tags="工作流")
@Slf4j
public class WfFormController {
    /**
     * 自动注入工作流表单层
     */
    @Autowired
    private WfFormService wfFormService;

    /**获取表单信息 ： 根据流程中任务id
    * @param taskId: 任务id
    * @param processInstanceId : 流程实例id
      @param procdefId : 流程定义id
     * @Return: org.jeecg.common.api.vo.Result<java.util.List<org.camunda.bpm.engine.form.FormField>>
    */
    @GetMapping("/get-form")
    @ApiOperation(value = "展示动态表单信息", notes = "展示动态表单信息")
    public Result<List<FormFieldVO>> showFormByTaskId(@ApiParam(value = "任务id", required = true) @RequestParam String taskId,
                                                      @ApiParam(value = "流程实例id", required = true) @RequestParam String processInstanceId,
                                                      @ApiParam(value = "流程定义id", required = true) @RequestParam String procdefId){
        List<FormFieldVO> list = wfFormService.showFormByTaskId(taskId,processInstanceId,procdefId);

        return Result.OK(list);
    }

    /**
     * 提交表单和意见 ：通过任务id，流程实例id，评论内容和表单中属性
     * @param formData ：所有传参
     *          formMap : 表单属性
     *          taskId ：任务节点id
     *          processInstanceId ：流程实例id
     *          message ：评论内容
     */
    @PostMapping("/submit-form")
    @ApiOperation(value = "提交表单和评论", notes = "提交表单和评论")
    public Result<Object> submitForm(@ApiParam(value = "表单属性和意见,任务id,实例id", required = true) @RequestBody FormDataCorrelationVO formData){
        wfFormService.submitForm(formData);
        return Result.OK();
    }

    /**
     * 展示外置表单根据任务id
     * @param taskId : 任务id
     * @return String : 外置表单字符串
     * @throws IOException
     */
    @GetMapping("/get-outForm")
    @ApiOperation(value = "展示外置表单信息", notes = "展示外置表单信息")
    public Result<Map<String,Object>> showOutFormByTaskId(@ApiParam(value = "任务id", required = true) @RequestParam String taskId) throws IOException {
        return Result.OK(wfFormService.showOutFormByTaskId(taskId));
    }


    /**
     * 跳转到开始节点
     * @param formData
     *          formMap:表单属性
     *          taskId:任务id
     *          processInstanceId:实例id
     *          message:意见内容
     */
    @PostMapping("/jump-form")
    @ApiOperation(value = "跳转到开始节点",notes = "跳转到开始节点")
    public Result<Object> jumpStartEvent(@ApiParam(value = "表单属性和意见,任务id,实例id,意见内容", required = true) @RequestBody FormDataCorrelationVO formData){
        wfFormService.jumpStartEvent(formData);

        return Result.OK();
    }

    /**
     * 返回formKey根据任务id
     * @param taskId : 任务id
     * @return : 返回formRole:
     *                  formKey：判断是动态表单还是外置表单
     *                  role:判断是申请人还是审批人
     */
    @GetMapping("/get-formKey")
    @ApiOperation(value = "获取formKey", notes = "获取formKey")
    public Result<FormRole> showFormKeyByTaskId(@ApiParam(value = "任务id", required = true) @RequestParam String taskId){
        FormRole formRole = wfFormService.showFormKeyByTaskId(taskId);

        return Result.OK(formRole);
    }

    /**
     * 保存表单参数
     * @param formData : 表单参数信息和任务id
     * @return : 返回是否保存成功信息
     */
    @PostMapping("/save-formVar")
    @ApiOperation(value = "保存表单参数", notes = "保存表单参数")
    public Result<Object> saveFormVar(@ApiParam(value = "表单参数信息和任务id", required = true) @RequestBody FormDataCorrelationVO formData){
        String taskId = formData.getTaskId();
        Map<String,Object> variables = formData.getFormMap();

        return wfFormService.saveFormVar(taskId,variables);
    }
}
