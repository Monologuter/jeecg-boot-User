package org.jeecg.modules.form.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.Range;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.entity.GeneratorTemplatesDO;
import org.jeecg.modules.form.service.GeneratorTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

import static org.jeecg.modules.form.constant.GeneratorTemplatesConstant.*;

/**
 * 代码生成器模板Controller类，对表单模板数据进行操作
 *
 * @author: HuQi
 * @date: 2021年07月31日 17:23
 */
@RequestMapping(value = "/generatorTemplates")
@RestController
@Api(tags = "表单代码生成器模板")
@Slf4j
public class GeneratorTemplatesController {

    @Autowired
    private GeneratorTemplatesService generatorTemplatesService;

    /**
     * 模板类型
     */
    private List<String> allowed = Arrays.asList(C_CONTROLLER, C_ENTITY, C_SERVICE, C_SERVICE_IMPL, C_MAPPER, C_VAR);

    /**
     * 添加代码生成器模板
     *
     * @param templateName 模板名称
     * @param isPublic 是否公开
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:06
     */
    @ApiOperation(value = "添加模板代码")
    @PostMapping("/addTemplates")
    @ResponseBody
    public Result<GeneratorTemplatesDO> addTemplates(
            @ApiParam(value = "模板名称", required = true) @RequestParam String templateName,
            @ApiParam(value = "模板是否公开") @RequestParam(defaultValue = "0") Integer isPublic) {
        GeneratorTemplatesDO templatesDO = generatorTemplatesService.addTemplates(templateName, isPublic);
        return Result.OK(templatesDO);
    }

    /**
     * 删除代码生成器模板
     *
     * @param templateId 模板ID
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:06
     */
    @ApiOperation(value = "删除模板代码")
    @GetMapping("/deleteTemplates")
    public Result<String> deleteTemplates(@RequestParam String templateId) {
        if (generatorTemplatesService.deleteTemplates(templateId) == 1){
            return Result.OK(MESSAGE_SUCCESS_DELETE_TEMPLATES);
        }else {
            return Result.Error(MESSAGE_ERROR_DELETE_TEMPLATES);
        }
    }

    /**
     * 获取公共模板信息（模板ID、模板名称、模板创建者）
     *
     * @param pageNum 第几页
     * @param pageSize 每页数量
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:08
     */
    @ApiOperation(value = "获取公共模板")
    @GetMapping("/getPublicTemplates")
    public Result<List<GeneratorTemplatesDO>> getPublicTemplates(@RequestParam(defaultValue = "1", required = false) Integer pageNum,
                                     @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Page<GeneratorTemplatesDO> page = new Page<>(pageNum, pageSize);
        List<GeneratorTemplatesDO> templatesDO = generatorTemplatesService.getPublicTemplates(page);
        return Result.OK(templatesDO);
    }

    /**
     * 获取当前登录的用户模板数据，输出的模板内容并未解密
     *
     * @param pageNum 第几页
     * @param pageSize 每页数量
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:09
     */
    @ApiOperation(value = "获取用户模板")
    @GetMapping("/getUserTemplates")
    public Result<List<GeneratorTemplatesDO>> getUserTemplates(@RequestParam(defaultValue = "1", required = false) Integer pageNum,
                                   @RequestParam(defaultValue = "10", required = false) Integer pageSize) {
        Page<GeneratorTemplatesDO> page = new Page<>(pageNum, pageSize);
        List<GeneratorTemplatesDO> templatesDO = generatorTemplatesService.getUserTemplates(page);
        return Result.OK(templatesDO);
    }

