package org.jeecg.modules.common.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Mybatis-plus自动填充功能配置类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/3 13:35
 */
@Slf4j
@Component
public class FieldFillHandler implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Date date = new Date();
        strictInsertFill(metaObject, "createBy", String.class, loginUser.getUsername());
        strictInsertFill(metaObject, "updateBy", String.class, loginUser.getUsername());
        strictInsertFill(metaObject, "createTime", Date.class, date);
        strictInsertFill(metaObject, "updateTime", Date.class, date);
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        LoginUser loginUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        strictUpdateFill(metaObject, "updateBy", String.class, loginUser.getUsername());
        strictUpdateFill(metaObject, "updateTime", Date.class, new Date());
    }
}
