package org.jeecg.modules.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.RepositoryService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.ProcGroupVO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.mapper.WorkbenchDisplayMapper;
import org.jeecg.modules.workflow.service.WfWorkbenchDisplayService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: TODO
 * @author: SunBoWen,qjc
 * @date: 2021年06月03日 14:13
 */
@Service
@Slf4j
public class WfWorkbenchDisplayImpl implements WfWorkbenchDisplayService {

    @Autowired
    private WorkbenchDisplayMapper workbenchDisplayMapper;

    @Override
    public Result<Object> workbenchDisplayFindAll(String userId) {
        //获取服务类
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        Result<Object> result = new Result<>();
        List<ProcGroupVO> concreteContentlist = new ArrayList<>();
        //获取所有的分组
        List<String> workbenchDisplayFindAllGroupList = workbenchDisplayMapper.getWorkbenchDisplayFindAllGroup();
        //通过流程分组名称获取该流程分组名称下的具体的流程信息
        for (int i = 0; i < workbenchDisplayFindAllGroupList.size();i++) {
            ProcGroupVO procGroupVO = new ProcGroupVO();
            procGroupVO.setProcGroupName(workbenchDisplayFindAllGroupList.get(i));
            List<ProcessDevelopmentVO> processDevelopmentVOList = workbenchDisplayMapper.getWorkbenchDisplayByGroup(workbenchDisplayFindAllGroupList.get(i));

            for (ProcessDevelopmentVO processDevelopmentVO:processDevelopmentVOList){
                long count = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDevelopmentVO.getProcKey()).count();
                processDevelopmentVO.setVersions(count);
            }
            procGroupVO.setNumber(processDevelopmentVOList.size());
            procGroupVO.setProjects(processDevelopmentVOList);
            concreteContentlist.add(procGroupVO);
        }
        //将集合转化为JSON
        String str = JSON.toJSONString(concreteContentlist);
        result.setResult(str);
        result.setMessage(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS);
        return result;
    }
}