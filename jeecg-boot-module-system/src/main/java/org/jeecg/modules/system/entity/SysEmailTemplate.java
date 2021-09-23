package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysEmailTemplate {
    /**
     * 邮件模板id
     */
    @TableId(type = IdType.ASSIGN_ID)
    @TableField(value = "template_id")
    private String templateId;
    /**邮件模板名称
     *
     */
    @TableField(value = "template_name")
    private String templateName;
    /**
     * 邮件模板描述
     */
    @TableField(value = "template_describe")
    private String templateDescribe;
    /**邮件网页
     *
     */
    @TableField(value = "email_content")
    private String emailHtml;


    public SysEmailTemplate(String templateId, String templateName, String templateDescribe, String emailHtml) {
        this.templateId=templateId;
        this.templateName=templateName;
        this.templateDescribe=templateDescribe;
        this.emailHtml=emailHtml;
    }
    public SysEmailTemplate( String templateName, String templateDescribe, String emailHtml) {
        this.templateName=templateName;
        this.templateDescribe=templateDescribe;
        this.emailHtml=emailHtml;
    }

    public SysEmailTemplate() {
    }
}
