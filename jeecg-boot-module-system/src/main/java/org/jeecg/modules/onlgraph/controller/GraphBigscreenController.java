package org.jeecg.modules.onlgraph.controller;


import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.service.IGraphBigscreenService;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;

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
@Api(tags = "大屏")
@RestController
@RequestMapping("/onlgraph/graph-bigscreen")
@Slf4j
public class GraphBigscreenController extends JeecgController<GraphBigscreenDO, IGraphBigscreenService> {

    @Autowired
    private IGraphBigscreenService graphBigscreenService;

    @Autowired
    private ISysPermissionService iSysPermissionService;

    /**
     * 分页列表查询
     *
     * @param graphBigscreen
     * @param pageNo
     * @param pageSize
     * @return
     */
    @AutoLog(value = "大屏-分页列表查询")
    @ApiOperation(value = "大屏-分页列表查询", notes = "大屏-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<GraphBigscreenDO>> queryPageList(GraphBigscreenDO graphBigscreen, @RequestParam(name = "pageNo",
            defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize",
            defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<GraphBigscreenDO> queryWrapper = new LambdaQueryWrapper<>();
        //筛选过滤已删除
        queryWrapper.eq(GraphBigscreenDO::getDelFlag, OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
        //根据所传模板字段值动态匹配(大屏为0 or 模板为1)
        queryWrapper.eq(GraphBigscreenDO::getGraphScreenIsTemplate, graphBigscreen.getGraphScreenIsTemplate());
        //根据大屏名称进行左右模糊查询
        queryWrapper.like(StringUtils.isNotBlank(graphBigscreen.getGraphBigscreenName()),
                GraphBigscreenDO::getGraphBigscreenName, graphBigscreen.getGraphBigscreenName());
        //根据大屏编码进行左右模糊查询
        queryWrapper.like(StringUtils.isNotBlank(graphBigscreen.getGraphBigscreenCode()),
                GraphBigscreenDO::getGraphBigscreenCode, graphBigscreen.getGraphBigscreenCode());
        //根据创建时间排序
        queryWrapper.orderByDesc(GraphBigscreenDO::getCreateTime);
        //分页
        Page<GraphBigscreenDO> page = new Page<>(pageNo, pageSize);
        IPage<GraphBigscreenDO> pageList = graphBigscreenService.page(page, queryWrapper);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, pageList);
    }

    /**
     * 添加
     *
     * @param graphBigscreenVO
     * @return
     */
    @AutoLog(value = "大屏-添加")
    @ApiOperation(value = "大屏-添加", notes = "大屏-添加")
    @PostMapping(value = "/save-all")
    public Result<GraphBigscreenDO> saveAll(@RequestBody GraphBigscreenVO graphBigscreenVO) {
        return Result.OK(graphBigscreenService.saveAll(graphBigscreenVO));
    }

