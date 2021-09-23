package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.GeneratorCodeDO;
import org.jeecg.modules.form.service.GeneratorCodeService;
import org.jeecg.modules.form.util.FileUtil;
import org.jeecg.modules.form.dto.GeneratorCodeDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.BeanDefinitionOverrideException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;

import static org.jeecg.modules.form.constant.GeneratorCodeConstant.*;
import static org.jeecg.modules.form.util.GeneratorCodeUtil.*;

/**
 * 代码生成器：添加代码生成器、移除代码生成器、清空代码生成器（所有）
 *
 * @author: HuQi
 * @date: 2021年07月13日 15:09
 */
@RequestMapping(value = "/generator")
@RestController
@Api(tags = "表单代码生成器")
@Slf4j
public class GeneratorCodeController {

    @Autowired
    private GeneratorCodeService generatorCodeService;

    private ResourceBundle setting;

    /**
     * 添加代码生成器，用户通过输入表名以及表所有的相关字段，和代码生成器所需要的模板ID，同时也可设定只生成部分内容
     * 模板ID可不设定，默认使用默认模板。表名需具备唯一性，存在了则无法再创建。若只生成部分内容，
     * 需注意前后部分内容是否有关联
     *
     * @param tableName 数据库表名
     * @param json 表的相关字段和注释，JSON格式
     * @param templateId 模板ID
     * @param isAutoTable 是否自动生成表
     * @param isAutoCode 是否自动生成表
     * @param isAutoCompile 是否自动生成代码
     * @param isAutoLoad 是否自动编译
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi、LiKun
     * @create 2021-08-02 17:08
     */
    @ApiOperation(value = "添加代码生成器")
    @PostMapping(value = "/addGenerator")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public Result<String> autoGenerator(@ApiParam(value = "数据库表名", required = true) @RequestParam String tableName,
                                        @ApiParam(value = "表的相关字段和注释，JSON格式", required = true) @RequestParam String json,
                                        @ApiParam(value = "需要更新的字段,k=old,v=new") @RequestParam String updateFieldsJson,
                                        @ApiParam(value = "模板ID") @RequestParam(required = false) String templateId,
                                        @ApiParam(value = "是否自动生成表（包括对表的修改）") @RequestParam(defaultValue = "1") Integer isAutoTable,
                                        @ApiParam(value = "是否自动生成代码") @RequestParam(defaultValue = "1") Integer isAutoCode,
                                        @ApiParam(value = "是否自动编译") @RequestParam(defaultValue = "1") Integer isAutoCompile,
                                        @ApiParam(value = "是否自动加载") @RequestParam(defaultValue = "1") Integer isAutoLoad)
    {
        GeneratorCodeDTO generatorCodeVO = new GeneratorCodeDTO();
        GeneratorCodeDO generatorTable = generatorCodeService.hasGeneratorTable(tableName);
        setting = generatorCodeVO.getGeneratorSetting();
        String tablePrefix = setting.getString(SETTING_TABLE_PREFIX);
        String tableNamePlus = tablePrefix + tableName;

        // 使用表名作为模块名，同时也是代码的上一文件夹名，方便管理
        generatorCodeVO.setModuleName(oConvertUtils.camelNameCapFirst(tableName));
        generatorCodeVO.setTableName(tableNamePlus);
        generatorCodeVO.setUserName(getUser().getUsername());

        if (generatorTable != null && !generatorTable.getCreateBy().equals(generatorCodeVO.getUserName())){
            return Result.Error(MESSAGE_ERROR_TABLE_ALREADY_USE);
        }

        StringBuilder res = new StringBuilder(MESSAGE_INFO_GENERATOR_START);

        // 1、创建创建表或更新表
        if (isAutoTable == 1) {
            generatorTable = createAutoTable(json, generatorTable, tablePrefix, tableName, tableNamePlus, updateFieldsJson, res);
            if (generatorTable == null){
                return Result.OK(tableName + MESSAGE_INFO_FIELD_NOT_CHANGE);
            }
        }

        // 2、创建业务层代码，前提表存在
        if (isAutoCode == 1) {
            createAutoCode(generatorTable, generatorCodeVO, templateId, res);
        }

        // 3、动态编译业务层代码并打包，无条件，重新打包文件夹内所有文件
        if (isAutoCompile == 1) {
            createAutoCompile(tableName, generatorCodeVO, res);
        }

        // 4、动态加载代码，jar包内需要相关编译后的class文件
        if (isAutoLoad == 1) {
            createAutoLoad(generatorTable, tableName, generatorCodeVO, res);
        }
        return Result.OK(res + MESSAGE_SUCCESS_GENERATOR);
    }

