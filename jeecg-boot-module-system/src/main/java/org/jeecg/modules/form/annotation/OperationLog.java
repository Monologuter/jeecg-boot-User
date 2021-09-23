package org.jeecg.modules.form.annotation;

import java.lang.annotation.*;

/**
 * 定义记录操作日志注解
 *
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName OperationServiceLog
 * @CreateTime 2021-08-30 15:31
 * @Version 1.0
 */
@Documented
@Inherited
@Target(value = {ElementType.METHOD,ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface OperationLog {
    String value() default "";
}
