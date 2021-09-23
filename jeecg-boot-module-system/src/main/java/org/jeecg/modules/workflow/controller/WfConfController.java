package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ConfInfoVO;
import org.jeecg.modules.workflow.entity.vo.FormFieldVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfConfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description: 工作流配置控制层
 * @author: liuchun
 * @date: 2021年03月18日 9:38
 */
@RestController
@RequestMapping("/workflow/workflow-conf")
@Api(tags="工作流")
@Slf4j
public class WfConfController {

    @Autowired
    private WfConfService wfConfService;

    /**
     * 获取任务节点信息 : 根据Bpmn流程xml文件
     * @param procdefId : 流程定义id
     * @param pageNo : 当前页
     * @param pageSize : 显示条数
     * @return
     */
    @GetMapping("/get-activitylist")
    @ApiOperation(value = "配置任务节点列表",notes = "配置任务节点列表")
    public Result<RecordsListPageVO> showActivityByBpmn(@ApiParam(value = "当前页",required = true)
                                                           @RequestParam Integer pageNo,
                                                        @ApiParam(value = "显示多少条",required = true)
                                                           @RequestParam Integer pageSize,
                                                        @ApiParam(value = "流程定义id",required = true)
                                                           @RequestParam String procdefId){
        return wfConfService.showActivityByBpmn(pageNo,pageSize,procdefId);
    }

    /**
     * 展示动态表单 ： 根据活动任务节点
     * @param procdefId : 流程定义id
     * @param activityId : 相关节点id
     * @return ：List<FormFieldVO>
     */
    @GetMapping("/get-formlist")
    @ApiOperation(value = "配置表单列表",notes = "配置表单列表")
    public Result<List<FormFieldVO>> showFormByBpmn(@ApiParam(value = "流程定义id",required = true)@RequestParam String procdefId,
                                                    @ApiParam(value = "任务活动节点id",required = true)@RequestParam String activityId){

        List<FormFieldVO> formFields = wfConfService.showFormByBpmn(procdefId,activityId);

        return Result.OK(formFields);
    }

    /**获取扩展属性信息：根据流程定义id
     * @param procdefId : 流程定义id
     * @return : ConfInfoVO
     */
    @GetMapping("/get-extension")
    @ApiOperation(value = "获取扩展属性信息",notes = "获取扩展属性信息")
    public Result<ConfInfoVO> getExtension(@ApiParam(value = "流程定义id",required = true)@RequestParam String procdefId){
        return wfConfService.getExtension(procdefId);
    }
}
