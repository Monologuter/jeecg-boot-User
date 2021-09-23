package org.jeecg.modules.onlgraph.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.entity.*;
import org.jeecg.modules.onlgraph.mapper.GraphReportMapper;
import org.jeecg.modules.onlgraph.service.IGraphReportService;
import org.jeecg.modules.onlgraph.vo.GraphReportVO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpServerErrorException;


import javax.transaction.Transactional;
import java.util.List;

/**
 * 服务实现类
 *
 * @author wangshuang
 * @Description: 报表
 * @company DXC.technology
 * @create 2021-09-10
 * @Version: V1.0
 */
@Slf4j
@Service
public class IGraphReportServiceImpl extends ServiceImpl<GraphReportMapper, GraphReportDO> implements IGraphReportService {

    @Autowired
    private GraphReportMapper graphReportMapper;

    /**
     * 保存
     *
     * @param graphReportDO
     */
    @Transactional
    @Override
    public GraphReportDO saveAll(GraphReportDO graphReportDO) {
        //插入报表数据
        graphReportMapper.insert(graphReportDO);
        return graphReportDO;
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
        GraphReportDO graphReportDO = graphReportMapper.selectById(id);
        //判断id查询是否有记录,若已删除抛异常
        if (ObjectUtil.isNotEmpty(graphReportDO) &&
                graphReportDO.getDelFlag().equals(OnlgraphConstant.GRAPH_DEL_FLAG_FLASE)) {
            //修改删除状态（删除）并更新数据库
            graphReportDO.setDelFlag(OnlgraphConstant.GRAPH_DEL_FLAG_SUCCESS);
            graphReportMapper.updateById(graphReportDO);
        } else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    OnlgraphConstant.GRAPH_QUERY_FAILED);
        }
    }

    /**
     * 根据ids批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void removeBatch(List<String> ids) {
        //根据ids，in操作实现批量删除
        this.update(Wrappers.<GraphReportDO>lambdaUpdate()
                .set(GraphReportDO::getDelFlag, OnlgraphConstant.GRAPH_DEL_FLAG_SUCCESS)
                .in(GraphReportDO::getId, ids));
    }

    /**
     * 根据ID更新数据
     *
     * @param graphReportDO
     */
    @Transactional
    @Override
    public void updateAllById(GraphReportDO graphReportDO) {
        //判断id是否为空，为空则报异常
        if (StringUtils.isEmpty(graphReportDO.getId())) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                    OnlgraphConstant.GRAPH_UPDATE_ID_EMPTY);
        }
        //更新主表数据
        graphReportMapper.updateById(graphReportDO);
    }

    /**
     * 根据ID查询
     *
     * @param id
     */
    @Transactional
    @Override
    public GraphReportVO getReports(String id) {
        return graphReportMapper.getReport(id);
    }

}