    /**
     * 设置模板是否公开
     *
     * @param templateId 模板ID
     * @param isPublic 是否公开
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:10
     */
    @ApiOperation(value = "设置模板公开")
    @GetMapping("/setPublicTemplates")
    public Result<String> setPublicTemplates(
            @ApiParam(value = "模板Id") @RequestParam String templateId,
            @ApiParam(value = "模板是否公开") @RequestParam(defaultValue = "0")
                @Range(min = 0, max = 1, message = "操作参数错误：0或1") Integer isPublic) {
        String rs = isPublic == 1 ? "公开" : "私有";
        if (generatorTemplatesService.setPublicTemplates(templateId, isPublic)!=0){
            return Result.OK("代码模板" + rs + "成功！");
        }else {
            return Result.Error("代码模板" + rs + "失败！");
        }
    }

    /**
     * 保存模板代码数据至数据库
     *
     * @param templateId 模板ID
     * @param type 模板类型，指定修改哪层的模板内容
     * @param content 模板内容
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:12
     */
    @ApiOperation(value = "保存模板代码数据至数据库")
    @PostMapping("/saveTemplatesContent")
    public Result<String> saveTemplatesContent(
            @ApiParam(value = "模板Id", required = true) String templateId,
            @ApiParam(value = "模板类型：" + C_CONTROLLER + "、" + C_ENTITY + "、" + C_MAPPER + "、" + C_SERVICE + "、"
                    + C_SERVICE_IMPL + "、" + C_VAR + "（JSON类型）", required = true) String type,
            @ApiParam(value = "模板内容，请遵循freemaker规则填写，同时请勿改动代码类名", required = true) String content) {
        if (!allowed.contains(type)){
            return Result.Error(T_ERROR_TYPE_MESSAGE);
        }
        if (generatorTemplatesService.saveTemplatesContent(templateId, content, type) != 0){
            return Result.OK(type + MESSAGE_SUCCESS_SAVE_TEMPLATES);
        }else {
            return Result.Error(type + MESSAGE_ERROR_SAVE_TEMPLATES);
        }
    }

    /**
     * 操作模板规则：
     *      可对模板进行获取、启用、禁用，分别对应操作类型：get、enable、disable
     *      同时也可指定针对某一层，或者所有All
     *      此接口get获取的输出的模板内容已由B64解密，为真时内容
     *
     * @param templateId 模板ID
     * @param type 模板类型
     * @param action 操作类型
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-26 17:14
     */
    @ApiOperation(value = "操作模板规则")
    @GetMapping("/templatesOpt")
    public Result<Object> templatesOpt(
            @ApiParam(value = "模板Id", required = true) String templateId,
            @ApiParam(value = "模板类型：" + C_CONTROLLER + "、" + C_ENTITY + "、" + C_MAPPER + "、" + C_SERVICE + "、"
                    + C_SERVICE_IMPL + "、" + C_VAR + "（JSON类型）", required = true) String type,
            @ApiParam(value = "操作类型：启用" + T_OPT_ENABLE + "、禁用" + T_OPT_DISABLE + "、获取" + T_OPT_GET, required = true) String action) {

        List<String> allowedAction = Arrays.asList(T_OPT_ENABLE, T_OPT_DISABLE, T_OPT_GET);
        if (!allowed.contains(type)&&!C_ALL.equals(type)){
            return Result.Error(T_ERROR_TYPE_MESSAGE);
        }
        if (!allowedAction.contains(action)){
            return Result.Error(T_OPT_ERROR_TYPE_MESSAGE);
        }

        Object rs = MESSAGE_SUCCESS_OPT_TEMPLATES;
        switch (action){
            case T_OPT_ENABLE:
                if (generatorTemplatesService.setTemplates(templateId, type, 1) == 0){
                    return Result.Error(T_OPT_ENABLE_ERROR_MESSAGE);
                }
                break;
            case T_OPT_DISABLE:
                if (generatorTemplatesService.setTemplates(templateId, type, 0) == 0){
                    return Result.Error(T_OPT_DISABLE_ERROR_MESSAGE);
                }
                break;
            case T_OPT_GET:
                rs = generatorTemplatesService.getTemplates(templateId, type);
                break;
            default:
                break;
        }
        return Result.OK(rs);
    }
}
