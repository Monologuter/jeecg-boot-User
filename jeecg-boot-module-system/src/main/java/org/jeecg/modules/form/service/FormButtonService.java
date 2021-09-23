package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.entity.PermissionButtonDo;

import java.util.List;

public interface FormButtonService extends IService<PermissionButtonDo> {
    PermissionButtonDo saveButton(PermissionButtonDo button);

    void deleteButton(String id);

    List<PermissionButtonDo> listButtons(String permissionId);
}
