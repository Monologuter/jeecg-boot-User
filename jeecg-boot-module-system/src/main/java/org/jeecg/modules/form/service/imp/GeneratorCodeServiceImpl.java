package org.jeecg.modules.form.service.imp;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.maven.shared.invoker.*;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.GeneratorCodeDO;
import org.jeecg.modules.form.entity.GeneratorTemplatesDO;
import org.jeecg.modules.form.mapper.GeneratorCodeMapper;
import org.jeecg.modules.form.service.GeneratorCodeService;
import org.jeecg.modules.form.service.GeneratorTemplatesService;
import org.jeecg.modules.form.util.FileUtil;
import org.jeecg.modules.form.util.FreemarkerTemplateEngine;
import org.jeecg.modules.form.util.GitLabUtil;
import org.jeecg.modules.form.util.ModuleClassLoaderUtil;
import org.jeecg.modules.form.dto.GeneratorCodeDTO;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.*;
import java.util.regex.Pattern;

import static org.jeecg.modules.form.constant.GeneratorCodeConstant.*;
import static org.jeecg.modules.form.util.FileUtil.*;
import static org.jeecg.modules.form.util.GeneratorCodeUtil.*;

/**
 * 代码生成器的实现类，完成具体实现操作
 *
 * @author: HuQi、HuangSn、LiKun
 * @date: 2021年07月13日 15:09
 */
@Service
@Slf4j
public class GeneratorCodeServiceImpl implements GeneratorCodeService, ApplicationContextAware {

    /**
     * 数据库连接地址
     */
    @Value("${spring.datasource.dynamic.datasource.master.url}")
    private String datasourceUrl;

    /**
     * 数据库连接用户名
     */
    @Value("${spring.datasource.dynamic.datasource.master.username}")
    private String datasourceUsername;

    /**
     * 数据库连接密码
     */
    @Value("${spring.datasource.dynamic.datasource.master.password}")
    private String datasourcePassword;

    /**
     * 数据库驱动
     */
    @Value("${spring.datasource.dynamic.datasource.master.driver-class-name}")
    private String datasourceDriver;

    @Autowired
    private RedisUtil redisUtil;

    private ApplicationContext applicationContext;

    private ResourceBundle setting;

    @Autowired
    private GeneratorCodeMapper generatorCodeMapper;

    @Autowired
    private GeneratorTemplatesService generatorTemplatesService;