    /**
     * 1、创建创建表或更新表
     *
     * @param json 表字段信息（包含表的字段名和注释）
     * @param generatorTable 代码生成器表实体类
     * @param tablePrefix 表前缀
     * @param tableName 表名
     * @param tableNamePlus 加了前缀后的表名
     * @param updateFieldsJson 需要更新字段的信息
     * @param res 结果
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO
     * @Author HuQi
     * @create 2021-08-27 14:15
     */
    private GeneratorCodeDO createAutoTable(String json, GeneratorCodeDO generatorTable, String tablePrefix, String tableName,
                                            String tableNamePlus, String updateFieldsJson, StringBuilder res) {
        // 表已经存在，增加，删除或更新字段
        log.info(MESSAGE_INFO_URL_PARSE);
        String result = "";
        try {
            result = java.net.URLDecoder.decode(json, "utf8");
        } catch (UnsupportedEncodingException e) {
            log.error(MESSAGE_ERROR_JSON_PARSE + e.getMessage());
        }
        log.info(MESSAGE_SUCCESS_URL_PARSE);
        Map<String, Object> jsonToMap = jsonToMap(result);
        String keyToString = mapKeyToString(setting.getString(SETTING_TABLE_PREFIX), jsonToMap);
        List<String> keyToList = mapKeyToList(jsonToMap);
        List<Object> valueToList = mapValueToList(jsonToMap);

        // 更新
        if (generatorTable != null) {
            if (keyToString.equals(generatorTable.getFields())){
                log.info(tableName + MESSAGE_INFO_FIELD_NOT_CHANGE);
                return null;
            }
            // 取新字段名
            String[] newFields = keyToString.split(FILE_SPLIT_STR);
            List<String> commonList = new ArrayList<>(Arrays.asList(newFields));
            List<String> addList = new ArrayList<>(Arrays.asList(newFields));
            // 取旧字段名
            String[] oldFields = generatorTable.getFields().split(FILE_SPLIT_STR);
            List<String> oldList = new ArrayList<>(Arrays.asList(oldFields));

            if (!"".equals(updateFieldsJson)) {
                // 更新字段
                updateFields(generatorTable, updateFieldsJson, tablePrefix, tableName, addList, commonList, oldList);
            }
            // 取交集
            commonList.retainAll(oldList);
            // 新增加
            addList.removeAll(commonList);
            if (!addList.isEmpty()) {
                List<String> list = new ArrayList<>();
                // 拿到字段描述
                for (String item : addList) {
                    list.add((String) jsonToMap.get(item.replace(tablePrefix, "")));
                }
                try {
                    generatorCodeService.addFields(addList, list, tablePrefix + tableName);
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    log.error(e.getMessage());
                }
            }
            // 旧删除
            oldList.removeAll(commonList);
            if (!oldList.isEmpty()) {
                // 删除表中字段
                try {
                    generatorCodeService.deleteFields(oldList, tablePrefix + tableName);
                } catch (Exception e) {
                    TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
                    log.error(e.getMessage());
                }
            }
            // 更新generator_code表中的fields信息
            generatorTable.setFields(keyToString);
            generatorCodeService.updateGeneratorTable(generatorTable);

            if (TRUE_STR.equals(setting.getString(SETTING_IS_PERSISTENT)) &&
                    SETTING_PERSISTENT_METHOD_GITLAB.equals(setting.getString(SETTING_PERSISTENT_METHOD))) {
                // 开启线程删除目录
                String path = setting.getString(SETTING_GITLAB_TREE) + "/" + oConvertUtils.camelNameCapFirst(tableName);
                new Thread(() -> generatorCodeService.deletePathFromGitLab(path, setting)).start();
            }
            removeGenerator(tableName, 0, 0, 1);
        } else {
            generatorTable = createTable(tableName, tableNamePlus, tablePrefix, keyToList, valueToList, keyToString, res);
        }
        return generatorTable;
    }

