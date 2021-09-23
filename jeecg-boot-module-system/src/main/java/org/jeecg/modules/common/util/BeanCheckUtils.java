package org.jeecg.modules.common.util;

import cn.hutool.core.lang.func.Func;
import org.jeecg.modules.common.constant.BeanErrorMessageConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

/**
 * What purpose
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/25 14:38
 */
public class BeanCheckUtils {
    private BeanCheckUtils() {
    }

    @SafeVarargs
    public static <T> void beanIsAllEmpty(T t, Function<T, ?>... functions) {
        AtomicBoolean notEmpty = new AtomicBoolean(false);
        Arrays.asList(functions).forEach(function -> notEmpty.set(notEmpty.get() || Objects.nonNull(function.apply(t))));
        ServiceUtils.throwIfFailed(notEmpty.get(), BeanErrorMessageConstant.BEAN_FIELDS_ALL_NULL);
    }

    @SafeVarargs
    public static <T> void beanIsEmpty(T t, Function<T, ?>... functions) {
        for(Function<T,?> function : functions){
            if(Objects.isNull(function.apply(t)))
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,BeanErrorMessageConstant.BEAN_FIELDS_NULL);
        }
    }
}
