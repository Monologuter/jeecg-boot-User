package org.jeecg.modules.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sun.mail.pop3.POP3Folder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.system.entity.SysMail;
import org.jeecg.modules.system.entity.SysMailUser;
import org.jeecg.modules.system.service.ISysMailService;
import org.jeecg.modules.system.service.ISysMailUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * 邮件配置管理
 */
@RestController
@RequestMapping("/sys/mail")
@Slf4j
@Api(tags = "邮件管理")
public class SysMailController {
    @Autowired
    private ISysMailUserService sysMailUserService;

    @Autowired
    private ISysMailService sysMailService;

    @PostMapping(value = "/send")
    @ApiOperation("发送邮件")
    public Result<String> sendMail(@RequestParam(name = "username") String username,
                                   @RequestParam(name = "subject", required = false) String subject,
                                   @RequestParam(name = "recipients") String recipients,
                                   @RequestParam(name = "file", required = false) MultipartFile[] multipartfiles,
                                   @RequestParam(name = "webpage", required = false) String webpage) throws Exception {
        Boolean sendMail = sysMailUserService.sendMail(username, subject, recipients, multipartfiles, webpage);
        Integer saveMail = sysMailService.saveMail(username, subject, recipients, webpage);
        if (sendMail) {
            if (saveMail != 0) {
                return Result.OK();
            }
        }
        return Result.Error("发送失败");


    }

    @GetMapping(value = "/userConfig")
    @ApiOperation("发送人配置查询")
    public Result<SysMailUser> queryUserConfig(@RequestParam(name = "email") String mail) {
        SysMailUser mailUser = sysMailUserService.queryUserConfig(mail);
        if (mailUser != null) {
            return Result.OK(mailUser);
        }
        return Result.Error("查询失败");
    }

    @GetMapping(value = "/userList")
    @ApiOperation("发送人列表查询")
    public Result<IPage<SysMailUser>> queryPageList(
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize
    ) {
        Page<SysMailUser> page = new Page<SysMailUser>(pageNo, pageSize);
        IPage<SysMailUser> pageList = sysMailUserService.page(page);
        return Result.OK(pageList);
    }

    @PutMapping(value = "userEdit")
    @ApiOperation("发送人配置修改")
    public Result<Integer> userEdit(@RequestBody SysMailUser sysMailUser) {
        Integer mailUser = sysMailUserService.updateSysMailUser(sysMailUser);
        if (mailUser == 1) {
            return Result.OK();
        }
        return Result.Error("修改失败");
    }

    @PostMapping(value = "/addUser")
    @ApiOperation("添加发送人")
    public Result<Integer> addUser(@RequestBody SysMailUser sysMailUser) {

        if (sysMailUserService.addUser(sysMailUser) != 0) {
            return Result.OK();
        }
        return Result.Error("添加失败");
    }


    @DeleteMapping(value = "deleteUser")
    @ApiOperation("删除发送人")
    public Result<Integer> deleteUser(@RequestParam(name = "username") String username) {

        if (sysMailUserService.deleteUser(username) != 0) {
            return Result.OK();
        }
        return Result.Error("删除失败");
    }

