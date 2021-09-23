package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.common.constant.OperationErrorMessageConstant;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.PermissionButtonDo;
import org.jeecg.modules.form.mapper.FormButtonMapper;
import org.jeecg.modules.form.service.FormButtonService;
import org.springframework.stereotype.Service;

import java.util.List;

@DS("form")
@Service
public class FormButtonServiceImpl extends ServiceImpl<FormButtonMapper, PermissionButtonDo> implements FormButtonService {
    @Override
    public PermissionButtonDo saveButton(PermissionButtonDo button) {
        ServiceUtils.throwIfFailed(save(button), OperationErrorMessageConstant.SAVE_FAILED);
        return button;
    }

    @Override
    public void deleteButton(String id) {
        ServiceUtils.throwIfFailed(() -> removeById(id), OperationErrorMessageConstant.DELETE_FAILED);
    }

    @Override
    public List<PermissionButtonDo> listButtons(String permissionId) {
        return lambdaQuery().eq(PermissionButtonDo::getPermissionId,permissionId).list();
    }
}