    /**
     * 通过实现ApplicationContextAware设置获取上下文
     */
    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    /**
     * 表结构新增字段，通过传入表名，及要新增的字段名、字段描述添加新字段
     *
     * @param fields    新字段
     * @param tableName 表名
     * @param listValue 字段描述
     * @author: LiKun
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addFields(List<String> fields, List<String> listValue, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName);
        for (int i = 0; i < fields.size(); i++) {
            sql.append(" ADD COLUMN ").append(fields.get(i)).append(" varchar(255) COMMENT '").append(listValue.get(i)).append("',");
        }
        sql.setLength(sql.length() - 1);
        log.info(MESSAGE_INFO_ADD_CLOUMN + sql);
        generatorCodeMapper.addFields(sql.toString());
    }

    /**
     * 通过传入表名，及要删除的字段名删除表中指定字段
     *
     * @param fields    要删除的字段
     * @param tableName 表名
     * @author: LiKun
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFields(List<String> fields, String tableName) {
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tableName);
        for (String item : fields) {
            sql.append(" DROP COLUMN ").append(item).append(" ,");
        }
        sql.setLength(sql.length() - 1);
        log.info(MESSAGE_INFO_DELETE_CLOUMN + sql);
        generatorCodeMapper.deleteFields(sql.toString());
    }

    /**
     * 更新字段：通过传入表名，及要更新的字段名信息，修改表中指定字段
     *
     * @param updateFieldsJsonToMap 更新字段JSON，如{"旧字段":{"新字段":"新字段备注"}}
     * @param tableName             表名
     * @param tablePrefix   表名前缀
     * @param oldList 旧字段
     * @author: LiKun
     * @Return: List<String> 返回新增的字段名列表
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> updateFields(Map<String, Object> updateFieldsJsonToMap, String tableName, String tablePrefix, List<String> oldList) {

        List<String> rsList = new ArrayList<>();
        StringBuilder sql = new StringBuilder();
        sql.append("ALTER TABLE ").append(tablePrefix).append(tableName);

        for (Map.Entry<String, Object> m : updateFieldsJsonToMap.entrySet()) {
            // 取新字段信息，包括字段名和备注
            Object str = m.getValue();
            Map<String, Object> comment = jsonToMap(str.toString());
            String newField = "";
            Object newcomment = "";
            for (Map.Entry<String, Object> n : comment.entrySet()) {
                // 拿到新字段名
                newField = tablePrefix + n.getKey();
                if (oldList.contains(newField)){
                    ServiceUtils.throwException(MESSAGE_ERROR_FIELD_EXIST);
                }
                rsList.add(newField);
                // 拿到新字段备注
                newcomment = n.getValue().toString();
            }
            sql.append(" CHANGE COLUMN ").append(tablePrefix).append(m.getKey())
                    .append(" ").append(newField).append(" varchar(255) COMMENT '").append(newcomment.toString()).append("',");
        }
        sql.setLength(sql.length() - 1);
        log.info(MESSAGE_INFO_UPDATE_CLOUMN + sql);
        generatorCodeMapper.updateFields(sql.toString());
        return rsList;
    }

    /**
     * 创建表，通过传入表名、字段名、字段描述的列表，循环遍历列表使用StringBuilder构造建表语句，同时添加固定字段
     *
     * @param tablePrefix 表名和字段名前缀
     * @param tableNamePlus 表名
     * @param listkey 键链
     * @param listValue 值链
     * @return java.lang.String 返回最终表名
     * @Author HuQi、HuangSn
     * @create 2021-08-2 09:56
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String createTable(String tablePrefix, String tableNamePlus, List<String> listkey, List<Object> listValue){

        //校验表是否存在
        if(generatorCodeMapper.ifTableIsCreated(tableNamePlus) != null){
            // 可能因上次创建时代码出错，但表已创建，代码生成其中并未记录该表，所以删除该表
            generatorCodeMapper.dropTable(tableNamePlus);
            log.error(tableNamePlus + MESSAGE_ERROR_TABLE_EXIST);
        }

        // 设置固定字段名，同BaseEntity设定的一致，不可重复添加
        List<String> notAllowed = Arrays.asList("id", "create_time", "create_by", "update_by", "update_time");

        // generatorMapper.dropTable(tableNamePlus); //删除已存在的表   AUTO_INCREMENT
        StringBuilder sql = new StringBuilder("create table " + tableNamePlus +
                " ( id varchar(32) not null primary key comment " +
                "\"主键id(自动生成)\"" + ",");
        for(int j=0;j<listkey.size();j++){
            String k = listkey.get(j);
            String v = listValue.get(j).toString();
            // 跳过固有字段，不可重复添加
            if (notAllowed.contains(k.toLowerCase())){
                continue;
            }
            if (!Pattern.matches("^[a-zA-Z_][a-zA-Z0-9_]*", k)){
                ServiceUtils.throwException(MESSAGE_ERROR_TABLE_COLUMN_NOT_MATCHES);
            }
            if (v.contains("\\") || v.contains("\"")){
                ServiceUtils.throwException(MESSAGE_ERROR_TABLE_COLUMN_NOT_MATCHES2);
            }
            // 加入前缀，防止出现class等关键字问题
            sql.append(tablePrefix).append((listkey.get(j))).append(" varchar(32) comment ")
                    .append("\"").append(v).append("\"").append(",");
        }
        sql.append("create_time datetime default null comment \"创建时间\" ," +
                   "create_by varchar(50) default null comment \"创建人\" ," +
                   "update_by varchar(50) default null comment \"更新人\" ," +
                   "update_time datetime default null comment \"更新时间\" )" +
                   "engine=INNODB default CHARACTER set utf8; ");
        log.info(MESSAGE_INFO_CREAT_TABLE + sql.toString());
        generatorCodeMapper.createTable(sql.toString());
        //校验表是否存在
        if(!generatorCodeMapper.ifTableIsCreated(tableNamePlus).equalsIgnoreCase(tableNamePlus)){
            ServiceUtils.throwException(MESSAGE_ERROR_TABLE_NOT_EXIST);
        }
        return tableNamePlus;
    }

    /**
     * 代码文件生成，只有在生成相关表后才能使用此部分。
     *
     * @param generatorCodeVO 值对象，用于业务层传输数据
     * @param templateId 模板ID
     * @return java.lang.String 返回生成代码的路径
     * @Author HuQi
     * @create 2021-08-02 17:27
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String generatorCode(GeneratorCodeDTO generatorCodeVO, String templateId) {
        String module = generatorCodeVO.getModuleName();
        String table = generatorCodeVO.getTableName();
        String projectPath = generatorCodeVO.getProjectPath();
        setting = generatorCodeVO.getGeneratorSetting();
        String rootGeneratorPath = projectPath + setting.getString(SETTING_AUTO_GENERATOR_PATH) + File.separator;

        // 查询用户的模板文件信息
        GeneratorTemplatesDO templatesDO = null;
        if (!StringUtils.isEmpty(templateId)) {
            templatesDO = generatorTemplatesService.getUserTemplatesById(templateId);
        }

        // 实例化mybatis plus的代码生成器
        AutoGenerator mpg = new AutoGenerator();

        // 判断表名是否已加前缀
        if (!table.startsWith(setting.getString(SETTING_TABLE_PREFIX))){
            table = setting.getString(SETTING_TABLE_PREFIX) + table;
        }

        // 检测表是否存在
        if(generatorCodeMapper.ifTableIsCreated(table) == null){
            ServiceUtils.throwException(table + MESSAGE_ERROR_TABLE_NOT_EXIST);
        }

        // 1、设置代码生成器全局变量
        GlobalConfig gc = setGlobalConfig(projectPath, setting.getString(SETTING_AUTO_GENERATOR_PATH),
                setting.getString(SETTING_AUTHOR), generatorCodeVO.getUserName());
        mpg.setGlobalConfig(gc);

        // 2、代码生成器数据源配置
        mpg.setDataSource(setDataSourceConfig());

        // 3、代码生成器包名配置
        mpg.setPackageInfo(setPackageConfig(module, setting.getString(SETTING_PARENT)));

        // 4、代码生成器策略配置
        mpg.setStrategy(setStrategyConfig(table, setting.getString(SETTING_PARENT_ENTITY),
                setting.getString(SETTING_PARENT_CONTROLLER), setting.getString(SETTING_TABLE_PREFIX)));

        // 5、设置代码生成器默认模板的配置，同时将文件复制到指定目录下
        TemplateConfig tc = setTemplateConfig(rootGeneratorPath);
        // 6、设置代码生成器自定义模板的配置，同时将文件复制到指定目录下
        File templatesPath = setMyTemplateConfig(rootGeneratorPath, templatesDO, tc);
        mpg.setTemplate(tc);

        // 7、代码生成器自定义模板变量设置
        InjectionConfig injectionConfig = setInjectionConfig(templatesDO);
        // 8、根据已有的代码文件复制，设置自定义外部模板输入输出
        List<FileOutConfig> focList = setFileOutConfig(gc);
        injectionConfig.setFileOutConfigList(focList);
        // mpg.setTemplate(new TemplateConfig().setXml(null)); // 取消某个模板设置生成
        mpg.setCfg(injectionConfig);

        // 9、选择 freemarker 引擎需要指定如下加，注意 pom 依赖必须有！ 同时制定模板文件存放路径
        mpg.setTemplateEngine(new FreemarkerTemplateEngine(new File(rootGeneratorPath)));

        // 10、执行代码生成器
        try {
            mpg.execute();
        }catch (Exception e){
            ServiceUtils.throwException(MESSAGE_ERROR_GENERATOR_CODE_CREAT_FILE);
        }finally {
            // 清空临时目录
            try {
                FileUtil.deleteDir(templatesPath);
                log.info(MESSAGE_SUCCESS_CREAT_TEMP_TEMPLATE_DIR + templatesPath.getAbsolutePath());
            } catch (IOException e) {
                log.error(MESSAGE_ERROR_CLEAN_TEMP_DIR + e.getMessage());
            }
        }

        // 11、复制编译时使用的额外的通用文件
        copyCommonFile(projectPath, generatorCodeVO.getUserName(), gc.getOutputDir());
        return gc.getOutputDir() + File.separator + setting.getString(SETTING_PARENT)
                .replace(".","/") + File.separator + module;
    }

    /**
     * 1、设置代码生成器全局变量
     *
     * @param projectPath 项目路径
     * @param autoGeneratorPath 代码生成相对文件夹名
     * @param author 代码作者配置
     * @param username 用户名
     * @return com.baomidou.mybatisplus.generator.config.GlobalConfig
     * @Author HuQi
     * @create 2021-08-26 11:41
     */
    private GlobalConfig setGlobalConfig(String projectPath, String autoGeneratorPath,String author, String username){
        // 全局配置
        GlobalConfig gc = new GlobalConfig();
        // 生成文件的输出目录
        gc.setOutputDir(projectPath + autoGeneratorPath + "/" + SETTING_USER_PATH + "/" + username + "/src/main/java");
        // 作者
        gc.setAuthor(author);
        // 是否覆盖原有文件
        gc.setFileOverride(true);
        // 是否打开输出目录
        gc.setOpen(false);
        // 开启 swagger2 模式
        gc.setSwagger2(true);
        // 开启 activeRecord 模式
        gc.setActiveRecord(false);
        // 去掉接口上的I
        gc.setServiceName("%s" + C_SERVICE);
        gc.setControllerName("%s" + C_CONTROLLER);
        gc.setServiceImplName("%s" + C_SERVICE_IMPL);
        gc.setMapperName("%s" + C_MAPPER);
        // XML 二级缓存
        gc.setEnableCache(false);
        // XML ResultMap 映射图
        gc.setBaseResultMap(true);
        // XML columList
        gc.setBaseColumnList(true);
        return gc;
    }

