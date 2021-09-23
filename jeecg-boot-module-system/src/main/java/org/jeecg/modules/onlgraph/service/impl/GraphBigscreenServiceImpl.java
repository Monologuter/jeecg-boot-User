package org.jeecg.modules.onlgraph.service.impl;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.dynamic.db.DynamicDBUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.common.exception.MultiDatasourceException;
import org.jeecg.modules.onlgraph.common.OnlgraphCheck;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.common.OnlgraphMessageConstant;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.mapper.GraphBigscreenCardMapper;
import org.jeecg.modules.onlgraph.mapper.GraphBigscreenMapper;
import org.jeecg.modules.onlgraph.service.IGraphBigscreenCardService;
import org.jeecg.modules.onlgraph.service.IGraphBigscreenService;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;
import org.jeecg.modules.system.entity.SysDataSource;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.entity.SysRolePermission;
import org.jeecg.modules.system.mapper.SysPermissionMapper;
import org.jeecg.modules.system.service.ISysDataSourceService;
import org.jeecg.modules.system.service.ISysRolePermissionService;
import org.jeecg.modules.system.service.ISysRoleService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wangjiahao, wangshuang
 * @Description: 大屏
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Service
@Slf4j
public class GraphBigscreenServiceImpl extends ServiceImpl<GraphBigscreenMapper, GraphBigscreenDO> implements IGraphBigscreenService {

    @Autowired
    private GraphBigscreenMapper graphBigscreenMapper;

    @Autowired
    private IGraphBigscreenService iGraphBigscreenService;

    @Autowired
    private GraphBigscreenCardMapper graphBigscreenCardMapper;

    @Autowired
    private IGraphBigscreenCardService iGraphBigscreenCardService;

    @Autowired
    private ISysRoleService sysRoleService;

    @Autowired
    private CommonAPI commonAPI;

    @Autowired
    private ISysRolePermissionService sysRolePermissionService;

    //注入多数据源service实例
    @Autowired
    private ISysDataSourceService sysDataSourceService;

    @Autowired
    private SysPermissionMapper sysPermissionMapper;

    @Autowired
    private OnlgraphCheck onlgraphBigscreenImportCheck;


    /**
     * 保存
     *
     * @param graphBigscreenVO
     */
    @Transactional
    @Override
    public GraphBigscreenDO saveAll(GraphBigscreenVO graphBigscreenVO) {
        //定义Java对象接收数据
        GraphBigscreenDO graphBigscreenDO = new GraphBigscreenDO();
        //将Vo对象转换为Java接受对象（赋值 - 复制属性）
        BeanUtils.copyProperties(graphBigscreenVO, graphBigscreenDO);
        //插入大屏数据
        graphBigscreenMapper.insert(graphBigscreenDO);
        List<GraphBigscreenCardDO> graphBigscreenCards = graphBigscreenVO.getGraphBigscreenCards();
        //循环遍历插入属性（判断是否有值、值是否可用）
        if (CollUtil.isNotEmpty(graphBigscreenCards)) {
            for (GraphBigscreenCardDO graphBigscreenCardDO : graphBigscreenCards) {
                graphBigscreenCardDO.setBiggreenId(graphBigscreenDO.getId());
            }
            //批量更新数据
            iGraphBigscreenCardService.saveBatch(graphBigscreenCards);
        }
        return graphBigscreenDO;
    }

