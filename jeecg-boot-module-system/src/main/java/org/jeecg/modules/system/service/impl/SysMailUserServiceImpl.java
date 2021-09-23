package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.SysMailUser;
import org.jeecg.modules.system.mapper.SysMailUserMapper;
import org.jeecg.modules.system.service.ISysMailUserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.Properties;


@Service
@Slf4j
public class SysMailUserServiceImpl extends ServiceImpl<SysMailUserMapper, SysMailUser> implements ISysMailUserService {
    /**
     * 发送邮件
     *
     * @param username
     * @param subject
     * @param recipients
     * @param multipartfiles
     * @param webpage
     * @return
     * @throws Exception
     */
    @Override
    public Boolean sendMail(String username, String subject, String recipients,
                            MultipartFile[] multipartfiles, String webpage) throws Exception {
        //邮箱配置
        SysMailUser mail = queryUserConfig(username);
        Message message = config(mail, subject, recipients);
        //设置邮件内容
        Multipart multipart = new MimeMultipart("mixed");
        BodyPart html = sendWebpage(webpage);
        multipart.addBodyPart(html);
        multipart = attachments(multipartfiles, multipart);
        message.setContent(multipart);
        //发送邮件
        try {
            Transport.send(message);
            //删除本地文件
            delteTempFile(multipartfiles);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    /**
     * 查询邮箱账号信息
     *
     * @param username
     * @return SysMail
     */
    public SysMailUser queryUserConfig(String username) {
        LambdaQueryWrapper<SysMailUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMailUser::getUsername, username);
        SysMailUser mail = baseMapper.selectOne(wrapper);
        return mail;
    }

    /**
     * 修改用户配置信息
     *
     * @param sysMailUser
     * @return
     */
    @Override
    public Integer updateSysMailUser(SysMailUser sysMailUser) {
        return baseMapper.updateById(sysMailUser);
    }

    /**
     * 添加发送内容为html网页
     *
     * @param webpage
     * @return BodyPart
     */
    public BodyPart sendWebpage(String webpage) {
        // 创建一个包含HTML内容的MimeBodyPart
        BodyPart html = new MimeBodyPart();
        try {
            html.setContent(webpage, "text/html; charset=utf-8");
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return html;
    }

    /**
     * 添加附件
     *
     * @param multipartfiles
     * @param multipart
     * @return Multipart
     * @throws Exception
     */

    public Multipart attachments(MultipartFile[] multipartfiles, Multipart multipart) throws Exception {
        for (MultipartFile multipartfile : multipartfiles) {
            File file = new File(multipartfile.getOriginalFilename());
            InputStream ins = null;
            ins = multipartfile.getInputStream();
            inputStreamToFile(ins, file);
            ins.close();
            BodyPart attach = new MimeBodyPart();
            DataHandler dh = new DataHandler(new FileDataSource((File) file));
            attach.setDataHandler(dh);
            attach.setFileName(MimeUtility.encodeWord(file.getName()));
            multipart.addBodyPart(attach);
        }

        return multipart;
    }

    /**
     * 删除本地临时文件
     *
     * @param multipartfiles
     */
    public Boolean delteTempFile(MultipartFile[] multipartfiles) {
        for (MultipartFile multipartfile : multipartfiles) {
            File file = new File(multipartfile.getOriginalFilename());
            if (file != null) {
                File del = new File(file.toURI());
                Boolean style = del.delete();
                return style;
            }
        }
        return false;
    }

    /**
     * 获取文件流
     *
     * @param ins
     * @param file
     */
    private void inputStreamToFile(InputStream ins, File file) throws IOException {
        OutputStream os = new FileOutputStream(file);
        try {
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            os.close();
            ins.close();
        }
    }


    /**
     * 邮箱配置
     *
     * @param mail
     * @return Message
     */
    public Message config(SysMailUser mail, String subject, String recipients) throws Exception {
        //邮件配置
        Properties props = new Properties();
        final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
        props.setProperty("mail.smtp.host", mail.getHost());
        props.setProperty("mail.smtp.auth", "true");
        props.setProperty("mail.smtp.port", "465");
        System.setProperty("mail.mime.splitlongparameters", "false");
        props.setProperty("mail.smtp.socketFactory.class", SSL_FACTORY);
        props.setProperty("mail.debug", "true");
        props.setProperty("mail.smtp.connectiontimeout", "5000");
        props.setProperty("mail.smtp.timeout", "5000");
        props.setProperty("mail.smtp.writetimeout", "5000");
        Session session = Session.getInstance(props, new Authenticator() {
            private String userName = mail.getUsername();
            private String passWord = mail.getPassword();

            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(userName, passWord);
            }
        });
        Message message = new MimeMessage(session);
        //设置发送人
        Address from = new InternetAddress(mail.getUsername());
        message.setFrom(from);
        //设置接收人
        Address[] sendTo = InternetAddress.parse(recipients);
        message.setRecipients(MimeMessage.RecipientType.TO, sendTo);
        //设置邮件主题
        message.setSubject(subject);
        return message;
    }

    /**
     * 添加发送人
     *
     * @param sysMailUser
     * @return Integer
     */
    @Override
    public Integer addUser(SysMailUser sysMailUser) {
        return baseMapper.insert(sysMailUser);
    }

    /**
     * 删除发送人
     *
     * @param username
     * @return Integer
     */
    @Override
    public Integer deleteUser(String username) {
        LambdaQueryWrapper<SysMailUser> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMailUser::getUsername, username);
        return baseMapper.delete(wrapper);
    }

    /**
     * @param sysMailUser
     * @return Folder
     * @throws MessagingException
     */
    public Folder acceptConfiguration(SysMailUser sysMailUser) throws MessagingException {
        Properties properties = new Properties();
        log.info("hostname:" + sysMailUser.getHost());
        properties.setProperty("mail.smtp.host", "pop.qq.com");
        //收件人smtp服务器地址
        properties.setProperty("mail.transport.protocol", "pop3");
        //获取连接
        Session session = Session.getDefaultInstance(properties);
        //是否开启debug
        session.setDebug(false);
        Store store = session.getStore("pop3");
        //登录pop3服务器认证
        store.connect("pop.qq.com", sysMailUser.getUsername(), sysMailUser.getPassword());
        Folder folder = store.getFolder("inbox");
        folder.open(Folder.READ_WRITE);
        return folder;
    }
}
