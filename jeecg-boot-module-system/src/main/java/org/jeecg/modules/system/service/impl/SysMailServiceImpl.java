package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.system.entity.SysMail;
import org.jeecg.modules.system.mapper.SysMailMapper;
import org.jeecg.modules.system.service.ISysMailService;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author ChenXiong
 * @Date 2021/8/27 10:27
 */
@Service
public class SysMailServiceImpl extends ServiceImpl<SysMailMapper, SysMail> implements ISysMailService {
    /**
     * 保存发送的邮件
     *
     * @param username
     * @param subject
     * @param recipients
     * @param webpage
     * @return
     */
    @Override
    public Integer saveMail(String username, String subject, String recipients, String webpage) {
        String type = "1";
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = dateFormat.format(date);
        SysMail mail = new SysMail(username, subject, recipients, webpage, type, time);
        return baseMapper.insert(mail);
    }

    /**
     * 删除发件箱邮件
     *
     * @param id
     * @return
     */
    @Override
    public Integer deleteInboxMail(String id) {
        return baseMapper.deleteById(id);
    }

}
