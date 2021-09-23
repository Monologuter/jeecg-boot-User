package org.jeecg.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysEmailTemplate;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


/**
 * @Description: 邮件模板管理
 * @Author: jeecg-boot
 * @Date:   2020-08-13
 * @Version: V1.0
 */
public interface ISysEmailTemplateService extends IService<SysEmailTemplate> {

    /**
     * 添加邮件模板
     * @param
     * @return
     */
    int AddEmailTemplate(SysEmailTemplate sysEmailTemplate );

    /**
     * 修改邮件模板
     * @param
     * @return
     */
    int UpdateEmailTemplate(SysEmailTemplate sysEmailTemplate);

    /**
     * 删除邮件模板
     * @param templateId
     * @return
     */
    int deleteEmailTemplate(String templateId);

    /**
     * 通过id查询邮件模板
     * @param templateId
     * @return
     */
    SysEmailTemplate selectEmailTemplateById( String templateId);

    /**
     * 关键词模糊查询
     * @param KeyWord
     * @return
     */
    List<SysEmailTemplate> selectEmailTemplateByKeyWord( String KeyWord);

}
