package org.jeecg.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.dynamic.db.DataSourceCachePool;
import org.jeecg.modules.system.common.SystemMessageConstant;
import org.jeecg.modules.system.entity.SysDataSource;
import org.jeecg.modules.system.model.SysDataSourceModel;
import org.jeecg.modules.system.service.ISysDataSourceService;
import org.jeecg.modules.system.util.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description: 多数据源管理
 * @Author: jeecg-boot
 * @Date: 2019-12-25
 * @Version: V1.0
 */
@Slf4j
@Api(tags = "多数据源管理")
@RestController
@RequestMapping("/sys/dataSource")
public class SysDataSourceController extends JeecgController<SysDataSource, ISysDataSourceService> {

    @Autowired
    private ISysDataSourceService sysDataSourceService;

    /**
     * 分页列表查询
     *
     * @param sysDataSource
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "多数据源管理-分页列表查询")
    @ApiOperation(value = "多数据源管理-分页列表查询", notes = "多数据源管理-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(
            SysDataSource sysDataSource,
            @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            HttpServletRequest req
    ) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
        Page<SysDataSource> page = new Page<>(pageNo, pageSize);
        IPage<SysDataSource> pageList = sysDataSourceService.page(page, queryWrapper);
        try {
            List<SysDataSource> records = pageList.getRecords();
            records.forEach(item -> {
                String dbPassword = item.getDbPassword();
                if (StringUtils.isNotBlank(dbPassword)) {
                    String decodedStr = SecurityUtil.jiemi(dbPassword);
                    item.setDbPassword(decodedStr);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(pageList);
    }

    /**
     * 分域查询
     *
     * @param
     * @return
     */
    @AutoLog(value = "多数据源管理-分域查询")
    @ApiOperation(value = "多数据源管理-分域查询", notes = "多数据源管理-分域查询")
    @GetMapping(value = "/list-class")
    public Result<List<SysDataSourceModel>> queryClassList() {
        List<SysDataSourceModel> resultList = sysDataSourceService.queryClassList();
        return Result.OK(resultList);
    }

    /**
     * @param sysDataSource
     * @param req
     * @return
     */
    @AutoLog(value = "多数据源管理-数据源字典")
    @ApiOperation(value = "多数据源管理-数据源字典", notes = "多数据源管理-数据源字典")
    @GetMapping(value = "/options")
    public Result<Object> queryOptions(SysDataSource sysDataSource, HttpServletRequest req) {
        QueryWrapper<SysDataSource> queryWrapper = QueryGenerator.initQueryWrapper(sysDataSource, req.getParameterMap());
        List<SysDataSource> pageList = sysDataSourceService.list(queryWrapper);
        List<Map> optionLists = pageList.stream().map(dataSource -> {
            Map<String, String> option = new HashMap<>();
            option.put("value", dataSource.getCode());
            option.put("label", dataSource.getName());
            option.put("text", dataSource.getName());
            return option;
        }).collect(Collectors.toList());
        return Result.OK(optionLists);
    }

    /**
     * 分域字典
     *
     * @return
     */
    @AutoLog(value = "多数据源管理-分域字典")
    @ApiOperation(value = "多数据源管理-分域字典", notes = "多数据源管理-分域字典")
    @GetMapping(value = "/list-class-dict")
    public Result<List<SysDataSourceModel>> queryClassDict() {
        List<SysDataSourceModel> resultList = sysDataSourceService.queryClassDict();
        return Result.OK(resultList);
    }

