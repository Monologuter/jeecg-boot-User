package org.jeecg.modules.form.util;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.GeneratorCodeConstant;
import org.jeecg.modules.form.service.GeneratorCodeService;
import org.jeecg.modules.form.dto.GeneratorCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

/**
 * 项目启动完成后，检查持久化状态，根据持久化配置进行操作。如：初始化加载相关生成的jar包
 * ApplicationContextInitializedEvent ：在准备ApplicationContext并调用ApplicationContextInitializers之后但在加载bean定义之前触发
 * ApplicationPreparedEvent ：在加载bean定义后触发
 * ApplicationStartedEvent ：在刷新上下文之后但在调用命令行和应用程序运行程序之前触发
 * ApplicationReadyEvent ：在调用任何应用程序和命令行运行程序之后触发
 * ApplicationFailedEvent ：如果启动时发生异常则触发
 *
 * @author: HuQi
 * @date: 2021年07月27日 16:58
 */
@Component
@Slf4j
public class RunAfterStartup {

    @Autowired
    private GeneratorCodeService generatorCodeService;

    /**
     * 监听项目启动，当项目启动完准备好后调用该方法，加载代码生成器已生成的代码
     *
     * @Author HuQi
     * @create 2021-08-27 16:44
     */
    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        GeneratorCodeDTO generatorCodeVO = new GeneratorCodeDTO();
        ResourceBundle setting = generatorCodeVO.getGeneratorSetting();
        String projectPath = generatorCodeVO.getProjectPath();

        if (GeneratorCodeConstant.TRUE_STR.equals(setting.getString(GeneratorCodeConstant.SETTING_IS_PERSISTENT)) &&
                GeneratorCodeConstant.SETTING_PERSISTENT_METHOD_FILE.equals(
                        setting.getString(GeneratorCodeConstant.SETTING_PERSISTENT_METHOD))) {
            log.info("已开启文件持久化，正在加载持久化文件。。。");
            log.info("初始化动态加载代码！");
            projectPath = projectPath + setting.getString(GeneratorCodeConstant.SETTING_AUTO_GENERATOR_PATH) + "/"
                    + GeneratorCodeConstant.SETTING_USER_PATH + "/";
            loadExistCode(projectPath, setting, generatorCodeVO);
            log.info("持久化文件加载结束。。。");
        }else {
            try {
                log.info("文件持久化未开启，清空代码生成器临时文件！");
                FileUtil.deleteDir(new File(projectPath
                        + setting.getString(GeneratorCodeConstant.SETTING_AUTO_GENERATOR_PATH)));
                // 删除相关数据表
                generatorCodeService.removeAllGeneratorTable(true);
                log.info("清空代码生成器临时文件完成！");
            } catch (IOException e) {
                e.printStackTrace();
                ServiceUtils.throwException("清空代码生成器失败！");
            }
        }
    }

    /**
     * 加载提供路径内，代码生成器已生成的代码
     *
     * @param projectPath 项目路径
     * @param setting 设置配置
     * @param generatorCodeVO 业务层数据传输类
     * @return void
     * @Author HuQi
     * @create 2021-08-27 16:43
     */
    private void loadExistCode(String projectPath, ResourceBundle setting, GeneratorCodeDTO generatorCodeVO) {
        //创建目录的File对象
        File dirSouce = new File(projectPath);
        //获取源目录下的File对象列表
        File[] files = dirSouce.listFiles();
        if (files != null) {
            for (File file : files) {
                //拼接新的fromDir(fromFile)和toDir(toFile)的路径
                String classPath = projectPath + file.getName()
                        + setting.getString(GeneratorCodeConstant.SETTING_CLASS_PATH);
                //判断File对象是目录还是文件
                //判断是否是目录
                // 判断文件夹及文件是否存在
                if (file.isDirectory() && new File(classPath).exists()) {
                    generatorCodeVO.setModuleName("");
                    List<String> loadResult;
                    try {
                        // 调用方法动态加载相关class文件
                        loadResult = generatorCodeService.loadClass(classPath, generatorCodeVO);
                        log.info(file.getName() + "加载Class结果：" + loadResult);
                        log.info(file.getName() + "加载接口共有：" + loadResult.size()/4 );
                    } catch (Exception e) {
                        log.error(file.getName() + "动态代码加载失败！");
                        e.printStackTrace();
                    }
                }
            }
        }
    }


}
