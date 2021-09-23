package org.jeecg.modules.form.aspect;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.springframework.stereotype.Component;

/**
 * 定义表单设计器切面
 * 删除关联路由时触发切面
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/25 16:47
 */
@Aspect
@Slf4j
public class FormAspect {
    private final FormSysPermissionService formSysPermissionService;

    public FormAspect(FormSysPermissionService formSysPermissionService) {
        this.formSysPermissionService = formSysPermissionService;
    }

    @Pointcut("execution(* org.jeecg.modules.system.service.impl.SysPermissionServiceImpl.deletePermission(..))")
    public void deletePointCut(){
        // 确定删除切面
    }

    @Pointcut("execution(* org.jeecg.modules.system.service.impl.SysPermissionServiceImpl.editPermission(..))")
    public void editPointCut(){
        // 确定修改切面
    }

    @Before("deletePointCut()")
    public void sysPermissionDeleteAspect(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        String id = (String) args[0];
        LambdaQueryWrapper<FormSysPermissionDO> queryWrapper = Wrappers
                .lambdaQuery(FormSysPermissionDO.class)
                .eq(FormSysPermissionDO::getSysPermissionId, id);
        if(formSysPermissionService.count(queryWrapper)>0) ServiceUtils.throwIfFailed(
                formSysPermissionService.remove(queryWrapper),
                FormErrorMessageConstant.RELATIVE_PERMISSIONS_OR_ROLE_PERMISSIONS_DELETE_FAILED);
    }
}
