package org.jeecg.modules.form.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.GeneratorTemplatesDO;
import org.jeecg.modules.form.mapper.GeneratorTemplatesMapper;
import org.jeecg.modules.form.service.GeneratorTemplatesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jeecg.modules.form.constant.GeneratorTemplatesConstant.*;
import static org.jeecg.modules.form.util.GeneratorCodeUtil.*;

/**
 * 代码生成器模板的实现类，完成具体实现操作
 *
 * @Author: HuQi
 * @Date: 2021年07月31日 17:34
 */
@Service
@Slf4j
public class GeneratorTemplatesServiceImpl implements GeneratorTemplatesService {

    @Autowired
    private GeneratorTemplatesMapper generatorTemplatesMapper;

    /**
     * 根据模板ID，修改指定的业务层代码。数据库最终存储的是以b64编码的方式存储的
     *
     * @param templateId 模板ID
     * @param content 模板内容
     * @param type 指定业务层类型
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:07
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int saveTemplatesContent(String templateId, String content, String type) {

        GeneratorTemplatesDO templatesFileRecord = new GeneratorTemplatesDO();
        GeneratorTemplatesDO userTemplates = getUserTemplatesById(templateId);
        // 将字符串转b64
        content = stringToBase64(content);

        switch (type){
            case C_CONTROLLER:
                templatesFileRecord.setControllerContent(content);
                templatesFileRecord.setIsUseController(1);
                break;
            case C_MAPPER:
                templatesFileRecord.setMapperContent(content);
                templatesFileRecord.setIsUseMapper(1);
                break;
            case C_ENTITY:
                templatesFileRecord.setEntityContent(content);
                templatesFileRecord.setIsUseEntity(1);
                break;
            case C_SERVICE:
                templatesFileRecord.setServiceContent(content);
                templatesFileRecord.setIsUseService(1);
                break;
            case C_SERVICE_IMPL:
                templatesFileRecord.setServiceImplContent(content);
                templatesFileRecord.setIsUseServiceImpl(1);
                break;
            case C_VAR:
                templatesFileRecord.setTemplatesVarJson(content);
                templatesFileRecord.setIsUseTemplatesVar(1);
                break;
            default:
                break;
        }

        templatesFileRecord.setId(userTemplates.getId());
        return generatorTemplatesMapper.updateById(templatesFileRecord);
    }

    /**
     * 根据模板ID，设置模板业务层的启用/禁用，若被禁用则使用默认的模板
     *
     * @param templateId 模板ID
     * @param type 指定业务层类型
     * @param num 启用或禁用标识，1为启用，0为禁用
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:12
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setTemplates(String templateId, String type, Integer num) {
        GeneratorTemplatesDO userTemplates = getUserTemplatesById(templateId);
        switch (type){
            case C_CONTROLLER:
                userTemplates.setIsUseController(num);
                break;
            case C_MAPPER:
                userTemplates.setIsUseMapper(num);
                break;
            case C_ENTITY:
                userTemplates.setIsUseEntity(num);
                break;
            case C_SERVICE:
                userTemplates.setIsUseService(num);
                break;
            case C_SERVICE_IMPL:
                userTemplates.setIsUseServiceImpl(num);
                break;
            case C_VAR:
                userTemplates.setIsUseTemplatesVar(num);
                break;
            case C_ALL:
                userTemplates.setIsUseController(num);
                userTemplates.setIsUseMapper(num);
                userTemplates.setIsUseEntity(num);
                userTemplates.setIsUseService(num);
                userTemplates.setIsUseServiceImpl(num);
                userTemplates.setIsUseTemplatesVar(num);
                break;
            default:
                break;
        }
        return generatorTemplatesMapper.updateById(userTemplates);
    }

    /**
     * 根据模板ID，获取模板内容
     *
     * @param templateId 模板ID
     * @param type 指定业务层类型
     * @return java.lang.Object
     * @Author HuQi
     * @create 2021-08-03 09:13
     */
    @Override
    public Object getTemplates(String templateId, String type) {

        GeneratorTemplatesDO selectOne = getUserTemplatesById(templateId);
        Map<String, Object> rs = new HashMap<>(12);
        if (C_ALL.equals(type)){
            rs.put(C_CONTROLLER, base64ToString(selectOne.getControllerContent()));
            rs.put(IS_USE_STR + C_CONTROLLER, selectOne.getIsUseController());
            rs.put(C_ENTITY, base64ToString(selectOne.getEntityContent()));
            rs.put(IS_USE_STR + C_ENTITY, selectOne.getIsUseEntity());
            rs.put(C_MAPPER, base64ToString(selectOne.getMapperContent()));
            rs.put(IS_USE_STR + C_MAPPER, selectOne.getIsUseMapper());
            rs.put(C_SERVICE, base64ToString(selectOne.getServiceContent()));
            rs.put(IS_USE_STR + C_SERVICE, selectOne.getIsUseService());
            rs.put(C_SERVICE_IMPL, base64ToString(selectOne.getServiceImplContent()));
            rs.put(IS_USE_STR + C_SERVICE_IMPL, selectOne.getIsUseServiceImpl());
            rs.put(C_VAR, base64ToString(selectOne.getTemplatesVarJson()));
            rs.put(IS_USE_STR + "Templates" + C_VAR, selectOne.getIsUseTemplatesVar());
            return rs;
        }

        switch (type){
            case C_CONTROLLER:
                rs.put(C_CONTROLLER, base64ToString(selectOne.getControllerContent()));
                rs.put(IS_USE_STR + C_CONTROLLER, selectOne.getIsUseController());
                break;
            case C_MAPPER:
                rs.put(C_MAPPER, base64ToString(selectOne.getMapperContent()));
                rs.put(IS_USE_STR + C_MAPPER, selectOne.getIsUseMapper());
                break;
            case C_ENTITY:
                rs.put(C_ENTITY, base64ToString(selectOne.getEntityContent()));
                rs.put(IS_USE_STR + C_ENTITY, selectOne.getIsUseEntity());
                break;
            case C_SERVICE:
                rs.put(C_SERVICE, base64ToString(selectOne.getServiceContent()));
                rs.put(IS_USE_STR + C_SERVICE, selectOne.getIsUseService());
                break;
            case C_SERVICE_IMPL:
                rs.put(C_SERVICE_IMPL, base64ToString(selectOne.getServiceImplContent()));
                rs.put(IS_USE_STR + C_SERVICE_IMPL, selectOne.getIsUseServiceImpl());
                break;
            case C_VAR:
                rs.put(C_VAR, base64ToString(selectOne.getTemplatesVarJson()));
                rs.put(IS_USE_STR + "Templates" + C_VAR, selectOne.getIsUseTemplatesVar());
                break;
            default:
                break;
        }
        return rs;
    }

