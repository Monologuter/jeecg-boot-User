package org.jeecg.modules.form.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * 动态代理，需要注意的是，这里用到的是JDK自带的动态代理，
 *  代理对象只能是接口，不能是类
 *
 * @author: HuQi
 * @date: 2021年07月16日 13:49
 */
@Slf4j
public class ServiceProxy<T> implements InvocationHandler {


    private Class<T> interfaceType;

    public ServiceProxy(Class<T> intefaceType) {
        this.interfaceType = interfaceType;
    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        log.info("代理开始....");
        Object result = method.invoke(interfaceType, args);
        log.info("代理结束...");
        return result;
    }
}
