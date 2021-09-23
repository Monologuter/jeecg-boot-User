package org.jeecg.modules.workflow.service.impl;

import org.camunda.bpm.engine.RepositoryService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.mapper.WorkFlowExtendsMapper;
import org.jeecg.modules.workflow.service.WfRecentUserService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * 最近使用-业务层
 * @author 权计超
 * @date 2021-6-10
 */
@Service
public class WfRecentUserServiceIpml implements WfRecentUserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private WorkFlowExtendsMapper workFlowExtendsMapper;
    /**
     * 获取最近使用
     * @param userId
     */
    public Result<List<ProcessDevelopmentVO>> getRecentUse(String userId){
        List<String> recentUse = stringRedisTemplate.opsForList().range(userId, 0L, -1L);
        if (recentUse.isEmpty()){
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,null);
        }
        List<ProcessDevelopmentVO> extendsByProcKey = workFlowExtendsMapper.getExtendsByProcKey(recentUse);
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        for (ProcessDevelopmentVO process:extendsByProcKey){
            long count = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(process.getProcKey())
                    .count();
            process.setVersions(count);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,extendsByProcKey);
    }
}