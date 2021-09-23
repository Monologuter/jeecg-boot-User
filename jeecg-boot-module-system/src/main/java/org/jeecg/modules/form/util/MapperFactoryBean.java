package org.jeecg.modules.form.util;

import com.baomidou.mybatisplus.core.MybatisMapperRegistry;
import com.baomidou.mybatisplus.core.toolkit.GlobalConfigUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.util.Assert;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 重写MapperFactoryBean，用在实例化时使用，添加对移除Bean时残留的内存数据清除，防止二次加载失败。
 *
 * @Author: HuQi
 * @Date: 2021年08月10日 11:35
 */
@Slf4j
public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {
    private Class<T> mapperInterface;
    private boolean addToConfig = true;

    public MapperFactoryBean(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    @Override
    protected void checkDaoConfig() {
        super.checkDaoConfig();
        Assert.notNull(this.mapperInterface, "Property 'mapperInterface' is required");
        Configuration configuration = this.getSqlSession().getConfiguration();
        String interfaceName = this.mapperInterface.getName();

        // 清除已存的残留  重要修改的地方
        boolean isSupper = configuration.getClass().getSuperclass() == Configuration.class;
        try {
            Field loadedResourcesField = isSupper
                    ? configuration.getClass().getSuperclass().getDeclaredField("loadedResources")
                    : configuration.getClass().getDeclaredField("loadedResources");
            loadedResourcesField.setAccessible(true);
            Set<String> loadedResourcesSet = ((Set) loadedResourcesField.get(configuration));
            loadedResourcesSet.remove("interface " + interfaceName);

            Set<String> mapperRegistryCache = GlobalConfigUtils.getMapperRegistryCache(configuration);
            mapperRegistryCache.remove("interface " + interfaceName);

            Field knownMappers = MybatisMapperRegistry.class.getDeclaredField("knownMappers");
            knownMappers.setAccessible(true);
            Map<Class<?>, MapperProxyFactory<?>> knownMappersMap = (Map<Class<?>, MapperProxyFactory<?>>)
                    knownMappers.get(configuration.getMapperRegistry());
            for (Class<?> key : knownMappersMap.keySet()) {
                if (key.getName().startsWith(interfaceName.substring(0, interfaceName.lastIndexOf(".")))){
                    knownMappersMap.remove(key);
                    logger.info("移除残留的knownMappersMap：" + key.getName());
                    break;
                }
            }

            Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
            List<MappedStatement> objects = Lists.newArrayList();
            for (Object object : mappedStatements) {
                if (object instanceof MappedStatement) {
                    MappedStatement mappedStatement = (MappedStatement) object;
                    if (mappedStatement.getId().startsWith(interfaceName)) {
                        objects.add(mappedStatement);
                    }
                }
            }
            mappedStatements.removeAll(objects);
            SqlSessionTemplate sqlSessionTemplate = this.getSqlSessionTemplate();
            sqlSessionTemplate.flushStatements();

        } catch (Exception e) {
            logger.error("Refresh IOException :" + e.getMessage());
        }

        if (this.addToConfig && !configuration.hasMapper(this.mapperInterface)) {
            try {
                configuration.addMapper(this.mapperInterface);
            } catch (Exception var6) {
                this.logger.error("Error while adding the mapper '" + this.mapperInterface + "' to configuration.", var6);
                throw new IllegalArgumentException(var6);
            } finally {
                ErrorContext.instance().reset();
            }
        }

    }

    @Override
    public T getObject() {
        return this.getSqlSession().getMapper(this.mapperInterface);
    }

    @Override
    public Class<T> getObjectType() {
        return this.mapperInterface;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public void setMapperInterface(Class<T> mapperInterface) {
        this.mapperInterface = mapperInterface;
    }

    public Class<T> getMapperInterface() {
        return this.mapperInterface;
    }

    public void setAddToConfig(boolean addToConfig) {
        this.addToConfig = addToConfig;
    }

    public boolean isAddToConfig() {
        return this.addToConfig;
    }
}

