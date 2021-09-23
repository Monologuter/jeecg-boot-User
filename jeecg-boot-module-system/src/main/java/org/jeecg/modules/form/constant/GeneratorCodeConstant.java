package org.jeecg.modules.form.constant;

/**
 * 代码生成器常量
 *
 * @Author: HuQi
 * @Date: 2021年08月25日 16:32
 */
public class GeneratorCodeConstant {

    private GeneratorCodeConstant() {
    }

    public static final String C_ENTITY = "Entity";
    public static final String C_SERVICE = "Service";
    public static final String C_SERVICE_IMPL = "ServiceImpl";
    public static final String C_MAPPER = "Mapper";
    public static final String C_CONTROLLER = "Controller";

    public static final String DISABLE_STR = "disable";
    public static final String ENABLE_STR = "enable";
    public static final String TRUE_STR = "true";
    public static final String FILE_SPLIT_STR = ",";
    public static final String OPT_ADMIN_USER = "admin";

    public static final String SETTING_AUTHOR = "author";
    public static final String SETTING_TABLE_PREFIX = "tablePrefix";
    public static final String SETTING_PERSISTENT_METHOD = "persistentMethod";
    public static final String SETTING_PERSISTENT_METHOD_GITLAB = "GitLab";
    public static final String SETTING_PERSISTENT_METHOD_FILE = "File";
    public static final String SETTING_IS_PERSISTENT = "isPersistent";
    public static final String SETTING_USER_PATH = "user";
    public static final String SETTING_PARENT = "parent";
    public static final String SETTING_AUTO_GENERATOR_PATH = "AutoGeneratorPath";
    public static final String SETTING_MAVEN_COMPILE_METHOD = "compile";
    public static final String SETTING_RESULT = "result";
    public static final String SETTING_PARENT_ENTITY = "parentEntity";
    public static final String SETTING_PARENT_CONTROLLER = "parentController";
    public static final String SETTING_JDBC_URL = "jdbc.url";
    public static final String SETTING_JDBC_USERNAME = "jdbc.username";
    public static final String SETTING_JDBC_PASSWORD = "jdbc.password";
    public static final String SETTING_JDBC_DRIVER = "jdbc.driver";
    public static final String SETTING_TAMPLATES_APPLICATION = "tamplates.application";
    public static final String SETTING_TAMPLATES_CONTROLLER = "tamplates.controller";
    public static final String SETTING_TAMPLATES_ENTITY = "tamplates.entity";
    public static final String SETTING_TAMPLATES_MAPPER = "tamplates.mapper";
    public static final String SETTING_TAMPLATES_XML = "tamplates.xml";
    public static final String SETTING_TAMPLATES_SERVICE = "tamplates.service";
    public static final String SETTING_TAMPLATES_SERVICEIMPL = "tamplates.serviceImpl";
    public static final String SETTING_TAMPLATES_BASECONTROLLER = "tamplates.baseController";
    public static final String SETTING_TAMPLATES_BASEENTITY = "tamplates.baseEntity";
    public static final String SETTING_TAMPLATES_RESULT = "tamplates.result";
    public static final String SETTING_TAMPLATES_POM = "tamplates.pom";
    public static final String SETTING_CLASS_PATH = "classPath";
    public static final String SETTING_MAVEN_PATH1 = "mavenPath1";
    public static final String SETTING_MAVEN_PATH2 = "mavenPath2";
    public static final String SETTING_JAVA_HOME1 = "javaHome1";
    public static final String SETTING_JAVA_HOME2 = "javaHome2";
    public static final String SETTING_GITLAB_TREE = "gitlab.tree";
    public static final String SETTING_GITLAB_USERNAME = "gitlab.username";
    public static final String SETTING_GITLAB_PASSWORD = "gitlab.password";
    public static final String SETTING_GITLAB_HTTP = "gitlab.http";
    public static final String SETTING_GITLAB_PROJECT = "gitlab.project";
    public static final String SETTING_GITLAB_BRANCH = "gitlab.branch";

    public static final String SETTING_PACKAGE_CONTROLLER = "controller";
    public static final String SETTING_PACKAGE_SERVICE = "service";
    public static final String SETTING_PACKAGE_SERVICE_IMPL = "service.impl";
    public static final String SETTING_PACKAGE_ENTITY = "entity";
    public static final String SETTING_PACKAGE_MAPPER = "mapper";
    public static final String SETTING_JAVA_FILE_EXTENSION = ".java";
    public static final String SETTING_FTL_FILE_EXTENSION = ".ftl";
    public static final String SETTING_FILE_POM_XML = "pom.xml";

    // 设置生成的默认模板名称

