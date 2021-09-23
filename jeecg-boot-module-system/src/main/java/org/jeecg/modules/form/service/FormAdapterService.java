package org.jeecg.modules.form.service;

import org.jeecg.modules.form.dto.FormSysPermissionDTO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;

import java.util.List;

/**
 * 表单关联菜单栏Service层接口
 *
 * @author XuDeQing
 * @create 2021-08-27 9:57
 * @modify 2021-08-27 9:57
 */
public interface FormAdapterService {

    /**
     * 删除所有关联的菜单
     *
     * @param list 关联的菜单对象的列表
     */
    void deleteSysPermissionsOfForm(List<FormSysPermissionDO> list);

    /**
     * 保存关联的菜单信息
     *
     * @param formSysPermissionDTO 关联的菜单对象
     */
    void saveFormSysPermission(FormSysPermissionDTO formSysPermissionDTO);
}
