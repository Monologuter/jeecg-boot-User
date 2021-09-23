package org.jeecg.modules.workflow.strategy;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName MappingStrategy
 * @CreateTime 2021-08-27 16:12
 * @Version 1.0
 * @Description: 原生实体类—>自定义实体类映射策略
 */
public interface MappingStrategy {
    /**
     * 执行映射
     * @param originals 原生实体类
     * @return 返沪自定义实体类
     */
    Object executeMapping(Object originals);
}
