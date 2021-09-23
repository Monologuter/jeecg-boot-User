package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.service.GeneratorCodeService;
import org.jeecg.modules.form.util.FileUtil;
import org.jeecg.modules.form.dto.GeneratorCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

/**
 * 代码生成器辅助工具
 *
 * @author: HuQi
 * @date: 2021年07月06日 14:43
 */
@RequestMapping(value = "/generator")
@RestController
@Api(tags = "表单代码生成器辅助工具")
@Slf4j
public class GeneratorCodeUtilController {

    @Autowired
    private final GeneratorCodeService generatorCodeService;

    private GeneratorCodeDTO generatorCodeVO;

    public GeneratorCodeUtilController(GeneratorCodeService generatorCodeService) {
        this.generatorCodeVO = new GeneratorCodeDTO();
        this.generatorCodeService = generatorCodeService;
    }

    /**
     * 获取jar运行时产生的日志文件，可指定日志文件路径
     * 接口具有安全隐患，在生产环境中关闭，仅开发环境使用
     *
     * @param logFilePath 日志文件路径，可不写，默认为相对目录下的nohup.out文件
     * @return java.lang.String
     * @Author HuQi
     * @create 2021-08-03 10:08
     */
    @ApiOperation(value = "获取日志文件")
    @GetMapping("/getLog")
    public String getLogFile(@ApiParam("日志文件路径") String logFilePath) {
        String projectPath = generatorCodeVO.getProjectPath();
        if (logFilePath==null){
            logFilePath = projectPath + "/nohup.out";
        }
        String logResult;
        try {
            logResult = FileUtil.readFile(new FileInputStream(logFilePath), "<br/>\r\n");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            logResult = "文件读取失败！";
        }
        return "<meta charset='utf-8'>\n" + logResult;
    }

    /**
     * 获取所有Spring已加载的Bean
     *
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author HuQi
     * @create 2021-08-03 10:10
     */
    @ApiOperation("获取所有Bean")
    @GetMapping(value = "/beans")
    public List<Map<String, Object>> beans(){
        return generatorCodeService.getAllBean();
    }

    /**
     * 获取单个Bean
     *
     * @param clazzName 类名
     * @return org.jeecg.common.api.vo.Result<java.lang.Object>
     * @Author HuQi
     * @create 2021-08-03 10:10
     */
    @GetMapping("/getBean")
    @ApiOperation("获取单个Bean")
    public Result<Object> getBean(@RequestParam String clazzName) {
        return Result.OK(generatorCodeService.getBean(clazzName));
    }

    /**
     * 获取系统中的所有接口地址
     *
     * @return org.jeecg.common.api.vo.Result<java.lang.Object>
     * @Author HuQi
     * @create 2021-08-03 10:11
     */
    @GetMapping("/getUrls")
    @ApiOperation("获取所有接口地址")
    public Result<Object> getUrls() {
        return Result.OK(generatorCodeService.getUrls());
    }

}
