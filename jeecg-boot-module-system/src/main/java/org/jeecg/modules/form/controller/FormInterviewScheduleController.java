package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormInterviewScheduleConstant;
import org.jeecg.modules.form.entity.FormInterviewScheduleDO;
import org.jeecg.modules.form.service.FormInterviewScheduleService;
import org.jeecg.modules.system.entity.SysDepart;
import org.jeecg.modules.system.service.ISysDepartService;
import org.jeecg.modules.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 面试日程记录Controller
 *
 * @Author: HuQi
 * @Date: 2021年08月31日 10:27
 */
@RestController
@RequestMapping("/form/interviewSchedule")
@Api(tags = "表单面试日程")
public class FormInterviewScheduleController {

    @Autowired
    private FormInterviewScheduleService formInterviewScheduleService;

    @Autowired
    private ISysDepartService sysDepartService;

    /**
     * 保存面试日程记录
     *
     * @param formInterviewScheduleDO 面试日程记录实体类
     * @return org.jeecg.common.api.vo.Result<org.jeecg.modules.form.entity.FormInterviewScheduleDO>
     * @Author HuQi
     * @create 2021-08-31 11:16
     */
    @ApiOperation(value = "保存面试日程记录")
    @PostMapping("/save")
    @Transactional(rollbackFor = Exception.class)
    public Result<FormInterviewScheduleDO> save(@RequestBody FormInterviewScheduleDO formInterviewScheduleDO) {
        formInterviewScheduleDO.setDepId(getUserCurrentDept().getId());
        Map<String, String> map = new HashMap<>(3);
        map.put("scene","type-blue");
        map.put("video","type-orange");
        map.put("phone","type-green");
        String styleName = map.get(formInterviewScheduleDO.getType());
        if (styleName != null){
            formInterviewScheduleDO.setClassNames(styleName);
        }
        FormInterviewScheduleDO formInterviewSchedule = formInterviewScheduleService.saveFormInterviewScheduleDO(formInterviewScheduleDO);
        return Result.OK(formInterviewSchedule);
    }

    /**
     * 根据面试日程Id删除面试日程记录
     *
     * @author: LiKun
     * @Return: org.jeecg.common.api.vo.Result<java.lang.String>
     */
    @ApiOperation(value = "删除面试日程记录")
    @DeleteMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> delete(@RequestParam String id) {
        if (formInterviewScheduleService.deleteFormInterviewSchedule(id) == 1){
            return Result.OK();
        }else {
            return Result.Error(FormInterviewScheduleConstant.MESSAGE_ERROR_DELETE);
        }
    }

    /**
     * 若当前用户部门为综合岗，则查询所有面试日程
     * 若当前用户部门不为综合岗，则只查询当前用户（面试官）得面试日程记录
     *
     * @return : org.jeecg.common.api.vo.Result<java.util.List<org.jeecg.modules.form.entity.FormInterviewScheduleDO>>
     * @author : lxk
     * @create :2021/8/31 17:12
    */
    @ApiOperation(value = "查询面试日程")
    @GetMapping("/list")
    public Result<List<FormInterviewScheduleDO>> list() {
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        String departName = getUserCurrentDept().getDepartName();
        if (FormInterviewScheduleConstant.C_GETBY_DEPARTMENTNAME.equals(departName)){
            return Result.OK(formInterviewScheduleService.getAllFormInterviewSchedule());
        }else{
            return Result.OK(formInterviewScheduleService.getByInterviewerFormInterviewSchedule(sysUser.getId()));
        }
    }

    /**
     * 方法描述:获取当前用户的当前部门信息
     *
     * @return org.jeecg.modules.system.entity.SysDepart
     * @author : lxk
     * @create :2021/8/31 17:23
    */
    private SysDepart getUserCurrentDept(){
        //获取当前登录用户的信息
        LoginUser sysUser = (LoginUser)SecurityUtils.getSubject().getPrincipal();
        List<SysDepart> sysDeparts = this.sysDepartService.queryUserDeparts(sysUser.getId());
        if (sysDeparts.isEmpty()){
            ServiceUtils.throwException(FormInterviewScheduleConstant.MESSAGE_ERROR_DEPT_NOT_EXIST);
        }
        return sysDeparts.get(0);
    }
}
