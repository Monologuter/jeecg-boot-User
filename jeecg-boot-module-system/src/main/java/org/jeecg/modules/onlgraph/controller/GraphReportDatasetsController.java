package org.jeecg.modules.onlgraph.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.entity.GraphReportDatasetsDO;
import org.jeecg.modules.onlgraph.service.IGraphReportDatasetsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author yanzhicheng
 * @since 2021-09-13
 */
@Api(tags = "报表数据集")
@RestController
@RequestMapping("/onlgraph/graph-report-datasets")
@Slf4j
public class GraphReportDatasetsController extends JeecgController<GraphReportDatasetsDO, IGraphReportDatasetsService> {

    @Autowired
    private IGraphReportDatasetsService IGraphReportDatasetsService;

    /**
     * 添加 POST请求方式
     *
     * @param graphReportDatasets 添加的对象
     * @return Result
     */
    @AutoLog(value = "数据集-添加")
    @ApiOperation("数据集-添加")
    @PostMapping("/save")
    public Result save(@RequestBody GraphReportDatasetsDO graphReportDatasets) {
        IGraphReportDatasetsService.save(graphReportDatasets);
        return Result.OK(OnlgraphConstant.GRAPH_ADD_SUCCESS, graphReportDatasets);
    }

    /**
     * 修改 PUT请求方式
     *
     * @param graphReportDatasets 修改的对象
     * @return Result
     */
    @AutoLog(value = "数据集-修改")
    @ApiOperation("数据集-修改")
    @PutMapping("/update")
    public Result update(@RequestBody GraphReportDatasetsDO graphReportDatasets) {
        IGraphReportDatasetsService.updateById(graphReportDatasets);
        return Result.OK(OnlgraphConstant.GRAPH_UPDATE_SUCCESS, graphReportDatasets);
    }

    /**
     * 根据Id删除
     *
     * @param id String 类型
     * @return Result
     */
    @AutoLog(value = "数据集-删除")
    @ApiOperation("数据集-删除")
    @DeleteMapping("/delete")
    public Result delete(@RequestParam(name = "id") String id) {
        IGraphReportDatasetsService.removeById(id);
        return Result.OK(OnlgraphConstant.GRAPH_DELETE_SUCCESS, id);
    }

    /**
     * 批量删除
     *
     * @param ids String 类型 List 集合
     * @return Result
     */
    @AutoLog("数据集-批量删除")
    @ApiOperation("数据集-批量删除")
    @DeleteMapping("/delete-all")
    public Result deleteAll(@RequestBody List<String> ids) {
        IGraphReportDatasetsService.removeByIds(ids);
        return Result.OK(OnlgraphConstant.GRAPH_DELETE_BATCH_SUCCESS, ids);
    }

    /**
     * 通过reportId查询  支持GET
     *
     * @param reportId 查找对象的主键ID
     * @return Result
     */
    @AutoLog("数据集-通过reportId查询")
    @ApiOperation("数据集-通过reportId查询")
    @GetMapping("/select")
    public Result select(String reportId) {
        List<GraphReportDatasetsDO> graphReportDatasets = IGraphReportDatasetsService.getByReportId(reportId);
        return Result.OK(OnlgraphConstant.GRAPH_QUERY_SUCCESS, graphReportDatasets);
    }

}
