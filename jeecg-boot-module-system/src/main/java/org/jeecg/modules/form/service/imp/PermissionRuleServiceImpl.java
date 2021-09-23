package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.common.constant.OperationErrorMessageConstant;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.PermissionRuleDo;
import org.jeecg.modules.form.mapper.PermissionRuleMapper;
import org.jeecg.modules.form.service.PermissionRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

@DS("form")
@Service
public class PermissionRuleServiceImpl extends ServiceImpl<PermissionRuleMapper, PermissionRuleDo> implements PermissionRuleService {

    @Override
    public void savePermissionRule(PermissionRuleDo permissionRuleDo) {
        ServiceUtils.throwIfFailed(save(permissionRuleDo), OperationErrorMessageConstant.SAVE_FAILED);
    }

    @Override
    public List<PermissionRuleDo> listPermissionRule(String permissionId) {
        return lambdaQuery().eq(PermissionRuleDo::getPermissionId,permissionId).list();
    }

    @Override
    public void deletePermissionRule(String id) {
        ServiceUtils.throwIfFailed(removeById(id),OperationErrorMessageConstant.DELETE_FAILED);
    }
}
