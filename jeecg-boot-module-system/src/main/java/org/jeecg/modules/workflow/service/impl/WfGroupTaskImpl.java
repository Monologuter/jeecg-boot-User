package org.jeecg.modules.workflow.service.impl;

import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.CandidateAboutVO;
import org.jeecg.modules.workflow.entity.vo.GroupVO;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WorkFlowGroupTaskMapper;
import org.jeecg.modules.workflow.service.WfGroupTaskService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 查询组任务
 * @author: lz
 * @date: 2021年05月13日 11:56
 */
@Service
public class WfGroupTaskImpl implements WfGroupTaskService {

    @Autowired
    private WorkFlowGroupTaskMapper workFlowGroupTaskMapper;
    /**
     * @Description:
     * 获取我的组任务
     * @param pageNo 当前页
     * @param pageSize 每页数据条数
     * @param assignee 用户id
     * @param startUserName 流程实例发起人名称
     * @param groupName 组名称
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:58
     */
    @Override
    public Result<RecordsListPageVO> queryGroupTask(Integer pageNo,
                                                    Integer pageSize,
                                                    String assignee,
                                                    String startUserName,
                                                    String groupName) {

        // 入参校验
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(assignee)) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数不能为空");
        }

        //调用mapper
        List<GroupVO> groupVOList = workFlowGroupTaskMapper
                .queryGroupTask(pageSize,
                        (pageNo - 1) * pageSize,
                        assignee,
                        startUserName,
                        groupName);


        //获得total（数据总条数）
        Long total = workFlowGroupTaskMapper
                .queryGroupTaskCount(assignee, startUserName, groupName);

        return Result.OK( UtilTools.getListPage(total, pageNo, pageSize, groupVOList));
    }
}