    /**
     * 创建表，当表不存在时创建
     *
     * @param tableName 表名
     * @param tableNamePlus 含有前缀的表名
     * @param tablePrefix 前缀
     * @param keyToList 所有字段名
     * @param valueToList 所有字段备注
     * @param keyToString 所有字段的连接字符串
     * @param res 结果输出字符串
     * @return org.jeecg.modules.form.entity.GeneratorCodeDO
     * @Author HuQi
     * @create 2021-08-27 14:20
     */
    private GeneratorCodeDO createTable(String tableName, String tableNamePlus, String tablePrefix,
                             List<String> keyToList, List<Object> valueToList, String keyToString, StringBuilder res) {
        // 表不存在，新生成表
        GeneratorCodeDO generatorTable = new GeneratorCodeDO();
        generatorTable.setTableName(tableName);
        if (keyToList.isEmpty()) {
            ServiceUtils.throwException(MESSAGE_ERROR_FIELD_NOT_EXIST);
        }
        try {
            log.info(tableName + MESSAGE_INFO_GENERATOR_TABLE);
            tableNamePlus = generatorCodeService.createTable(tablePrefix, tableNamePlus, keyToList, valueToList);
            generatorTable.setTableNamePlus(tableNamePlus);
            generatorTable.setIsEnableUrl(1);
            // 更新generator_code表中的fields信息
            generatorTable.setFields(keyToString);
            generatorCodeService.addGenertatorTable(generatorTable);
            log.info(MESSAGE_SUCCESS_GENERATOR_TABLE + tableNamePlus);
            res.append(MESSAGE_SUCCESS_GENERATOR_TABLE).append(tableNamePlus).append("    ");
        } catch (Exception e) {
            log.error(MESSAGE_ERROR_GENERATOR_TABLE + e.getMessage());
            ServiceUtils.throwException(res + e.getMessage() + MESSAGE_ERROR_GENERATOR_TABLE);
        }
        return generatorTable;
    }

    /**
     * 更新字段，修改旧字段名
     *
     * @param generatorTable 代码生成器表实体类
     * @param updateFieldsJson 更新字段的json信息
     * @param tablePrefix 表名前缀
     * @param tableName 表名
     * @param addList 新增字段的列表
     * @param commonList 共同字段列表
     * @param oldList 旧字段的列表
     * @return void
     * @Author HuQi
     * @create 2021-08-27 17:24
     */
    private void updateFields(GeneratorCodeDO generatorTable, String updateFieldsJson, String tablePrefix,
                              String tableName, List<String> addList, List<String> commonList, List<String> oldList) {
        log.info(MESSAGE_INFO_UPDATE_URL_PARSE);
        String str = "";
        try {
            str = java.net.URLDecoder.decode(updateFieldsJson, "utf8");
        } catch (UnsupportedEncodingException e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            log.error(e.getMessage());
            ServiceUtils.throwException(MESSAGE_ERROR_UPDATE_JSON_PARSE);
        }
        log.info(MESSAGE_SUCCESS_UPDATE_URL_PARSE);
        Map<String, Object> updateFieldsJsonToMap = jsonToMap(str);
        // 判断更新的字段是否存在
        List<String> oldFieldsList = new ArrayList<>();
        for (Map.Entry<String, Object> m : updateFieldsJsonToMap.entrySet()) {
            oldFieldsList.add(tablePrefix + m.getKey());
            // 移除修改的旧的字段名
            oldList.remove(tablePrefix + m.getKey());
        }
        // 旧新字段取交集
        oldFieldsList.removeAll(Arrays.asList(generatorTable.getFields().split(FILE_SPLIT_STR)));
        if (!updateFieldsJsonToMap.isEmpty() && !oldFieldsList.isEmpty()) {
            ServiceUtils.throwException("更新的" + oldFieldsList.toString().replace(tablePrefix, "") + "字段不存在！");
        }

        try {
            List<String> rsList = generatorCodeService.updateFields(updateFieldsJsonToMap, tableName, tablePrefix, oldList);
            // 移除修改的新的字段名
            commonList.removeAll(rsList);
            addList.removeAll(rsList);
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            ServiceUtils.throwException(e.getMessage());
        }
    }

