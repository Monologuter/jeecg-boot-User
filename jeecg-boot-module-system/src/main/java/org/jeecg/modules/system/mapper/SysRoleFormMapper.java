package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.system.entity.SysRoleForm;

import java.util.List;

/**
 * @author 胡文晓
 * @Title:
 * @Package
 * @Description:
 * @date 2021/8/1813:36
 */
@Mapper
public interface SysRoleFormMapper extends BaseMapper<SysRoleForm> {

    /**
     * 根据条件查询出相关数据
     * @param roleId
     * @param formId
     * @param formKey
     * @param formValue
     * @return
     */
    List<FormDataDO> queryFormDataDO(String roleId,String formId,String formKey,String formValue);
}
