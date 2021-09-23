package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysEmailTemplate;
import org.jeecg.modules.system.mapper.SysEmailTemplateMapper;
import org.jeecg.modules.system.service.ISysEmailTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;


/**
 * @Description: 邮件模板表
 * @Author: jeecg-boot
 * @Date:   2020-08-13
 * @Version: V1.0
 */
@Service
public class SysEmailTemplateServiceImpl extends ServiceImpl<SysEmailTemplateMapper, SysEmailTemplate> implements ISysEmailTemplateService {

    @Autowired
   SysEmailTemplateMapper SysEmailTemplateMapper;

    /**
     * 邮件模板添加
     * @param
     * @return
     */
    @Override
    @Transactional
    public int AddEmailTemplate(SysEmailTemplate sysEmailTemplate){
        //判断名字是否重复
        String emailTemplate=sysEmailTemplate.getTemplateName();
        SysEmailTemplate sysEmailTemplate1=SysEmailTemplateMapper.selectOne(new LambdaQueryWrapper<SysEmailTemplate>()
                .eq(SysEmailTemplate::getTemplateName,emailTemplate));
        if (!Objects.isNull(sysEmailTemplate1)){
            return -1;
        }
        int result=SysEmailTemplateMapper.insert(sysEmailTemplate);
        return result;
    }

    /**
     * 邮件模板修改
     * @param
     * @return
     */
    @Override
    @Transactional
    public int UpdateEmailTemplate(SysEmailTemplate sysEmailTemplate){
       int result=SysEmailTemplateMapper.updateById(sysEmailTemplate);
       return result;
    }

    /**
     * 邮件模板删除
     * @param templateId
     * @return
     */
    @Override
    @Transactional
    public int deleteEmailTemplate(String templateId){
        int result=SysEmailTemplateMapper.delete(new LambdaQueryWrapper<SysEmailTemplate>()
        .eq(SysEmailTemplate::getTemplateId,templateId));
        return result;
    }

    /**
     * 通过模板id查询邮件模板内容
     * @param templateId
     * @return
     */
    @Override
    @Transactional
    public SysEmailTemplate selectEmailTemplateById(String templateId){
        SysEmailTemplate sysEmailTemplate=SysEmailTemplateMapper.selectOne(new LambdaQueryWrapper<SysEmailTemplate>()
        .eq(SysEmailTemplate::getTemplateId,templateId));
        return sysEmailTemplate;
    }

    /**
     * 关键词模糊查询
     * @param KeyWord
     * @return
     */
    @Override
    @Transactional
    public List<SysEmailTemplate> selectEmailTemplateByKeyWord( String KeyWord){
        List<SysEmailTemplate> sysEmailTemplates=SysEmailTemplateMapper.selectList(new LambdaQueryWrapper<SysEmailTemplate>()
        .like(SysEmailTemplate::getTemplateName,KeyWord));
        return sysEmailTemplates;
    }
}
