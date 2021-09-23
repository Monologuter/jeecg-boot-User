package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysDataSource;
import org.jeecg.modules.system.model.SysDataSourceModel;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
public interface ISysDataSourceService extends IService<SysDataSource> {

    //分域查询列表
    List<SysDataSourceModel> queryClassList();

    //分域字典
    List<SysDataSourceModel> queryClassDict();

    /**
     * 根据sql查询数据 连接其他数据源
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeSelectBySql(String sql, String dbKey) throws SQLException;

    /**
     * 根据sql查询数据 本地数据库
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeSelectBySqlNative(String sql);

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    List<Map<String, Object>> getDbTableList(String dbKey);

    /**
     * 获取数据源表内字段
     *
     * @param dbKey
     * @param tableName
     * @return
     */
    List<Map<String, Object>> getDbTableItems(String dbKey, String tableName);
}
