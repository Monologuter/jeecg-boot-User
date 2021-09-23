package org.jeecg.modules.onlgraph.service;


import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;
import org.jeecg.modules.system.entity.SysPermission;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * @author wangjiahao, wangshuang
 * @Description: 大屏
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
public interface IGraphBigscreenService extends IService<GraphBigscreenDO> {

    /**
     * 通过id保存
     *
     * @param graphBigscreenVO
     */
    GraphBigscreenDO saveAll(GraphBigscreenVO graphBigscreenVO);

    /**
     * 通过id删除多表
     *
     * @param id
     */
    void removeAll(String id);

    /**
     * 根据ID更新数据
     *
     * @param graphBigscreenVO
     */
    void updateAllById(GraphBigscreenVO graphBigscreenVO);

    /**
     * 根据id查询图表配置
     *
     * @param id 主键ID
     * @return
     */
    GraphBigscreenVO getBigscreens(String id);

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

    /**
     * 大屏导出
     *
     * @param ids
     */
    ModelAndView exportXls(List<String> ids, Integer graphScreenIsTemplate);

    /**
     * 导入
     *
     * @param xlsFile
     */
    Result<String> importExcel(MultipartFile xlsFile);

}
