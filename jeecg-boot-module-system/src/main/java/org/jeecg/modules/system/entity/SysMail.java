package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Author ChenXiong
 * @Date 2021/8/26 16:57
 */
@Data
@TableName("sys_mail")
@NoArgsConstructor
public class SysMail {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * 邮箱账号
     */
    private String username;
    /**
     * 邮箱主题
     */
    private String subject;
    /**
     * 邮箱内容
     */
    private String contents;
    /**
     * 邮箱附件
     */
    private String attachment;
    /**
     * 收件人
     */
    private String recipients;
    /**
     * 邮箱发送或者接受时间
     */
    private String time;
    /**
     * 邮件状态(0是未读，1是已读)
     */
    private String state;
    /**
     * 邮件类型(0是接收，1是发送)
     */
    private String type;

    public SysMail(String username, String subject, String recipients, String contents, String type,String time) {
        this.username = username;
        this.subject = subject;
        this.recipients = recipients;
        this.contents = contents;
        this.type = type;
        this.time=time;
    }
}
