package org.jeecg.modules.system.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysMail;


/**
 * @Author ChenXiong
 * @Date 2021/8/27 10:26
 */
public interface ISysMailService extends IService<SysMail> {
    Integer saveMail(String username, String subject, String recipients, String webpage);

    Integer deleteInboxMail(String id);
}