    /**
     * 2、代码生成器数据源配置
     *
     * @return com.baomidou.mybatisplus.generator.config.DataSourceConfig
     * @Author HuQi
     * @create 2021-08-26 14:32
     */
    private DataSourceConfig setDataSourceConfig() {
        // 判断主yaml文件是否配置正确的数据库配置
        if (datasourceUrl == null){
            datasourceUrl = setting.getString(SETTING_JDBC_URL);
            datasourceUsername = setting.getString(SETTING_JDBC_USERNAME);
            datasourcePassword = setting.getString(SETTING_JDBC_PASSWORD);
            datasourceDriver = setting.getString(SETTING_JDBC_DRIVER);
        }

        DataSourceConfig dsc = new DataSourceConfig();
        dsc.setUrl(datasourceUrl);
        dsc.setDbType(DbType.MYSQL);
        dsc.setTypeConvert(new MySqlTypeConvert());
        dsc.setDriverName(datasourceDriver);
        dsc.setUsername(datasourceUsername);
        dsc.setPassword(datasourcePassword);
        return dsc;
    }

    /**
     * 3、代码生成器包名配置
     *
     * @param module 模块名
     * @param parent 父包名
     * @return com.baomidou.mybatisplus.generator.config.PackageConfig
     * @Author HuQi
     * @create 2021-08-26 14:32
     */
    private PackageConfig setPackageConfig(String module, String parent) {
        PackageConfig pc = new PackageConfig();
        pc.setModuleName(module);
        pc.setParent(parent);
        // 设置包名
        pc.setController(SETTING_PACKAGE_CONTROLLER);
        pc.setService(SETTING_PACKAGE_SERVICE);
        pc.setServiceImpl(SETTING_PACKAGE_SERVICE_IMPL);
        pc.setEntity(SETTING_PACKAGE_ENTITY);
        pc.setMapper(SETTING_PACKAGE_MAPPER);
        return pc;
    }

    /**
     * 4、设置代码生成器策略配置
     *
     * @param table 表名
     * @param parentEntity 父类Entity类
     * @param parentController 父类Controller类
     * @param tablePrefix 表名前缀
     * @return com.baomidou.mybatisplus.generator.config.StrategyConfig
     * @Author HuQi
     * @create 2021-08-26 13:14
     */
    private StrategyConfig setStrategyConfig(String table, String parentEntity, String parentController, String tablePrefix) {
        StrategyConfig strategy = new StrategyConfig();
        // 表名生成策略
        strategy.setNaming(NamingStrategy.underline_to_camel);
        strategy.setColumnNaming(NamingStrategy.underline_to_camel);
        // strategy.setExclude(new String[]{"test"}); // 排除生成的表
        // 自定义实体父类
        strategy.setSuperEntityClass(parentEntity);
        strategy.setEntityLombokModel(true);
        // 自定义 controller 父类
        strategy.setSuperControllerClass(parentController);
        // 设置需要生成的表
        strategy.setInclude(table);
        // 自定义实体，公共字段
        strategy.setSuperEntityColumns("id", "createTime", "updateTime");
        strategy.setControllerMappingHyphenStyle(true);
        // 此处可以修改为您的表前缀，如果没有，注释掉即可  去掉表中前缀
        strategy.setTablePrefix(tablePrefix);
        // 自定义 service 父类
        // strategy.setSuperServiceClass("com.baomidou.demo.TestService");
        // 自定义 service 实现类父类
        // strategy.setSuperServiceImplClass("com.baomidou.demo.TestServiceImpl");
        // 【实体】是否生成字段常量（默认 false）
        // public static final String ID = "test_id";
        // strategy.setEntityColumnConstant(true);
        // 【实体】是否为构建者模型（默认 false）
        // public User setName(String name) {this.name = name; return this;}
        // strategy.setEntityBuilderModel(true);
        // strategy.Builder().entityBuilder().addSuperEntityColumns("id","create_time","update_time");
        // strategy.superClass(BaseEntity.class)   //自动识别父类字段
        //         .addTableFills(new Column("create_time", FieldFill.INSERT))     //基于字段填充
        //         .addTableFills(new Property("updateTime",FieldFill.UPDATE))    //基于属性填充
        // strategy.setTableFillList();
        return strategy;
    }

