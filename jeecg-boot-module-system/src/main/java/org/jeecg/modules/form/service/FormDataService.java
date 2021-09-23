package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.dto.FormDataDTO;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.vo.FormDataGraphVO;
import org.jeecg.modules.form.vo.FormDataToWorkflowVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 表单数据Service层接口
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/17 17:09
 */
public interface FormDataService extends IService<FormDataDO> {

    /**
     * 返回表单模板数据中的所有输入框列表
     *
     * @param dataJson 表单模板中的json字段
     * @return 数据中所有的输入框列表
     */
    ArrayList<FormDataToWorkflowVO.params> getDataJsonToWorkflow(String dataJson);

    /**
     * 保存表单数据
     * @param formData 表单数据对象
     * @return FormDataDTO
     */
    FormDataDTO saveFormData(FormDataDTO formData);

    /**
     * 获取表单数据
     * @param page 分页
     * @param formId 表单id
     * @param searchRules 规则
     * @param orderBy 排序
     * @param isDesc 降序
     * @return page
     */
    IPage<FormDataDO> getFormDataList(Page<FormDataDO> page,
                                      String formId,
                                      String permissionId,
                                      List<FormRoleDo> searchRules,
                                      List<String> orderBy,
                                      boolean isDesc);

    /**
     * 图表组件需要  获取表单统计数据
     *
     * @param formId 表单数据
     * @param rules 统计规则
     * @return java.lang.String 返回统计数据
     */
    Long getFormDataCount(String formId,List<FormRoleDo> rules);

    /**
     * @Description 表单日志需要：根据表单数据id查询表单id
     * @Author huang sn
     * @Date 11:05 2021/9/6
     * @Param [formDataId]
     * @return java.lang.String
     */
    String getFormIdByFormDataId(String formDataId);

    /**
     * @Description 图标组定制化数据
     * @Author huang sn
     * @Date 4:45 下午 2021/9/16
     * @Param []
     * @return org.jeecg.modules.form.vo.FormDataGraphVO
     */
    Map<String,Integer> getGraphData();
}
