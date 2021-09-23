package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.HistoryProcessVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.mapper.WorkFlowMyStartProcessMapper;
import org.jeecg.modules.workflow.service.WfHistoricalProcessService;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * @Description: 我的流程相关
 * @author: lz
 * @date: 2021年04月13日 15:41
 */
@Service
@Slf4j
public class WfHistoricalProcessServiceImpl implements WfHistoricalProcessService {

    @Autowired
    private WorkFlowMyStartProcessMapper workFlowMyStartProcessMapper;

    /**
     * @Description:
     * 由我发起的正在运行中的流程
     * @param pageNo 当前页码
     * @param pageSize 当前页码显示数据条数
     * @param startUserId 流程发起人id
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key（业务标题）
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 11:37
    */
    @Override
    public Result<RecordsListPageVO> queryMyProcess(Integer pageNo,
                                                    Integer pageSize,
                                                    String  startUserId,
                                                    String  proDefName,
                                                    String  proDefKey) {

        //获取之前页的数据条数
        int firstResult = (pageNo - 1) * pageSize;
        long duration=0;
        String state=null;

        //通过mapper查询数据
        List<HistoryProcessVO> myStartProcessList= workFlowMyStartProcessMapper
                .myStartProcessList(proDefName,proDefKey,startUserId);

        for(HistoryProcessVO hiProcessVo : myStartProcessList){
            //获得流程当前状态
            state=hiProcessVo.getState();
            switch (state){
                case "ACTIVE":
                    state="审批中";
                    break;
                case "SUSPENDED":
                    state="被挂起";
                    break;
                case "COMPLETED":
                    state="已通过";
                    break;
                case "INTERNALLY_TERMINATED":
                    state="外部暂停";
                    break;
                case "EXTERNALLY_TERMINATED":
                    state="主动作废";
                    break;
                default:
                    break;
            }
            hiProcessVo.setState(state);

            //获得该流程的耗时时间
            if(hiProcessVo.getEndTime()==null){
                hiProcessVo.setDuration(UtilTools.timeConsuming(hiProcessVo.getStartTime()));
            }else {
                duration=Long.parseLong(hiProcessVo.getDuration());
                hiProcessVo.setDuration(UtilTools.timeCon(duration));
            }
        }

        //分页
        List<HistoryProcessVO> reasultList = null;
        if ((pageSize + firstResult) < myStartProcessList.size()) {
            reasultList = myStartProcessList.subList(firstResult, pageSize + firstResult);
        } else {
            reasultList = myStartProcessList.subList(firstResult, myStartProcessList.size());
        }

        long total = myStartProcessList.size();
        return Result.OK("我发起的流程获取",UtilTools.getListPage(total,pageNo,pageSize,reasultList));
    }

    /**
     * @Description:
     * 由我发起的所有流程
     * @param pageNo 当前页码
     * @param pageSize 当前页显示数据条数
     * @param startUserId 流程发起人id
     * @param proDefName 流程定义名称
     * @param proDefKey 流程定义的key （业务标题）
     * @param proState 流程的状态
     * @Return : org.jeecg.common.api.vo.Result<org.jeecg.modules.workflow.entity.vo.RecordsListPageVO>
     * @Author : LZ @DXC.Technology
     * @Time: 2021/8/27 11:38
    */
    @Override
    public Result<RecordsListPageVO> queryHistoricalProcess(Integer pageNo,
                                                            Integer pageSize,
                                                            String  startUserId,
                                                            String  proDefName,
                                                            String  proDefKey,
                                                            String  proState) {

        //获取之前页的数据条数
        int firstResult = (pageNo - 1) * pageSize;
        long duration=0;
        String state=null;

        //通过mapper查询数据
        List<HistoryProcessVO> myHistoricalStartProcessList= workFlowMyStartProcessMapper
                .myHistoricalStartProcessList(proDefName,proDefKey,startUserId,proState);

        for(HistoryProcessVO hiProcessVo : myHistoricalStartProcessList){
            //获得流程当前状态
            state=hiProcessVo.getState();
            switch (state){
                case "ACTIVE":
                    state="审批中";
                    break;
                case "SUSPENDED":
                    state="被挂起";
                    break;
                case "COMPLETED":
                    state="已完成";
                    break;
                case "INTERNALLY_TERMINATED":
                    state="主动作废";
                    break;
                case "EXTERNALLY_TERMINATED":
                    state="被动作废";
                    break;
                default:
                    break;
            }
            hiProcessVo.setState(state);

            //获得taskrole
            String taskRole = UtilTools
                    .getExtensions(hiProcessVo.getProcessDefinitionId(), hiProcessVo.getTaskId()).values().toString();
            hiProcessVo.setTaskRole(taskRole.equals("[申请人]"));

            //获得该流程的耗时时间
            if(hiProcessVo.getEndTime()==null){
                hiProcessVo.setDuration(UtilTools.timeConsuming(hiProcessVo.getStartTime()));
            }else {
                duration=Long.parseLong(hiProcessVo.getDuration());
                hiProcessVo.setDuration(UtilTools.timeCon(duration));
            }
        }

        //分页
        List<HistoryProcessVO> reasultList;
        if ((pageSize + firstResult) < myHistoricalStartProcessList.size()) {
            reasultList = myHistoricalStartProcessList.subList(firstResult, pageSize + firstResult);
        } else {
            reasultList = myHistoricalStartProcessList.subList(firstResult, myHistoricalStartProcessList.size());
        }

        long total = myHistoricalStartProcessList.size();
        return Result.OK("我发起的历史流程获取",UtilTools.getListPage(total,pageNo,pageSize,reasultList));
    }
}
