package org.jeecg.modules.system.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.properties.ApplicationProperties;
import org.jeecg.modules.system.properties.OperationProperties;
import org.jeecg.modules.system.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Component
@Aspect
@Slf4j
public class SysApplicationAop {
    @Autowired
    private ISysApplicationService sysApplicationService;

    @Autowired
    private ISysOperationRecordService sysOperationRecordService;

    @Autowired
    private ISysDepartApproleApplicationService sysDepartApproleApplicationService;

    @Autowired
    private ISysUserApproleApplicationService sysUserApproleApplicationService;

    @Autowired
    private ISysRoleApproleApplicationService sysRoleApproleApplicationService;

    /**
     * 应用基础信息数据的切面
     */
    @Pointcut("execution(public * org.jeecg.modules.system.controller.SysApplicationController.edit(..))")
    public void ApplicationBaseRecord() {
    }

    /**
     * 应用成员管理操作切面
     */
    @Pointcut("execution(public * org.jeecg.modules.system.controller.SysApplicationController.updateRoleDistribution(..))")
    public void ApplicationRoleRecord() {
    }


    /**
     * 应用基础信息数据的切面功能实现
     *
     * @param joinPoint 切入点
     */
    @Before(value = "ApplicationBaseRecord()")
    @Transactional
    public void ApplicationDataGet(JoinPoint joinPoint) {
        //获取参数 此处为应用的json信息
        Object[] args = joinPoint.getArgs();
        SysApplication sysApplication = (SysApplication) args[0];
        SysApplication oldSysApplication = sysApplicationService.getById(sysApplication.getId());
        //根据应用id查询 基本信息修改前的应用 并以json形式保存下来
        String json = JSON.toJSONString(oldSysApplication, true);
        String currentJson=JSON.toJSONString(sysApplication,true);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //操作信息
        SysOperationRecord sysOperationRecord = new SysOperationRecord();
        sysOperationRecord.setApplicationId(sysApplication.getId());
        sysOperationRecord.setOperationType(OperationProperties.OPERATION_TYPE_BASE_INFO);
        sysOperationRecord.setData(json);
        sysOperationRecord.setCurrentData(currentJson);
        sysOperationRecord.setTime(new Date());
        sysOperationRecord.setOperator(user.getUsername());
        sysOperationRecordService.save(sysOperationRecord);
        log.info("应用基本信息修改前保存完成");
    }

    /**
     * 应用下应用角色更改切面功能实现
     *
     * @param joinPoint 切入点
     */
    @Before(value = "ApplicationRoleRecord()")
    @Transactional
    public void ApplicationRoleChangeRecord(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        JSONObject json = (JSONObject) args[0];
        String currentData=json.toJSONString();
        String applicationId = json.getString("applicationId");
        String appRoleId = json.getString("appRoleId");
        int selectFlag = json.getInteger("selectFlag");

        //键记录选择标记，值记录id集合
        Map<String, Object> map = new HashMap<String, Object>();
        //选择标记为用户
        if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_USER) {
            //根据应用id和应用角色id取得更新前的用户id集合
            Set<String> userIds = sysUserApproleApplicationService.list(new LambdaQueryWrapper<SysUserApproleApplication>()
                    .eq(SysUserApproleApplication::getApplicationId, applicationId)
                    .eq(SysUserApproleApplication::getApproleId, appRoleId))
                    .stream()
                    .map(SysUserApproleApplication::getUserId)
                    .collect(Collectors.toSet());
            map.put("ids", userIds);
        } else if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_ROLE) {
            //根据应用id和应用角色id取得更新前的角色id集合
            Set<String> roleIds = sysRoleApproleApplicationService.list(new LambdaQueryWrapper<SysRoleApproleApplication>()
                    .eq(SysRoleApproleApplication::getApplicationId, applicationId)
                    .eq(SysRoleApproleApplication::getApproleId, appRoleId))
                    .stream()
                    .map(SysRoleApproleApplication::getRoleId)
                    .collect(Collectors.toSet());
            map.put("ids", roleIds);
        } else if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_DEPART) {
            //根据应用id和应用角色id取得更新前的部门id集合
            Set<String> departIds = sysDepartApproleApplicationService.list(new LambdaQueryWrapper<SysDepartApproleApplication>()
                    .eq(SysDepartApproleApplication::getApplicationId, applicationId)
                    .eq(SysDepartApproleApplication::getApproleId, appRoleId))
                    .stream()
                    .map(SysDepartApproleApplication::getDepartId)
                    .collect(Collectors.toSet());
            map.put("ids", departIds);
        } else {
            log.info("参数有误-记录操作失败");
            return;
        }
        map.put("selectFlag", selectFlag);
        map.put("appRoleId", appRoleId);
        //操作表中的记录数据
        String jsonObject = JSON.toJSONString(map, true);
        //保存操作
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        //操作信息
        SysOperationRecord sysOperationRecord = new SysOperationRecord();
        sysOperationRecord.setApplicationId(applicationId);
        sysOperationRecord.setOperationType(OperationProperties.OPERATION_APPLICATION_ROLE_INFO);
        sysOperationRecord.setData(jsonObject);
        sysOperationRecord.setCurrentData(currentData);
        sysOperationRecord.setTime(new Date());
        sysOperationRecord.setOperator(user.getUsername());
        sysOperationRecordService.save(sysOperationRecord);
    }
}
