package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.CopyVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;
import org.jeecg.modules.workflow.service.WfCopyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: qjc
 * 抄送功能
 * @date 2021/5/25 10：59
 */
@RestController
@RequestMapping("workflow/workflow-copy")
@Api(tags="工作流")
@Slf4j
public class WfCopyController {
    @Autowired
    private WfCopyService wfCopyService;

    @GetMapping("/copy-list")
    @ApiOperation(value = "抄送给我的流程列表",notes = "抄送给我的流程列表")
    public Result<RecordsListPageVO> getCopyInstanceList(@ApiParam(value = "当前用户id",required = true)
                                                  @RequestParam String userId,
                                                         @ApiParam(value = "当前页",required = true)
                                                  @RequestParam int pageNo,
                                                         @ApiParam(value = "每页多少条",required = true)
                                                  @RequestParam int pageSize,
                                                         @ApiParam(value = "当前状态",required = false)
                                                  @RequestParam(required = false) String state,
                                                         @ApiParam(value = "流程实例id",required = false)
                                                  @RequestParam(required = false) String definitionId
                                                         ){
        return wfCopyService.getCopyInstance(userId,pageNo,pageSize,state,definitionId);

    }

    @PostMapping("/set-copys")
    @ApiOperation(value = "设置抄送人",notes = "设置抄送人")
    public Result setCopy(@ApiParam(value = "设置抄送人参数",required = true)@RequestBody CopyVO copyVO){
        return wfCopyService.setCopy(copyVO.getProcessInstanceId(),copyVO.getAddCopyArray());
    }

    @GetMapping("/get-copys")
    @ApiOperation(value = "获取抄送人",notes = "获取抄送人")
    public Result<List<UserDTO>> getCopy(@ApiParam(value = "流程实例id",required = true)
                                             @RequestParam String processInstanceId){
        return wfCopyService.getCopy(processInstanceId);
    }
}