package org.jeecg.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.system.entity.SysDataSource;

import java.util.List;
import java.util.Map;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
public interface SysDataSourceMapper extends BaseMapper<SysDataSource> {

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeSeleteBySql(@Param("sql") String sql);
}
