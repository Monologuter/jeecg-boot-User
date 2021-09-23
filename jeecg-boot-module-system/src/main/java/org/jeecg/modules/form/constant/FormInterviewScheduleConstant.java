package org.jeecg.modules.form.constant;

/**
 * 面试日程常量
 *
 * @Author: HuQi
 * @Date: 2021年08月31日 14:32
 */
public class FormInterviewScheduleConstant {
    private FormInterviewScheduleConstant() {
    }

    public static final String MESSAGE_INFO_SAVE_LACK_FILED = "请填写必填字段[面试官、面试者、面试类型、日程名]信息！";

    public static final String MESSAGE_ERROR_UPDATE = "面试日程记录数据修改失败！";
    public static final String MESSAGE_ERROR_INSERT = "面试日程记录数据添加失败！";
    public static final String MESSAGE_ERROR_START_RATHER_END = "面试开始时间不能大于结束时间！";
    public static final String MESSAGE_ERROR_TIME_FIELD = "请填写正确的面试开始和结束时间！";
    public static final String MESSAGE_ERROR_DELETE = "面试日程记录删除失败！！！";
    public static final String MESSAGE_ERROR_DEPT_NOT_EXIST = "当前登录用户无部门信息，请添加部门信息后操作！";
    public static final String MESSAGE_ERROR_INTERVIEWER_NOT_FOUNT = "面试官信息不存在！";

    public static final String C_GETBY_DEPARTMENTNAME = "综合岗";

    public static final String MESSAGE_SUCCESS_URL_PARSE = "完成！";
}
