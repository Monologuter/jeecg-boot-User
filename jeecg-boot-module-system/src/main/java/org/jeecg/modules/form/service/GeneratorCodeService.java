package org.jeecg.modules.form.service;

import org.jeecg.modules.form.entity.GeneratorCodeDO;
import org.jeecg.modules.form.util.GitLabUtil;
import org.jeecg.modules.form.dto.GeneratorCodeDTO;

import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 代码生成器Service层接口类
 *
 * @author: HuQi、HuangSn、LiKun
 * @date: 2021年07月16日 17:39
 */
public interface GeneratorCodeService {

    /**
     * 更新字段
     *
     * @param updateFieldsJsonToMap 更新字段JSON，如{"旧字段":{"新字段":"新字段备注"}}
     * @param tableName 表名
     * @param tablePrefix 表名前缀
     * @param oldList 旧字段
     * @return java.util.List<java.lang.String>
     * @Author LiKun
     */
    List<String> updateFields(Map<String, Object> updateFieldsJsonToMap, String tableName, String tablePrefix, List<String> oldList);

    /**
     * 新增字段
     *
     * @param fields    新字段
     * @param tableName 表名
     * @param listValue 字段描述
     * @author: LiKun
     * @Return: void
     */
    void addFields(List<String> fields, List<String> listValue, String tableName);

    /**
     * 删除字段
     *
     * @param fields    要删除的字段
     * @param tableName 表名
     * @author: LiKun
     * @Return: int
     */
    void deleteFields(List<String> fields, String tableName);

    /**
     * 创建表
     *
     * @param tablePrefix 表名和字段名前缀
     * @param tableName 表名
     * @param listKey 键链
     * @param listValue 值链
     * @return java.lang.String
     * @Author HuQi、HuangSn
     * @create 2021-08-26 09:56
     */
    String createTable(String tablePrefix, String tableName, List<String> listKey, List<Object> listValue);

    /**
     * 代码文件生成，只有在生成相关表后才能使用此部分。
     *
     * @param generatorCodeVO 值对象，用于业务层传输数据
     * @param templateId 模板ID
     * @return java.lang.String 返回生成代码的路径
     * @Author HuQi
     * @create 2021-08-02 17:27
     */
    String generatorCode(GeneratorCodeDTO generatorCodeVO, String templateId);

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
    void mavenCompileLock(String pomPath, String command, GeneratorCodeDTO generatorCodeVO, Boolean isUploadGit);

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
    boolean mavenCompile(String pomPath, String command, GeneratorCodeDTO generatorCodeVO, Boolean isUploadGit);

    /**
     * 类的动态加载。此方法调用ModuleClassLoaderUtil工具类，完成对jar包中的字节码文件的加载
     *
     * @param jarPath jar包的存放路径
     * @param generatorCodeVO 值对象，业务层间调用的数据参数
     * @return java.util.List<java.lang.String> 返回
     * @Author HuQi
     * @create 2021-08-02 17:45
     */
    List<String> loadClass(String jarPath, GeneratorCodeDTO generatorCodeVO);

    /**
     * 移除代码生成器注入的所有Bean，同时移除对应的接口地址
     *
     * @param tableName 表名，内部拼接为类名
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 19:59
     */
    boolean removeBean(String tableName);

    /**
     * 移除对应的相关代码生成器的表和代码生成器中的表中数据
     *
     * @param generatorCodeDO 代码生成器表的实体类
     * @param tableNamePlus 已生成的表名
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 20:03
     */
    boolean removeTable(GeneratorCodeDO generatorCodeDO, String tableNamePlus);

    /**
     * 清除代码生成器代码、数据库以及持久化文件等。只允许管理员admin用户操作。
     *
     * @param generatorCodeVO 值对象
     * @return boolean
     * @Author HuQi
     * @create 2021-08-02 20:05
     */
    boolean cleanAllGenerator(GeneratorCodeDTO generatorCodeVO);

    /**
     * 删除GitLab上的一个目录下的所有文件
     *
     * @param path 目录地址
     * @param setting 获得获取配置的变量
     * @Author HuQi
     * @create 2021-08-04 13:04
     */
    void deletePathFromGitLab(String path, ResourceBundle setting);

    /**
     * 通过表名查找相关代码生成器的数据
     *
     * @param tableName 表名
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO 返回代码生成器表的实体类
     * @Author HuQi
     * @create 2021-08-02 20:07
     */
    GeneratorCodeDO hasGeneratorTable(String tableName);

    /**
     * 通过表名查找相关代码生成器的数据，同时判断用户是否有权限操作
     *
     * @param tableName 表名
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO 返回代码生成器表的实体类
     * @Author HuQi
     * @create 2021-08-02 20:08
     */
    GeneratorCodeDO getGeneratorTable(String tableName);

    /**
     * 更新代码生成器表数据
     *
     * @param generatorTable 代码生成器表实体类
     * @Author HuQi
     * @create 2021-08-02 17:55
     */
    void updateGeneratorTable(GeneratorCodeDO generatorTable);

    /**
     * 添加代码生成器表数据
     *
     * @param generatorTable 代码生成器表实体类
     * @Author HuQi
     * @create 2021-08-02 17:54
     */
    void addGenertatorTable(GeneratorCodeDO generatorTable);

    /**
     * 清除代码生成器所生成的表以及代清空码生成器表中的数据
     *
     * @param init 是否是初始化标识
     * @Author HuQi
     * @create 2021-08-02 17:49
     */
    void removeAllGeneratorTable(Boolean init);

    /**
     * 代码生成器开发的辅助工具方法——获取系统所有加载的Bean
     *
     * @return java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author HuQi
     * @create 2021-08-02 19:55
     */
    List<Map<String, Object>> getAllBean();

    /**
     * 代码生成器开发的辅助工具方法——通过BBeanName查找Bean是否存在
     *
     * @param clazzName BeanName
     * @return java.lang.Object
     * @Author HuQi
     * @create 2021-08-02 19:56
     */
    Object getBean(String clazzName);

    /**
     * 代码生成器开发的辅助工具方法——获取系统中的所有可用的接口地址
     *
     * @return java.util.List<java.lang.String> 返回接口地址列表
     * @Author HuQi
     * @create 2021-08-02 19:57
     */
    List<String> getUrls();

    /**
     * 实例化得到GitLabUtil工具类
     *
     * @param setting 获得获取配置的变量
     * @return org.jeecg.modules.form.util.GitLabUtil
     * @Author HuQi
     * @create 2021-08-04 15:21
     */
    GitLabUtil getGitLabUtil(ResourceBundle setting);

    /**
     * 操作代码生成器生成的接口，对其进行启用或禁用操作，只需将Controller从RequestMappingHandlerMapping中删除或添加
     * 移除代码生成器的接口地址，即在RequestMappingHandlerMapping中删除该类
     * 因在此处移除其他类后，相同类名也无法再次重载，需重启才有效，所以不移除相关其他类。
     * 只将接口从RequestMappingHandlerMapping中移除，使用时还可再次重新将Controller加载到RequestMappingHandlerMapping即可
     *
     * @param moduleName 模块名
     * @param action 操作名
     * @param generatorTable 代码生成器表的实体类，为null时不更新接口启用标识
     * @return boolean
     * @Author HuQi
     * @create 2021-08-12 09:33
     */
    boolean optGeneratorUrl(String moduleName, String action, GeneratorCodeDO generatorTable);
}
