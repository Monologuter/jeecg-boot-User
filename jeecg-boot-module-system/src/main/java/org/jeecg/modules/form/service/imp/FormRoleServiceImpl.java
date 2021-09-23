package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.mapper.FormRoleMapper;
import org.jeecg.modules.form.service.FormRoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 表单角色Service层实现类
 *
 * @author XuDeQing
 * @create 2021-08-31 17:09
 * @modify 2021-08-31 17:09
 */
@Slf4j
@DS("form")
@Service
public class FormRoleServiceImpl extends ServiceImpl<FormRoleMapper, FormRoleDo> implements FormRoleService {

    /**
     * 保存表单角色信息
     *
     * @param formRoleDo 表单角色DO对象
     * @return org.jeecg.modules.form.entity.FormRoleDo 返回保存后的表单角色DO对象
     */
    @Override
    public FormRoleDo saveFormRole(FormRoleDo formRoleDo) {
        save(formRoleDo);
        return formRoleDo;
    }

    /**
     * 根据表单ID查询相关的表单角色信息
     *
     * @param formId 表单ID
     * @return java.util.List<org.jeecg.modules.form.entity.FormRoleDo> 表单角色DO数据列表
     */
    @Override
    public List<FormRoleDo> listFormRoleList(String formId) {
        return lambdaQuery().eq(FormRoleDo::getFormId,formId).list();
    }

    /**
     * 根据表单角色Id删除表单角色信息
     *
     * @param id 表单角色ID
     */
    @Override
    public void deleteFormRole(String id) {
        removeById(id);
    }
}
