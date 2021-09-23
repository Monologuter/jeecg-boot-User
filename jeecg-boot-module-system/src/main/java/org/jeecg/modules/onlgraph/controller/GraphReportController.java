package org.jeecg.modules.onlgraph.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.entity.GraphReportDO;
import org.jeecg.modules.onlgraph.service.IGraphReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.onlgraph.vo.GraphReportVO;
import org.springframework.beans.factory.annotation.Autowired;

import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * graphReport前端控制器  RestController注解 将结果以JSON形式返回
 *
 * @author system
 * @Description: 报表
 * @company DXC.technology
 * @create 2021-09-09
 * @Version: V1.0
 */
@Api(tags = "报表")
@RestController
@RequestMapping("/onlgraph/report-form")
@Slf4j
public class GraphReportController extends JeecgController<GraphReportDO, IGraphReportService> {

    @Autowired
    private IGraphReportService graphReportService;

    /**
     * 分页列表查询
     *
     * @param graphReportDO
     * @param pageNo        当前页
     * @param pageSize      每页最大数据数
     * @return Result
     */
    @AutoLog(value = "报表-分页列表查询")
    @ApiOperation(value = "报表-分页列表查询", notes = "报表-分页列表查询")
    @GetMapping(value = "/list")
    public Result<IPage<GraphReportDO>> queryPageList(GraphReportDO graphReportDO, @RequestParam(name = "pageNo",
            defaultValue = "1") Integer pageNo, @RequestParam(name = "pageSize",
            defaultValue = "10") Integer pageSize) {
        LambdaQueryWrapper<GraphReportDO> queryWrapper = new LambdaQueryWrapper<>();
        //筛选过滤已删除
        queryWrapper.eq(GraphReportDO::getDelFlag, OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
        //根据所传模板字段值动态匹配(报表为0 or 模板为1)
        queryWrapper.eq(GraphReportDO::getIsTemplate, graphReportDO.getIsTemplate());
        //根据报表名称进行左右模糊查询
        queryWrapper.like(StringUtils.isNotBlank(graphReportDO.getGraphReportName()),
                GraphReportDO::getGraphReportName, graphReportDO.getGraphReportName());
        //根据报表编码进行左右模糊查询
        queryWrapper.like(StringUtils.isNotBlank(graphReportDO.getGraphReportCode()),
                GraphReportDO::getGraphReportCode, graphReportDO.getGraphReportCode());
        //根据创建时间排序
        queryWrapper.orderByDesc(GraphReportDO::getCreateTime);
        //分页
        Page<GraphReportDO> page = new Page<>(pageNo, pageSize);
        IPage<GraphReportDO> pageList = graphReportService.page(page, queryWrapper);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, pageList);
    }

    /**
     * 添加
     *
     * @param graphReportDO 保存的对象
     * @return Result
     */
    @AutoLog(value = "报表-添加")
    @ApiOperation(value = "报表-添加", notes = "报表-添加")
    @PostMapping(value = "/save-all")
    public Result<GraphReportDO> saveAll(@RequestBody GraphReportDO graphReportDO) {
        return Result.OK(graphReportService.saveAll(graphReportDO));
    }

    /**
     * 编辑（更新）
     *
     * @param graphReportDO 修改的对象
     * @return Result
     */
    @AutoLog(value = "报表-编辑")
    @ApiOperation(value = "报表-编辑", notes = "报表-编辑")
    @PutMapping(value = "/edit-all")
    public Result<String> editAll(@RequestBody GraphReportDO graphReportDO) {
        graphReportService.updateAllById(graphReportDO);
        return Result.OK(OnlgraphConstant.GRAPH_UPDATE_SUCCESS);
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return Result
     */
    @AutoLog(value = "报表-通过id删除")
    @ApiOperation(value = "报表-通过id删除", notes = "报表-通过id删除")
    @DeleteMapping(value = "/delete-all")
    public Result<String> deleteAll(@RequestParam(name = "id") String id) {
        graphReportService.removeAll(id);
        return Result.OK(OnlgraphConstant.GRAPH_DELETE_SUCCESS);
    }

    /**
     * 批量删除
     *
     * @param ids String 类型 List 集合
     * @return Result
     */
    @AutoLog(value = "报表-批量删除")
    @ApiOperation(value = "报表-批量删除", notes = "报表-批量删除")
    @DeleteMapping(value = "/delete-batch")
    public Result<String> deleteBatch(@RequestParam(name = "ids") List<String> ids) {
        graphReportService.removeBatch(ids);
        return Result.OK(OnlgraphConstant.GRAPH_DELETE_BATCH_SUCCESS);
    }

    /**
     * 通过id查询
     *
     * @param id 查找对象的主键ID
     * @return Result
     */
    @AutoLog(value = "报表-通过id查询")
    @ApiOperation(value = "报表-通过id查询", notes = "报表-通过id查询")
    @GetMapping(value = "/query-id")
    public Result<GraphReportVO> queryId(@RequestParam(name = "id") String id) {
        Result<GraphReportVO> result = new Result<>();
        //通过id查询报表
        GraphReportVO graphReportVO = graphReportService.getReports(id);
        //判断是否查询到数据，若有直接return
        if (ObjectUtil.isEmpty(graphReportVO)) {
            result.success(OnlgraphConstant.GRAPH_QUERY_FAILED);
        } else {
            result.setResult(graphReportVO);
            result.success(OnlgraphConstant.GRAPH_QUERY_SUCCESS);
        }
        return result;
    }

    /**
     * 编码校验
     *
     * @param graphReportCode 编码
     * @return
     */
    @AutoLog(value = "报表-编码校验")
    @ApiOperation(value = "报表-编码校验", notes = "报表-编码校验")
    @GetMapping(value = "/report-code/{reportCode}")
    public Result<String> codeCheck(@PathVariable(name = "reportCode") String graphReportCode) {
        Result<String> result = new Result<>();
        //匹配Code
        LambdaQueryWrapper<GraphReportDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(GraphReportDO::getGraphReportCode, graphReportCode);
        //判断报表设计器删除状态为未删除
        lambdaQueryWrapper.eq(GraphReportDO::getDelFlag, 0);
        //匹配code是否存在，若结果大于0则校验失败
        if (graphReportService.count(lambdaQueryWrapper) > 0) {
            return result.error500(OnlgraphConstant.GRAPH_CHECK_FAILED);
        } else {
            result.success(OnlgraphConstant.GRAPH_CHECK_SUCCESS);
        }
        return result;
    }

}
