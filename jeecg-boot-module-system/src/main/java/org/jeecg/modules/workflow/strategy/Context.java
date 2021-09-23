package org.jeecg.modules.workflow.strategy;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName Context
 * @CreateTime 2021-08-27 16:58
 * @Version 1.0
 * @Description: 映射上下文对象
 */
public class Context {
    //映射策略
    private MappingStrategy mappingStrategy;
    public Context(MappingStrategy strategy){
        this.mappingStrategy = strategy;
    }

    /**
     * 执行映射
     * @param original 原生类
     * @return
     */
    public Object executeMapping(Object original){
        return mappingStrategy.executeMapping(original);
    }
}
