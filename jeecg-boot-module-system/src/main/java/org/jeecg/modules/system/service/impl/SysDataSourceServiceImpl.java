package org.jeecg.modules.system.service.impl;

import cn.hutool.core.util.ReUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.jeecg.common.util.dynamic.db.DynamicDBUtil;
import org.jeecg.modules.common.exception.MultiDatasourceException;
import org.jeecg.modules.system.common.SystemMessageConstant;
import org.jeecg.modules.system.entity.SysDataSource;
import org.jeecg.modules.system.mapper.SysDataSourceMapper;
import org.jeecg.modules.system.model.SysDataSourceModel;
import org.jeecg.modules.system.service.ISysDataSourceService;
import org.jeecg.modules.system.util.SecurityUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author  zhangqijian
 * @company dxc
 * @create  2021-05-19 11:13
 */
@Service
public class SysDataSourceServiceImpl extends ServiceImpl<SysDataSourceMapper, SysDataSource> implements ISysDataSourceService {

    @Autowired
    private SysDataSourceMapper sysDataSourceMapper;

    /**
     * 分域查询列表
     *
     * @return
     */
    @Override
    public List<SysDataSourceModel> queryClassList() {
        //获取多数据源列表,并按照修改时间排序
        List<SysDataSource> classList = sysDataSourceMapper.selectList(new LambdaQueryWrapper<SysDataSource>()
                .orderByDesc(SysDataSource::getUpdateTime)
        );
        //加密密码解密
        classList.forEach(item -> {
            String dbPassword = item.getDbPassword();
            if (StringUtils.isNotBlank(dbPassword)) {
                String decodedStr = SecurityUtil.jiemi(dbPassword);
                item.setDbPassword(decodedStr);
            }
        });
        //设置结果集
        List<SysDataSourceModel> resultList = new ArrayList<>();
        for (SysDataSource dataSource : classList) {
            //一级分类
            if (dataSource.getParentId() == null) {
                SysDataSourceModel nextList = getNextList(dataSource, classList);
                if (ObjectUtils.isNotEmpty(nextList)) {
                    resultList.add(nextList);
                }
            }
        }
        return resultList;
    }

    /**
     * 分域字典查询
     *
     * @return
     */
    @Override
    public List<SysDataSourceModel> queryClassDict() {

        return this.queryClassList();
    }

    /**
     * 获取下一个节点
     *
     * @param sysDataSource
     * @param sysDataSourceList
     * @return
     */
    public SysDataSourceModel getNextList(SysDataSource sysDataSource, List<SysDataSource> sysDataSourceList) {
        SysDataSourceModel sysDataSourceModel = new SysDataSourceModel();
        //将实例对象转为模板对象
        BeanUtils.copyProperties(sysDataSource, sysDataSourceModel);
        for (SysDataSource dataSource : sysDataSourceList) {
            //获取下一级数据 注：null值不可调用方法 parentId可能为null
            if (sysDataSource.getId().equals(dataSource.getParentId())) {
                //递归实现下级查询
                SysDataSourceModel nextList = getNextList(dataSource, sysDataSourceList);
                if (ObjectUtils.isNotEmpty(nextList)) {
                    //初始化子节点
                    if (sysDataSourceModel.getChildren() == null) {
                        sysDataSourceModel.setChildren(new ArrayList<>());
                    }
                    sysDataSourceModel.getChildren().add(nextList);
                }
            }
        }
        return sysDataSourceModel;
    }

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    @Override
    public List<Map<String, Object>> executeSelectBySql(String sql, String dbKey) {
        //endsWith() - 后缀为";"返回true
        if (sql.endsWith(";")) {
            //位置截取，去除末尾";"（结束索引不包括）
            sql = sql.substring(0, sql.length() - 1);
        }
        SysDataSource dataSource = this.getById(dbKey);
        if (ObjectUtils.isEmpty(dataSource)) {
            throw new MultiDatasourceException(SystemMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //contains() - 判断sql是否包含指定字符、字符串
        //equalsIgnoreCase() - 方法用于将字符串与指定的对象比较，不考虑大小写
        //判断数据库类型为 - "3"（3为sqlServer）
        if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "3".equalsIgnoreCase(dataSource.getDbType())) {
            throw new MultiDatasourceException(SystemMessageConstant.MUTIDATASOURCE_SQLSERVER_SORTINSQL_NOTSUPPOT);
        }
        //工具包，面向对象的sql解析
        List<Map<String, Object>> list = DynamicDBUtil.findList(dataSource.getCode(), sql, new Object[0]);
        return list;
    }


    /**
     * sql解析系统数据库
     *
     * @param sql
     * @return
     */
    @Override
    public List<Map<String, Object>> executeSelectBySqlNative(String sql) {
        //位置截取
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        //根据sql查询本地数据库数据
        return sysDataSourceMapper.executeSeleteBySql(sql);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @Override
    public List<Map<String, Object>> getDbTableList(String dbKey) {
        SysDataSource sysDataSource = this.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(SystemMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";
        //判断数据库类型  2为oracle  3为sqlServer 其他为mysql
        if (sysDataSource.getDbType().equals("2")) {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLES_SQL_ORACLE + sysDataSource.getDbUsername().toUpperCase() + "'";
        } else if (sysDataSource.getDbType().equals("3")) {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLES_SQL_SQLSERVER;
        } else {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLES_SQL + sysDataSource.getDbName() + "'";
        }
        //jdbcTemplate.queryForList() - 返回一个List,List每一项为一个Map对象,即对应着数据库中的某一行,Map每一项对应着数据库行中的某一列值
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }

    /**
     * 获取数据源表内字段
     *
     * @param dbKey
     * @param tableName
     * @return
     */
    @Override
    public List<Map<String, Object>> getDbTableItems(String dbKey, String tableName) {
        SysDataSource sysDataSource = this.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(SystemMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";
        //判断数据库类型 2为oracle  3为sqlServer 其他为mysql
        if (sysDataSource.getDbType().equals("2")) {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_ORACLE.replace("TABLENAME", tableName);
        } else if (sysDataSource.getDbType().equals("3")) {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_SQLSERVER.replace("TABLENAME", tableName);
        } else {
            sql += SystemMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL.replace("SCHEMA", sysDataSource.getDbName()).replace("TABLENAME", tableName);
        }
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }
}
