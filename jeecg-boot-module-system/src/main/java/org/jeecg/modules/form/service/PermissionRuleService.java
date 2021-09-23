package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.entity.PermissionRuleDo;

import java.util.List;

public interface PermissionRuleService extends IService<PermissionRuleDo> {
    void savePermissionRule(PermissionRuleDo formRoleDo);

    List<PermissionRuleDo> listPermissionRule(String permissionId);

    void deletePermissionRule(String id);
}