    /**
     * 编辑（更新）
     *
     * @param graphBigscreen
     * @return
     */
    @AutoLog(value = "大屏-编辑")
    @ApiOperation(value = "大屏-编辑", notes = "大屏-编辑")
    @PutMapping(value = "/edit-all")
    public Result<String> editAll(@RequestBody GraphBigscreenVO graphBigscreen) {
        graphBigscreenService.updateAllById(graphBigscreen);
        return Result.OK(OnlgraphConstant.GRAPH_UPDATE_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "大屏-通过id删除")
    @ApiOperation(value = "大屏-通过id删除", notes = "大屏-通过id删除")
    @DeleteMapping(value = "/delete-all")
    public Result<String> deleteAll(@RequestParam(name = "id") String id) {
        graphBigscreenService.removeAll(id);
        return Result.OK(OnlgraphConstant.GRAPH_DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "大屏-批量删除")
    @ApiOperation(value = "大屏-批量删除", notes = "大屏-批量删除")
    @DeleteMapping(value = "/delete-batch")
    public Result<String> deleteBatch(@ApiParam("id数组") @RequestParam(name = "ids") String ids) {
        Result<String> result = new Result<>();
        //分割String对象ids 为 String数组id
        String[] id = ids.split(",");
        //循环遍历执行 根据id删除操作
        for (int i = 0; i < id.length; i++) {
            graphBigscreenService.removeAll(id[i]);
        }
        result.success(OnlgraphConstant.GRAPH_DELETE_BATCH_SUCCESS);
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "大屏-通过id查询")
    @ApiOperation(value = "大屏-通过id查询", notes = "大屏-通过id查询")
    @GetMapping(value = "/query-id")
    public Result<GraphBigscreenVO> queryId(@RequestParam(name = "id") String id) {
        Result<GraphBigscreenVO> result = new Result<>();
        //通过id查询大屏
        GraphBigscreenVO graphBigscreenVO = graphBigscreenService.getBigscreens(id);
        //判断是否查询到数据，若有直接return
        if (ObjectUtil.isEmpty(graphBigscreenVO)) {
            result.success(OnlgraphConstant.GRAPH_QUERY_FAILED);
        } else {
            result.setResult(graphBigscreenVO);
            result.success(OnlgraphConstant.GRAPH_QUERY_SUCCESS);
        }
        return result;
    }

    /**
     * 大屏动态路由配置
     *
     * @param sysPermission 菜单权限配置
     * @return
     */
    @AutoLog(value = "大屏-动态路由配置")
    @ApiOperation(value = "大屏-动态路由配置", notes = "大屏-动态路由配置")
    @PostMapping(value = "/menu-routing")
    public Result<String> menuRouting(@RequestBody SysPermission sysPermission) {
        //1.判断是否第一次生成路由地址,有就直接返回
        if (ObjectUtil.isNotEmpty(sysPermission.getId())) {
            iSysPermissionService.updateById(sysPermission);
            return Result.OK(OnlgraphConstant.GRAPH_UPDATE_SUCCESS);
        }
        //2.获取前端传递参数生成动态路由菜单
        graphBigscreenService.menuRouting(sysPermission);
        return Result.OK(OnlgraphConstant.GRAPH_MENU_ROUTING_SUCCESS);
    }

    /**
     * 通过id查询大屏菜单配置
     *
     * @param id onlCfgraphHead主键ID
     * @return
     */
    @AutoLog(value = "大屏-通过id查询菜单配置")
    @ApiOperation(value = "大屏-通过id查询菜单配置", notes = "大屏-通过id查询菜单配置")
    @GetMapping(value = "/query-permission")
    public Result<SysPermission> queryPermission(@RequestParam(name = "id") String id) {
        Result<SysPermission> result = new Result<>();
        //1.判断是否第一次生成路由地址,有就直接返回
        LambdaQueryWrapper<SysPermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //拼接URL并匹配
        lambdaQueryWrapper.eq(SysPermission::getUrl,
                OnlgraphConstant.GRAPH_BIGSCREEN_PERMISSION_URL + id);
        //getone()拿到匹配的结果,判断是否为空
        SysPermission sysPermission = iSysPermissionService.getOne(lambdaQueryWrapper);
        if (ObjectUtil.isEmpty(sysPermission)) {
            result.success(OnlgraphConstant.GRAPH_QUERY_FAILED);
        } else {
            result.setResult(sysPermission);
            result.success(OnlgraphConstant.GRAPH_QUERY_SUCCESS);
        }
        return result;
    }

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    @AutoLog(value = "大屏-SQL解析")
    @ApiOperation(value = "大屏-SQL解析", notes = "大屏-SQL解析")
    @GetMapping(value = "/sql-parsing")
    public Result<List<Map<String, Object>>> executeSelectBySql(@RequestParam(name = "sql") String sql,
                                                                @RequestParam(name = "dbKey", required = false)
                                                                        String dbKey) throws SQLException {
        //sql解析字段进行校验
        SqlInjectionUtil.specialFilterContentForOnlineCgfraph(sql);
        List<Map<String, Object>> mapList = null;
        //根据参数选择sql解析数据源(是否有dbkey，若无默认本地数据库)
        if (StringUtils.isNotBlank(dbKey)) {
            mapList = graphBigscreenService.executeSelectBySql(sql, dbKey);
        } else {
            mapList = graphBigscreenService.executeSelectBySqlNative(sql);
        }
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, mapList);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @AutoLog(value = "大屏-数据源表名列表")
    @ApiOperation(value = "大屏-数据源表名列表", notes = "大屏-数据源表名列表")
    @GetMapping(value = "/db-list")
    public Result<List<Map<String, Object>>> getDbTableList(@RequestParam(name = "dbKey") String dbKey) {
        List<Map<String, Object>> result = graphBigscreenService.getDbTableList(dbKey);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, result);
    }

    /**
     * 获取数据源表内字段
     *
     * @param dbKey
     * @param tableName
     * @return
     */
    @AutoLog(value = "大屏-数据源表内字段列表")
    @ApiOperation(value = "大屏-数据源表内字段列表", notes = "大屏-数据源表内字段列表")
    @GetMapping(value = "/db-items")
    public Result<List<Map<String, Object>>> getDbTableItems(@RequestParam(name = "dbKey") String dbKey,
                                                             @RequestParam(name = "tableName") String tableName) {
        List<Map<String, Object>> result = graphBigscreenService.getDbTableItems(dbKey, tableName);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, result);
    }