    /**
     * 5、设置代码生成器默认模板的配置，同时将文件复制到指定目录下
     *
     * @param rootGeneratorPath 代码生成器的代码生成路径的根目录
     * @return com.baomidou.mybatisplus.generator.config.TemplateConfig
     * @Author HuQi
     * @create 2021-08-26 14:40
     */
    private TemplateConfig setTemplateConfig(String rootGeneratorPath) {
        // 自定义模板配置，可以 copy 源码 mybatis-plus/src/main/resources/templates 下面内容修改，
        // 放置自己项目的 src/main/resources/templates 目录下, 默认名称一下可以不配置，也可以自定义模板名称
        TemplateConfig tc = new TemplateConfig();
        File templatesPath = new File(rootGeneratorPath + DEFAULT_TEMPLATES_PATH);

        // 初始化模板为默认目录下
        // 如上任何一个模块如果设置 空 OR Null 将不生成该模块。
        tc.setXml(DEFAULT_TEMPLATES_PATH + FILE_NAME_MAPPERXML);
        tc.setController(DEFAULT_TEMPLATES_PATH + FILE_NAME_CONTROLLER);
        tc.setEntity(DEFAULT_TEMPLATES_PATH + FILE_NAME_ENTITY);
        tc.setMapper(DEFAULT_TEMPLATES_PATH + FILE_NAME_MAPPER);
        tc.setService(DEFAULT_TEMPLATES_PATH + FILE_NAME_SERVICE);
        tc.setServiceImpl(DEFAULT_TEMPLATES_PATH + FILE_NAME_SERVICEIMPL);

        // 创建默认目录
        if (!templatesPath.exists()){
            try {
                if (templatesPath.mkdirs()) {
                    log.info(MESSAGE_INFO_CREAT_DEAFULT_TEMPLATE_DIR + templatesPath.getAbsolutePath());
                }else {
                    ServiceUtils.throwException(MESSAGE_ERROR_CREAT_DEAFULT_TEMPLATE_DIR);
                }
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_CONTROLLER),
                        templatesPath + FILE_NAME_CONTROLLER + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_ENTITY),
                        templatesPath + FILE_NAME_ENTITY + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_MAPPER),
                        templatesPath + FILE_NAME_MAPPER + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_XML),
                        templatesPath + FILE_NAME_MAPPERXML + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_SERVICE),
                        templatesPath + FILE_NAME_SERVICE + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_SERVICEIMPL),
                        templatesPath + FILE_NAME_SERVICEIMPL + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_BASECONTROLLER),
                        templatesPath + FILE_NAME_BASECONTROLLER + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_BASEENTITY),
                        templatesPath + FILE_NAME_BASEENTITY + SETTING_FTL_FILE_EXTENSION);
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_RESULT),
                        templatesPath + FILE_NAME_RESULT + SETTING_FTL_FILE_EXTENSION);
            }catch (Exception e){
                log.error(e.getMessage());
                try {
                    FileUtil.deleteDir(templatesPath);
                } catch (IOException ex) {
                    log.error(ex.getMessage());
                }
                log.error(MESSAGE_ERROR_CREAT_DEAFULT_TEMPLATE_FILE);
            }
        }
        return tc;
    }

    /**
     * 6、设置代码生成器自定义模板的配置，读取用户的模板设置内容，同时将数据写入文件复制到指定目录下
     *
     * @param rootGeneratorPath 代码生成器的代码生成路径的根目录
     * @param templatesDO 数据库查询到的模板数据
     * @param tc 模板配置变量
     * @return java.io.File
     * @Author HuQi
     * @create 2021-08-26 14:38
     */
    private File setMyTemplateConfig(String rootGeneratorPath, GeneratorTemplatesDO templatesDO, TemplateConfig tc) {
        // 创建临时目录，外部生成一个UUID目录放置从数据库提取的代码写入的临时文件
        String uuidPath = "/" + UUID.randomUUID().toString() + "/";
        File templatesPath = new File(rootGeneratorPath + uuidPath);
        if (templatesPath.mkdirs()) {
            log.info(MESSAGE_INFO_CREAT_TEMP_TEMPLATE_DIR + templatesPath.getAbsolutePath());
        }else {
            ServiceUtils.throwException(MESSAGE_ERROR_CREAT_TEMP_TEMPLATE_DIR);
        }

        if (templatesDO != null){
            if (!StringUtils.isEmpty(templatesDO.getIsUseController())&&templatesDO.getIsUseController()!=0){
                FileUtil.stringToFile(base64ToString(templatesDO.getControllerContent()),
                        templatesPath + FILE_NAME_CONTROLLER + SETTING_FTL_FILE_EXTENSION);
                tc.setController(uuidPath + FILE_NAME_CONTROLLER);
            }
            if (!StringUtils.isEmpty(templatesDO.getIsUseEntity())&&templatesDO.getIsUseEntity()!=0){
                FileUtil.stringToFile(base64ToString(templatesDO.getEntityContent()),
                        templatesPath + FILE_NAME_ENTITY + SETTING_FTL_FILE_EXTENSION);
                tc.setEntity(uuidPath + FILE_NAME_ENTITY);
            }
            if (!StringUtils.isEmpty(templatesDO.getIsUseMapper())&&templatesDO.getIsUseMapper()!=0){
                FileUtil.stringToFile(base64ToString(templatesDO.getMapperContent()),
                        templatesPath + FILE_NAME_MAPPER + SETTING_FTL_FILE_EXTENSION);
                tc.setMapper(uuidPath + FILE_NAME_MAPPER);
            }
            if (!StringUtils.isEmpty(templatesDO.getIsUseService())&&templatesDO.getIsUseService()!=0){
                FileUtil.stringToFile(base64ToString(templatesDO.getServiceContent()),
                        templatesPath + FILE_NAME_SERVICE + SETTING_FTL_FILE_EXTENSION);
                tc.setService(uuidPath + FILE_NAME_SERVICE);
            }
            if (!StringUtils.isEmpty(templatesDO.getIsUseServiceImpl())&&templatesDO.getIsUseServiceImpl()!=0){
                FileUtil.stringToFile(base64ToString(templatesDO.getServiceImplContent()),
                        templatesPath + FILE_NAME_SERVICEIMPL + SETTING_FTL_FILE_EXTENSION);
                tc.setServiceImpl(uuidPath + FILE_NAME_SERVICEIMPL);
            }
        }
        return templatesPath;
    }

    /**
     * 7、代码生成器模板中的变量的自定义属性注入:abc
     * .ftl(或者是.vm)模板中，通过${cfg.abc}获取属性
     *
     * @param templatesDO 模板数据
     * @return com.baomidou.mybatisplus.generator.InjectionConfig
     * @Author HuQi
     * @create 2021-08-26 13:25
     */
    private InjectionConfig setInjectionConfig(GeneratorTemplatesDO templatesDO) {
        String resultPackage = setting.getString(SETTING_RESULT);
        String controllerPackage = setting.getString(SETTING_PARENT_CONTROLLER);
        String entityPackage = setting.getString(SETTING_PARENT_ENTITY);
        return new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>(10);
                // 提取数据库存储设定的变量，使用例子：${cfg.result}  可获取下方定义的result变量
                if (templatesDO != null && !StringUtils.isEmpty(templatesDO.getIsUseTemplatesVar())) {
                    String templatesVarJson = base64ToString(templatesDO.getTemplatesVarJson());
                    try {
                        Map<String, Object> templatesVar = jsonToMap(templatesVarJson);
                        map.putAll(templatesVar);
                    } catch (Exception e) {
                        log.error(e.getMessage());
                        log.error(MESSAGE_ERROR_GENERATOR_CODE_VAR_MAP);
                    }
                }
                map.put("result", resultPackage);
                map.put("packageBaseController", controllerPackage.substring(0, controllerPackage.lastIndexOf(".")));
                map.put("packageBaseEntity", entityPackage.substring(0, entityPackage.lastIndexOf(".")));
                map.put("packageResult", resultPackage.substring(0, resultPackage.lastIndexOf(".")));
                this.setMap(map);
            }
        };
    }

    /**
     * 8、根据已有的代码文件复制，设置自定义外部模板输入输出
     *
     * @param gc 全局配置
     * @return java.util.List<com.baomidou.mybatisplus.generator.config.FileOutConfig>
     * @Author HuQi
     * @create 2021-08-26 14:36
     */
    private List<FileOutConfig> setFileOutConfig(GlobalConfig gc) {
        List<FileOutConfig> focList = new ArrayList<>();
        // 根据项目中的文件进行复制
        String resultPath = gc.getOutputDir() + File.separator +
                setting.getString(SETTING_RESULT).replace(".", File.separator) + SETTING_JAVA_FILE_EXTENSION;
        String baseControllerPath = gc.getOutputDir() + File.separator +
                setting.getString(SETTING_PARENT_CONTROLLER).replace(".", File.separator) + SETTING_JAVA_FILE_EXTENSION;
        String baseEntityPath = gc.getOutputDir() + File.separator +
                setting.getString(SETTING_PARENT_ENTITY).replace(".", File.separator) + SETTING_JAVA_FILE_EXTENSION;

        Map<String, String> fileListsMap = new HashMap<>(3);
        if (!new File(baseControllerPath).exists()) {
            fileListsMap.put(DEFAULT_TEMPLATES_PATH + FILE_NAME_BASECONTROLLER + SETTING_FTL_FILE_EXTENSION, baseControllerPath);
        }
        if (!new File(baseEntityPath).exists()) {
            fileListsMap.put(DEFAULT_TEMPLATES_PATH + FILE_NAME_BASEENTITY + SETTING_FTL_FILE_EXTENSION, baseEntityPath);
        }
        if (!new File(resultPath).exists()) {
            fileListsMap.put(DEFAULT_TEMPLATES_PATH + FILE_NAME_RESULT + SETTING_FTL_FILE_EXTENSION, resultPath);
        }

        for (Map.Entry<String, String> fileList : fileListsMap.entrySet()) {
            focList.add(new FileOutConfig(fileList.getKey()) {
                @Override
                public String outputFile(TableInfo tableInfo) {
                    // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                    return fileList.getValue();
                }
            });
        }
        return focList;
    }

    /**
     * 11、复制通用文件
     *
     * @param projectPath 项目路径
     * @param userName 用户名
     * @param outputDir 代码生成器代码输出目录
     * @return void
     * @Author HuQi
     * @create 2021-08-26 14:34
     */
    private void copyCommonFile(String projectPath, String userName, String outputDir) {
        File pomFile = new File(projectPath + File.separator + setting.getString(SETTING_AUTO_GENERATOR_PATH)
                + "/" + SETTING_USER_PATH + "/" + userName + File.separator + SETTING_FILE_POM_XML);
        String application = setting.getString(SETTING_TAMPLATES_APPLICATION);
        application = application.substring(application.lastIndexOf("/")+1)
                .replace(SETTING_JAVA_FILE_EXTENSION + SETTING_FTL_FILE_EXTENSION,"");
        File appFile = new File(outputDir + File.separator + setting.getString(SETTING_PARENT)
                .replace(".","/") + File.separator + application + SETTING_JAVA_FILE_EXTENSION);
        try {
            if (!appFile.exists()) {
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_APPLICATION), appFile.getAbsolutePath());
            }
            if (!pomFile.exists()) {
                copyTemplatesFile(setting.getString(SETTING_TAMPLATES_POM), pomFile.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            ServiceUtils.throwException(MESSAGE_ERROR_COPY_COMMON_FILE);
        }
    }

    /**
     * 此方法调用mavenCompile，只是在mavenCompile放上上一层加一个锁，用于在移除代码后开启的线程同步编译功能
     * 考虑到响应时间，在添加代码生成器之后则只需调用mavenCompile
     *
     * @param pomPath Maven所需的pom.xml地址
     * @param command 执行命令，如：install、compile
     * @param generatorCodeVO 值对象
     * @param isUploadGit 是否上传到gitlab
     * @Author HuQi
     * @create 2021-08-02 17:32
     */
    @Override
    public synchronized void mavenCompileLock(String pomPath, String command, GeneratorCodeDTO generatorCodeVO, Boolean isUploadGit){
        log.info(MESSAGE_INFO_COMPILE_THREAD);
        mavenCompile(pomPath, command, generatorCodeVO, isUploadGit);
    }

    /**
     * 用于对生成的代码目录进行编译，同时生成jar包文件，为动态加载做准备文件
     *
     * @param pomPath Maven所需的pom.xml地址
     * @param command 执行命令，如：install、compile
     * @param generatorCodeVO 值对象
     * @param isUploadGit 是否上传到gitlab
     * @return boolean 
     * @Author HuQi
     * @create 2021-08-02 17:41
     */
    @Override
    public boolean mavenCompile(String pomPath, String command, GeneratorCodeDTO generatorCodeVO, Boolean isUploadGit) {

        String projectPath = generatorCodeVO.getProjectPath();
        setting = generatorCodeVO.getGeneratorSetting();
        String module = generatorCodeVO.getModuleName();
        String removePath = generatorCodeVO.getProjectPath() + setting.getString(SETTING_AUTO_GENERATOR_PATH) + "/"
                + SETTING_USER_PATH + "/" + generatorCodeVO.getUserName() + "/src/main/java" + File.separator
                + setting.getString(SETTING_PARENT).replace(".","/") + File.separator + module;
        if (StringUtils.isEmpty(pomPath)) {
            pomPath = projectPath + File.separator + setting.getString(SETTING_AUTO_GENERATOR_PATH) + "/"
                    + SETTING_USER_PATH + "/" + generatorCodeVO.getUserName() + File.separator + SETTING_FILE_POM_XML;
        }

        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File(pomPath) );
        //compile：编译  install：编译并生成jar包
        request.setGoals( Collections.singletonList( command ) );
        Invoker invoker = new DefaultInvoker();

        File mavenPath = new File(setting.getString(SETTING_MAVEN_PATH1));
        File javaPath = new File(setting.getString(SETTING_JAVA_HOME1));
        if (!javaPath.exists()) {
            javaPath = new File(setting.getString(SETTING_JAVA_HOME2));
        }
        request.setJavaHome(javaPath);
        if (!mavenPath.exists()) {
            mavenPath = new File(setting.getString(SETTING_MAVEN_PATH2));
            if (!mavenPath.exists()) {
                ServiceUtils.throwException(MESSAGE_ERROR_COMPILE_MAVEN_PATH);
            }
        }
        invoker.setMavenHome(mavenPath);

        invoker.setLogger(new PrintStreamLogger(System.err,  InvokerLogger.ERROR){
        } );

        // 控制台打印日志
        invoker.setOutputHandler(log::info);

        try{
            InvocationResult execute = invoker.execute(request);
            if(execute.getExitCode()==0){
                // 让其编译成功后再上传，否则若代码有错误上传之后会导致整个项目启动失败
                if (TRUE_STR.equals(setting.getString(SETTING_IS_PERSISTENT)) && SETTING_PERSISTENT_METHOD_GITLAB
                        .equals(setting.getString(SETTING_PERSISTENT_METHOD)) && isUploadGit) {
                    new Thread(() -> addFileToGitLab(removePath, setting, module)).start();
                }
                return true;
            }else{
                throw new MavenInvocationException(MESSAGE_ERROR_COMPILE_EXIT_CODE);
            }
        }catch (MavenInvocationException e) {
            try {
                FileUtil.deleteDir(new File(removePath));
                log.info(MESSAGE_INFO_GENERATOR_SREMOVE_PATH + removePath);
            } catch (IOException ex) {
                log.error(MESSAGE_ERROR_CLEAN_DIR + removePath);
            }
            ServiceUtils.throwException(MESSAGE_ERROR_COMPILE + removePath);
            return false;
        }
    }

    /**
     * 用于代码生成器生成代码时，若文件能正常编译完成后，最后开启一个线程上传对应代码至GitLab的方法
     *
     * @param generatorPath 生成器代码本地存放路径
     * @param setting 设置配置变量
     * @param module 模块名
     * @Author HuQi
     * @create 2021-08-04 11:54
     */
    private void addFileToGitLab(String generatorPath, ResourceBundle setting, String module) {
        Map<String, Object> gitMap = getDirFileToMap(generatorPath);
        List<Map<String, Object>> infos = new ArrayList<>();
        for (Map.Entry<String, Object> entry : gitMap.entrySet()) {
            Map<String, Object> map = new HashMap<>(2);
            map.put("file_path", setting.getString(SETTING_GITLAB_TREE) + entry.getKey()
                    .replace(generatorPath, "/" + module).replace("\\","/"));
            map.put("content", entry.getValue().toString().replace("\"", "\\\""));
            infos.add(map);
        }
        GitLabUtil gitLabUtil = getGitLabUtil(setting);
        if (gitLabUtil.addFile(infos, MESSAGE_INFO_GENERATOR_GITLAB_ADD_COMMIT +
                setting.getString(SETTING_GITLAB_TREE) + "/" + module)){
            log.info(MESSAGE_SUCCESS_GENERATOR_GITLAB_ADD);
        }else {
            log.info(MESSAGE_ERROR_GENERATOR_GITLAB_ADD);
        }
    }

    /**
     * 类的动态加载。此方法调用ModuleClassLoaderUtil工具类，完成对jar包中的字节码文件的加载
     *
     * @param classPath jar包的存放路径
     * @param generatorCodeVO 值对象，业务层间调用的数据参数
     * @return java.util.List<java.lang.String> 返回
     * @Author HuQi
     * @create 2021-08-02 17:45
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<String> loadClass(String classPath, GeneratorCodeDTO generatorCodeVO) {
        setting = generatorCodeVO.getGeneratorSetting();
        String projectPath = generatorCodeVO.getProjectPath();

        if (classPath == null || classPath.isEmpty()){
            // Jar包生成路径
            classPath = projectPath + setting.getString(SETTING_AUTO_GENERATOR_PATH) + "/" + SETTING_USER_PATH + "/" +
                    generatorCodeVO.getUserName() + File.separator + setting.getString(SETTING_CLASS_PATH);
        }
        String rootClassPath = classPath + "/";
        // 调用模块类加载的工具方法
        List<String> rs = null;
        String moduleName = generatorCodeVO.getModuleName();
        if (moduleName == null){
            moduleName = "auto";
        }
        classPath = getFilePath(classPath + "/" + setting.getString(SETTING_PARENT).replace(".", "/")
                + File.separator + moduleName).replace("\\", "/") + "/";
        List<String> fileList = getDirFileList(classPath);
        rootClassPath = "file:" + rootClassPath;
        try (ModuleClassLoaderUtil md = new ModuleClassLoaderUtil(new URL[]{new URL(rootClassPath)},
                applicationContext.getClassLoader(), fileList, setting.getString(SETTING_PARENT))){
            md.setApplicationContext(applicationContext);
            List<String> controllers = md.initBean();
            for (String controller:controllers) {
                // 将controller加载到requestMappingHandlerMapping
                if (!"".equals(controller)){
                    GeneratorCodeDO generatorTable = hasGeneratorTable(oConvertUtils.camelToUnderline(
                            controller.replace( C_CONTROLLER, "")));
                    if (generatorTable == null){
                        removeBean(controller.replace( C_CONTROLLER, ""));
                        log.error(oConvertUtils.camelToUnderline(controller.replace( C_CONTROLLER,
                                "")) + MESSAGE_ERROR_ADD_GENERATOR_URL_NOT_TABLE);
                    }else if (generatorTable.getIsEnableUrl() == 1) {
                        if (optGeneratorUrl(controller, ENABLE_STR, null)) {
                            log.info(controller + MESSAGE_SUCCESS_GENERATOR_ADD_URL);
                        } else {
                            log.error(controller + MESSAGE_ERROR_GENERATOR_ADD_URL);
                            log.info(controller + MESSAGE_INFO_GENERATOR_SECOND_ADD_URL);
                            md.initBean();
                            if (optGeneratorUrl(controller, ENABLE_STR, null)) {
                                log.info(controller + MESSAGE_SUCCESS_GENERATOR_SECOND_ADD_URL);
                            } else {
                                log.error(controller + MESSAGE_ERROR_GENERATOR_SECOND_ADD_URL);
                            }
                        }
                    }
                }
            }
            rs = md.getRegisteredBean();
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        return rs;
    }

    /**
     * 移除代码生成器注入的所有Bean，同时移除对应的接口地址
     *
     * @param tableName 表名，内部拼接为类名
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 19:59
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeBean(String tableName){
        try {
            List<String> lis = Arrays.asList(C_CONTROLLER, "", C_SERVICE_IMPL, C_SERVICE, C_MAPPER);
            for (String li: lis) {
                if (li.startsWith(C_CONTROLLER) &&
                        !optGeneratorUrl(tableName + li, DISABLE_STR, null)){
                    log.error(tableName + MESSAGE_ERROR_REMOVE_GENERATOR_URL);
                }
                BeanDefinitionRegistry beanFactory = (BeanDefinitionRegistry) ((ConfigurableApplicationContext)
                        getApplicationContext()).getBeanFactory();
                beanFactory.removeBeanDefinition(tableName + li);
                log.info(MESSAGE_INFO_BEAN_REMOVE + tableName + li);
            }
            return true;
        }catch (Exception e){
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 移除对应的相关代码生成器的表和代码生成器中的表中数据
     *
     * @param generatorCodeDO 代码生成器表的实体类
     * @param tableNamePlus 已生成的表名
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 20:03
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeTable(GeneratorCodeDO generatorCodeDO, String tableNamePlus) {
        // 移除记录
        generatorCodeMapper.deleteById(generatorCodeDO.getId());
        // 移除表
        generatorCodeMapper.dropTable(tableNamePlus);
        return true;
    }

    /**
     * 清除代码生成器代码、数据库以及持久化文件等。只允许管理员admin用户操作。
     *
     * @param generatorCodeVO 值对象
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 20:05
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cleanAllGenerator(GeneratorCodeDTO generatorCodeVO) {
        try {
            if (!OPT_ADMIN_USER.equals(getUser().getUsername())){
                ServiceUtils.throwException(MESSAGE_ERROR_NOT_AUTH);
            }
            List<GeneratorCodeDO> li = generatorCodeMapper.selectList(new LambdaQueryWrapper<GeneratorCodeDO>()
                    .select(GeneratorCodeDO::getTableName));
            for(GeneratorCodeDO generatorCode : li){
                removeBean(oConvertUtils.camelName(generatorCode.getTableName()));
            }
            setting = generatorCodeVO.getGeneratorSetting();
            String projectPath = generatorCodeVO.getProjectPath();
            FileUtil.deleteDir(new File(projectPath + setting.getString(SETTING_AUTO_GENERATOR_PATH)));
            if (TRUE_STR.equals(setting.getString(SETTING_IS_PERSISTENT)) &&
                    SETTING_PERSISTENT_METHOD_GITLAB.equals(setting.getString(SETTING_PERSISTENT_METHOD))) {
                String path = setting.getString(SETTING_GITLAB_TREE);
                new Thread(() -> deletePathFromGitLab(path, setting)).start();
            }
            removeAllGeneratorTable(false);
            return true;
        } catch (IOException e) {
            log.warn("文件或目录不存在！未生成任何代码生成器的代码。" + e.getMessage());
            return true;
        }
    }

    /**
     * 操作代码生成器生成的接口，对其进行启用或禁用操作，只需将Controller从RequestMappingHandlerMapping中删除或添加
     * 移除代码生成器的接口地址，即在RequestMappingHandlerMapping中删除该类
     * 因在此处移除其他类后，相同类名也无法再次重载，需重启才有效，所以不移除相关其他类。
     * 只将接口从RequestMappingHandlerMapping中移除，使用时还可再次重新将Controller加载到RequestMappingHandlerMapping即可
     *
     * @param moduleName 模块名，包含Controller后缀
     * @param action 操作名
     * @param generatorTable 代码生成器表的实体类，为null时不更新接口启用标识
     * @return boolean
     * @Author HuQi
     * @create 2021-08-12 09:33
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean optGeneratorUrl(String moduleName, String action, GeneratorCodeDO generatorTable) {

        Object controller;
        final RequestMappingHandlerMapping requestMappingHandlerMapping = getApplicationContext().getBean(RequestMappingHandlerMapping.class);
        try {
            controller = getApplicationContext().getBean(moduleName);
        }catch (Exception e){
            log.error(moduleName + MESSAGE_ERROR_ADD_BEAN + e.getMessage());
            return false;
        }

        if (DISABLE_STR.equals(action)) {
            Class<?> targetClass = controller.getClass();
            ReflectionUtils.doWithMethods(targetClass, method -> {
                Method specificMethod = ClassUtils.getMostSpecificMethod(method, targetClass);
                try {
                    Method createMappingMethod = RequestMappingHandlerMapping.class.
                            getDeclaredMethod("getMappingForMethod", Method.class, Class.class);
                    createMappingMethod.setAccessible(true);
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                            createMappingMethod.invoke(requestMappingHandlerMapping, specificMethod, targetClass);
                    if (requestMappingInfo != null) {
                        requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
                    }
                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }, ReflectionUtils.USER_DECLARED_METHODS);
            if (generatorTable != null) {
                generatorTable.setIsEnableUrl(0);
                updateGeneratorTable(generatorTable);
            }
            return true;
        }else if (ENABLE_STR.equals(action)){
            // 获取url与类和方法的对应信息
            Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
            for (RequestMappingInfo info : map.keySet()) {
                // 获取url的Set集合，一个方法可能对应多个url
                Set<String> patterns = info.getPatternsCondition().getPatterns();
                String firstPattern =  patterns.iterator().next();
                firstPattern = firstPattern.substring(firstPattern.lastIndexOf("/",
                        firstPattern.lastIndexOf("/")-1)+1, firstPattern.lastIndexOf("/"));
                // 取出所有request路径，判断是否存在，以findAll接口为标识
                if (firstPattern.equals(moduleName.replace(C_CONTROLLER, ""))){
                    log.info(MESSAGE_ERROR_ADD_REQUEST_MAPPING + moduleName);
                    return true;
                }
            }
            try {
                Method method = requestMappingHandlerMapping.getClass().getSuperclass().getSuperclass().
                        getDeclaredMethod("detectHandlerMethods",Object.class);
                //将private改为可使用
                method.setAccessible(true);
                method.invoke(requestMappingHandlerMapping, moduleName);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                log.error(e.getMessage());
                return false;
            }
            if (generatorTable != null) {
                generatorTable.setIsEnableUrl(1);
                updateGeneratorTable(generatorTable);
            }
            return true;
        }
        return false;
    }

    /**
     * @Description: 删除GitLab上的一个目录下的所有文件
     * @param path 目录地址
     * @param setting 获得获取配置的变量
     * @Author HuQi
     * @create 2021-08-04 13:04
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePathFromGitLab(String path, ResourceBundle setting) {
        // 删除GitLab上的指定目录
        log.info(MESSAGE_INFO_GITLAB_REMOVE);
        GitLabUtil gitLabUtil = getGitLabUtil(setting);
        if (gitLabUtil.deletePackage(path)){
            log.info(MESSAGE_SUCCESS_GITLAB_REMOVE + path);
        }else {
            log.info(MESSAGE_ERROR_GITLAB_REMOVE + path);
        }
    }

    /**
     * @Description: 通过表名查找相关代码生成器的数据
     * @param tableName 表名
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO 返回代码生成器表的实体类
     * @Author HuQi
     * @create 2021-08-02 20:07
     */
    @Override
    public GeneratorCodeDO hasGeneratorTable(String tableName) {
        LambdaQueryWrapper<GeneratorCodeDO> generatorQ = Wrappers.lambdaQuery();
        generatorQ.eq(GeneratorCodeDO::getTableName, tableName);
        return generatorCodeMapper.selectOne(generatorQ);
    }

    /**
     * 通过表名查找相关代码生成器的数据，同时判断用户是否有权限操作
     *
     * @param tableName 表名
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO 返回代码生成器表的实体类
     * @Author HuQi
     * @create 2021-08-02 20:08
     */
    @Override
    public GeneratorCodeDO getGeneratorTable(String tableName) {
        GeneratorCodeDO generatorCodeDO = hasGeneratorTable(tableName);
        LoginUser user = getUser();
        if (generatorCodeDO == null){
            ServiceUtils.throwException(tableName + MESSAGE_ERROR_TABLE_NOT_EXIST);
        }else if (StringUtils.isEmpty(generatorCodeDO.getCreateBy()) || !generatorCodeDO.getCreateBy().equals(user.getUsername())){
            ServiceUtils.throwException(tableName + MESSAGE_ERROR_TABLE_ALREADY_USE);
        }
        return generatorCodeDO;
    }

    /**
     * 更新代码生成器表数据
     *
     * @param generatorTable 代码生成器表实体类
     * @Author HuQi
     * @create 2021-08-02 17:55
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateGeneratorTable(GeneratorCodeDO generatorTable) {
        generatorCodeMapper.updateById(generatorTable);
    }

    /**
     * 添加代码生成器表数据
     *
     * @param generatorTable 代码生成器表实体类
     * @Author HuQi
     * @create 2021-08-02 17:54
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addGenertatorTable(GeneratorCodeDO generatorTable) {
        generatorCodeMapper.insert(generatorTable);
    }

    /**
     * 清除代码生成器所生成的表以及代清空码生成器表中的数据
     *
     * @param init 是否是初始化标识
     * @Author HuQi
     * @create 2021-08-02 17:49
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeAllGeneratorTable(Boolean init) {
        LambdaQueryWrapper<GeneratorCodeDO> generatorQ = Wrappers.lambdaQuery();
        generatorQ.select(GeneratorCodeDO::getTableNamePlus);
        List<GeneratorCodeDO> generatorCodeDoS = generatorCodeMapper.selectList(generatorQ);
        try {
            for (GeneratorCodeDO generatorCodeDO : generatorCodeDoS) {
                String tableNamePlus = generatorCodeDO .getTableNamePlus();
                String tableName = generatorCodeDO .getTableName();
                if (!init) {
                    removeBean(oConvertUtils.camelName(tableName));
                }
                generatorCodeMapper.dropTable(tableNamePlus);
            }
            generatorCodeMapper.delete(null);
        }catch (Exception e){
            log.error(e.getMessage());
            ServiceUtils.throwException(MESSAGE_ERROR_TABLE_REMOVE + e.getMessage());
        }
    }

    /**
     * 代码生成器开发的辅助工具方法——获取系统所有加载的Bean
     *
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author HuQi
     * @create 2021-08-02 19:55
     */
    @Override
    public List<Map<String, Object>> getAllBean() {
        List<Map<String, Object>> list = new ArrayList<>();
        String[] beans = getApplicationContext().getBeanDefinitionNames();
        for (String beanName : beans) {
            Class<?> beanType = getApplicationContext().getType(beanName);
            Map<String, Object> map = new HashMap<>(2);
            map.put("BeanName", beanName);
            map.put("beanType", beanType);
            // map.put("package", beanType.getPackage()); // Bean包详细内容
            list.add(map);
        }
        return list;
    }

    /**
     * 代码生成器开发的辅助工具方法——通过BBeanName查找Bean是否存在
     *
     * @param clazzName BeanName
     * @return java.lang.Object
     * @Author HuQi
     * @create 2021-08-02 19:56
     */
    @Override
    public Object getBean(String clazzName) {
        return getApplicationContext().getBean(clazzName);
    }

    /**
     * 代码生成器开发的辅助工具方法——获取系统中的所有可用的接口地址
     *
     * @return java.util.List<java.lang.String> 返回接口地址列表
     * @Author HuQi
     * @create 2021-08-02 19:57
     */
    @Override
    public List<String> getUrls(){
        List<String> urls = new ArrayList<>();

        RequestMappingHandlerMapping requestMappingHandlerMapping = getApplicationContext()
                .getBean(RequestMappingHandlerMapping.class);
        // 获取url与类和方法的对应信息
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (RequestMappingInfo info : map.keySet()) {
            // 获取url的Set集合，一个方法可能对应多个url
            Set<String> patterns = info.getPatternsCondition().getPatterns();
            //把结果存入静态变量中程序运行一次次方法之后就不用再次请求次方法
            urls.addAll(patterns);
        }
        return urls;
    }

    /**
     * 实例化得到GitLabUtil工具类
     *
     * @param setting 获得获取配置的变量
     * @return org.jeecg.modules.form.util.GitLabUtil
     * @Author HuQi
     * @create 2021-08-04 15:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GitLabUtil getGitLabUtil(ResourceBundle setting) {
        GitLabUtil gitLabUtil = null;
        try {
            gitLabUtil = new GitLabUtil(setting.getString(SETTING_GITLAB_USERNAME)
                    , setting.getString(SETTING_GITLAB_PASSWORD), setting.getString(SETTING_GITLAB_HTTP),
                    setting.getString(SETTING_GITLAB_PROJECT), setting.getString(SETTING_GITLAB_BRANCH), redisUtil);
        }catch (Exception e){
            log.error(e.getMessage());
            ServiceUtils.throwException(e.getMessage());
        }
        return gitLabUtil;
    }

}
