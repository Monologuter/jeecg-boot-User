package org.jeecg.modules.onlgraph.vo;

import lombok.Data;
import org.jeecg.modules.onlgraph.entity.GraphReportCellDO;
import org.jeecg.modules.onlgraph.entity.GraphReportDO;

import java.util.List;

/**
 * @author wangshuang
 * @Description: 接受前端对象
 * @company DXC.technology
 * @create 2021-09-10
 * @Version: V1.0
 */
@Data
public class GraphReportVO extends GraphReportDO {

    //主表对单元格一对多
    private List<GraphReportCellDO> GraphReportCells;
}
