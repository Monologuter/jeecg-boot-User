package org.jeecg.modules.form.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.OperationLog;
import org.jeecg.modules.form.service.OperationLogService;
import org.jeecg.modules.form.vo.OpeartionLogListVO;
import org.jeecg.modules.form.vo.OperationLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName OperationLogController
 * @CreateTime 2021-08-31 14:11
 * @Version 1.0
 * @Description:
 */
@Api(tags = "表单日志")
@RequestMapping(value = "/formLog")
@RestController
@Slf4j
public class OperationLogController {
    @Autowired
    OperationLogService operationLogService;

    /**
     * 表单日志条件查询
     *
     * @param pageNo   页号
     * @param pageSize 页面大小
     * @param user     用户名
     * @param type     操作类型
     * @param platform 平台
     * @return page
     */
    @ApiOperation("表单日志条件查询")
    @GetMapping(value = "/list")
    public Result<IPage<OperationLog>> listLog(
            @ApiParam("当前页面，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("用户名") String user,
            @ApiParam("操作类型") String type,
            @ApiParam("操作平台") String platform) {
        Page<OperationLog> page = new Page<>(pageNo, pageSize);
        operationLogService.listLogByParam(page, user, type, platform);
        return Result.OK(page);
    }

    /**
     * 表单日志查询
     *
     * @param formId 表单id
     * @return JSON
     */
    @ApiOperation(value = "表单日志查询", notes = "传入表单id，在日志表中查询数据")
    @GetMapping(value = "/getLog")
    public Result<List<OpeartionLogListVO>> getLog(
            @ApiParam("表单id") @RequestParam(required = true) String formId) {
        return Result.OK(operationLogService.getLogByFormId(formId));
    }
}
