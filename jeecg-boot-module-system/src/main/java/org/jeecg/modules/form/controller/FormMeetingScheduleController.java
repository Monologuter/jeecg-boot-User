package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.constant.FormMeetingScheduleConstant;
import org.jeecg.modules.form.vo.GetFormMeetingScheduleVO;
import org.jeecg.modules.form.vo.SaveFormMeetingScheduleVO;
import org.jeecg.modules.form.service.FormMeetingScheduleService;
import org.jeecg.modules.form.entity.FormMeetingScheduleDO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 表单会议日程
 * @author: LiKun
 * @date: 2021年09月10日 17:08
 */
@RestController
@RequestMapping("/form/meetingSchedule")
@Api(tags = "表单会议日程")
public class FormMeetingScheduleController {
    @Autowired
    private FormMeetingScheduleService formMeetingScheduleService;
    
    /**
     * 保存或更新会议日程记录
     * 
     * @param saveFormMeetingScheduleVO  会议日程记录实体类
     * @author LiKun
     */
    @ApiOperation(value = "保存或更新会议日程记录")
    @PostMapping("/saveOrUpdate")
    @Transactional(rollbackFor = Exception.class)
    public Result<FormMeetingScheduleDO> save(@RequestBody SaveFormMeetingScheduleVO saveFormMeetingScheduleVO) {
        FormMeetingScheduleDO formMeetingScheduleDO = formMeetingScheduleService.saveFormMeetingScheduleDO(saveFormMeetingScheduleVO);
        return Result.OK(formMeetingScheduleDO);
    }

    /**
     * 删除会议会议记录
     * @author: LiKun
     * @param id 会议id
     */
    @ApiOperation(value = "删除会议会议记录")
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(@RequestParam String id) {
        if (formMeetingScheduleService.deleteFormMeetingSchedule(id) == 1){
            return Result.OK();
        }else {
            return Result.Error(FormMeetingScheduleConstant.MESSAGE_ERROR_DELETE);
        }
    }

//    /**
//     * 根据预定者或者会议主题查询会议信息
//     * @author: LiKun
//     * @param condition 预定者或者会议主题
//     */
//    @ApiOperation(value = "根据预定者或者会议主题查询会议日程")
//    @GetMapping("/list")
//    public Result<List<GetFormMeetingScheduleVO>> list(@ApiParam(value = "预定者或会议主题", required = true)@RequestParam String condition) {
//        List<GetFormMeetingScheduleVO> list = formMeetingScheduleService.getFormMeetingSchedule(condition);
//        if(!CollectionUtils.isEmpty(list)){
//            return Result.OK(list);
//        }
//        return Result.Error("未查询到对应会议");
//    }

    /**
     * 查询所有会议信息
     * @author: LiKun
     */
    @ApiOperation(value = "查询所有会议日程")
    @GetMapping("/listAll")
    public Result<List<GetFormMeetingScheduleVO>> list(){
        return Result.OK(formMeetingScheduleService.getAllFormMeetingSchedule());
    }

}
