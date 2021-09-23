package org.jeecg.modules.workflow.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.controller.FormDataController;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.vo.FormDataToWorkflowVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * @author 权计超
 * 外置表单接口
 * @date 2021-6-9
 */
@RestController
@RequestMapping("workflow/workflow-extend-form")
@Api(tags="工作流")
@Slf4j
public class WfExtendFormController {
    @Autowired
    private FormDataController formDataController;

    @GetMapping("/get-extend-from")
    @ApiOperation(value = "获取外置表单",notes = "获取外置表单")
    public Result<IPage<FormDO>> foemData(@ApiParam(value = "表单名称")@RequestParam(defaultValue = "")String formName,
                         @ApiParam("当前页,可选,默认1")@RequestParam(defaultValue = "1")Integer pageNo,
                         @ApiParam("页容量,可选,默认10")@RequestParam(defaultValue = "10")Integer pageSize){
        return formDataController.getListTemplate(formName, pageNo, pageSize);
    }


    //TODO
    @GetMapping("/get-extend-from-detail")
    @ApiOperation(value = "获取外置表单参数",notes = "获取外置表单参数")
    public Result<FormDataToWorkflowVO> getFormModelByID(@ApiParam(value = "表单code",required = true)@RequestParam()String formCode){
        return formDataController.getFormModelByID(formCode);
    }

}
