package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysApplicationPermission;

public interface SysApplicationPermissionMapper extends BaseMapper<SysApplicationPermission> {
    // 批量删除应用与菜单的关系
    void deleteBatchApplicationPermissionRelation(@Param("applicationIdArray") String[] applicationIdArray);
}
