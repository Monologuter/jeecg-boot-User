package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.MyTaskVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WfMyTaskMapper;
import org.jeecg.modules.workflow.service.WfMyTaskService;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @Description: 我的任务实现
 * @author: ghz
 * @date: 2021年03月19日 15:58
 */
@Service
@Slf4j
public class WfMyTaskServiceImpl implements WfMyTaskService {

    @Resource
    private WfMyTaskMapper wfMyTaskMapper;

    /**
     * 获取我的任务：
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param assignee 当前登录用户的用户名
     * @return Result<List<MyTaskVO>>
     */
    @Override
    public Result<RecordsListPageVO> queryMyTask(Integer pageNo, Integer pageSize, String assignee, String procInstName, String startUserId) {
        int firstResult = (pageNo-1)*pageSize; //获取之前页的数据条数
        long total;
        List<MyTaskVO> myTaskVOS = wfMyTaskMapper.queryMyTaskPage(firstResult,
                                                                    pageSize,
                                                                    assignee,
                                                                    procInstName,
                                                                    startUserId);
        List<MyTaskVO> resultList = myTaskVOS.stream()
                .map(myTaskVO -> {
                    myTaskVO.setClaim(wfMyTaskMapper.queryGroupTask(myTaskVO.getTaskId()) != 0);
                    return myTaskVO;
                })
                .collect(Collectors.toList());
        total = wfMyTaskMapper.queryMyTask(
                assignee,
                procInstName,
                startUserId)
                .size();
        return Result.OK("我的任务获取",
                UtilTools.getListPage(total,pageNo,pageSize,resultList));
    }

}