    public static final String DEFAULT_TEMPLATES_PATH = "/defaultTemplates";
    public static final String FILE_NAME_CONTROLLER = "/controller.java";
    public static final String FILE_NAME_ENTITY = "/entity.java";
    public static final String FILE_NAME_MAPPER = "/mapper.java";
    public static final String FILE_NAME_SERVICE = "/service.java";
    public static final String FILE_NAME_SERVICEIMPL = "/serviceImpl.java";
    public static final String FILE_NAME_MAPPERXML = "/mapper.xml";
    public static final String FILE_NAME_BASECONTROLLER = "/BaseController.java";
    public static final String FILE_NAME_BASEENTITY = "/BaseEntity.java";
    public static final String FILE_NAME_RESULT = "/Result.java";

    public static final String MESSAGE_ERROR_TABLE_EXIST = "数据表已存在！";
    public static final String MESSAGE_ERROR_TABLE_NOT_EXIST = "数据表不存在，请先生成表！";
    public static final String MESSAGE_ERROR_FIELD_EXIST = "新字段不能与原有其他字段名重名！";
    public static final String MESSAGE_ERROR_FIELD_NOT_EXIST = "请给出相关需要的字段信息，字段信息不能为空！";
    public static final String MESSAGE_ERROR_TABLE_ALREADY_USE = "该表已由其他用户占用，无权操作，请更换表名！";
    public static final String MESSAGE_ERROR_TABLE_REMOVE = "清除表失败！";
    public static final String MESSAGE_ERROR_TABLE_COLUMN_NOT_MATCHES = "字段名不允许出现除大小写字母、数字、下划线之外的字符，且不允许数字开头！";
    public static final String MESSAGE_ERROR_TABLE_COLUMN_NOT_MATCHES2 = "字段注释中不允许出现英文的反斜杠、双引号等字符！";
    public static final String MESSAGE_ERROR_GENERATOR_CODE_CREAT_FILE = "业务层模板文件创建失败！";
    public static final String MESSAGE_ERROR_GITLAB_REMOVE = "删除GitLab目录失败：";
    public static final String MESSAGE_ERROR_ADD_REQUEST_MAPPING = "RequestMappingHandlerMapping中已存在该接口：";
    public static final String MESSAGE_ERROR_ADD_BEAN = "不存在或加载异常！";
    public static final String MESSAGE_ERROR_NOT_AUTH = "无权限操作！";
    public static final String MESSAGE_ERROR_CLEAN_DIR = "清空目录失败！";
    public static final String MESSAGE_ERROR_REMOVE_GENERATOR_URL = "接口移除失败！";
    public static final String MESSAGE_ERROR_ADD_GENERATOR_URL_NOT_TABLE = "表信息不存在，请删除相关代码文件后重新生成！";
    public static final String MESSAGE_ERROR_GENERATOR_ADD_URL = "接口第一次尝试添加失败！";
    public static final String MESSAGE_ERROR_GENERATOR_SECOND_ADD_URL = "接口第二次尝试添加失败！";
    public static final String MESSAGE_ERROR_COMPILE = "编译失败，请检查相关模板是否正确！移除相关代码文件！";
    public static final String MESSAGE_ERROR_COMPILE_EXIT_CODE = "编译退出代码出错！";
    public static final String MESSAGE_ERROR_COMPILE_MAVEN_PATH = "Maven目录配置无效！";
    public static final String MESSAGE_ERROR_GENERATOR_GITLAB_ADD = "上传至GitLab失败！";
    public static final String MESSAGE_ERROR_GENERATOR_CODE_VAR_MAP = "模板变量转换合并失败，请检查内容是否为标准的JSON格式！";
    public static final String MESSAGE_ERROR_CREAT_DEAFULT_TEMPLATE_FILE = "创建默认文件失败！清除模板目录！";
    public static final String MESSAGE_ERROR_CREAT_DEAFULT_TEMPLATE_DIR = "创建默认模板目录失败！";
    public static final String MESSAGE_ERROR_CREAT_TEMP_TEMPLATE_DIR = "创建临时模板目录失败！";
    public static final String MESSAGE_ERROR_COPY_COMMON_FILE = "复制通用文件application、pom.xml失败！";
    public static final String MESSAGE_ERROR_CLEAN_TEMP_DIR = "清空临时目录失败！";
    public static final String MESSAGE_ERROR_JSON_PARSE = "JSON信息解析失败！";
    public static final String MESSAGE_ERROR_GENERATOR_TABLE = "创建数据表失败！";
    public static final String MESSAGE_ERROR_UPDATE_JSON_PARSE = "UpdateJSON信息解析失败！";
    public static final String MESSAGE_ERROR_GENERATOR_CODE = "创建业务层代码失败！";
    public static final String MESSAGE_ERROR_GENERATOR_COMPILE = "编译失败！";
    public static final String MESSAGE_ERROR_GENERATOR_LOAD_START = "表未被创建，或未生成并编译相关代码！";
    public static final String MESSAGE_ERROR_GENERATOR_LOAD_EXIST = "动态代码加载失败，内存已存在bean，不可重复加载！";
    public static final String MESSAGE_ERROR_GENERATOR_LOAD = "动态代码加载失败！";
    public static final String MESSAGE_ERROR_REMOVE_GENERATOR_BEAN = "Bean不存在或删除失败，移除Bean失败！";
    public static final String MESSAGE_ERROR_REMOVE_GENERATOR_TABLE = "移除相关表失败！";
    public static final String MESSAGE_ERROR_REMOVE_GENERATOR_CODE = "移除相关代码成功！相关代码路径：";
    public static final String MESSAGE_ERROR_REMOVE_ALL_GENERATOR = "清除所有代码生成器失败！";
    public static final String MESSAGE_ERROR_OPT_URL_OPEN = "接口启用失败，请检查代码生成器是否已经注入该模块！";
    public static final String MESSAGE_ERROR_OPT_URL_CLOSE = "接口禁用失败，请检查代码生成器是否已经注入该模块！";
    public static final String MESSAGE_ERROR_OPT_URL_TYPE = "请输入正确的操作类型！";

