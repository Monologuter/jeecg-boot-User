package org.jeecg.modules.system.service;

import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.system.entity.SysRoleForm;

import java.util.List;

/**
 * @author 胡文晓
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/1813:28
 */
public interface ISysRoleFormService {
    /**
     * 添加筛选规则
     *
     * @param sysRoleForm
     */
    void addSysRoleForm(SysRoleForm sysRoleForm);

    /**
     * 根据条件筛选数据
     * @param sysRoleForm
     * @return
     */
    List<FormDataDO> queryFormDataDO(SysRoleForm sysRoleForm);

}


