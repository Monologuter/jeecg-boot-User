package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("sys_mail_user")
public class SysMailUser implements Serializable {
    /**
     * 主键
     */
    @TableId(type = IdType.ASSIGN_ID)
    private String id;
    /**
     * smtp服务器
     */
    private String host;
    /**
     * 发送人
     */
    private String username;
    /**
     * 授权码
     */
    private String password;
//    /**编码格式*/
//    private java.lang.String default_encoding;
}
