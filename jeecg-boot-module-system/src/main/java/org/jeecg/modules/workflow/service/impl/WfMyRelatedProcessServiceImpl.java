package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WorkFlowMyRelatedProcessMapper;
import org.jeecg.modules.workflow.service.WfMyRelatedProcessService;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

/**
 * @Description: 我的历史任务所属流程状态
 * @author: lz
 * @date: 2021-8-20
 */
@Service
@Slf4j
public class WfMyRelatedProcessServiceImpl implements WfMyRelatedProcessService {

    @Autowired
    private WorkFlowMyRelatedProcessMapper workFlowMyRelatedProcessMapper;

    /**
     * @Description:
     * 我已办（工作台）
     * 查询我的历史任务所属流程的相关信息
     * @param pageNo 当前页
     * @param pageSize 每页数据的条数
     * @param assignee 办理人id
     * @param proState 流程的状态
     * @param startUserId 流程发起人
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/30 14:11
    */
    @Override
    public Result<RecordsListPageVO> queryProcessByTaskAssignee(Integer pageNo,
                                                                Integer pageSize,
                                                                String assignee,
                                                                String proState,
                                                                String startUserId) {

        //入参判断
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(assignee)) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数不能为空");
        }

        //调用mapper
        List<ProcessInstanceDTO> listResult = workFlowMyRelatedProcessMapper
                .queryProcessByTaskAssignee(
                        pageSize,
                        (pageNo - 1) * pageSize,
                        assignee,
                        proState,
                        startUserId);

        //获得total（数据总条数）
        Long total = workFlowMyRelatedProcessMapper
                .queryProcessByTaskAssigneeCount(assignee, proState, startUserId);

        return Result.OK("我的历史任务所属流程状态", UtilTools.getListPage(total, pageNo, pageSize, listResult));
    }
}
