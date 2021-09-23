package org.jeecg.modules.workflow.service.impl;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.task.Comment;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ActAssMsgVO;
import org.jeecg.modules.workflow.entity.vo.ActivitiHighLineDTO;
import org.jeecg.modules.workflow.entity.vo.HiActInstVO;
import org.jeecg.modules.workflow.entity.vo.RecordsListPageVO;
import org.jeecg.modules.workflow.service.WfTaskBpmnService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @Description 是taskBpmnService实现类，实现有关于历史跟踪页面的功能
 * @author ghz
 * @date 2021年03月29日 9:52
 */
@Service
@Slf4j
public class WfTaskBpmnServiceImpl implements WfTaskBpmnService {

    /**
     * 获取流程图xml文件以String类型进行传输
     * @param processDefinitionId 流程定义ID
     * @throws IOException 抛出IO流错误
     */
    @Override
    public Result<String> getBpmnXml(String processDefinitionId) throws IOException{
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId()
                ,processDefinition.getResourceName());
        //获取repositoryService单例
        String content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        inputStream.close();
        return Result.OK(content);
    }

    /**
     * 历史追踪table
     * @param pageNo 展示第几页的数据
     * @param pageSize 每页展示多少条数据
     * @param processInsId 当前登录用户的用户名(即申请人申请的委托人)
     */
    @Override
    public Result<RecordsListPageVO> getHistoryActive(Integer pageNo, Integer pageSize, String processInsId) {
        // 入参校验
        if (StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize)
                || StringUtils.isEmpty(processInsId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }

        //简历历史节点表的单例
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();

        //定义时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");

        // 3、调用listPage()进行数据分页传输//获取之前页的数据条数
        int firstResult = (pageNo-1)*pageSize;

        List<HiActInstVO> hiActInstVOList = new ArrayList<>();

        List<HistoricTaskInstance> historicTaskInstances = historyService.
                createHistoricTaskInstanceQuery()
                .processInstanceId(processInsId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .listPage(firstResult, pageSize);

        List<Comment> processInstanceComments = taskService
                .getProcessInstanceComments(processInsId);

        Map<String,String> commentsMap = new HashMap<>();

        for (Comment comment:processInstanceComments){
            commentsMap.put(comment.getTaskId(),comment.getFullMessage());
        }

        //传值入对象
        for (HistoricTaskInstance historicTaskInstance : historicTaskInstances) {
            HiActInstVO hiActInstVO = new HiActInstVO();
            String startTime = simpleDateFormat.format(historicTaskInstance.getStartTime());
            String endTime = "";

            if (historicTaskInstance.getEndTime() != null) {
                endTime = simpleDateFormat.format(historicTaskInstance.getEndTime());
                hiActInstVO.setResults("已完成");
            } else {
                hiActInstVO.setResults("未完成");
            }

            List<User> firstName = identityService.createUserQuery()
                    .userId(historicTaskInstance.getAssignee())
                    .list();
            if (firstName.size() == 0){
                hiActInstVO.setAssignee("该用户已被删除");
            }else {
                if ("".equals(firstName.get(0).getFirstName())){
                    hiActInstVO.setAssignee("该用户未命名");
                } else {
                    hiActInstVO.setAssignee(firstName.get(0).getFirstName());
                }
            }

            hiActInstVO.setBusinessTitle(historicTaskInstance.getProcessDefinitionKey());

            hiActInstVO.setProcessInstanceId(historicTaskInstance.getProcessInstanceId());

            hiActInstVO.setStartTime(startTime);

            hiActInstVO.setEndTime(endTime);

            if (!processInstanceComments.isEmpty()) {
                hiActInstVO.setOpinion(commentsMap.get(historicTaskInstance.getId()));
            }
            hiActInstVOList.add(hiActInstVO);
        }

        long total = historyService.
                createHistoricTaskInstanceQuery()
                .processInstanceId(processInsId)
                .count();
        return Result.OK("查询历史流程跟踪",
                UtilTools.getListPage(total,pageNo,pageSize,hiActInstVOList));
    }
    /**
     *获取高亮
     * @param processInsId 流程实例ID
     */
    @Override
    public Result<ActivitiHighLineDTO> getHigh(String processInsId) throws IOException {

        //创建历史服务单例
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();

        //未完成节点
        Set<String> waitingToDo = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInsId)
                .unfinished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());

        //已完成节点
        Set<String> highPointResult = historyService.createHistoricActivityInstanceQuery()
                .processInstanceId(processInsId)
                .finished()
                .list()
                .stream()
                .map(HistoricActivityInstance::getActivityId)
                .collect(Collectors.toSet());

        //返回结果
        ActivitiHighLineDTO activitiHighLineDTO =new ActivitiHighLineDTO();
        //已完成节点
        activitiHighLineDTO.setHighPoint(highPointResult);
        //待完成节点
        activitiHighLineDTO.setWaitingToDo(waitingToDo);
        return Result.OK(activitiHighLineDTO);
    }

    /**
     * 悬停显示信息
     * @param processInsId 流程实例ID
     */
    @Override
    public Result<List<ActAssMsgVO>> getActMsg(String processInsId) {
        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();

        // 入参校验
        if (StringUtils.isEmpty(processInsId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,"参数不能为空");
        }
        //所有节点
        List<HistoricActivityInstance> allActivity = historyService
                .createHistoricActivityInstanceQuery()
                .processInstanceId(processInsId)
                .orderByHistoricActivityInstanceStartTime()
                .asc()
                .list();

        List<Comment> processInstanceComments = taskService.getProcessInstanceComments(processInsId);

        Map<String,String> commentsMap = new HashMap<>();

        for (Comment comment:processInstanceComments){
            commentsMap.put(comment.getTaskId(),comment.getFullMessage());
        }

        //定义时间格式
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
        List<ActAssMsgVO> actAssMsgVOS = new ArrayList<>();

        //进行数据封装
        allActivity.forEach(historicActivityInstance->{
            ActAssMsgVO actAssMsgVO = new ActAssMsgVO();
            String startTime = simpleDateFormat.format(historicActivityInstance.getStartTime());
            String endTime = "";

            if (historicActivityInstance.getEndTime() != null) {
                endTime = simpleDateFormat.format(historicActivityInstance.getEndTime());
            }
            actAssMsgVO.setTaskName(historicActivityInstance.getActivityName());

            actAssMsgVO.setActId(historicActivityInstance.getActivityId());

            if (StringUtils.isEmpty(historicActivityInstance.getAssignee())){
                actAssMsgVO.setAssignee("该用户已被删除");
            }else {
                List<User> firstName = identityService.createUserQuery()
                        .userId(historicActivityInstance.getAssignee())
                        .list();

                if (firstName.size() != 1){
                    actAssMsgVO.setAssignee("该用户已被删除");
                }else {
                    if ("".equals(firstName.get(0).getFirstName())){
                        actAssMsgVO.setAssignee("该用户未命名");
                    } else {
                        actAssMsgVO.setAssignee(firstName.get(0).getFirstName());
                    }
                }
            }

            actAssMsgVO.setStartTime(startTime);

            actAssMsgVO.setEndTime(endTime);

            if (historicActivityInstance.getDurationInMillis() != null){
                actAssMsgVO.setDuration(UtilTools.timeCon(historicActivityInstance.getDurationInMillis()));
            } else {
                actAssMsgVO.setDuration(null);
            }
            if (!processInstanceComments.isEmpty()) {
                actAssMsgVO.setOpinion(commentsMap.get(historicActivityInstance.getTaskId()));
            }
            actAssMsgVOS.add(actAssMsgVO);
        });
        return Result.OK(actAssMsgVOS);
    }
}
