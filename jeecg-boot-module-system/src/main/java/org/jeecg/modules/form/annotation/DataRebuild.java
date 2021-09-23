package org.jeecg.modules.form.annotation;

import java.lang.annotation.*;

/**
 * @ClassName DataRebuid
 * @Description: 表单数据重构
 * @Author HuangSn
 * @Date 2021-09-16 10:26 上午
 */
@Target(value = {ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DataRebuild {
    String value() default "";
}