    /**
     * 用户添加模板，新增模板时只需指定模板名称及模板是否开放（所有用户可用）
     *
     * @param templateName 模板名
     * @param isPublic 开放标识
     * @return org.jeecg.modules.form.entity.GeneratorTemplatesDO 返回代码生成器模板实体类，包含新增的模板ID
     * @Author HuQi
     * @create 2021-08-03 09:15
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public GeneratorTemplatesDO addTemplates(String templateName, Integer isPublic) {
        GeneratorTemplatesDO templatesDO = new GeneratorTemplatesDO();
        templatesDO.setTemplateName(templateName);
        templatesDO.setIsPublic(isPublic);
        generatorTemplatesMapper.insert(templatesDO);
        return templatesDO;
    }

    /**
     * 根据模板ID，删除模板，只能删除自己的模板
     *
     * @param templateId 模板ID
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:17
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteTemplates(String templateId) {
        LoginUser user = getUser();
        LambdaQueryWrapper<GeneratorTemplatesDO> templatesQ = Wrappers.lambdaQuery();
        templatesQ.eq(GeneratorTemplatesDO::getId, templateId).eq(GeneratorTemplatesDO::getCreateBy, user.getUsername());
        return generatorTemplatesMapper.delete(templatesQ);
    }

    /**
     * 获取开放的模板名称、模板ID和模板创建者
     *
     * @param page 分页参数
     * @return java.util.List<org.jeecg.modules.form.entity.GeneratorTemplatesDO> 返回代码生成器模板实体类的列表
     * @Author HuQi
     * @create 2021-08-03 09:18
     */
    @Override
    public List<GeneratorTemplatesDO> getPublicTemplates(Page<GeneratorTemplatesDO> page) {
        LambdaQueryWrapper<GeneratorTemplatesDO> templatesQ = Wrappers.lambdaQuery();
        templatesQ.eq(GeneratorTemplatesDO::getIsPublic, 1)
                .select(GeneratorTemplatesDO::getTemplateName, GeneratorTemplatesDO::getId, GeneratorTemplatesDO::getCreateBy)
                .orderByAsc(GeneratorTemplatesDO::getUpdateTime);
        Page<GeneratorTemplatesDO> selectPage = generatorTemplatesMapper.selectPage(page, templatesQ);
        return selectPage.getRecords();
    }

