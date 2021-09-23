package org.jeecg.modules.workflow.strategy.impl;

import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.jeecg.modules.workflow.entity.vo.DefinitionVersionVO;
import org.jeecg.modules.workflow.strategy.MappingStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName DefinitionVersionVoStarttegy
 * @CreateTime 2021-08-27 16:40
 * @Version 1.0
 * @Description: List<ProcessDefinition> 转 List<DefinitionVersionVO>
 */
public class DefinitionVersionVoStarttegy implements MappingStrategy {
    /**
     * 执行映射
     * @param originals 原生实体类
     * @return 返沪自定义实体类
     */
    @Override
    public Object executeMapping(Object originals) {
        List<ProcessDefinition> original = (List<ProcessDefinition>)originals;
        //创建自定义实体类
        List<DefinitionVersionVO> listVo = new ArrayList<>();
        for(ProcessDefinition temp:original){
            DefinitionVersionVO vo = new DefinitionVersionVO();
            vo.setDefinitionId(temp.getId());
            vo.setDefinitionKey(temp.getKey());
            vo.setDefinitionVersion(temp.getVersion());
            vo.setSuspension(temp.isSuspended());
            vo.setDefinitionName(temp.getName());
            listVo.add(vo);
        }
        return listVo;
    }
}
