package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.form.entity.GeneratorTemplatesDO;

import java.util.List;

/**
 * 代码生成器模板Service层接口类
 *
 * @author: HuQi
 * @date: 2021-07-31 17:34
 */
public interface GeneratorTemplatesService {

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
    int saveTemplatesContent(String templateId, String content, String type);

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
    int setTemplates(String templateId, String type, Integer num);

    /**
     * 根据模板ID，获取模板内容
     *
     * @param templateId 模板ID
     * @param type 指定业务层类型
     * @return java.lang.Object
     * @Author HuQi
     * @create 2021-08-03 09:13
     */
    Object getTemplates(String templateId, String type);

    /**
     * 用户添加模板，新增模板时只需指定模板名称及模板是否开放（所有用户可用）
     *
     * @param templateName 模板名
     * @param isPublic 开放标识
     * @return org.jeecg.modules.form.entity.GeneratorTemplatesDO 返回代码生成器模板实体类，包含新增的模板ID
     * @Author HuQi
     * @create 2021-08-03 09:15
     */
    GeneratorTemplatesDO addTemplates(String templateName, Integer isPublic);

    /**
     * 根据模板ID，删除模板，只能删除自己的模板
     *
     * @param templateId 模板ID
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:17
     */
    int deleteTemplates(String templateId);

    /**
     * 获取开放的模板名称、模板ID和模板创建者
     *
     * @param page 分页参数
     * @return java.util.List<org.jeecg.modules.form.entity.GeneratorTemplatesDO> 返回代码生成器模板实体类的列表
     * @Author HuQi
     * @create 2021-08-03 09:18
     */
    List<GeneratorTemplatesDO> getPublicTemplates(Page<GeneratorTemplatesDO> page);

    /**
     * 获取用户个人的模板
     *
     * @param page 分页参数
     * @return java.util.List<org.jeecg.modules.form.entity.GeneratorTemplatesDO> 返回代码生成器模板实体类的列表
     * @Author HuQi
     * @create 2021-08-03 09:19
     */
    List<GeneratorTemplatesDO> getUserTemplates(Page<GeneratorTemplatesDO> page);

    /**
     * 设置模板的公有（开放）或私有
     *
     * @param templateId 模板DI
     * @param isPublic 公开标识
     * @return int
     * @Author HuQi
     * @create 2021-08-03 09:21
     */
    int setPublicTemplates(String templateId, Integer isPublic);

    /**
     * 通过模板ID获取模板所有信息。可以查询用户所有的模板信息，或公有的模板信息。
     *
     * @param templateId 模板ID
     * @return org.jeecg.modules.form.entity.GeneratorTemplatesDO 返回代码生成器模板实体类
     * @Author HuQi
     * @create 2021-08-03 09:21
     */
    GeneratorTemplatesDO getUserTemplatesById(String templateId);
}