    /**
     * 根据ID删除
     *
     * @param id
     */
    @Transactional
    @Override
    public void removeAll(String id) {
        //拿到id
        GraphBigscreenDO graphBigscreenDO = graphBigscreenMapper.selectById(id);
        //判断id查询是否有记录,若已删除抛异常
        if (ObjectUtil.isNotEmpty(graphBigscreenDO) &&
                graphBigscreenDO.getDelFlag().equals(OnlgraphConstant.GRAPH_DEL_FLAG_FLASE)) {
            //修改删除状态（删除）并更新数据库
            graphBigscreenDO.setDelFlag(OnlgraphConstant.GRAPH_DEL_FLAG_SUCCESS);
            graphBigscreenMapper.updateById(graphBigscreenDO);
            //根据主表id匹配到对应biggreenId的子表
            LambdaQueryWrapper<GraphBigscreenCardDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GraphBigscreenCardDO::getBiggreenId, graphBigscreenDO.getId());
            //匹配到删除状态为未删除的子表
            lambdaQueryWrapper.eq(GraphBigscreenCardDO::getDelFlag,
                    OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
            List<GraphBigscreenCardDO> graphBigscreenCardDOS = graphBigscreenCardMapper.selectList(lambdaQueryWrapper);
            //判断查询到的非删除状态的子表记录是否为空
            if (CollUtil.isNotEmpty(graphBigscreenCardDOS)) {
                //循环遍历list中的子表，修改子表删除状态（删除）并更新数据库
                for (GraphBigscreenCardDO graphBigscreenCardDO : graphBigscreenCardDOS) {
                    graphBigscreenCardDO.setDelFlag(OnlgraphConstant.GRAPH_DEL_FLAG_SUCCESS);
                    graphBigscreenCardMapper.updateById(graphBigscreenCardDO);
                }
            }
        } else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    OnlgraphConstant.GRAPH_QUERY_FAILED);
        }
    }

    /**
     * 根据ID更新数据
     *
     * @param graphBigscreenVO
     */
    @Transactional
    @Override
    public void updateAllById(GraphBigscreenVO graphBigscreenVO) {
        GraphBigscreenDO graphBigscreenDO = new GraphBigscreenDO();
        //赋值属性
        BeanUtils.copyProperties(graphBigscreenVO, graphBigscreenDO);
        //判断id是否为空，为空则报异常
        if (StringUtils.isEmpty(graphBigscreenDO.getId())) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    OnlgraphConstant.GRAPH_UPDATE_ID_EMPTY);
        }
        //更新主表数据
        graphBigscreenMapper.updateById(graphBigscreenDO);

        //判断子表数据是否为空
        if (CollUtil.isNotEmpty(graphBigscreenVO.getGraphBigscreenCards())) {
            //更新子表数据
            LambdaQueryWrapper<GraphBigscreenCardDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GraphBigscreenCardDO::getBiggreenId, graphBigscreenDO.getId());
            //删除匹配到的子表所有数据
            graphBigscreenCardMapper.delete(lambdaQueryWrapper);
            //判断子表数据是否为空
            if (CollUtil.isNotEmpty(graphBigscreenVO.getGraphBigscreenCards())) {
                List<GraphBigscreenCardDO> graphBigscreenCard = graphBigscreenVO.getGraphBigscreenCards();
                //批量更新数据（并往新增卡片插入大屏id）
                iGraphBigscreenCardService.saveBatch(graphBigscreenCard.stream()
                        .map(e -> e.setBiggreenId(graphBigscreenVO.getId()))
                        .collect(Collectors.toList()));
            }
        }
    }

    /**
     * 根据ID查询
     *
     * @param id
     */
    @Override
    public GraphBigscreenVO getBigscreens(String id) {
        return graphBigscreenMapper.getBigscreen(id);
    }

    /**
     * 大屏设计动态配置路由
     *
     * @param sysPermission 菜单权限
     */
    @Transactional
    @Override
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

        //修改大屏状态,更新大屏
        if (StringUtils.isNotBlank(sysPermission.getUrl())) {
            LambdaQueryWrapper<GraphBigscreenDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GraphBigscreenDO::getId, StringUtils.substringAfterLast(sysPermission.getUrl(), "/"));
            GraphBigscreenDO graphBigscreenDO = graphBigscreenMapper.selectOne(lambdaQueryWrapper);
            graphBigscreenDO.setPermissionStatus(OnlgraphConstant.GRAPH_PERMISSION_STATUS_STATE);
            graphBigscreenMapper.updateById(graphBigscreenDO);
        } else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, OnlgraphConstant.GRAPH_MENU_ROUTING_FLASE);
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
        //判断后缀为";"返回true
        if (sql.endsWith(";")) {
            //位置截取，去除末尾";"（结束索引不包括）
            sql = sql.substring(0, sql.length() - 1);
        }
        //通过dbkey找到对应数据源
        SysDataSource dataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(dataSource)) {
            throw new MultiDatasourceException(OnlgraphConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //判断数据库类型为sqlServer 且判断是否支持排序
        if (ReUtil.contains(OnlgraphConstant.GRAPH_DATABASE_REGEX_MATCH, sql.toLowerCase()) &&
                OnlgraphConstant.GRAPH_DATABASE_TYPE_SQLSERVER.equalsIgnoreCase(dataSource.getDbType())) {
            throw new MultiDatasourceException(OnlgraphConstant.MUTIDATASOURCE_SQLSERVER_SORTINSQL_NOTSUPPOT);
        }
        //工具包，面向对象的sql解析
        return DynamicDBUtil.findList(dataSource.getCode(), sql);
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
        return graphBigscreenMapper.executeSeleteBySql(sql);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @Override
    public List<Map<String, Object>> getDbTableList(String dbKey) {
        //通过dbkey找到对应数据源
        SysDataSource sysDataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(OnlgraphConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";

        //判断数据库类型为oracle
        if (sysDataSource.getDbType().equals(OnlgraphConstant.GRAPH_DATABASE_TYPE_ORACLE)) {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLES_SQL_ORACLE + sysDataSource.getDbUsername().toUpperCase() + "'";
            //判断数据库类型为sqlServer
        } else if (sysDataSource.getDbType().equals(OnlgraphConstant.GRAPH_DATABASE_TYPE_SQLSERVER)) {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLES_SQL_SQLSERVER;
            //判断数据库类型为mysql（其他为mysql）
        } else {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLES_SQL + sysDataSource.getDbName() + "'";
        }
        //jdbcTemplate.queryForList() - 返回一个List,List每一项为一个Map对象,即对应着数据库中的某一行,Map每一项对应着数据库行中的某一列值
        return jdbcTemplate.queryForList(sql);
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
        //通过dbkey找到对应数据源
        SysDataSource sysDataSource = sysDataSourceService.getById(dbKey);
        if (ObjectUtils.isEmpty(sysDataSource)) {
            throw new MultiDatasourceException(OnlgraphMessageConstant.MUTIDATASOURCE_NOT_FOUND);
        }
        //获取数据源连接
        DruidDataSource dbSource = DynamicDBUtil.getDbSourceByDbKey(sysDataSource.getCode());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dbSource);
        //拼接sql 执行
        String sql = "";

        //判断数据库类型为oracle
        if (sysDataSource.getDbType().equals(OnlgraphConstant.GRAPH_DATABASE_TYPE_ORACLE)) {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_ORACLE
                    .replace(OnlgraphConstant.GRAPH_REPLACE_TABLENAME, tableName);
            //判断数据库类型为sqlServer
        } else if (sysDataSource.getDbType().equals(OnlgraphConstant.GRAPH_DATABASE_TYPE_SQLSERVER)) {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL_SQLSERVER
                    .replace(OnlgraphConstant.GRAPH_REPLACE_TABLENAME, tableName);
            //判断数据库类型为mysql（其他为mysql）
        } else {
            sql += OnlgraphConstant.MUTIDATASOURCE_TABLE_ITEMS_SQL
                    .replace(OnlgraphConstant.GRAPH_REPLACE_SCHEMA, sysDataSource.getDbName())
                    .replace(OnlgraphConstant.GRAPH_REPLACE_TABLENAME, tableName);
        }
        return jdbcTemplate.queryForList(sql);
    }

    /**
     * 大屏数据Excel导出
     *
     * @param ids
     * @return
     */
    @Override
    public ModelAndView exportXls(List<String> ids, Integer graphScreenIsTemplate) {
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        List<GraphBigscreenVO> resultList = new ArrayList<>();
        //判断ids是否为空(若为空，则全部导出)
        if (ObjectUtil.isNotEmpty(ids)) {
            //for循环查询每一项
            for (int i = 0; i < ids.size(); i++) {
                GraphBigscreenVO graphBigscreenVO = iGraphBigscreenService.getBigscreens(ids.get(i));
                resultList.add(graphBigscreenVO);
            }
        } else {
            LambdaQueryWrapper<GraphBigscreenDO> queryWrapper = new LambdaQueryWrapper<>();
            //筛选并匹配数据(筛选未删除数据，并动态匹配是否为模板的数据)
            queryWrapper.eq(GraphBigscreenDO::getDelFlag, OnlgraphConstant.GRAPH_DEL_FLAG_FLASE)
                    .eq(GraphBigscreenDO::getGraphScreenIsTemplate, graphScreenIsTemplate)
                    .orderByDesc(GraphBigscreenDO::getCreateTime);
            //queryWrapper为空时，graphBigscreenMapper.selectList(queryWrapper) 全查
            List<GraphBigscreenDO> list = graphBigscreenMapper.selectList(queryWrapper);

            //遍历List查询所有数据
            for (GraphBigscreenDO graphBigscreenDO : list) {
                GraphBigscreenVO graphBigscreenVO = iGraphBigscreenService.getBigscreens(graphBigscreenDO.getId());
                resultList.add(graphBigscreenVO);
            }
        }
        //定义参数，导出Excel文件
        log.info(resultList.toString());
        mv.addObject(NormalExcelConstants.FILE_NAME, "设计器");
        mv.addObject(NormalExcelConstants.CLASS, GraphBigscreenVO.class);
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("大屏设计器导出数据", "导出人:" + user.getRealname(), "导出信息"));
        mv.addObject(NormalExcelConstants.DATA_LIST, resultList);
        return mv;
    }

    /**
     * Excel导入大屏数据
     *
     * @param xlsFile
     * @return
     */
    @Transactional
    @Override
    public Result<String> importExcel(MultipartFile xlsFile) {
        //定义初始时间
        long start = System.currentTimeMillis();
        Result<String> result = new Result<>();
        List<GraphBigscreenVO> list;
        try {
            ImportParams importParams = new ImportParams();
            importParams.setTitleRows(2);
            importParams.setHeadRows(1);
            //自定义编码校验
            importParams.setVerifyHanlder(onlgraphBigscreenImportCheck);
            //调用easypoi导入Excel方法
            list = ExcelImportUtil.importExcel(xlsFile.getInputStream(), GraphBigscreenVO.class, importParams);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    OnlgraphConstant.GRAPH_FILE_READ_FALSE);
        }

        if (!list.isEmpty()) {
            for (GraphBigscreenVO graphBigscreenVO : list) {
                //for循环遍历,插入数据
                GraphBigscreenDO graphBigscreenDO = saveAll(graphBigscreenVO);
                //判断通过校验的数据是否有生成菜单操作,存在即生成动态路由菜单
                if (ObjectUtil.isNotEmpty(graphBigscreenVO.getPermissionStatus()) && graphBigscreenVO.getPermissionStatus()
                        .equals(OnlgraphConstant.GRAPH_PERMISSION_STATUS_STATE)) {
                    SysPermission sysPermission = new SysPermission();
                    //因GraphBigscreenVO未生成大屏id,通过saveAll()中的GraphBigscreenDO拿到大屏id
                    sysPermission.setUrl(OnlgraphConstant.GRAPH_BIGSCREEN_PERMISSION_URL + graphBigscreenDO.getId())
                            .setComponent(OnlgraphConstant.GRAPH_BIGSCREEN_PERMISSION_COMPONENT)
                            .setComponentName(OnlgraphConstant.GRAPH_BIGSCREEN_PERMISSION_NAME +
                                    graphBigscreenDO.getGraphBigscreenName())
                            .setName(OnlgraphConstant.GRAPH_BIGSCREEN_PERMISSION_NAME +
                                    graphBigscreenDO.getGraphBigscreenName())
                            .setStatus(OnlgraphConstant.GRAPH_STATUS_STATE).setRoute(true);
                    //调用动态配置路由
                    iGraphBigscreenService.menuRouting(sysPermission);
                }
            }
            //计算导入消耗得时间
            log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
        }
        return result.success(OnlgraphConstant.GRAPH_FILE_IMPORT_SUCCESS + list.size());
    }

}
