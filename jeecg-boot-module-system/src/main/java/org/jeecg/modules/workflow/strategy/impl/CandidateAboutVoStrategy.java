package org.jeecg.modules.workflow.strategy.impl;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.jeecg.modules.workflow.entity.vo.CandidateAboutVO;
import org.jeecg.modules.workflow.strategy.MappingStrategy;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.util.StringUtils;
import org.camunda.bpm.engine.task.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName CandidateAboutVoStrategy
 * @CreateTime 2021-08-30 9:50
 * @Version 1.0
 * @Description: List<Task> 转 List<CandidateAboutVO>
 */
public class CandidateAboutVoStrategy implements MappingStrategy {

    @Override
    public Object executeMapping(Object originals) {

        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        List<Task> taskList =  (List<Task>) originals;
        List<CandidateAboutVO> candidateAboutVoList = new ArrayList<>();
        for (Task task : taskList) {
            ProcessDefinitionQuery processDefinitionQuery = repositoryService
                    .createProcessDefinitionQuery()
                    .processDefinitionId(task.getProcessDefinitionId());
            List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        
            HistoricProcessInstance processInstance=historyService.createHistoricProcessInstanceQuery().processInstanceId(task.getProcessInstanceId()).singleResult();
            for (ProcessDefinition processDefinition : processDefinitionList) {
                //将数据进行封装
                CandidateAboutVO candidateAboutVo = new CandidateAboutVO();
                candidateAboutVo.setProcessDefinitionId(task.getProcessDefinitionId());
                candidateAboutVo.setTaskId(task.getId());
                candidateAboutVo.setTaskName(task.getName());
                candidateAboutVo.setProcessName(processDefinition.getName());
                candidateAboutVo.setStartUserId(processInstance.getStartUserId());
                candidateAboutVo.setProcessInstanceId(task.getProcessInstanceId());
                candidateAboutVo.setStartTime(task.getCreateTime());
                candidateAboutVoList.add(candidateAboutVo);
            }
        }
        return candidateAboutVoList;
    }
}
