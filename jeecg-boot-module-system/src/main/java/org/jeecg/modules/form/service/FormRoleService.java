package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.entity.FormRoleDo;

import java.util.List;

/**
 * 表单角色Service层接口类
 *
 * @author XuDeQing
 * @create 2021-08-31 16:59
 * @modify 2021-08-31 16:59
 */
public interface FormRoleService extends IService<FormRoleDo> {

    /**
     * 保存表单角色信息
     *
     * @param formRoleDo 表单角色DO对象
     * @return org.jeecg.modules.form.entity.FormRoleDo 返回保存后的表单角色DO对象
     */
    FormRoleDo saveFormRole(FormRoleDo formRoleDo);

    /**
     * 根据表单ID查询相关的表单角色信息
     *
     * @param formId 表单ID
     * @return java.util.List<org.jeecg.modules.form.entity.FormRoleDo> 表单角色DO数据列表
     */
    List<FormRoleDo> listFormRoleList(String formId);

    /**
     * 根据表单角色Id删除表单角色信息
     *
     * @param id 表单角色ID
     */
    void deleteFormRole(String id);
}