    /**
     * 获取用户个人的模板
     *
     * @param page 分页参数
     * @return java.util.List<org.jeecg.modules.form.entity.GeneratorTemplatesDO> 返回代码生成器模板实体类的列表
     * @Author HuQi
     * @create 2021-08-03 09:19
     */
    @Override
    public List<GeneratorTemplatesDO> getUserTemplates(Page<GeneratorTemplatesDO> page) {
        LoginUser user = getUser();
        LambdaQueryWrapper<GeneratorTemplatesDO> templatesQ = Wrappers.lambdaQuery();
        templatesQ.eq(GeneratorTemplatesDO::getCreateBy, user.getUsername())
                .orderByAsc(GeneratorTemplatesDO::getUpdateTime);
        Page<GeneratorTemplatesDO> selectPage = generatorTemplatesMapper.selectPage(page, templatesQ);
        return selectPage.getRecords();
    }

    /**
     * 设置模板的公有（开放）或私有
     *
     * @param templateId 模板DI
     * @param isPublic 公开标识
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:21
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int setPublicTemplates(String templateId, Integer isPublic) {
        GeneratorTemplatesDO selectOne = getUserTemplatesById(templateId);
        selectOne.setIsPublic(isPublic);
        return generatorTemplatesMapper.updateById(selectOne);
    }

    /**
     * 通过模板ID获取模板所有信息。可以查询用户所有的模板信息，或公有的模板信息。
     *
     * @param templateId 模板ID
     * @return org.jeecg.modules.form.entity.GeneratorTemplatesDO 返回代码生成器模板实体类
     * @Author HuQi
     * @create 2021-08-03 09:21
     */
    @Override
    public GeneratorTemplatesDO getUserTemplatesById(String templateId) {
        LoginUser user = getUser();
        LambdaQueryWrapper<GeneratorTemplatesDO> templatesQ = Wrappers.lambdaQuery();
        templatesQ.eq(GeneratorTemplatesDO::getId, templateId).eq(GeneratorTemplatesDO::getCreateBy, user.getUsername())
                .or()
                .eq(GeneratorTemplatesDO::getId, templateId).eq(GeneratorTemplatesDO::getIsPublic, 1);
        GeneratorTemplatesDO selectOne = generatorTemplatesMapper.selectOne(templatesQ);
        if (selectOne==null){
            ServiceUtils.throwException(MESSAGE_ERROR_NOT_FOUND_TEMPLATES);
        }
        return selectOne;
    }
}