    /**
     * 编码校验
     *
     * @param graphBigscreenCode 编码
     * @return
     */
    @AutoLog(value = "大屏-编码校验")
    @ApiOperation(value = "大屏-编码校验", notes = "大屏-编码校验")
    @GetMapping(value = "/bigscreen-code/{bigscreenCode}")
    public Result<String> codeCheck(@PathVariable(name = "bigscreenCode") String graphBigscreenCode) {
        Result<String> result = new Result<>();
        //匹配Code
        LambdaQueryWrapper<GraphBigscreenDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GraphBigscreenDO::getGraphBigscreenCode, graphBigscreenCode);
        //判断大屏设计器删除状态为未删除
        lambdaQueryWrapper.eq(GraphBigscreenDO::getDelFlag,
                OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
        //匹配code是否存在，若结果大于0则校验失败
        if (graphBigscreenService.count(lambdaQueryWrapper) > 0) {
            return result.error500(OnlgraphConstant.GRAPH_CHECK_FAILED);
        } else {
            result.success(OnlgraphConstant.GRAPH_CHECK_SUCCESS);
        }
        return result;
    }

    /**
     * 大屏模板查询
     *
     * @return
     */
    @AutoLog(value = "大屏-模板列表查询")
    @ApiOperation(value = "大屏-模板列表查询", notes = "大屏-模板列表查询")
    @GetMapping(value = "/template-list")
    public Result<List<GraphBigscreenDO>> queryTemplateList() {
        LambdaQueryWrapper<GraphBigscreenDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //判断大屏设计器删除状态为未删除
        lambdaQueryWrapper.eq(GraphBigscreenDO::getDelFlag,
                OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
        //判断是否为模板
        lambdaQueryWrapper.eq(GraphBigscreenDO::getGraphScreenIsTemplate,
                OnlgraphConstant.GRAPH_TEMPLATE_FLAG_TRUE);
        //根据创建时间排序
        lambdaQueryWrapper.orderByDesc(GraphBigscreenDO::getCreateTime);
        List<GraphBigscreenDO> list = graphBigscreenService.list(lambdaQueryWrapper);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, list);
    }

    /**
     * 导出excel
     *
     * @param selections
     * @return
     */
    @AutoLog(value = "大屏-导出excel")
    @ApiOperation(value = "大屏-导出excel", notes = "大屏-导出excel")
    @GetMapping(value = "/export-xls")
    public ModelAndView exportXls(@ApiParam("id数组") @RequestParam(name = "selections", required = false) List<String> selections,
                                  @RequestParam(name = "graphScreenIsTemplate", required = false) Integer graphScreenIsTemplate) {
        return graphBigscreenService.exportXls(selections, graphScreenIsTemplate);
    }

    /**
     * 通过excel导入数据
     *
     * @param file
     * @return
     */
    @AutoLog(value = "大屏-导入excel")
    @ApiOperation(value = "大屏-导入excel", notes = "大屏-导入excel")
    @PostMapping(value = "/import-xls")
    public Result<Object> importExcel(@ApiParam("Excel文件") MultipartFile file) {
        return Result.OK(graphBigscreenService.importExcel(file));
    }

}
