package org.jeecg.modules.onlgraph.common;

import cn.hutool.core.util.ReUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.service.IGraphBigscreenService;
import org.jeecgframework.poi.excel.entity.result.ExcelVerifyHanlderResult;
import org.jeecgframework.poi.handler.inter.IExcelVerifyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangshuang
 * @Description: atuo_poi自定义数据校验
 * @company DXC.technology
 * @create  2021-07-26 13:22
 */
@Component
public class OnlgraphCheck implements IExcelVerifyHandler {

    @Autowired
    private IGraphBigscreenService graphBigscreenService;

    @Override
    public String[] getNeedVerifyFields() {
        return new String[0];
    }

    @Override
    public void setNeedVerifyFields(String[] arr) {
        //实现接口需要重写此方法
    }

    /**
     * atuo_poi自定义编码数据校验
     *
     * @param obj
     * @param name
     * @param value
     * @return
     */
    @Override
    public ExcelVerifyHanlderResult verifyHandler(Object obj, String name, Object value) {
        //正则表达式校验
        if (ReUtil.contains(OnlgraphConstant.GRAPH_CODE_CHECK_REGEX, value.toString())) {
            //获取编码 查询数据库校验
            LambdaQueryWrapper<GraphBigscreenDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(GraphBigscreenDO::getGraphBigscreenCode, value.toString());
            //匹配数据逻辑删除状态为未删除
            lambdaQueryWrapper.eq(GraphBigscreenDO::getDelFlag,
                    OnlgraphConstant.GRAPH_DEL_FLAG_FLASE);
            //匹配结果大于0,则校验失败
            if (graphBigscreenService.count(lambdaQueryWrapper) > 0) {
                return new ExcelVerifyHanlderResult(false,
                        OnlgraphConstant.GRAPH_CODE_CHECK_FALSE);
            }
            return new ExcelVerifyHanlderResult(true,
                    OnlgraphConstant.GRAPH_CODE_CHECK_SUCCESS);
        }
        return new ExcelVerifyHanlderResult(false,
                OnlgraphConstant.GRAPH_CODE_CHECK_FALSE);
    }
}