package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import java.util.List;

/**
 * 拓展流程实体类
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年05月31日 15:44
 */
public interface WfProcessDevelopmentService {

   /** 获取拓展页相关信息 **/
   Result processDevelopmentFindAll(String procKey);

   /** 流程拓展属性 **/
   Result processDevelopmentUpDate(ProcessDevelopmentVO processDevelopmentVO);

   /** 获取流程定义分类 **/
   Result<List<String>> processDevelopmentFindProcClass(String userId);

   /** 获取icon颜色 **/
   Result processDevelopmentFindIconColour(String userId);

   /** 获取icon样式 **/
   Result processDevelopmentFindIconStyle(String userId);

   /**根据外置表单id获取流程定义信息*/
   Result<List<ProcessDefinitionDTO>> getProceDefByFormKey(String formKey);
}