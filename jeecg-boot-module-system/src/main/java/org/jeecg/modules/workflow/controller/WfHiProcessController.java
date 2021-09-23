package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfHistoricalProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: 我的流程相关
 * @author: lz
 * @date: 2021年04月14日 8:57
 */
@RestController
@RequestMapping("workflow/workflow-myProcess")
@Api(tags="工作流")
@Slf4j
public class WfHiProcessController {
    @Autowired
    private WfHistoricalProcessService wfHistoricalProcessService;

    /**
     * @Description:
     * 查询由我发起的正在运行中的流程实例
     * @param pageNo 当前页
     * @param pageSize 当前页显示页码条数
     * @param startUserId 流程发起人的id
     * @param proDefName 流程定义的名称
     * @param proDefKey 流程定义的key（业务标题）
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology  
     * @Time: 2021/8/27 11:14
    */
    @AutoLog(value = "我（发起）的流程")
    @ApiOperation(value = "我（发起）的流程",notes = "我（发起）的流程")
    @GetMapping("/get-myProcess")
    public Result<RecordsListPageVO> queryMyProcess(@ApiParam(value = "当前页" ,required = true)
                                                    @RequestParam Integer pageNo,
                                                    @ApiParam(value = "显示多少条",required = true)
                                                    @RequestParam Integer pageSize,
                                                    @ApiParam(value = "发起人", required = true)
                                                    @RequestParam String startUserId,
                                                    @ApiParam("流程定义名称")
                                                    @RequestParam(required = false) String proDefName,
                                                    @ApiParam("流程定义的key")
                                                    @RequestParam(required = false) String proDefKey) {

        return wfHistoricalProcessService.queryMyProcess(pageNo, pageSize, startUserId,proDefName,proDefKey);
    }

    /**
     * @Description:
     * 由我发起的所有流程
     * @param pageNo 当前页码
     * @param pageSize 当前页显示数据条数
     * @param startUserId 流程发起人id
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key （业务标题）
     * @param proState 流程的状态
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 11:24
    */
    @AutoLog(value = "我（发起）的历史流程")
    @ApiOperation(value = "我（发起）的历史流程",notes = "我（发起）的历史流程")
    @GetMapping("/get-historicalProcess")
    public Result<RecordsListPageVO> queryHistoricalProcess(@ApiParam(value = "当前页" ,required = true)
                                                                @RequestParam Integer pageNo,
                                                           @ApiParam(value = "显示多少条",required = true)
                                                                @RequestParam Integer pageSize,
                                                           @ApiParam(value = "发起人", required = true)
                                                                @RequestParam String startUserId,
                                                           @ApiParam("流程定义名称")
                                                                @RequestParam(required = false) String proDefName,
                                                           @ApiParam("流程定义的key")
                                                                @RequestParam(required = false) String proDefKey,
                                                            @ApiParam("流程定义的状态")
                                                                @RequestParam(required = false) String proState) {

        return wfHistoricalProcessService.queryHistoricalProcess(pageNo, pageSize, startUserId,proDefName,proDefKey,proState);
    }



}
