package org.jeecg.modules.onlgraph.service;

import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysPermission;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_head图表配置表汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
public interface IOnlCfgraphHeadService extends IService<OnlCfgraphHeadDO> {

    /**
     * 通过id保存多表
     *
     * @param onlCfgraphHeadVo
     */
    void saveAll(OnlCfgraphHeadVO onlCfgraphHeadVo);

    /**
     * 通过ID删除多表
     *
     * @param id
     */
    void removeAll(String id);

    /**
     * 根据id查询图表配置
     *
     * @param id 主键ID
     * @return
     */
    OnlCfgraphHeadVO getOnlCfgraphHeads(String id);

    /**
     * 根据ID更新数据
     *
     * @param onlCfgraphHeadVo 主附表数据
     */
    void updateAllById(OnlCfgraphHeadVO onlCfgraphHeadVo);

    /**
     * 动态配置路由
     *
     * @param sysPermission 菜单权限
     */
    void menuRouting(SysPermission sysPermission);

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
