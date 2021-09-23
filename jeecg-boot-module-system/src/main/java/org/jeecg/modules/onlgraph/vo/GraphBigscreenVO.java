package org.jeecg.modules.onlgraph.vo;

import lombok.Data;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecgframework.poi.excel.annotation.ExcelCollection;

import java.util.List;

/**
 * @author wangjiahao, wangshuang
 * @Description: 接受前端对象
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
@Data
public class GraphBigscreenVO extends GraphBigscreenDO {

    @ExcelCollection(name = "卡片", orderNum = "1")
    private List<GraphBigscreenCardDO> GraphBigscreenCards;

}
