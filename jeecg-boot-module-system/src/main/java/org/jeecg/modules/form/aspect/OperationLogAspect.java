package org.jeecg.modules.form.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.form.constant.OperationTypeEnum;
import org.jeecg.modules.form.entity.OperationLog;
import org.jeecg.modules.form.service.FormDataService;
import org.jeecg.modules.form.service.OperationLogService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @ClassName: OperationLogAspect
 * @Description: 表单日志的切面方法
 * @author: HuangSn
 * @date: 2021/8/30  10:40
 */
@Aspect
@Component
@Slf4j
public class OperationLogAspect {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    FormDataService formDataService;

    @Autowired
    OperationLogService operationLogService;

    /**
     * 切入点方法，使用了自定义注解的方式
     *
     * @Author huang sn
     * @Date 17:46 2021/9/2
     * @Param []
     */
    @Pointcut(value = "@annotation(org.jeecg.modules.form.annotation.OperationLog)")
    private void aspect() {
    }

    /**
     * 环绕通知
     *
     * @return java.lang.Object
     * @Author huang sn
     * @Date 15:19 2021/9/6
     * @Param [joinPoint]
     */
    @Around(value = "aspect()")
    public Object before(ProceedingJoinPoint joinPoint) throws Throwable {
        /*
         * 表单数据查询路径
         */
        final String getPath = "/jeecg-boot/form/data/get";
        /*
         *  表单数据删除路径
         */
        final String deletePath = "/jeecg-boot/form/data/delete";
        /*
         * 表单数据批量删除路径
         */
        final String deleteBatchPath = "/jeecg-boot/form/data/delete-batch";
        /*
         * 表单数据保存路径
         */
        final String savePath = "/jeecg-boot/form/data/save";
        /*
         * 表单数据更新路径
         */
        final String updatePath = "/jeecg-boot/form/data/update";
        /*
         * request 请求头
         */
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert requestAttributes != null;
        HttpServletRequest request = requestAttributes.getRequest();
        /*
         * 时间处理
         */
        long visitTime = System.currentTimeMillis();
        Date date = new Date(visitTime);
        String requestTime = new SimpleDateFormat().format(date);
        /*
         * IP地址
         */
        String requestIp = request.getRemoteAddr();
        /*
         * 请求平台
         */
        String requestAgent = request.getHeader("User-Agent");
        /*
         * 请求端口
         */
        String requestPort = Integer.toString(request.getLocalPort());
        /*
         * 请求路径
         */
        String requestUrl = request.getRequestURI();
        /*
         * 请求方式
         */
        String requestMethod = request.getMethod();
        /*
         * 请求参数
         */
        String requestParam = request.getParameterMap().toString();
        /*
         * 用户名
         */
        String loginUser = ((LoginUser) SecurityUtils.getSubject().getPrincipal()).getUsername();
        /*
         * 部门id
         */
        String departmentId = ((LoginUser) SecurityUtils.getSubject().getPrincipal()).getDepartIds();
        /*
         * 删除数据接口传入的单个的表单数据id
         */
        String[] singleId = request.getParameterMap().get("id");
        /*
         * 批量删除表单数据接口传入的多个表单数据
         */
        String[] multiId = request.getParameterMap().get("ids");
        /*
         * 旧的表单数据
         */
        JSONObject oldData;
        /*
         * 返回结果
         * TODO:这个默认全是“1”，原因：
         */
        String result = "1";
        /*
         * 操作类型
         */
        String operationType = "";
        /*
         * 表单id
         */
        String formId = "";
        /*
         * 请求类方法
         */
        String requestDeclaringTypeName = joinPoint.getSignature().getDeclaringTypeName();
        /*
         * 请求的参数
         */
        Object[] requestArgs = joinPoint.getArgs();
        JSONObject jsonObject = null;
        /*
         * 日志打印
         */
        printLog(requestTime, requestIp, requestAgent, requestPort, requestUrl, requestMethod, requestParam, requestDeclaringTypeName, requestArgs);
        /*
         * 处理multiId，因为传入的是以 “,” 分隔的一串id，所以需要切割开
         */
        if (multiId != null) {
            multiId = multiId[0].split(",");
        }
        /*
         * 根据不同的请求路径，判断请求方式
         */
        if (getPath.equals(requestUrl)) {
            operationType = OperationTypeEnum.READ.name();
        } else if (deletePath.equals(requestUrl) || deleteBatchPath.equals(requestUrl)) {
            operationType = OperationTypeEnum.DELETE.name();
        } else if (savePath.equals(requestUrl)) {
            jsonObject = (JSONObject) JSON.toJSON(requestArgs[0]);
            operationType = OperationTypeEnum.INSERT.name();
        } else if (updatePath.equals(requestUrl)) {
            jsonObject = (JSONObject) JSON.toJSON(requestArgs[0]);
            operationType = OperationTypeEnum.UPDATE.name();
        }
        /*
         * 读数据
         * (1)从数据库中，获取当前传入form_data_id对应的数据，赋值给oldData；
         * (2)根据form_data_id，查表，拿到对应的form_id
         */
        if (operationType.equals(OperationTypeEnum.READ.name())) {
            oldData = getRawData(singleId[0]);
            formId = getFormId(singleId[0]);
            operationLogService.addOperationLog(getOperationLog(null, singleId[0], operationType, loginUser,
                    departmentId, requestAgent, requestIp, requestPort, oldData, null, result, formId));
        }
        /*
         * 删除数据
         * (1)前端传入一个form_data_id --> singleId
         * (2)根据singleId去数据库查找row_data的值，赋给oldData
         * (3)获取表单id
         */
        else if (operationType.equals(OperationTypeEnum.DELETE.name())) {
            if (singleId != null && multiId == null) {
                log.info("删除数据");
                oldData = getRawData(singleId[0]);
                formId = getFormId(singleId[0]);
                log.info("表单id:" + formId);
                operationLogService.addOperationLog(getOperationLog(null, singleId[0], operationType, loginUser,
                        departmentId, requestAgent, requestIp, requestPort, oldData, null, result, formId));
            } else if (singleId == null && multiId != null) {
                log.info("批量删除");
                for (String s : multiId) {
                    oldData = getRawData(s);
                    formId = getFormId(s);
                    operationLogService.addOperationLog(getOperationLog(null, s, operationType, loginUser,
                            departmentId, requestAgent, requestIp, requestPort, oldData, null, result, formId));
                }
            }
        }
        /*
         * 新增数据
         * (1)前端传入id --> form_data_id,   前端传入 row_data
         * (2)(JSONObject) jsonObject.get("rowData") 获取到前端传入的row_data
         */
        else if (operationType.equals(OperationTypeEnum.INSERT.name())) {
            if (requestArgs != null) {
                formId = getFormIdByArgsString(requestArgs[0].toString());
            }
            operationLogService.addOperationLog(getOperationLog(null, null, operationType, loginUser,
                    departmentId, requestAgent, requestIp, requestPort, null, (JSONObject) jsonObject.get("rowData"), result, formId));
        }
        /*
         * 更新数据
         */
        else if (operationType.equals(OperationTypeEnum.UPDATE.name())) {
            formId = jsonObject.get("formId").toString();
            String formDataId = jsonObject.get("id").toString();
            operationLogService.addOperationLog(getOperationLog(null, formDataId, operationType, loginUser,
                    departmentId, requestAgent, requestIp, requestPort, getRawData((String) jsonObject.get("id")),
                    (JSONObject) jsonObject.get("rowData"), result, formId));
        }
        log.info("切面结束，返回数据");
        return joinPoint.proceed(requestArgs);
    }

