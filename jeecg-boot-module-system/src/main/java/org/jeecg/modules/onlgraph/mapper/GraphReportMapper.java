package org.jeecg.modules.onlgraph.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.onlgraph.entity.GraphReportDO;
import org.jeecg.modules.onlgraph.vo.GraphReportVO;

/**
 * @author wangshuang
 * @Description: 报表
 * @company DXC.technology
 * @create 2021-09-10
 * @Version: V1.0
 */
public interface GraphReportMapper extends BaseMapper<GraphReportDO> {

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    GraphReportVO getReport(String id);
}
