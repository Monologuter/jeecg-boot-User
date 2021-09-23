package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.util.Strings;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.camunda.bpm.engine.identity.User;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.HistoryProcessVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;
import org.jeecg.modules.workflow.mapper.ActIdInfoMapper;
import org.jeecg.modules.workflow.service.WfCopyService;
import org.jeecg.modules.workflow.common.ProcInstState;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 抄送相关业务
 * @author qjc
 * @date 2021-8-26
 */

@Slf4j
@Service
public class WfCopyServiceImpl implements WfCopyService {

    @Resource
    private ActIdInfoMapper actIdInfoMapper;
    /**
     * 抄送给我的流程列表
     * @param userId 用户ID
     * @param pageNo 当前页数
     * @param pageSize 页数大小
     */
    @Override
    public Result<RecordsListPageVO> getCopyInstance(String userId,
                                                     Integer pageNo,
                                                     Integer pageSize,
                                                     String  state,
                                                     String definitionId) {

        //入参校验
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        // 1、historicProcessInstanceQuery()得到所有历史流程实例
        HistoricProcessInstanceQuery historicProcessInstanceQuery = historyService
                .createHistoricProcessInstanceQuery();

        //获取抄送列表
        Set<String> userCopyList = UtilTools.getUserCopyList(userId);
        if (userCopyList.isEmpty()){
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_NULL,UtilTools.getListPage(0L,
                                                                                                pageNo,
                                                                                                pageSize,
                                                                                                null));
        }
        //添加抄送过滤条件
        historicProcessInstanceQuery.processInstanceIds(userCopyList);

        //如果流程定义id不为空
        if (!Strings.isEmpty(definitionId)){
            historicProcessInstanceQuery.processInstanceId(definitionId);
        }

        // 2、调用listPage()进行数据分页传输,获取之前页的数据条数
        int firstResult = (pageNo - 1) * pageSize;

        //查询表数据所用集合historicProcessInstanceList
        List<HistoricProcessInstance> historicProcessInstanceList;


        //自定义实体类集合myHistoricalTaskVoList
        List<HistoryProcessVO> hiProcessVoList = new ArrayList<>();
        historicProcessInstanceList = historicProcessInstanceQuery
                .orderByProcessInstanceStartTime()
                .desc()
                .list();
        historicProcessInstanceList.forEach(historicProcessInstance -> System.out.println(historicProcessInstance.getState()));
        for (HistoricProcessInstance historicProcessInstance : historicProcessInstanceList) {

            if (StringUtils.isEmpty(state)){
                //遍历将数据存入到myHistoricalTaskVoList中
                //任务Id判断
                List<HistoricTaskInstance> getTaskId = historyService.createHistoricTaskInstanceQuery()
                        .processInstanceId(historicProcessInstance.getId())
                        .list();

                //将数据进行封装
                HistoryProcessVO hiProcessVo = new HistoryProcessVO();

                hiProcessVo = UtilTools.getHistoryProcessVO(historicProcessInstance);
                hiProcessVo.setName(historicProcessInstance.getProcessDefinitionName());

                //获取任务ID
                if (!getTaskId.isEmpty()){
                    hiProcessVo.setTaskId(getTaskId.get(0).getId());
                    hiProcessVo.setTaskName(getTaskId.get(0).getName());
                } else {
                    hiProcessVo.setTaskId("该流程存在问题，建议删除流程");
                    hiProcessVo.setTaskName("该流程存在问题，建议删除流程");
                }

                if(ProcInstState
                        .valueOf(historicProcessInstance.getState())
                        .name()
                        .equals(historicProcessInstance.getState())){
                    hiProcessVo.setState(ProcInstState
                            .valueOf(historicProcessInstance.getState())
                            .getDesc());
                    hiProcessVo.setStateSignal(ProcInstState
                            .valueOf(historicProcessInstance.getState())
                            .getSingle());
                }
                hiProcessVoList.add(hiProcessVo);
            } else if (state.equals(historicProcessInstance.getState())){
                    //遍历将数据存入到myHistoricalTaskVoList中

                    //任务Id判断
                    List<HistoricTaskInstance> getTaskId = historyService.createHistoricTaskInstanceQuery()
                            .processInstanceId(historicProcessInstance.getId())
                            .list();

                    //将数据进行封装
                    HistoryProcessVO hiProcessVo = new HistoryProcessVO();

                    hiProcessVo = UtilTools.getHistoryProcessVO(historicProcessInstance);
                    //获取任务ID
                if (!getTaskId.isEmpty()){
                    hiProcessVo.setTaskId(getTaskId.get(0).getId());
                    hiProcessVo.setTaskName(getTaskId.get(0).getName());
                } else {
                    hiProcessVo.setTaskId("该流程存在问题，建议删除流程");
                    hiProcessVo.setTaskName("该流程存在问题，建议删除流程");
                }
                    hiProcessVo.setName(historicProcessInstance.getProcessDefinitionName());
                    hiProcessVo.setState(ProcInstState.valueOf(state).getDesc());
                    hiProcessVoList.add(hiProcessVo);
            }
        }


        long total = hiProcessVoList.size();
        List<HistoryProcessVO> resultList;
        if ((pageSize+firstResult) < hiProcessVoList.size()){
            resultList = hiProcessVoList.subList(firstResult, pageSize + firstResult);
        }else {
            resultList = hiProcessVoList.subList(firstResult, hiProcessVoList.size());
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,
                UtilTools.getListPage(total,pageNo,pageSize,resultList));
    }

    /**
     * 设置抄送人
     * @param processInstanceId 流程实例id
     * @param copyArray 新增的抄送人列表
     */
    @Override
    public Result<String> setCopy(String processInstanceId, String[] copyArray) {
        //获取抄送人id列表
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("copy")
                .singleResult();
        String[] copy = (String[])historicVariableInstance.getValue();
        //新增id
        String[] newCopy = ArrayUtils.addAll(copy, copyArray);
        //重新设置抄送人
        ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService().setVariable(processInstanceId,"copy",newCopy);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    /**
     * 获取抄送人
     * @param processInstanceId 用户实例ID
     */
    @SuppressWarnings("unchecked")
    @Override
    public Result<List<UserDTO>> getCopy(String processInstanceId) {
        //获取抄送人列表
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        HistoricVariableInstance historicVariableInstance = historyService.createHistoricVariableInstanceQuery()
                .processInstanceId(processInstanceId)
                .variableName("copy")
                .singleResult();
        List<User> list;
        List<UserDTO> result = new ArrayList<>();
        if (historicVariableInstance.getValue() instanceof List){
            List<String> copy = (List<String>) historicVariableInstance.getValue();
            //获取抄送人详细信息

            if (copy.isEmpty()){
                return Result.OK("查询完毕",result);
            }
            String[] copyArray = copy.toArray(new String[0]);
            list = identityService.createUserQuery()
                    .userIdIn(copyArray)
                    .list();
        }else {
            String[] copy = (String[])historicVariableInstance.getValue();
            //获取抄送人详细信息
            if (copy.length==0){
                return Result.OK("查询完毕",result);
            }
            list = identityService.createUserQuery()
                    .userIdIn(copy)
                    .list();
        }
        for (User user:list){
            UserDTO userDTO = new UserDTO();
            userDTO.setUserId(user.getId());
            userDTO.setEmail(user.getEmail());
            userDTO.setFirstName(user.getFirstName());
            userDTO.setLastName(user.getLastName());
            //添加头像字段
            userDTO.setAvatar(actIdInfoMapper.selectAvatarById(user.getId()));
            result.add(userDTO);
        }
        return Result.OK("查询完毕",result);
    }
}
