package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.common.constant.OperationErrorMessageConstant;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.mapper.FormSysPermissionMapper;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.springframework.stereotype.Service;

/**
 * 表单菜单Service层实现类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/23 13:41
 */
@DS("form")
@Service
public class FormSysPermissionServiceImpl extends ServiceImpl<FormSysPermissionMapper, FormSysPermissionDO> implements FormSysPermissionService {
    @Override
    public void updateFormPermission(FormSysPermissionDO formSysPermissionDO) {
        ServiceUtils.throwIfFailed(updateById(formSysPermissionDO),
                OperationErrorMessageConstant.UPDATE_FAILED);
    }
}
