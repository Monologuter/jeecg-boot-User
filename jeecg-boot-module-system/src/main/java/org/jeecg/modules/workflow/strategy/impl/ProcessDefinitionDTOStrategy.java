package org.jeecg.modules.workflow.strategy.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.checkerframework.checker.units.qual.A;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.mapper.WorkFlowExtendsMapper;
import org.jeecg.modules.workflow.service.WfDefinitionService;
import org.jeecg.modules.workflow.strategy.MappingStrategy;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName ProcessDefinitionDTOStrategy
 * @CreateTime 2021-08-30 17:48
 * @Version 1.0
 * @Description: ProcessDefinitionDTO 转 UpdatList
 */
public class ProcessDefinitionDTOStrategy implements MappingStrategy {

    @Autowired
    private WorkFlowExtendsMapper workFlowExtendsMapper;
    @Autowired
    private WfDefinitionService wfDefinitionService;
    @Override
    public Object executeMapping(Object originals) {
        List<ProcessDefinition> processDefinitions = (List<ProcessDefinition>)originals;
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        List<ProcessDefinitionDTO> updateList = new ArrayList<>();
        for(ProcessDefinition p:processDefinitions){
            //所有被修改过的流程定义
            Long count = runtimeService.createExecutionQuery()
                    .processDefinitionId(p.getId())
                    .count();
            ProcessDefinitionDTO pdVO = new ProcessDefinitionDTO();
            pdVO.setProcessDefinitionName(p.getName());
            pdVO.setProcessDefinitionDeployState(count!=0);
            pdVO.setProcessDefinitionKey(p.getKey());
            pdVO.setProcessDefinitionId(p.getId());
            pdVO.setDeploymentId(p.getDeploymentId());
            pdVO.setTagVersion(p.getVersionTag());
            updateList.add(pdVO);
        }
        return updateList;
    }
}
