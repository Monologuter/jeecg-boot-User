package org.jeecg.modules.form.util;

import com.baomidou.mybatisplus.generator.config.ConstVal;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.jeecg.modules.form.constant.GeneratorCodeConstant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;

/**
 * 自定义的代码生成器的freemaker模板引擎，因已存在FreemarkerTemplateEngine的模板根路径写死在Resources目录下，
 * 所以自定义重写该类，新建一个有参构造方法，使用setDirectoryForTemplateLoading设定模板根路径目录
 *
 * @Author: HuQi
 * @Date: 2021年07月31日 18:34
 */
public class FreemarkerTemplateEngine extends AbstractTemplateEngine {
    private File basePath;
    private Configuration configuration;

    public FreemarkerTemplateEngine() {
    }

    public FreemarkerTemplateEngine(File basePath) {
        this.basePath = basePath;
    }

    @Override
    public FreemarkerTemplateEngine init(ConfigBuilder configBuilder) {
        super.init(configBuilder);
        this.configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        this.configuration.setDefaultEncoding(ConstVal.UTF8);
        // 自定义路径
        try {
            this.configuration.setDirectoryForTemplateLoading(basePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // this.configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, "/");  // 默认为Resources目录下
        return this;
    }

    @Override
    public void writer(Map<String, Object> objectMap, String templatePath, String outputFile) throws Exception {
        Template template = this.configuration.getTemplate(templatePath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        Throwable var6 = null;

        try {
            template.process(objectMap, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
        } catch (Throwable var15) {
            var6 = var15;
            throw var15;
        } finally {
            if (var6 != null) {
                try {
                    fileOutputStream.close();
                } catch (Throwable var14) {
                    var6.addSuppressed(var14);
                }
            } else {
                fileOutputStream.close();
            }

        }

        logger.debug("模板:" + templatePath + ";  文件:" + outputFile);
    }

    @Override
    public String templateFilePath(String filePath) {
        return filePath + GeneratorCodeConstant.SETTING_FTL_FILE_EXTENSION;
    }
}