    /**
     * @param requestTime              请求时间
     * @param requestIp                请求IP
     * @param requestAgent             请求平台
     * @param requestPort              请求端口
     * @param requestUrl               请求路径
     * @param requestMethod            请求方法
     * @param requestParam             请求参数
     * @param requestDeclaringTypeName 请求参数类型
     * @param requestArgs              请求参数
     */
    private void printLog(String requestTime, String requestIp, String requestAgent, String requestPort,
                          String requestUrl, String requestMethod, String requestParam, String requestDeclaringTypeName,
                          Object[] requestArgs) {
        log.info("==== 表单数据日志监控，开始 ====");
        log.info("请求时间：" + requestTime);
        log.info("请求IP：" + requestIp);
        log.info("请求平台：" + requestAgent);
        log.info("请求端口：" + requestPort);
        log.info("请求地址：" + requestUrl);
        log.info("请求方式：" + requestMethod);
        log.info("请求类方法：" + requestDeclaringTypeName);
        log.info("请求类方法的参数：" + Arrays.toString(requestArgs));
        log.info("请求参数Map:" + requestParam);
        log.info("==== 表单数据日志监控，结束 ====");
    }

    /**
     * 剪切字段，拿到表单id
     */
    public String getFormIdByArgsString(String str) {
        int start = str.indexOf("formId") + 7;
        int end = str.indexOf("rowData") - 2;
        return str.substring(start, end);
    }

    /**
     * 创建日志对象，将数据写入对象
     *
     * @param requestMethods 请求方法
     * @param loginUser      操作的用户
     * @param departmentId   部门id
     * @param platform       登录平台
     * @param loginIp        登录ip
     * @param loginPort      登录端口
     * @param oldData        旧的数据
     * @param newData        新的数据
     * @param result         返回值
     * @param id             id
     * @return OperationLog
     */
    @NotNull
    private OperationLog getOperationLog(String id, String formDataId, String requestMethods, String loginUser,
                                         String departmentId, String platform, String loginIp, String loginPort,
                                         JSONObject oldData, JSONObject newData, String result, String formId) {
        OperationLog operationLog = new OperationLog();
        // 设置id,控制，U_ID
        operationLog.setId(id);
        // 设置表单数据id
        operationLog.setFormDataId(formDataId);
        // 设置表单类型
        operationLog.setOperType(requestMethods);
        // 设置登录平台
        operationLog.setOperPlatform(platform);
        // 设置登陆账号
        operationLog.setOperUser(loginUser);
        // 设置部门id
        operationLog.setOperDeptId(departmentId);
        // 设置登录IP
        operationLog.setOperIp(loginIp);
        // 设置登录端口
        operationLog.setOperPort(loginPort);
        // 旧的raw_date
        operationLog.setRawOldData(oldData);
        // 新的raw_date
        operationLog.setRawNewData(newData);
        // 设置取值
        operationLog.setOperResult(result);
        // 设置formId
        operationLog.setFormId(formId);
        return operationLog;
    }

    /**
     * 根据前端传入的参数，获取表单已经存在的raw_data
     *
     * @param id 表单数据ID
     * @return raw_data
     */
    public JSONObject getRawData(String id) {
        return formDataService.getById(id).getRowData();
    }

    /**
     * afterReturning
     *
     * @param obj 对象
     * @throws JsonProcessingException 异常
     */
    @AfterReturning(returning = "obj", pointcut = "aspect()")
    public Object methodAfterReturning(Object obj) throws JsonProcessingException {
        log.info(objectMapper.writeValueAsString(obj));
        return obj;
    }

    /**
     * 根据表单数据id获取表单id
     *
     * @return java.lang.String
     * @Author huang sn
     * @Date 11:18 2021/9/6
     * @Param [id]
     */
    private String getFormId(String id) {
        return formDataService.getFormIdByFormDataId(id);
    }
}
