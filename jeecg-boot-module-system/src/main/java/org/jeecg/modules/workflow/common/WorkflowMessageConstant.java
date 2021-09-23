package org.jeecg.modules.workflow.common;

/**
 * @author qjc
 * @date 2021-8-26
 */
public class WorkflowMessageConstant {
    /**
     * 私有化构造方法
     */
    private WorkflowMessageConstant() {
    }
    /**
     * 查询成功
     */
    public static final String WORK_FLOW_QUERY_SUCCESS = "查询成功";
    /**
     * 查询结果为空
     */
    public static final String WORK_FLOW_QUERY_NULL = "查询结果为空";
    /**
     * 查询成功
     */
    public static final String WORK_FLOW_DELETE_SUCCESS = "删除成功";
    /**
     * 执行成功
     */
    public static final String WORK_FLOW_ACTIVE_SUCCESS = "执行成功";
    /**
     * 此模板已启动过流程实例不可删除
     */
    public static final String WORK_FLOW_DELETE_FAIL_CASCADE = "此模板已启动过流程实例不可删除";
    /**
     * 入参为空
     */
    public static final String WORK_FLOW_PARAMETER_NULL = "入参为空";
    /**
     * 租户不存在
     */
    public static final String WORK_FLOW_TENANT_NULL = "租户不存在";
    /**
     * 用户不存在
     */
    public static final String WORK_FLOW_USER_NULL = "用户不存在";
    /**
     * 流程定义不存在
     */
    public static final String WORK_FLOW_PROC_NULL = "流程定义不存在";
    /**
     * 部署失败：有外置表单不存在
     */
    public static final String WORK_FLOW_DEPLOY_FAIL_FORM = "部署失败：有外置表单不存在";
    /**
     * 部署失败：文件类型异常
     */
    public static final String WORK_FLOW_DEPLOY_FAIL_FILE = "部署失败：文件类型异常";
    /**
     * 部署失败：未知异常
     */
    public static final String WORK_FLOW_DEPLOY_FAIL_NONE = "部署失败：未知异常";
    /**
     * 部署失败：流程定义名称不能为空
     */
    public static final String WORK_FLOW_DEPLOY_FAIL_NAME = "部署失败：流程定义名称不能为空";
    /**
     * 启动成功
     */
    public static final String WORK_FLOW_START_FAIL = "启动失败";
    /**
     * 启动失败：至少传入一个启动参数
     */
    public static final String WORK_FLOW_START_FAIL_PARAMETER = "启动失败：至少传入一个启动参数";
    /**
     * 流程已挂起，请联系管理员上线
     */
    public static final String WORK_FLOW_SUSPENSION = "流程已挂起，请联系管理员上线";
    /**
     * 无需重复挂起
     */
    public static final String WORK_FLOW_SUSPENSION_FAIL = "无需重复挂起";
    /**
     * 无需重复激活
     */
    public static final String WORK_FLOW_ACTIVE_FAIL = "无需重复激活";
    /**
     * 流程定义复制成功
     */
    public static final String WORK_FLOW_PROC_COPY_SUCCESS = "流程定义复制成功";
    /**
     * 流程定义复制成功
     */
    public static final String WORK_FLOW_PROC_COPY_FAIL = "流程定义复制失败";
    /**
     * 改用户已存在
     */
    public static final String WORK_FLOW_USER_ALREADY = "用户已存在";
    /**
     * 租户已存在
     */
    public static final String WORK_FLOW_TENANT_ALREADY = "租户已存在";
    /**
     * 组已存在
     */
    public static final String WORK_FLOW_GROUP_ALREADY = "组已存在";
}