package org.jeecg.modules.onlgraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.onlgraph.entity.GraphReportDatasetsDO;

import java.util.List;

/**
 * 服务类
 *
 * @author yanzhicheng
 * @since 2021-09-09
 */
public interface IGraphReportDatasetsService extends IService<GraphReportDatasetsDO> {

    /**
     * 根据报表ID查询数据集
     *
     * @param reportId
     */
    List<GraphReportDatasetsDO> getByReportId(String reportId);

}
