package org.jeecg.modules.onlgraph.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.dynamic.db.DynamicDBUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.exception.MultiDatasourceException;
import org.jeecg.modules.onlgraph.common.OnlgraphMessageConstant;
import org.jeecg.modules.onlgraph.service.IOnlCfgraphFieldService;
import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import org.jeecg.modules.onlgraph.mapper.OnlCfgraphFieldMapper;
import org.jeecg.modules.onlgraph.mapper.OnlCfgraphHeadMapper;
import org.jeecg.modules.onlgraph.service.IOnlCfgraphHeadService;
import org.jeecg.modules.system.entity.SysDataSource;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.service.ISysDataSourceService;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.jeecg.modules.system.service.ISysRoleService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.util.*;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_head图表配置表汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Service
public class OnlCfgraphHeadServiceImpl extends ServiceImpl<OnlCfgraphHeadMapper, OnlCfgraphHeadDO> implements IOnlCfgraphHeadService {


    //将DAO注入Service层
    @Autowired
    private OnlCfgraphFieldMapper onlCfgraphFieldMapper;

    @Autowired
    private IOnlCfgraphFieldService iOnlCfgraphFieldService;

    @Autowired
    private OnlCfgraphHeadMapper onlCfgraphHeadMapper;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private CommonAPI commonAPI;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    //注入多数据源service实例
    @Autowired
    private ISysDataSourceService sysDataSourceService;


    /**
     * 图表配置  保存
     *
     * @param onlCfgraphHeadVo 主附表数据实体
     */
    @Transactional
    @Override
    public void saveAll(OnlCfgraphHeadVO onlCfgraphHeadVo) {
        //定义java对象接收数据
        OnlCfgraphHeadDO onlCfgraphHeadDO = new OnlCfgraphHeadDO();
        //将Vo对象转换为Java接收对象
        BeanUtils.copyProperties(onlCfgraphHeadVo, onlCfgraphHeadDO);
        onlCfgraphHeadDO.setUpdateTime(new Date());
        onlCfgraphHeadMapper.insert(onlCfgraphHeadDO);
        //循环遍历插入数据
        if (CollUtil.isNotEmpty(onlCfgraphHeadVo.getOnlCfgraphFields())) {
            List<OnlCfgraphFieldDO> onlCfgraphFields = onlCfgraphHeadVo.getOnlCfgraphFields();
            for (OnlCfgraphFieldDO onlCfgraphFieldDO : onlCfgraphFields) {
                onlCfgraphFieldDO.setOnlCfgraphId(onlCfgraphHeadDO.getId());
            }
            //批量更新数据
            iOnlCfgraphFieldService.saveBatch(onlCfgraphFields);
        }
    }

    /**
     * 图表配置  删除
     *
     * @param id
     */
    @Override
    @Transactional
    public void removeAll(String id) {
        //修改head表删除状态,逻辑删除
        OnlCfgraphHeadDO onlCfgraphHeadDO = onlCfgraphHeadMapper.selectById(id);
        onlCfgraphHeadDO.setDelFlag(OnlgraphMessageConstant.GRAPH_HEAD_DEL_FLAG_SUCCESS);
        onlCfgraphHeadMapper.updateById(onlCfgraphHeadDO);
    }

    /**
     * 图表配置  根据ID查询
     *
     * @param id
     */
    @Override
    public OnlCfgraphHeadVO getOnlCfgraphHeads(String id) {
        return onlCfgraphHeadMapper.getOnlCfgraphHeads(id);
    }

