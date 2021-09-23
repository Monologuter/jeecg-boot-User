package org.jeecg.modules.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.system.entity.SysRoleForm;
import org.jeecg.modules.system.mapper.SysRoleFormMapper;
import org.jeecg.modules.system.service.ISysRoleFormService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author 胡文晓
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/1813:30
 */
@Service
public class SysRoleFormServiceImpl extends ServiceImpl<SysRoleFormMapper, SysRoleForm>implements ISysRoleFormService {

    @Autowired
    private SysRoleFormMapper sysRoleFormMapper;

    @Override
    @Transactional
    public void addSysRoleForm(SysRoleForm sysRoleForm){
        sysRoleFormMapper.insert(sysRoleForm);
    }

    @Override
    public List<FormDataDO> queryFormDataDO(SysRoleForm sysRoleForm) {
        return sysRoleFormMapper.queryFormDataDO(sysRoleForm.getRoleId(),
                                                    sysRoleForm.getFormId(),
                                                    sysRoleForm.getFormKey(),
                                                    sysRoleForm.getFormValue());
    }
}
