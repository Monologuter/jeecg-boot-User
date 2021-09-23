package org.jeecg.modules.onlgraph.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.common.util.SqlInjectionUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.onlgraph.common.OnlgraphImportCheck;
import org.jeecg.modules.onlgraph.common.OnlgraphMessageConstant;
import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import org.jeecg.modules.onlgraph.service.IOnlCfgraphHeadService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_head图表配置表汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Api(tags = "onl图表配置表汇总表")
@RestController
@RequestMapping("/onlgraph/onl-cfgraph-head")
@Slf4j
public class OnlCfgraphHeadController extends JeecgController<OnlCfgraphHeadDO, IOnlCfgraphHeadService> {

    //将Service注入Web层

    @Autowired
    private IOnlCfgraphHeadService onlCfgraphHeadService;

    @Autowired
    private ISysPermissionService iSysPermissionService;

    @Autowired
    private OnlgraphImportCheck onlgraphImportCheck;

    /**
     * 分页列表查询
     *
     * @param onlCfgraphHeadDO 在线图表配置主表实体类
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "onl图表配置表汇总表-分页列表查询")
    @ApiOperation(value = "onl图表配置表汇总表-分页列表查询", notes = "onl图表配置表汇总表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<OnlCfgraphHeadDO>> queryPageList(OnlCfgraphHeadDO onlCfgraphHeadDO,
                                                         @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                         HttpServletRequest req) {
        LambdaQueryWrapper<OnlCfgraphHeadDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据对象条件进行模糊查询
        lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getDelFlag, OnlgraphMessageConstant.GRAPH_HEAD_DEL_FLAG_FLASE);
        lambdaQueryWrapper.like(StringUtils.isNotBlank(onlCfgraphHeadDO.getGraphName()), OnlCfgraphHeadDO::getGraphName, onlCfgraphHeadDO.getGraphName());
        lambdaQueryWrapper.like(StringUtils.isNotBlank(onlCfgraphHeadDO.getGraphCode()), OnlCfgraphHeadDO::getGraphCode, onlCfgraphHeadDO.getGraphCode());
        lambdaQueryWrapper.orderByDesc(OnlCfgraphHeadDO::getCreateTime);
        Page<OnlCfgraphHeadDO> page = new Page<>(pageNo, pageSize);
        IPage<OnlCfgraphHeadDO> pageList = onlCfgraphHeadService.page(page, lambdaQueryWrapper);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, pageList);
    }


    /**
     * 保存
     *
     * @param onlCfgraphHeadVo 在线图表配置主表,附表集合类
     * @return
     */
    @AutoLog(value = "onl图表配置表-添加图表配置")
    @ApiOperation(value = "onl图表配置表汇总表-添加图表配置", notes = "onl图表配置表汇总表-添加图表配置")
    @PostMapping(value = "/save-all")
    public Result<String> saveAll(@RequestBody OnlCfgraphHeadVO onlCfgraphHeadVo) {
        onlCfgraphHeadService.saveAll(onlCfgraphHeadVo);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_ADD_SUCCESS);
    }

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    @AutoLog(value = "onl图表配置表-SQL解析")
    @ApiOperation(value = "onl图表配置表汇总表-SQL解析", notes = "onl图表配置表汇总表-SQL解析")
    @GetMapping(value = "/sql-parsing")
    public Result<List<Map<String, Object>>> executeSelectBySql(@RequestParam(name = "sql", required = true) String sql,
                                                                @RequestParam(name = "dbKey", required = false) String dbKey) throws SQLException {
        //sql解析字段进行校验
        SqlInjectionUtil.specialFilterContentForOnlineCgfraph(sql);
        List<Map<String, Object>> mapList = null;
        //根据参数选择sql解析数据源
        if (StringUtils.isNotBlank(dbKey)) {
            mapList = onlCfgraphHeadService.executeSelectBySql(sql, dbKey);
        } else {
            mapList = onlCfgraphHeadService.executeSelectBySqlNative(sql);
        }
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, mapList);
    }

    /**
     * 获取数据源表列表
     *
     * @param dbKey
     * @return
     */
    @AutoLog(value = "onl图表配置表-数据源表名列表")
    @ApiOperation(value = "onl图表配置表汇总表-数据源表名列表", notes = "onl图表配置表汇总表-数据源表名列表")
    @GetMapping(value = "/db-list")
    public Result<List<Map<String, Object>>> getDbTableList(@RequestParam(name = "dbKey") String dbKey) {
        List<Map<String, Object>> result = onlCfgraphHeadService.getDbTableList(dbKey);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, result);
    }

    /**
     * 获取数据源表内字段
     *
     * @param dbKey
     * @param tableName
     * @return
     */
    @AutoLog(value = "onl图表配置表-数据源表内字段列表")
    @ApiOperation(value = "onl图表配置表汇总表-数据源表内字段列表", notes = "onl图表配置表汇总表-数据源表内字段列表")
    @GetMapping(value = "/db-items")
    public Result<List<Map<String, Object>>> getDbTableItems(@RequestParam(name = "dbKey") String dbKey,
                                                             @RequestParam(name = "tableName") String tableName) {
        List<Map<String, Object>> result = onlCfgraphHeadService.getDbTableItems(dbKey, tableName);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS, result);
    }

    /**
     * 通过id查询
     *
     * @param id onlCfgraphHead主键ID
     * @return
     */
    @AutoLog(value = "onl图表配置表汇总表-通过id查询图表配置")
    @ApiOperation(value = "onl图表配置表汇总表-通过id查询图表配置", notes = "onl图表配置表汇总表-通过id查询图表配置")
    @GetMapping(value = "/query-id")
    public Result<OnlCfgraphHeadVO> queryId(@RequestParam(name = "id", required = true) String id) {
        Result<OnlCfgraphHeadVO> result = new Result<>();
        OnlCfgraphHeadVO onlCfgraphHeadVo = onlCfgraphHeadService.getOnlCfgraphHeads(id);
        if (onlCfgraphHeadVo == null) {
            result.success(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_FAILED);
        } else {
            result.setResult(onlCfgraphHeadVo);
            result.success(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS);
        }
        return result;
    }


    /**
     * 根据ID更新数据
     *
     * @param onlCfgraphHeadVo
     * @return
     */
    @AutoLog(value = "onl图表配置-编辑更新图表配置")
    @ApiOperation(value = "onl图表配置-编辑更新图表配置", notes = "onl图表配置-编辑更新图表配置")
    @PutMapping(value = "/edit-all")
    public Result<String> editAll(@RequestBody OnlCfgraphHeadVO onlCfgraphHeadVo) {
        onlCfgraphHeadService.updateAllById(onlCfgraphHeadVo);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_UPDATE_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "onl图表配置表汇总表-通过id删除")
    @ApiOperation(value = "onl图表配置表汇总表-通过id删除", notes = "onl图表配置表汇总表-通过id删除")
    @DeleteMapping(value = "/delete-all")
    public Result<String> deleteAll(@RequestParam(name = "id", required = true) String id) {
        onlCfgraphHeadService.removeAll(id);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "onl图表配置表汇总表-批量删除")
    @ApiOperation(value = "onl图表配置表汇总表-批量删除", notes = "onl图表配置表汇总表-批量删除")
    @DeleteMapping(value = "/delete-batch")
    public Result<String> deleteBatch(@ApiParam("id数组") @RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<>();
        String[] id = ids.split(",");
        for (int i = 0; i < id.length; i++) {
            onlCfgraphHeadService.removeAll(id[i]);
        }
        result.success(OnlgraphMessageConstant.GRAPH_HEAD_DELETE_BATCH_SUCCESS);
        return result;
    }


    /**
     * 编码校验
     *
     * @param graphCode 编码
     * @return
     */
    @AutoLog(value = "onl图表配置-编码校验")
    @ApiOperation(value = "onl图表配置-编码校验", notes = "onl图表配置-编码校验")
    @GetMapping(value = "/graph-code/{graphCode}")
    public Result<Object> codeCheck(@PathVariable(name = "graphCode", required = true) String graphCode) {
        Result<Object> result = new Result<>();
        LambdaQueryWrapper<OnlCfgraphHeadDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getGraphCode, graphCode);
        lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getDelFlag, OnlgraphMessageConstant.GRAPH_HEAD_DEL_FLAG_FLASE);
        if (onlCfgraphHeadService.count(lambdaQueryWrapper) > 0) {
            return Result.error(200, OnlgraphMessageConstant.GRAPH_HEAD_CHECK_FAILED);
        } else {
            result.success(OnlgraphMessageConstant.GRAPH_HEAD_CHECK_SUCCESS);
        }
        return result;
    }


    /**
     * 动态路由配置
     *
     * @param sysPermission 菜单权限配置
     * @return
     */
    @AutoLog(value = "onl图表配置-动态路由配置")
    @ApiOperation(value = "onl图表配置-动态路由配置", notes = "onl图表配置-动态路由配置")
    @PostMapping(value = "/menu_routing")
    public Result<Object> menuRouting(@RequestBody SysPermission sysPermission) {
        //1.判断是否第一次生成路由地址,有就直接返回
        if (ObjectUtil.isNotEmpty(sysPermission.getId())) {
            iSysPermissionService.updateById(sysPermission);
            return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_UPDATE_SUCCESS);
        }
        //2.获取前端传递参数生成动态路由菜单
        onlCfgraphHeadService.menuRouting(sysPermission);
        return Result.OK(OnlgraphMessageConstant.GRAPH_HEAD_MENU_ROUTING_SUCCESS);
    }

    /**
     * 通过id查询菜单配置
     *
     * @param id onlCfgraphHead主键ID
     * @return
     */
    @AutoLog(value = "onl图表配置表汇总表-通过id查询菜单配置")
    @ApiOperation(value = "onl图表配置表汇总表-通过id查询菜单配置", notes = "onl图表配置表汇总表-通过id查询菜单配置")
    @GetMapping(value = "/query-permission")
    public Result<SysPermission> queryPermission(@RequestParam(name = "id", required = true) String id) {
        Result<SysPermission> result = new Result<>();
        //1.判断是否第一次生成路由地址,有就直接返回
        LambdaQueryWrapper<SysPermission> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SysPermission::getUrl, OnlgraphMessageConstant.GRAPH_HEAD_PERMISSION_URL + id);
        SysPermission sysPermission = iSysPermissionService.getOne(lambdaQueryWrapper);
        if (sysPermission == null) {
            result.success(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_FAILED);
        } else {
            result.setResult(sysPermission);
            result.success(OnlgraphMessageConstant.GRAPH_HEAD_QUERY_SUCCESS);
        }
        return result;
    }

    /**
     * 导出excel
     *
     * @param request
     * @param onlCfgraphHeadDO 主表实体类
     */
    @AutoLog(value = "onl图表配置-导出excel")
    @ApiOperation(value = "onl图表配置-导出excel", notes = "onl图表配置-导出excel")
    @GetMapping(value = "/export-xls")
    public ModelAndView exportXls(HttpServletRequest request, OnlCfgraphHeadDO onlCfgraphHeadDO) {
        return exportXls(request, onlCfgraphHeadDO, OnlCfgraphHeadDO.class, "图表总表");
    }

    /**
     * 导出execl实现类
     *
     * @param request
     * @param onlCfgraphHeadDO
     * @param clazz
     * @param title
     * @return
     */
    @Override
    protected ModelAndView exportXls(HttpServletRequest request, OnlCfgraphHeadDO onlCfgraphHeadDO, Class<OnlCfgraphHeadDO> clazz, String title) {
        // Step.1 组装查询条件
        QueryWrapper<OnlCfgraphHeadDO> queryWrapper = QueryGenerator.initQueryWrapper(onlCfgraphHeadDO, request.getParameterMap());
        LoginUser sysUser = (LoginUser) SecurityUtils.getSubject().getPrincipal();

        // Step.2 获取导出数据
        List<OnlCfgraphHeadDO> pageList = onlCfgraphHeadService.list(queryWrapper);
        List<OnlCfgraphHeadDO> exportList = null;

        // 过滤选中数据
        String selections = request.getParameter("selections");
        if (oConvertUtils.isNotEmpty(selections)) {
            List<String> selectionList = Arrays.asList(selections.split(","));
            //目前后台先过滤逻辑删除为空的数据
            exportList = pageList.stream().filter(item -> selectionList.contains(item.getId())).filter(e -> e.getDelFlag().equals(0)).collect(Collectors.toList());
        } else {
            //目前后台先过滤逻辑删除为空的数据
            exportList = pageList.stream().filter(e -> e.getDelFlag().equals(0)).collect(Collectors.toList());
        }

        // Step.3 AutoPoi 导出Excel
        ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
        mv.addObject(NormalExcelConstants.FILE_NAME, title); //此处设置的filename无效 ,前端会重更新设置一下
        mv.addObject(NormalExcelConstants.CLASS, clazz);
        mv.addObject(NormalExcelConstants.PARAMS, new ExportParams(title + "图表", "导出人:" + sysUser.getRealname(), title));
        mv.addObject(NormalExcelConstants.DATA_LIST, exportList);
        return mv;
    }


    /**
     * 通过excel导入数据
     *
     * @param request
     * @param response
     * @return
     */
    @AutoLog(value = "onl图表配置-导入excel")
    @ApiOperation(value = "onl图表配置-导入excel", notes = "onl图表配置-导入excel")
    @PostMapping(value = "/import-xls")
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return importlExcel(request, response, OnlCfgraphHeadDO.class);
    }

    /**
     * 导入Excel方法
     *
     * @param request
     * @param response
     * @param onlCfgraphHeadDOs Class类型
     * @return
     * @throws Exception
     */
    protected Result<Object> importlExcel(HttpServletRequest request, HttpServletResponse response, Class<OnlCfgraphHeadDO> onlCfgraphHeadDOs) throws Exception {
        Result<Object> result = new Result<>();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
        for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
            // 获取上传文件对象
            MultipartFile file = entity.getValue();
            ImportParams params = new ImportParams();
            params.setTitleRows(2);
            params.setHeadRows(1);
            params.setNeedSave(true);
            //自定义校验
            params.setVerifyHanlder(onlgraphImportCheck);
            List<OnlCfgraphHeadDO> onlCfgraphList = ExcelImportUtil.importExcel(file.getInputStream(), onlCfgraphHeadDOs, params);
            if (!onlCfgraphList.isEmpty()) {
                //初始时间
                long start = System.currentTimeMillis();
                //批量插入数据
                onlCfgraphHeadService.saveBatch(onlCfgraphList);
                //获取通过校验后是否有生成菜单操作,存在即进行生成动态路由菜单
                onlCfgraphList.forEach(onlCfgraphHeadDO -> {
                    if (onlCfgraphHeadDO.getGraphPermissionStatus() == 1) {
                        SysPermission sysPermission = new SysPermission();
                        sysPermission.setUrl(OnlgraphMessageConstant.GRAPH_HEAD_PERMISSION_URL + onlCfgraphHeadDO.getId())
                                .setComponent(OnlgraphMessageConstant.GRAPH_HEAD_PERMISSION_COMPONENT)
                                .setComponentName(OnlgraphMessageConstant.GRAPH_HEAD_PERMISSION_NAME + onlCfgraphHeadDO.getGraphName())
                                .setName(OnlgraphMessageConstant.GRAPH_HEAD_PERMISSION_NAME + onlCfgraphHeadDO.getGraphName())
                                .setStatus("1").setRoute(true);
                        onlCfgraphHeadService.menuRouting(sysPermission);
                    }
                });
                //计算导入消耗得时间
                log.info("消耗时间" + (System.currentTimeMillis() - start) + "毫秒");
            }
            return result.success(OnlgraphMessageConstant.GRAPH_HEAD_FILE_IMPORT_SUCCESS + onlCfgraphList.size());
        }
        //导入失败,默认抛出code500
        throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, OnlgraphMessageConstant.GRAPH_HEAD_FILE_IMPORT_FALSE);
    }
}
