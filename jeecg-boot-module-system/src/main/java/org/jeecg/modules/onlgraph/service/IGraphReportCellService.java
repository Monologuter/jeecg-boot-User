package org.jeecg.modules.onlgraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.onlgraph.entity.GraphReportCellDO;

import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author system
 * @since 2021-09-09
 */
public interface IGraphReportCellService extends IService<GraphReportCellDO> {

    boolean updateById(GraphReportCellDO entity);

    boolean save(GraphReportCellDO entity);

    boolean removeByIds(List<String> ids);

    GraphReportCellDO getById(String id);

    IPage<GraphReportCellDO> page(Page<GraphReportCellDO> page);

}
