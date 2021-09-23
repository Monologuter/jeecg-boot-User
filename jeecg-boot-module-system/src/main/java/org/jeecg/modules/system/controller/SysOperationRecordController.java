package org.jeecg.modules.system.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysOperationRecord;
import org.jeecg.modules.system.service.ISysOperationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.jeecg.modules.system.util.ResultUtil.badRequest;
import static org.jeecg.modules.system.util.ResultUtil.ok;

@RestController
@RequestMapping("/sys/operation")
@Slf4j
@Api(tags = "变更记录")
public class SysOperationRecordController {

    @Autowired
    private ISysOperationRecordService sysOperationRecordService;

    /**
     * 接收还原信息id并区分还原
     * @param OperationRecordId
     * @return
     */
    @ApiOperation("还原基本信息的修改")
    @GetMapping(path = "/reduction")
    public Result<String> reduction(@RequestBody String OperationRecordId){
        Boolean success = sysOperationRecordService.reduction(OperationRecordId);
        return success ? ok() : badRequest();
    }
    @ApiOperation("展示变更操作记录")
    @GetMapping(path = "/display")
    public Result<List<SysOperationRecord>> displayOperationRecord(@RequestBody String applicationId){
        List<SysOperationRecord> sysOperationRecords=sysOperationRecordService.displayOperationRecord(applicationId);
        Result<List<SysOperationRecord>> result=new Result<>();
        result.setCode(200);
        result.setResult(sysOperationRecords);
        return result;
    }
}
