package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysMailUser;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.Folder;
import javax.mail.MessagingException;

public interface ISysMailUserService extends IService<SysMailUser> {
    Boolean sendMail(String username, String subject, String recipients, MultipartFile[] multipartfiles, String webpage) throws Exception;

    Integer addUser(SysMailUser sysMailUser);

    Integer deleteUser(String username);

    SysMailUser queryUserConfig(String mail);

    Folder acceptConfiguration(SysMailUser sysMailUser) throws MessagingException;

    Integer updateSysMailUser(SysMailUser sysMailUser);
}
