package org.jeecg.modules.onlgraph.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;

import java.util.List;
import java.util.Map;

/**
 * @author wangjiahao, wangshuang
 * @Description: 大屏
 * @company DXC.technology
 * @create 2021-05-31
 * @Version: V2.0
 */
public interface GraphBigscreenMapper extends BaseMapper<GraphBigscreenDO> {

    /**
     * 根据ID查询数据
     *
     * @param id
     * @return
     */
    GraphBigscreenVO getBigscreen(String id);

    /**
     * 根据sql查询数据
     *
     * @param sql
     * @return
     */
    List<Map<String, Object>> executeSeleteBySql(@Param("sql") String sql);
}
