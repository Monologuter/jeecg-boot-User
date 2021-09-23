package org.jeecg.modules.onlgraph.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.onlgraph.entity.GraphReportCellDO;
import org.jeecg.modules.onlgraph.mapper.GraphReportCellMapper;
import org.jeecg.modules.onlgraph.service.IGraphReportCellService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author system
 * @since 2021-09-09
 */
@Service
public class IGraphReportCellServiceImpl extends ServiceImpl<GraphReportCellMapper, GraphReportCellDO> implements IGraphReportCellService {

    @Autowired
    private GraphReportCellMapper graphReportCellMapper;

    @Override
    public boolean updateById(GraphReportCellDO entity) {
        return graphReportCellMapper.updateById(entity) == 1;
    }

    @Override
    public boolean save(GraphReportCellDO entity) {
        return graphReportCellMapper.insert(entity) == 1;
    }

    @Override
    public boolean removeByIds(List<String> ids) {
        return graphReportCellMapper.deleteBatchIds(ids) >= 1;
    }

    @Override
    public GraphReportCellDO getById(String id) {
        return graphReportCellMapper.selectById(id);
    }

    @Override
    public IPage<GraphReportCellDO> page(Page<GraphReportCellDO> page) {
        return graphReportCellMapper.selectPage(page, null);
    }

}