    @GetMapping(value = "inbox")
    @ApiOperation("发件箱")
    public Result<IPage<SysMail>> Inbox(@RequestParam(name = "email", required = false) String mail,
                                            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {
        if (mail == null) {
            LambdaQueryWrapper<SysMail> queryWrapper = new LambdaQueryWrapper<SysMail>();
            queryWrapper.orderByDesc(SysMail::getTime);
            queryWrapper.eq(SysMail::getType, "1");
            Page<SysMail> page = new Page<SysMail>(pageNo, pageSize);
            IPage<SysMail> pageList = sysMailService.page(page, queryWrapper);
            return Result.OK(pageList);
        } else {
            LambdaQueryWrapper<SysMail> queryWrapper = new LambdaQueryWrapper<SysMail>();
            queryWrapper.eq(SysMail::getUsername, mail);
            queryWrapper.eq(SysMail::getType, "1");
            queryWrapper.orderByDesc(SysMail::getTime);
            Page<SysMail> page = new Page<SysMail>(pageNo, pageSize);
            IPage<SysMail> pageList = sysMailService.page(page, queryWrapper);
            return Result.OK(pageList);
        }

    }

    @DeleteMapping(value = "deleteInboxMail")
    @ApiOperation("删除发件箱邮件")
    public Result<Integer> deleteInboxMail(@RequestParam(name = "id") String id) {
        Integer inboxMail = sysMailService.deleteInboxMail(id);
        if (inboxMail == 1) {
            return Result.OK();
        }
        return Result.Error("删除失败");
    }

    @DeleteMapping(value = "deleteBatch")
    @ApiOperation("批量删除发件箱邮件")
    public Result<Integer> deleteBatch(@RequestParam(name = "ids") String ids) {
        if (ids == null || "".equals(ids.trim())) {
            return Result.Error("参数不识别！");
        } else {
            sysMailService.removeByIds(Arrays.asList(ids.split(",")));
            return Result.OK();
        }
    }

    @GetMapping(value = "getEmailByUserName")
    @ApiOperation("根据邮件人查所有邮件")
    public Result<IPage<SysMail>> getEmailByUserName(@RequestParam(name = "username") String username,
                                                     @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                     @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize)
            throws MessagingException, IOException {

        List<SysMail> sysMails = new ArrayList<>();
        SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //获取邮件人所有信息
        SysMailUser sysMailUser = sysMailUserService.queryUserConfig(username);

        if (Objects.isNull(sysMailUser)) {
            return Result.Error("邮箱为找到");
        }
        //获取收邮件文件
        Folder folder = sysMailUserService.acceptConfiguration(sysMailUser);
        //获取邮箱信息
        Message[] messages = folder.getMessages();
        if (folder instanceof POP3Folder) {
            POP3Folder pop3Folder = (POP3Folder) folder;
            for (Message message : messages) {
                SysMail sysMail = new SysMail();
                sysMail.setRecipients(username);
                //获取发送人邮箱
                String from = InternetAddress.toString(message.getFrom());
                log.info("from: " + from);
                //获取邮箱id
                String uid = pop3Folder.getUID(message);
                log.info("邮箱编号:" + uid);
                sysMail.setId(uid);
                sysMail.setSubject(message.getSubject());
                sysMail.setType("0");
                sysMail.setState("0");
                sysMail.setTime(date.format(message.getSentDate()));
                String mailstr = from.substring(from.indexOf("<") + 1, from.length());
                if (mailstr.lastIndexOf(">") == -1) {
                    sysMail.setUsername(mailstr);
                } else {
                    sysMail.setUsername(mailstr.substring(0, mailstr.lastIndexOf(">")));
                }
                SysMail sysMail12 = sysMailService.getById(uid);
                StringBuffer bodyText = new StringBuffer();
                if (message.isMimeType("text/plain") || message.isMimeType("text/html")) {
                    bodyText.append(message.getContent());
                    sysMail.setContents(bodyText.toString());
                } else {
                    Multipart multipart = (Multipart) message.getContent();
                    BodyPart bodyPart = multipart.getBodyPart(0);
                    bodyText.append(bodyPart.getContent());
                    sysMail.setContents(bodyText.toString());
                }
                if (Objects.isNull(sysMail12)) {
                    sysMails.add(sysMail);
                }
            }
            if (null != sysMails || sysMails.size() != 0) {
                sysMailService.saveBatch(sysMails);
            }
        }
        //收件人的全部邮箱
        Page<SysMail> page = new Page<SysMail>(pageNo, pageSize);
        IPage<SysMail> pageList = sysMailService.page(page, new LambdaQueryWrapper<SysMail>()
                .eq(SysMail::getRecipients, username).orderByDesc(SysMail::getTime));
        return Result.OK(pageList);
    }


}
