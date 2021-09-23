package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.FormDataCorrelationVO;
import org.jeecg.modules.workflow.entity.vo.FormFieldVO;
import org.jeecg.modules.workflow.entity.vo.FormRole;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* 表单业务层
* @author: liuchun
* @date: 2021/3/21 15:39
*/
public interface WfFormService {

    /**
     * 根据任务节点获取表单
     * @param taskId : 任务节点id
     * @param processInstanceId : 流程实例id
     * @return List<FormField>
     */
    List<FormFieldVO> showFormByTaskId(String taskId, String processInstanceId, String procdefId);

    /**
     * 提交表单和意见 ：通过任务id，流程实例id，评论内容和表单中属性
     * @param formData ：所有传参
     *          formMap : 表单属性
     *          taskId ：任务节点id
     *          processInstanceId ：流程实例id
     *          message ：评论内容
     */
    void submitForm(FormDataCorrelationVO formData);

    /**
     * 展示外置表单根据任务id
     * @param taskId : 任务id
     * @return String : 外置表单字符串
     * @throws IOException
     */
    Map<String,Object> showOutFormByTaskId(String taskId) throws IOException;

    /**
     * 跳转到开始节点
     * @param formData
     *          formMap:表单属性
     *          taskId:任务id
     *          processInstanceId:实例id
     *          message:意见内容
     */
    void jumpStartEvent(FormDataCorrelationVO formData);

    /**
     * 返回formKey根据任务id
     * @param taskId : 任务id
     * @return : 返回formRole:
     *                formKey：判断是动态表单还是外置表单
     *                role:判断是申请人还是审批人
     */
    FormRole showFormKeyByTaskId(String taskId);

    /**
     * 保存表单参数
     * @param taskId : 任务id
     *        variables : 表单参数信息
     * @return : 返回是否保存成功信息
     */
    Result<Object> saveFormVar(String taskId, Map<String,Object> variables);
}