    /**
     * 添加
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多数据源管理-添加")
    @ApiOperation(value = "多数据源管理-添加", notes = "多数据源管理-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody SysDataSource sysDataSource) {
        try {
            if (StringUtils.isBlank(sysDataSource.getName())) {
                return Result.Error("数据源名称不能为空！");
            }
            sysDataSource.setName(sysDataSource.getName().trim());
            if (sysDataSource.getIsLeaf().equals("1")) {
                if (StringUtils.isBlank(sysDataSource.getDbName())) {
                    return Result.Error("数据库名称不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbUrl())) {
                    return Result.Error("数据库地址不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbUsername())) {
                    return Result.Error("用户名不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbPassword())) {
                    return Result.Error("密码不能为空！");
                }
                sysDataSource.setDbName(sysDataSource.getDbName().trim());
                sysDataSource.setDbUrl(sysDataSource.getDbUrl().trim());
                sysDataSource.setDbUsername(sysDataSource.getDbUsername().trim());
                String encrypt = SecurityUtil.jiami(sysDataSource.getDbPassword().trim());
                sysDataSource.setDbPassword(encrypt);
            }
            //赋值修改时间为当前时间，便于后面按修改时间排序
            sysDataSource.setUpdateTime(new Date());
            boolean save = sysDataSourceService.save(sysDataSource);
            if (!save) {
                return Result.Error("添加失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("添加成功！");
    }

    /**
     * 编辑
     *
     * @param sysDataSource
     * @return
     */
    @AutoLog(value = "多数据源管理-编辑")
    @ApiOperation(value = "多数据源管理-编辑", notes = "多数据源管理-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody SysDataSource sysDataSource) {
        try {
            if (StringUtils.isBlank(sysDataSource.getName())) {
                return Result.Error("数据源名称不能为空！");
            }
            sysDataSource.setName(sysDataSource.getName().trim());
            if (sysDataSource.getIsLeaf().equals("1")) {
                if (StringUtils.isBlank(sysDataSource.getDbName())) {
                    return Result.Error("数据库名称不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbUrl())) {
                    return Result.Error("数据库地址不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbUsername())) {
                    return Result.Error("用户名不能为空！");
                }
                if (StringUtils.isBlank(sysDataSource.getDbPassword())) {
                    return Result.Error("密码不能为空！");
                }
                sysDataSource.setDbName(sysDataSource.getDbName().trim());
                sysDataSource.setDbUrl(sysDataSource.getDbUrl().trim());
                sysDataSource.setDbUsername(sysDataSource.getDbUsername().trim());
                String encrypt = SecurityUtil.jiami(sysDataSource.getDbPassword().trim());
                sysDataSource.setDbPassword(encrypt);
            }
            SysDataSource d = sysDataSourceService.getById(sysDataSource.getId());
            DataSourceCachePool.removeCache(d.getCode());
            if (sysDataSource.getParentId() == null) {
                sysDataSourceService.update(sysDataSource, Wrappers.<SysDataSource>lambdaUpdate()
                        .set(SysDataSource::getParentId, null)
                        .eq(SysDataSource::getId, sysDataSource.getId()));
            }
            sysDataSourceService.updateById(sysDataSource);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多数据源管理-通过id删除")
    @ApiOperation(value = "多数据源管理-通过id删除", notes = "多数据源管理-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        DataSourceCachePool.removeCache(sysDataSource.getCode());
        sysDataSourceService.removeById(id);
        return Result.ok("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "多数据源管理-批量删除")
    @ApiOperation(value = "多数据源管理-批量删除", notes = "多数据源管理-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids") String ids) {
        List<String> idList = Arrays.asList(ids.split(","));
        idList.forEach(item -> {
            SysDataSource sysDataSource = sysDataSourceService.getById(item);
            DataSourceCachePool.removeCache(sysDataSource.getCode());
        });
        this.sysDataSourceService.removeByIds(idList);
        return Result.ok("批量删除成功！");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "多数据源管理-通过id查询")
    @ApiOperation(value = "多数据源管理-通过id查询", notes = "多数据源管理-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id") String id) {
        SysDataSource sysDataSource = sysDataSourceService.getById(id);
        return Result.ok(sysDataSource);
    }

    /**
     * 导出excel
     *
     * @param request
     * @param sysDataSource
     */
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, SysDataSource sysDataSource) {
        return super.exportXls(request, sysDataSource, SysDataSource.class, "多数据源管理");
    }

    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, SysDataSource.class);
    }

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    @AutoLog(value = "多数据源管理-SQL解析")
    @ApiOperation(value = "多数据源管理-SQL解析", notes = "多数据源管理-SQL解析")
    @GetMapping(value = "/sql-parsing")
    public Result<List<Map<String, Object>>> executeSelectBySql(@RequestParam(name = "sql", required = true) String sql,
                                                                @RequestParam(name = "dbKey", required = false) String dbKey) throws SQLException {
        //sql解析字段进行校验
        SqlInjectionUtil.specialFilterContentForOnlineCgfraph(sql);
        List<Map<String, Object>> mapList = null;
        //根据参数选择sql解析数据源
        if (org.apache.commons.lang3.StringUtils.isNotBlank(dbKey)) {
            mapList = sysDataSourceService.executeSelectBySql(sql, dbKey);
        } else {
            mapList = sysDataSourceService.executeSelectBySqlNative(sql);
        }
        return Result.OK(SystemMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, mapList);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @AutoLog(value = "多数据源管理-数据源表名列表")
    @ApiOperation(value = "多数据源管理-数据源表名列表", notes = "多数据源管理-数据源表名列表")
    @GetMapping(value = "/db-list")
    public Result<List<Map<String, Object>>> getDbTableList(@RequestParam(name = "dbKey") String dbKey) {
        List<Map<String, Object>> result = sysDataSourceService.getDbTableList(dbKey);
        return Result.OK(SystemMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, result);
    }

    /**
     * 获取数据源表内字段
     *
     * @param dbKey
     * @param tableName
     * @return
     */
    @AutoLog(value = "多数据源管理-数据源表内字段列表")
    @ApiOperation(value = "多数据源管理-数据源表内字段列表", notes = "多数据源管理-数据源表内字段列表")
    @GetMapping(value = "/db-items")
    public Result<List<Map<String, Object>>> getDbTableItems(@RequestParam(name = "dbKey") String dbKey,
                                                             @RequestParam(name = "tableName") String tableName) {
        List<Map<String, Object>> result = sysDataSourceService.getDbTableItems(dbKey, tableName);
        return Result.OK(SystemMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, result);
    }
}
