package org.jeecg.modules.workflow.common;

/**
 * description:
 *
 * @author ghz
 * @Description:  流程状态
 */
public enum ProcInstState {
    completed("已完成",false),
    EXTERNALLY_TERMINATED("被动作废",false),
    INTERNALLY_TERMINATED("主动作废",false),
    deleted("被驳回",false),
    ACTIVE("审批中",true),
    COMPLETED("已完成",false),
    SUSPENDED("流程已挂起",false),
    DELETE("被驳回",false);

    private final String desc;
    private final Boolean single;

    ProcInstState(String desc ,Boolean single){
        this.desc = desc;
        this.single = single;
    }

    public String getDesc(){
        return desc;
    }

    public Boolean getSingle(){
        return single;
    }

}