    /**
     * 根据ID更新数据
     *
     * @param onlCfgraphHeadVo 主附表数据实体
     */
    @Override
    @Transactional
    public void updateAllById(OnlCfgraphHeadVO onlCfgraphHeadVo) {
        //将前端对象转为数据库实体类对象
        OnlCfgraphHeadDO onlCfgraphHeadDO = new OnlCfgraphHeadDO();
        BeanUtils.copyProperties(onlCfgraphHeadVo, onlCfgraphHeadDO);
        if (StringUtils.isEmpty(onlCfgraphHeadDO.getId())) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, OnlgraphMessageConstant.GRAPH_HEAD_UPDATE_ID_EMPTY);
        }
        //更新主表数据
        onlCfgraphHeadMapper.updateById(onlCfgraphHeadDO);
        //更新附表接口 先根据主表ID删除附表内容再进行插入
        LambdaQueryWrapper<OnlCfgraphFieldDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OnlCfgraphFieldDO::getOnlCfgraphId, onlCfgraphHeadDO.getId());
        onlCfgraphFieldMapper.delete(lambdaQueryWrapper);
        if (CollUtil.isNotEmpty(onlCfgraphHeadVo.getOnlCfgraphFields())) {
            List<OnlCfgraphFieldDO> fieldList = onlCfgraphHeadVo.getOnlCfgraphFields();
            //批量更新数据
            iOnlCfgraphFieldService.saveBatch(fieldList);
        }
    }

    /**
     * 动态配置路由
     *
     * @param sysPermission 菜单权限
     */
    @Override
    @Transactional
    public void menuRouting(SysPermission sysPermission) {
        //设置父菜单ID
        sysPermission.setParentId("1372376113725030402");
        //菜单为图表类型
        sysPermission.setMenuType(3);
        //应用级别菜单
        //todo
        sysPermission.setCreateWays(0);
        //排序
        sysPermission.setSortNo(2.0);
        String pid = sysPermission.getParentId();
        if (oConvertUtils.isNotEmpty(pid)) {
            //设置父节点不为叶子节点
            this.sysPermissionMapper.setMenuLeaf(pid, 0);
        }
        sysPermission.setCreateTime(new Date()).setDelFlag(0).setLeaf(true)
                .setHidden(false).setRuleFlag(0).setAlwaysShow(false);
        //添加菜单表
        sysPermissionMapper.insert(sysPermission);
        //向子菜单内添加role权限，目前写死
        // 获取当前登录用户
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Set<String> roles = commonAPI.queryUserRoles(user.getUsername());
        LambdaQueryWrapper<SysRole> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(SysRole::getRoleCode, roles).select(SysRole::getId);
        // 获取用户角色列表
        List<SysRole> sysRoleList = sysRoleService.list(queryWrapper);
        //更新菜单权限表
        sysRolePermissionService.saveBatch(sysRoleList.stream()
                .map(sysRole -> new SysRolePermission(sysRole.getId(), sysPermission.getId()))
                .collect(Collectors.toSet()));
        //1.修改图表状态,更新图表
        if (StringUtils.isNotBlank(sysPermission.getUrl())) {
            LambdaQueryWrapper<OnlCfgraphHeadDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getId, StringUtils.substringAfterLast(sysPermission.getUrl(), "/"));
            OnlCfgraphHeadDO onlCfgraphHeadDO = onlCfgraphHeadMapper.selectOne(lambdaQueryWrapper);
            onlCfgraphHeadDO.setGraphPermissionStatus(1);
            onlCfgraphHeadMapper.updateById(onlCfgraphHeadDO);
        } else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "菜单路径不存在");
        }
    }

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    @Override
    public List<Map<String, Object>> executeSelectBySql(String sql, String dbKey) {
        //位置截取
        if (sql.endsWith(";")) {
            sql = sql.substring(0, sql.length() - 1);
        }
        SysDataSource dataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(dataSource)) {
            throw new MultiDatasourceException(OnlgraphMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        if (ReUtil.contains(" order\\s+by ", sql.toLowerCase()) && "3".equalsIgnoreCase(dataSource.getDbType())) {
            throw new MultiDatasourceException(OnlgraphMessageConstant.MUTIDATASOURCE_SQLSERVER_SORTINSQL_NOTSUPPOT);
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
        return onlCfgraphHeadMapper.executeSeleteBySql(sql);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @Override
    public List<Map<String, Object>> getDbTableList(String dbKey) {
        SysDataSource sysDataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(OnlgraphMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";
        //数据库类型  2为oracle  3为sqlServer 其他为mysql
        if (sysDataSource.getDbType().equals("2")) {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLES_SQL_ORACLE + sysDataSource.getDbUsername().toUpperCase() + "'";
        } else if (sysDataSource.getDbType().equals("3")) {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLES_SQL_SQLSERVER;
        } else {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLES_SQL + sysDataSource.getDbName() + "'";
        }
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
        SysDataSource sysDataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(OnlgraphMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";
        //数据库类型 2为oracle  3为sqlServer 其他为mysql
        if (sysDataSource.getDbType().equals("2")) {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_ORACLE.replace("TABLENAME", tableName);
        } else if (sysDataSource.getDbType().equals("3")) {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_SQLSERVER.replace("TABLENAME", tableName);
        } else {
            sql += OnlgraphMessageConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL.replace("SCHEMA", sysDataSource.getDbName()).replace("TABLENAME", tableName);
        }
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        return result;
    }
}
