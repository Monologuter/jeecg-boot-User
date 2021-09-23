package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysRoleApplication;
/**
 * @Author Zhouhonghan
 */
public interface SysRoleApplicationMapper extends BaseMapper<SysRoleApplication> {
    // 批量删除角色与应用的关系
    void deleteBathRoleApplicationRelation(@Param("applicationIdArray") String[] applicationIdArray);
}
