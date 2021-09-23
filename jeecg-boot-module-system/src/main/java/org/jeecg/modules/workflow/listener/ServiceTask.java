package org.jeecg.modules.workflow.listener;

import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.jeecg.modules.system.service.IPushService;
import org.jeecg.modules.workflow.utils.HttpUtil;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;


/**
 * @Description: 发送消息
 * @author: lz
 * @date: 2021年07月14日 17:19
 */
@Component
public class ServiceTask implements JavaDelegate{
    @Autowired
    private IPushService iPushService;

    @Override
    public void execute(DelegateExecution execution)  {
        //调用相应的服务类
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();

        //获取当前流程实例id
        String activityId=execution.getProcessInstanceId();

        //获取当前服务节点的父节点id
        String assigneeid = taskService.createTaskQuery()
                .processInstanceId(activityId)
                .active()
                .singleResult()
                .getAssignee();

        //通过父节点id查询当前任务的
        iPushService.unicast("admin","超时提醒","您有一个任务马上要超时",assigneeid);
    }

    /**
     * 发送通知
     * @param userId
     */
    public void send(String userId,String message){
        iPushService.unicast("admin","超时提醒",message,userId);
    }

    /**
     * 存入日历信息
     */
    public void save(String interviewee,String interviewer,String start,String end) throws IOException {
        String token = HttpUtil.getToken();
        HttpUtil.saveInterViewSchedule(token, interviewee, interviewer, start, end);
    }

    /**
     * 存入会议日历信息
     */
    public void order(String title,String booker,String team,String status,String start,String end) throws IOException {
        String token = HttpUtil.getToken();
        HttpUtil.saveOrder(token,title,booker,team,status,start,end);
    }
}
