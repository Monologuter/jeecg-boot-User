package org.jeecg.modules.onlgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.onlgraph.common.OnlgraphConstant;
import org.jeecg.modules.onlgraph.entity.GraphReportDatasetsDO;
import org.jeecg.modules.onlgraph.mapper.GraphReportDatasetsMapper;
import org.jeecg.modules.onlgraph.service.IGraphReportDatasetsService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 服务实现类
 *
 * @author yanzhicheng
 * @since 2021-09-09
 */
@Service
public class IGraphReportDatasetsServiceImpl extends ServiceImpl<GraphReportDatasetsMapper, GraphReportDatasetsDO> implements IGraphReportDatasetsService {

    @Autowired
    private GraphReportDatasetsMapper graphReportDatasetsMapper;

    /**
     * 根据报表ID查询数据集
     *
     * @param reportId
     */
    @Override
    @Transactional
    public List<GraphReportDatasetsDO> getByReportId(String reportId) {
        return lambdaQuery().eq(GraphReportDatasetsDO::getReportId, reportId)
                .eq(GraphReportDatasetsDO::getDelFlag, OnlgraphConstant.GRAPH_DEL_FLAG_FLASE)
                .list();
    }

}
