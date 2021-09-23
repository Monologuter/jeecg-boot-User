package org.jeecg.modules.onlgraph.mapper;

import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;

import java.util.List;
import java.util.Map;

/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_head图表配置表汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
public interface OnlCfgraphHeadMapper extends BaseMapper<OnlCfgraphHeadDO> {

    /**
     * 根据ID查询主附表数据
     *
     * @param id
     * @return
     */
    OnlCfgraphHeadVO getOnlCfgraphHeads(String id);


    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeSeleteBySql(@Param("sql") String sql);
}
