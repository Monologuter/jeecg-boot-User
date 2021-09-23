package org.jeecg.modules.message.websocket;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import javax.websocket.server.ServerEndpointConfig;

@Component
public class CustomSpringConfigurator extends ServerEndpointConfig.Configurator implements ApplicationContextAware {
    /**
     * Spring application context.
     */
    /*
     *
     *  static 不要去掉!!! 去掉不能运行了, 此类是spring兼容jakarta ee webSocket的胶水代码,
     * 用于使org.jeecg.modules.message.websocket.WebSocket单例，而不是每连接一个实例，此段代码参考于
     * https://stackoverflow.com/questions/30483094/springboot-serverendpoint-failed-to-find-the-root-webapplicationcontext
     * 具体为什么这么写不太清楚，后续有人看的懂的话，麻烦解释一下，谢谢
     */

    private static BeanFactory context;

    @Override
    public synchronized <T> T getEndpointInstance(Class<T> clazz) throws InstantiationException {
        return context.getBean(clazz);
    }

    @Override
    public synchronized void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        CustomSpringConfigurator.context = applicationContext;
    }
}
