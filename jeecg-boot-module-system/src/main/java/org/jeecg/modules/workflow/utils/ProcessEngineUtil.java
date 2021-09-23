package org.jeecg.modules.workflow.utils;

import org.camunda.bpm.engine.*;

/**
 * 枚举实现引擎服务类单例
 * @author bin
 * @company DXC.technology
 * @data 2021-03-16 13:41
 */
public enum ProcessEngineUtil {

    PROCESS_ENGINE_INSTANCE;

    private ProcessEngine processEngine;
    private RepositoryService repositoryService;
    private RuntimeService runtimeService;
    private TaskService taskService;
    private IdentityService identityService;
    private ManagementService managementService;
    private HistoryService historyService;
    private FormService formService;
    private AuthorizationService authorizationService;

    private ProcessEngineUtil() {
        processEngine = ProcessEngines.getDefaultProcessEngine();
        repositoryService = processEngine.getRepositoryService();
        runtimeService = processEngine.getRuntimeService();
        taskService = processEngine.getTaskService();
        identityService = processEngine.getIdentityService();
        managementService = processEngine.getManagementService();
        historyService = processEngine.getHistoryService();
        formService = processEngine.getFormService();
        authorizationService  = processEngine.getAuthorizationService();
    }

    /**
     * Repository Service 提供了对 repository 的存取服务
     * @return
     */
    public RepositoryService getRepositoryService(){
        return repositoryService;
    }

    /**
     * Runtime Service 提供了启动流程、查询流程实例、设置获取流程实例变量等功能
     * 此外它还提供了对流程部署，流程定义和流程实例的存取服务
     * @return
     */
    public RuntimeService getRuntimeService(){
        return runtimeService;
    }

    /**
     * Task Service 提供了对用户 Task 和 Form 相关的操作
     * 它提供了运行时任务查询、领取、完成、删除以及变量设置等功能
     * @return
     */
    public TaskService getTaskService(){
        return taskService;
    }

    /**
     * Identity Service 提供了对系统中的用户和组的管理功能
     * @return
     */
    public IdentityService getIdentityService(){
        return identityService;
    }

    /**
     * Management Service 提供了对流程引擎的管理和维护功能
     * @return
     */
    public ManagementService getManagementService(){
        return managementService;
    }

    /**
     * History Service 用于获取正在运行或已经完成的流程实例的信息
     * 与 Runtime Service 中获取的流程信息不同，历史信息包含已经持久化存储的永久信息，并已经被针对查询优化
     * @return
     */
    public HistoryService getHistoryService(){
        return historyService;
    }

    /**
     * Form Service 可以存取启动和完成任务所需的表单数据并且根据需要来渲染表单
     * @return
     */
    public FormService getFormService(){
        return formService;
    }

    /**
     * AuthorizationService管理权限相关操作
     * @return
     */
    public AuthorizationService getAuthorizationService(){return authorizationService;}
}