    /**
     * 2、创建业务层代码，前提表存在
     * 根据生成的数据库表生成相应的代码文件
     * 若有自定义模板，则根据自定义模板生成，若无模板，则根据默认模板生成
     *
     * @param generatorTable 代码生成器表实体类
     * @param generatorCodeVO 业务层交互数据传输类
     * @param templateId 模板ID
     * @param res 结果输出字符串
     * @return void
     * @Author HuQi
     * @create 2021-08-27 17:26
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAutoCode(GeneratorCodeDO generatorTable, GeneratorCodeDTO generatorCodeVO, String templateId, StringBuilder res) {
        if (generatorTable == null){
            ServiceUtils.throwException(generatorCodeVO.getTableName() + MESSAGE_ERROR_TABLE_NOT_EXIST);
            return;
        }
        try {
            String projectPath = generatorCodeService.generatorCode(generatorCodeVO, templateId);
            generatorTable.setIsGeneratorCode(1);
            generatorTable.setCodePath(projectPath);
            generatorTable.setTemplateId(templateId);
            generatorCodeService.updateGeneratorTable(generatorTable);
            log.info(MESSAGE_SUCCESS_GENERATOR_CODE + projectPath);
            res.append(MESSAGE_SUCCESS_GENERATOR_CODE).append(projectPath).append("    ");
        } catch (Exception e) {
            log.error(generatorCodeVO.getTableName() + MESSAGE_ERROR_GENERATOR_CODE + e.getMessage());
            ServiceUtils.throwException(res + e.getMessage() + MESSAGE_ERROR_GENERATOR_CODE);
        }
    }

    /**
     * 3、动态编译业务层代码并打包，无条件，重新打包文件夹内所有文件
     * 对已生成的代码进行编译
     *
     * @param tableName 表名
     * @param generatorCodeVO 业务层交互数据传输类
     * @param res 结果输出字符串
     * @return void
     * @Author HuQi
     * @create 2021-08-27 17:29
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAutoCompile(String tableName, GeneratorCodeDTO generatorCodeVO, StringBuilder res) {
        log.info(tableName + MESSAGE_INFO_GENERATOR_COMPILE);
        if (!generatorCodeService.mavenCompile("", SETTING_MAVEN_COMPILE_METHOD, generatorCodeVO, true)) {
            log.info(tableName + MESSAGE_ERROR_GENERATOR_COMPILE);
            ServiceUtils.throwException(res + MESSAGE_ERROR_GENERATOR_COMPILE);
        }else {
            log.info(tableName + MESSAGE_SUCCESS_GENERATOR_COMPILE);
            res.append(MESSAGE_SUCCESS_GENERATOR_COMPILE).append("    ");
        }
    }

    /**
     * 4、动态加载代码，加载编译完后的class字节码代码文件
     *
     * @param generatorTable 代码生成器表实体类
     * @param tableName 表名
     * @param generatorCodeVO 业务层交互数据传输类
     * @param res 结果输出字符串
     * @return void
     * @Author HuQi
     * @create 2021-08-27 17:30
     */
    @Transactional(rollbackFor = Exception.class)
    public void createAutoLoad(GeneratorCodeDO generatorTable, String tableName, GeneratorCodeDTO generatorCodeVO, StringBuilder res) {
        if (generatorTable == null || StringUtils.isEmpty(generatorTable.getIsGeneratorCode()) ||generatorTable.getIsGeneratorCode() == 0){
            ServiceUtils.throwException(tableName + MESSAGE_ERROR_GENERATOR_LOAD_START);
            return;
        }
        List<String> loadResult;
        try {
            loadResult = generatorCodeService.loadClass("", generatorCodeVO);
            if (!loadResult.isEmpty()){
                generatorTable.setIsGeneratorLoad(1);
                generatorCodeService.updateGeneratorTable(generatorTable);
            }
            log.info(tableName + MESSAGE_SUCCESS_GENERATOR_LOAD + loadResult);
            res.append(MESSAGE_SUCCESS_GENERATOR_LOAD).append(loadResult).append("    ");
        }catch (BeanDefinitionOverrideException ex){
            log.error(tableName + MESSAGE_ERROR_GENERATOR_LOAD_EXIST);
            ServiceUtils.throwException(res + MESSAGE_ERROR_GENERATOR_LOAD_EXIST);
        }catch (Exception e) {
            log.error(tableName + MESSAGE_ERROR_GENERATOR_LOAD);
            ServiceUtils.throwException(res + MESSAGE_ERROR_GENERATOR_LOAD + e.getMessage());
        }
    }

