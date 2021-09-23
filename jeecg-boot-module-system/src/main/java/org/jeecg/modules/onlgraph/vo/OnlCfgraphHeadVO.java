package org.jeecg.modules.onlgraph.vo;

import lombok.Data;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;

import java.util.List;

/**
 * @author wangjiahao, zhangqijian
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Data
public class OnlCfgraphHeadVO extends OnlCfgraphHeadDO {

    private List<OnlCfgraphFieldDO> onlCfgraphFields;
}
