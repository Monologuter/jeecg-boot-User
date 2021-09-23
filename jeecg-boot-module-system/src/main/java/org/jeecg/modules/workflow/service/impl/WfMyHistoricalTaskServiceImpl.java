package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.MyHistoricalTaskVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WorkFlowMyHistoricalTaskMapper;
import org.jeecg.modules.workflow.service.WfMyHistoricalTaskService;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description: 我的历史任务实现
 * @author: lz
 * @date: 2021年04月09日 14:15
 */
@Service
@Slf4j
public class WfMyHistoricalTaskServiceImpl implements WfMyHistoricalTaskService {

    @Autowired
    private WorkFlowMyHistoricalTaskMapper workFlowMyHistoricalTaskMapper;

    private final Map<String, String> map = new HashMap<>();

    /**
     * @Description:
     * 查询我的历史任务
     * @param pageNo 当前页码
     * @param pageSize 当前页码显示数据条数
     * @param assignee 任务办理人
     * @param processInstanceId 流程定义id
     * @param startUserName 启动流程的用户
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 17:06
    */
    @Override
    public Result<RecordsListPageVO> queryMyHistoricalTask(Integer pageNo,
                                                           Integer pageSize,
                                                           String assignee,
                                                           String processInstanceId,
                                                           String startUserName) {


        // 入参校验
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize) || StringUtils.isEmpty(assignee)) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "参数不能为空");
        }

        //调mapper查询
        List<MyHistoricalTaskVO> myHistoricalTaskVOS = workFlowMyHistoricalTaskMapper
                .queryMyHistoricalTask(pageSize,
                        (pageNo - 1) * pageSize,
                        assignee,
                        processInstanceId,
                        startUserName);

        //状态判断
        if (map.isEmpty()) {
            map.put(null, "未完成");
            map.put("completed", "已完成");
            map.put("EXTERNALLY_TERMINATED", "被驳回");
            map.put("INTERNALLY_TERMINATED", "被驳回");
            map.put("deleted", "被驳回");
            map.put("主动作废", "被驳回");
        }

        //耗时转换
        for (MyHistoricalTaskVO myHistoricalTaskVO : myHistoricalTaskVOS) {
            myHistoricalTaskVO.setState(map.get(myHistoricalTaskVO.getState()));
            if (myHistoricalTaskVO.getEndTime() == null) {
                myHistoricalTaskVO.setDuration(UtilTools.timeConsuming(myHistoricalTaskVO.getStartTime()));
            } else {
                myHistoricalTaskVO.setDuration(UtilTools.timeCon(Long.valueOf(myHistoricalTaskVO.getDuration())));
            }
        }

        Long total = workFlowMyHistoricalTaskMapper
                .queryMyHistoricalTaskCount(assignee, processInstanceId, startUserName);
        return Result.OK( UtilTools.getListPage(total, pageNo, pageSize, myHistoricalTaskVOS));

    }
}