    /**
     * 移除代码生成器：用户通过输入表名移除有关的代码生成器，同时也可设定只移除部分内容
     *
     * @param tableName 数据库表名
     * @param isDeleteTable 是否移除相关表
     * @param isDeleteCode 是否移除相关代码
     * @param isDeleteBean 是否移除相关Bean
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-02 17:18
     */
    @ApiOperation(value = "移除代码生成器", notes = "用户输入表的表名")
    @PostMapping(value = "/removeGenerator")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> removeGenerator(@ApiParam(value = "数据库表名", required = true) @RequestParam String tableName,
                                  @ApiParam(value = "是否移除相关表") @RequestParam(defaultValue = "1") Integer isDeleteTable,
                                  @ApiParam(value = "是否移除相关代码") @RequestParam(defaultValue = "1") Integer isDeleteCode,
                                  @ApiParam(value = "是否移除相关Bean") @RequestParam(defaultValue = "1") Integer isDeleteBean)
    {
        GeneratorCodeDTO generatorCodeVO = new GeneratorCodeDTO();
        setting = generatorCodeVO.getGeneratorSetting();
        String tableNamePlus = setting.getString(SETTING_TABLE_PREFIX) + tableName;
        GeneratorCodeDO generatorTable = generatorCodeService.getGeneratorTable(tableName);
        StringBuilder res = new StringBuilder(MESSAGE_INFO_GENERATOR_START);
        generatorCodeVO.setUserName(getUser().getUsername());

        if (!generatorTable.getCreateBy().equals(generatorCodeVO.getUserName())){
            return Result.Error(MESSAGE_ERROR_TABLE_ALREADY_USE);
        }

        if (isDeleteBean == 1) {
            try {
                if (generatorCodeService.removeBean(oConvertUtils.camelName(tableName))) {
                    log.info(MESSAGE_SUCCESS_REMOVE_GENERATOR_BEAN);
                    generatorTable.setIsGeneratorLoad(0);
                    // 防止二次注入时未开启接口导致接口后续无法开启
                    generatorTable.setIsEnableUrl(1);
                    generatorCodeService.updateGeneratorTable(generatorTable);
                    res.append(MESSAGE_SUCCESS_REMOVE_GENERATOR_BEAN).append("    ");
                } else {
                    return Result.Error(res + MESSAGE_ERROR_REMOVE_GENERATOR_BEAN);
                }
            }catch (Exception e){
                return Result.Error(res + e.getMessage() + MESSAGE_ERROR_REMOVE_GENERATOR_BEAN);
            }
        }

        if (isDeleteCode == 1) {
            String removePath = generatorCodeVO.getProjectPath() + setting.getString(SETTING_AUTO_GENERATOR_PATH)
                    + "/" + SETTING_USER_PATH + "/" + getUser().getUsername() + "/src/main/java" + File.separator
                    + setting.getString(SETTING_PARENT).replace(".","/") + File.separator
                    + oConvertUtils.camelNameCapFirst(tableName);
            log.info(MESSAGE_INFO_REMOVE_GENERATOR_CODE + removePath);
            deleteCode(generatorTable, tableName, generatorCodeVO, res, removePath);
        }

        if (isDeleteTable == 1) {
            if (generatorCodeService.removeTable(generatorTable, tableNamePlus)){
                log.info(MESSAGE_SUCCESS_REMOVE_GENERATOR_TABLE + tableNamePlus);
                res.append(MESSAGE_SUCCESS_REMOVE_GENERATOR_TABLE).append(tableNamePlus).append("    ");
            }else {
                return Result.Error(res + MESSAGE_ERROR_REMOVE_GENERATOR_TABLE);
            }
        }

        return Result.OK(res + MESSAGE_SUCCESS_REMOVE_GENERATOR);
    }

