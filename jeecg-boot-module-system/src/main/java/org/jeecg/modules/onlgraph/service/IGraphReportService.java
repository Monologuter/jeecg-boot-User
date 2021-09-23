package org.jeecg.modules.onlgraph.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.onlgraph.entity.GraphReportDO;
import org.jeecg.modules.onlgraph.vo.GraphReportVO;

import java.util.List;

/**
 * 服务类
 *
 * @author wangshuang
 * @Description: 报表
 * @company DXC.technology
 * @create 2021-09-10
 * @Version: V1.0
 */
public interface IGraphReportService extends IService<GraphReportDO> {

    /**
     * 通过id保存
     *
     * @param graphReportDO
     */
    GraphReportDO saveAll(GraphReportDO graphReportDO);

    /**
     * 通过id删除
     *
     * @param id
     */
    void removeAll(String id);

    /**
     * 通过ids批量删除
     *
     * @param ids
     */
    void removeBatch(List<String> ids);

    /**
     * 根据ID更新数据
     *
     * @param graphReportDO
     */
    void updateAllById(GraphReportDO graphReportDO);

    /**
     * 根据id查询报表配置
     *
     * @param id 主键ID
     * @return
     */
    GraphReportVO getReports(String id);
}