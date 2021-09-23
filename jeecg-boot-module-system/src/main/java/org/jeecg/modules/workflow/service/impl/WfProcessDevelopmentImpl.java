package org.jeecg.modules.workflow.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.ProcessDefinitionDTO;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import org.jeecg.modules.workflow.entity.vo.ProcessInstanceDTO;
import org.jeecg.modules.workflow.mapper.*;
import org.jeecg.modules.workflow.service.WfProcessDevelopmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * 流程拓展
 * @Description: TODO
 * @author: SunBoWen
 *@date: 2021年05月31日 9:46
 */
@Service
@Slf4j
public class WfProcessDevelopmentImpl implements WfProcessDevelopmentService {

    @Autowired
    private ProcessDevelopmentMapper processDevelopmentMapper;

    @Autowired
    private IconColourMapper iconColourMapper;

    @Autowired
    private IconStyleMapper iconStyleMapper;

    @Autowired
    private ProcClassMapper procClassMapper;

    /** 流程拓展属性 **/
    @Override
    public Result processDevelopmentFindAll(String procKey) {
        if (procKey == null){
            return Result.error(500,"未传入流程定义ID");
        }
        ProcessDevelopmentVO processDevelopmentVO = processDevelopmentMapper.selectById(procKey);
        return Result.OK("查询流程拓展成功",processDevelopmentVO);
    }

    /** 流程拓展属性 **/
    @Override
    public Result processDevelopmentUpDate(ProcessDevelopmentVO processDevelopmentVO) {
        processDevelopmentVO.setProcKey(processDevelopmentVO.getProcKey());
        if (processDevelopmentVO.getProcDefGroup() == null){
            return Result.error(500,"请选择流程定义分组");
        }
        processDevelopmentVO.setProcDefGroup(processDevelopmentVO.getProcDefGroup());
        if (processDevelopmentVO.getIconColour() == null){
            return Result.error(500,"请选择icon颜色");
        }
        processDevelopmentVO.setIconColour(processDevelopmentVO.getIconColour());
        if (processDevelopmentVO.getIconStyle() == null){
            return Result.error(500,"请选择icon样式");
        }
        processDevelopmentVO.setIconStyle(processDevelopmentVO.getIconStyle());
        if (processDevelopmentVO.getProcDesc().length()>100){
            return Result.error(500,"输入的流程定义描述过长");
        }
        processDevelopmentVO.setProcDesc(processDevelopmentVO.getProcDesc());
        processDevelopmentMapper.updateById(processDevelopmentVO);
        return Result.OK("修改成功");
    }

    /** 获取流程定义分类 **/
    @Override
    public Result<List<String>> processDevelopmentFindProcClass(String userId) {
        List<String> procClassVOList = procClassMapper.getProcClass();
        return Result.OK("获取流程定义分类查询成功",procClassVOList);
    }

    /** 获取icon颜色 **/
    @Override
    public Result<List<String>> processDevelopmentFindIconColour(String userId) {
        List<String> iconColourVOList = iconColourMapper.getIconColour();
        return Result.OK("icon颜色查询成功",iconColourVOList);
    }

    /** 获取icon样式 **/
    @Override
    public Result<List<String>> processDevelopmentFindIconStyle(String userId) {
        List<String> iconStyleVOList = iconStyleMapper.getIconStyle();
        return Result.OK("icon样式查询成功",iconStyleVOList);
    }

    /**根据外置表单id获取流程定义信息*/
    @Override
    public Result<List<ProcessDefinitionDTO>> getProceDefByFormKey(String formKey){
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,processDevelopmentMapper.getProceDefByFormKey(formKey));
    }
}
