package org.jeecg.modules.form.dto;

import lombok.Data;

import java.util.ResourceBundle;

/**
 * 值对象，代码生成器各业务层数据交互使用的数据DTO
 *
 * @author: HuQi
 * @date: 2021年07月17日 18:41
 */
@Data
public class GeneratorCodeDTO {

    /**
     * 用来获取genreator.properties文件的配置信息
     */
    private ResourceBundle generatorSetting = ResourceBundle.getBundle("autoGeneratorTemplates/genreator");

    /**
     * 项目路径
     */
    private String projectPath = System.getProperty("user.dir");

    /**
     * 模块名
     */
    private String moduleName;

    /**
     * 表名
     */
    private String tableName;

    /**
     * 用户名
     */
    private String userName;
}
