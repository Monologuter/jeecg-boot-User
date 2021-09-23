package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.workflow.entity.vo.ActAssMsgVO;
import org.jeecg.modules.workflow.entity.vo.ActivitiHighLineDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfTaskBpmnService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @Description: 历史流程以及bpmn图片
 * @author: ghz
 * @date: 2021年03月29日 9:15
 */
@RestController
@RequestMapping("workflow/workflow-taskbpmn")
@Api(tags="工作流")
@Slf4j
public class WfTaskBpmnController {
    @Resource
    private WfTaskBpmnService wfTaskBpmnService;


    /**
     * @param processDefinitionId 流程定义ID
     * @throws IOException
     * 根据流程定义ID返回Bpmn文件Json字符串
     */
    @AutoLog(value = "bpmnJson")
    @ApiOperation(value = "bpmnJson",notes = "bpmnJson")
    @GetMapping("/get-bpmnJson")
    public Result<String> bpmnJson(@ApiParam("流程定义ID") @RequestParam String processDefinitionId) throws IOException {
        return wfTaskBpmnService.getBpmnXml(processDefinitionId);
    }


    /**
     * 历史流程追踪表
     * @param pageNo 当前页
     * @param pageSize 显示多少条
     * @param processInsId 流程实例Id
     */

    @AutoLog(value = "历史流程追踪")
    @ApiOperation(value = "历史流程追踪",notes = "历史流程追踪")
    @GetMapping("/get-hiActInst")
    public Result<RecordsListPageVO> historicalProcessTracking(@ApiParam("当前页") @RequestParam Integer pageNo ,
                                                               @ApiParam("显示多少条") @RequestParam Integer pageSize ,
                                                               @ApiParam("流程实例ID") @RequestParam String processInsId){
        return wfTaskBpmnService.getHistoryActive(pageNo , pageSize , processInsId);
    }

    /**
     * 显示高亮节点
     * @param processInsId 流程实例ID
     */
    @SneakyThrows
    @AutoLog(value = "bpmnHighLight")
    @ApiOperation(value = "bpmnHighLight",notes = "bpmnHighLight")
    @GetMapping("/get-bpmnHighLight")
    public Result<ActivitiHighLineDTO> queryHighLight(@ApiParam("流程实例ID") @RequestParam String processInsId){

        return wfTaskBpmnService.getHigh(processInsId);

    }

    /**
     *获取任务节点的执行人信息
     * @param processInsId 流程实例ID
     */
    @AutoLog(value = "获取任务节点信息")
    @ApiOperation(value = "获取任务节点信息",notes = "获取任务节点信息")
    @GetMapping("/get-actMsg")
    public  Result<List<ActAssMsgVO>> queryActMsg(@ApiParam("流程实例ID") @RequestParam String processInsId){

        return wfTaskBpmnService.getActMsg(processInsId);

    }
}
