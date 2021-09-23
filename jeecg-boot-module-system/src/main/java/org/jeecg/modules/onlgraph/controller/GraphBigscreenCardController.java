package org.jeecg.modules.onlgraph.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecg.modules.onlgraph.service.IGraphBigscreenCardService;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;


/**
 * @author wangjiahao, wangshuang
 * @Description: 大屏-卡片
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Api(tags = "大屏-卡片")
@RestController
@RequestMapping("/onlgraph/graph_bigscreen_card")
@Slf4j
public class GraphBigscreenCardController extends JeecgController<GraphBigscreenCardDO, IGraphBigscreenCardService> {
    @Autowired
    private IGraphBigscreenCardService graphBigscreenCardService;

    /**
     * 分页列表查询
     *
     * @param graphBigscreenCard
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "大屏-卡片-分页列表查询")
    @ApiOperation(value = "大屏-卡片-分页列表查询", notes = "大屏-卡片-分页列表查询")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(GraphBigscreenCardDO graphBigscreenCard,
                                   @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                   HttpServletRequest req) {
        QueryWrapper<GraphBigscreenCardDO> queryWrapper = QueryGenerator.initQueryWrapper(graphBigscreenCard, req.getParameterMap());
        Page<GraphBigscreenCardDO> page = new Page<>(pageNo, pageSize);
        IPage<GraphBigscreenCardDO> pageList = graphBigscreenCardService.page(page, queryWrapper);
        return Result.OK(pageList);
    }

    /**
     * 添加
     *
     * @param graphBigscreenCard
     * @return
     */
    @AutoLog(value = "大屏-卡片-添加")
    @ApiOperation(value = "大屏-卡片-添加", notes = "大屏-卡片-添加")
    @PostMapping(value = "/add")
    public Result<?> add(@RequestBody GraphBigscreenCardDO graphBigscreenCard) {
        graphBigscreenCardService.save(graphBigscreenCard);
        return Result.OK("添加成功！");
    }

    /**
     * 编辑
     *
     * @param graphBigscreenCard
     * @return
     */
    @AutoLog(value = "大屏-卡片-编辑")
    @ApiOperation(value = "大屏-卡片-编辑", notes = "大屏-卡片-编辑")
    @PutMapping(value = "/edit")
    public Result<?> edit(@RequestBody GraphBigscreenCardDO graphBigscreenCard) {
        graphBigscreenCardService.updateById(graphBigscreenCard);
        return Result.OK("编辑成功!");
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @AutoLog(value = "大屏-卡片-通过id删除")
    @ApiOperation(value = "大屏-卡片-通过id删除", notes = "大屏-卡片-通过id删除")
    @DeleteMapping(value = "/delete")
    public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
        graphBigscreenCardService.removeById(id);
        return Result.OK("删除成功!");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @AutoLog(value = "大屏-卡片-批量删除")
    @ApiOperation(value = "大屏-卡片-批量删除", notes = "大屏-卡片-批量删除")
    @DeleteMapping(value = "/deleteBatch")
    public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
        this.graphBigscreenCardService.removeByIds(Arrays.asList(ids.split(",")));
        return Result.OK("批量删除成功!");
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @AutoLog(value = "大屏-卡片-通过id查询")
    @ApiOperation(value = "大屏-卡片-通过id查询", notes = "大屏-卡片-通过id查询")
    @GetMapping(value = "/queryById")
    public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
        GraphBigscreenCardDO graphBigscreenCard = graphBigscreenCardService.getById(id);
        if (graphBigscreenCard == null) {
            return Result.error("未找到对应数据");
        }
        return Result.OK(graphBigscreenCard);
    }

}
