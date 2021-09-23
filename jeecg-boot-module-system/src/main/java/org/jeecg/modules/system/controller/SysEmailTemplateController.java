package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.system.entity.SysEmailTemplate;
import org.jeecg.modules.system.service.ISysEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Api(tags = "邮件模板管理")
@RequestMapping("/sys/emailTemplate")
@Slf4j
public class SysEmailTemplateController {
    @Autowired
    ISysEmailTemplateService SysEmailTemplateService;

    /**
     * 邮件模板添加
     *
     * @param
     * @return
     */
    @ApiOperation("邮件模板添加")
    @PostMapping("/addEmailTemplate")
    public Result<String> addEmailTemplate(@RequestBody SysEmailTemplate sysEmailTemplate) {
        int addResult = SysEmailTemplateService.AddEmailTemplate(sysEmailTemplate);
        Result<String> result = new Result<>();
        if (addResult > 0) {
            result.setCode(200);
            result.setMessage("数据添加成功");
        } else {
            result.setCode(400);
            result.setMessage("数据添加失败或模板名称重复");
        }
        return result;
    }

    /**
     * 邮件模板修改
     *
     * @param
     * @return
     */
    @ApiOperation("邮件模板修改")
    @PostMapping("/updateEmailTemplate")
    public Result<String> updateEmailTemplate(@RequestBody SysEmailTemplate sysEmailTemplate ) {
        int updateResult = SysEmailTemplateService.UpdateEmailTemplate(sysEmailTemplate);
        Result<String> result = new Result<>();
        if (updateResult > 0) {
            result.setCode(200);
            result.setMessage("修改成功！");
        } else {
            result.setCode(400);
            result.setMessage("修改失败或模板名称重复！");
        }
        return result;
    }

    /**
     * 邮件模板删除
     *
     * @param
     * @return
     */
    @ApiOperation("邮件模板删除")
    @DeleteMapping("/deleteEmailTemplate")
    public Result<String> deleteEmailTemplate(@RequestParam("templateId") List<String> templateIds) {
        Result<String> result = new Result<>();
        for (String templateId : templateIds) {
            int deleteResult = SysEmailTemplateService.deleteEmailTemplate(templateId);
            if (deleteResult > 0) {
                result.setCode(200);
                result.setMessage("删除成功！");
            } else {
                result.setCode(400);
                result.setMessage("删除失败！");
            }
        }
        return result;
    }

    /**
     * 通过模板id查询邮件模板内容
     *
     * @param templateId
     * @return
     */
    @ApiOperation("通过模板id查询邮件模板内容")
    @GetMapping("/selectEmailTemplateById")
    public SysEmailTemplate selectEmailTemplateById(@RequestParam("templateId") String templateId) {
        SysEmailTemplate sysEmailTemplate = SysEmailTemplateService.selectEmailTemplateById(templateId);
        return sysEmailTemplate;
    }


    /**
     * 分页查询所有邮件模板
     *
     * @param sysEmailTemplate
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation("分页查询所有邮件模板")
    @GetMapping("/selectAllEmailTemplate")
    public Result<IPage<SysEmailTemplate>> selectEmailTemplateInPage(SysEmailTemplate sysEmailTemplate,
                                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                                     @RequestParam(name = "pageSize", defaultValue = "5") Integer pageSize,
                                                                     HttpServletRequest req) {
        Result<IPage<SysEmailTemplate>> result = new Result<>();
        QueryWrapper<SysEmailTemplate> queryWrapper = QueryGenerator.initQueryWrapper(sysEmailTemplate, req.getParameterMap());
        Page<SysEmailTemplate> page = new Page<>(pageNo, pageSize);
        IPage<SysEmailTemplate> pageList = SysEmailTemplateService.page(page);
        result.setSuccess(true);
        result.setCode(200);
        result.setResult(pageList);
        return result;
    }


    @ApiOperation("关键词模糊查询")
    @GetMapping("/selectEmailTemplateByKeyWord")
    public Result<List<SysEmailTemplate>> selectEmailTemplateByKeyWord(@RequestParam("KeyWord") String KeyWord) {
        List<SysEmailTemplate> sysEmailTemplates = SysEmailTemplateService.selectEmailTemplateByKeyWord(KeyWord);
        Result<List<SysEmailTemplate>> result = new Result<>();
        result.setCode(200);
        result.setResult(sysEmailTemplates);
        return result;
    }
}
