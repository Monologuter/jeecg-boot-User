package org.jeecg.modules.workflow.common;

/**
 * @Author LC
 * Company DXC.technology
 * @ClassName WorkflowExtensionConstant
 * @CreateTime 2021-08-31 11:11
 * @Version 1.0
 * @Description: bpmn扩展属性常量
 */
public class WorkflowExtensionConstant {
    /**
     * 私有化构造方法
     */
    private WorkflowExtensionConstant(){}

    /**
     * 扩展属性：申请节点（审批人对应表单的节点）
     */
    public static final String WORK_FLOW_EXTENSION_EXAMINE = "examine";

    /**
     * 扩展属性：申请人
     */
    public static final String WORK_FLOW_EXTENSION_APPLY = "申请人";

    /**
     * 扩展属性： 角色
     */
    public static final String WORK_FLOW_EXTENSION_ROLE = "role";

}