    public static final String MESSAGE_SUCCESS_URL_PARSE = "Url解码完成！";
    public static final String MESSAGE_SUCCESS_UPDATE_URL_PARSE = "UpdateJSON解码完成！";
    public static final String MESSAGE_SUCCESS_GITLAB_REMOVE = "删除GitLab目录成功：";
    public static final String MESSAGE_SUCCESS_GENERATOR = "代码生成器执行完成！";
    public static final String MESSAGE_SUCCESS_GENERATOR_ADD_URL = "接口添加成功！";
    public static final String MESSAGE_SUCCESS_GENERATOR_SECOND_ADD_URL = "接口第二次尝试添加成功！";
    public static final String MESSAGE_SUCCESS_GENERATOR_GITLAB_ADD = "上传至GitLab成功！";
    public static final String MESSAGE_SUCCESS_GENERATOR_CODE = "创建业务层代码成功，项目根路径：";
    public static final String MESSAGE_SUCCESS_CREAT_TEMP_TEMPLATE_DIR = "删除临时目录成功！";
    public static final String MESSAGE_SUCCESS_GENERATOR_TABLE = "生成数据库表完成，数据库表名为：";
    public static final String MESSAGE_SUCCESS_GENERATOR_COMPILE = "编译成功！";
    public static final String MESSAGE_SUCCESS_GENERATOR_LOAD = "加载Class结果：";
    public static final String MESSAGE_SUCCESS_REMOVE_GENERATOR_BEAN = "移除注入Bean成功！";
    public static final String MESSAGE_SUCCESS_REMOVE_GENERATOR_TABLE = "移除相关表成功！表名为：";
    public static final String MESSAGE_SUCCESS_REMOVE_GENERATOR = "移除相关代码生成器执行完成！";
    public static final String MESSAGE_SUCCESS_REMOVE_ALL_GENERATOR = "清除所有代码生成器成功！";
    public static final String MESSAGE_SUCCESS_OPT_URL_OPEN = "接口启用成功！";
    public static final String MESSAGE_SUCCESS_OPT_URL_CLOSE = "接口禁用成功！";

    public static final String MESSAGE_INFO_GITLAB_REMOVE = "删除GitLab上生成的代码！";
    public static final String MESSAGE_INFO_GENERATOR_SECOND_ADD_URL = "接口尝试二次添加！";
    public static final String MESSAGE_INFO_BEAN_REMOVE = "移除Bean：";
    public static final String MESSAGE_INFO_GENERATOR_SREMOVE_PATH = "移除相关代码路径：";
    public static final String MESSAGE_INFO_COMPILE_THREAD = "启用线程重新编译！";
    public static final String MESSAGE_INFO_GENERATOR_GITLAB_ADD_COMMIT = "AutoCreate GeneratorCode：";
    public static final String MESSAGE_INFO_CREAT_DEAFULT_TEMPLATE_DIR = "创建默认模板目录：";
    public static final String MESSAGE_INFO_CREAT_TEMP_TEMPLATE_DIR = "创建临时模板目录：";
    public static final String MESSAGE_INFO_CREAT_TABLE = "创建表：";
    public static final String MESSAGE_INFO_UPDATE_CLOUMN = "更新表字段SQL：";
    public static final String MESSAGE_INFO_DELETE_CLOUMN = "删除表字段SQL：";
    public static final String MESSAGE_INFO_ADD_CLOUMN = "添加表字段SQL：";
    public static final String MESSAGE_INFO_GENERATOR_START = "开始执行：";
    public static final String MESSAGE_INFO_FIELD_NOT_CHANGE = "字段内容未发生任何变化！";
    public static final String MESSAGE_INFO_URL_PARSE = "Url解码！";
    public static final String MESSAGE_INFO_GENERATOR_TABLE = "正在生成表！";
    public static final String MESSAGE_INFO_UPDATE_URL_PARSE = "UpdateJSON解码！";
    public static final String MESSAGE_INFO_GENERATOR_COMPILE = "开始编译！";
    public static final String MESSAGE_INFO_REMOVE_GENERATOR_CODE = "移除相关代码路径：";
}
