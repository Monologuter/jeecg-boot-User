package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.entity.FormSysPermissionDO;

/**
 * 表单菜单Service层接口
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 13:40
 */
public interface FormSysPermissionService extends IService<FormSysPermissionDO> {
    void updateFormPermission(FormSysPermissionDO formSysPermissionDO);
}