    /**
     * 删除代码生成器生成的代码
     *
     * @param generatorTable 代码生成器表实体类
     * @param tableName 表名
     * @param generatorCodeVO 业务层交互数据传输类
     * @param res 结果输出字符串
     * @param removePath 代码移除路径
     * @return void
     * @Author HuQi
     * @create 2021-08-28 14:55
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteCode(GeneratorCodeDO generatorTable, String tableName, GeneratorCodeDTO generatorCodeVO, StringBuilder res, String removePath) {
        try {
            FileUtil.deleteDir(new File(removePath));
            new Thread(() -> generatorCodeService.mavenCompileLock("", "compile", generatorCodeVO, false)).start();
            generatorTable.setIsGeneratorCode(0);
            generatorTable.setCodePath("");
            generatorCodeService.updateGeneratorTable(generatorTable);
            if (TRUE_STR.equals(setting.getString(SETTING_IS_PERSISTENT)) &&
                    SETTING_PERSISTENT_METHOD_GITLAB.equals(setting.getString(SETTING_PERSISTENT_METHOD))) {
                // 开启线程删除目录
                String path = setting.getString(SETTING_GITLAB_TREE) + "/" + oConvertUtils.camelNameCapFirst(tableName);
                new Thread(() -> generatorCodeService.deletePathFromGitLab(path, setting)).start();
            }
            log.info(MESSAGE_ERROR_REMOVE_GENERATOR_CODE + removePath);
            res.append(MESSAGE_ERROR_REMOVE_GENERATOR_CODE).append(removePath).append("    ");
        } catch (IOException e) {
            log.error(e.getMessage());
            ServiceUtils.throwException(res + MESSAGE_ERROR_CLEAN_DIR);
        }
    }

    /**
     * 清空代码生成器此功能只有admin账号管理员才有权限操作
     *
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-02 17:23
     */
    @ApiOperation(value = "清空代码生成器", notes = "管理员admin账号操作。")
    @GetMapping("/cleanGenerator")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> cleanGenerator(){

        GeneratorCodeDTO generatorCodeVO = new GeneratorCodeDTO();
        if (generatorCodeService.cleanAllGenerator(generatorCodeVO)){
            return Result.OK(MESSAGE_SUCCESS_REMOVE_ALL_GENERATOR);
        }else {
            return Result.Error(MESSAGE_ERROR_REMOVE_ALL_GENERATOR);
        }

    }

    /**
     * 通过传入模块名以及对应的禁用、启用操作名，进行对相应的接口的操作
     *
     * @param moduleName 模块名
     * @param action 操作名
     * @return org.jeecg.common.api.vo.Result
     * @Author HuQi
     * @create 2021-08-12 09:39
     */
    @ApiOperation(value = "代码生成器的接口启用/禁用")
    @GetMapping("/optGeneratorURL")
    @Transactional(rollbackFor = Exception.class)
    public Result<String> optGeneratorUrl(@ApiParam(value = "模块名 接口倒数第二级的地址", required = true)
                                            @RequestParam String moduleName,
                                          @ApiParam(value = "操作 启用：" + ENABLE_STR + "  禁用：" + DISABLE_STR, required = true)
                                            @RequestParam String action){
        GeneratorCodeDO generatorTable = generatorCodeService.getGeneratorTable(oConvertUtils.camelToUnderline(moduleName));
        if (ENABLE_STR.equals(action)){
            if (generatorCodeService.optGeneratorUrl(moduleName + C_CONTROLLER, action, generatorTable)) {
                return Result.OK(moduleName + MESSAGE_SUCCESS_OPT_URL_OPEN);
            }else {
                return Result.Error(moduleName + MESSAGE_ERROR_OPT_URL_OPEN);
            }
        }else if (DISABLE_STR.equals(action)){
            if (generatorCodeService.optGeneratorUrl(moduleName + C_CONTROLLER, action, generatorTable)) {
                return Result.OK(moduleName + MESSAGE_SUCCESS_OPT_URL_CLOSE);
            }else {
                return Result.Error(moduleName + MESSAGE_ERROR_OPT_URL_CLOSE);
            }
        }else {
            return Result.Error(MESSAGE_ERROR_OPT_URL_TYPE);
        }

    }
}
