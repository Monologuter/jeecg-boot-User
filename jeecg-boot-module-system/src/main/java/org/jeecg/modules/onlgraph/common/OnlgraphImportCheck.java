package org.jeecg.modules.onlgraph.common;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import org.jeecg.modules.onlgraph.service.IOnlCfgraphHeadService;
import org.jeecgframework.poi.excel.entity.result.ExcelVerifyHanlderResult;
import org.jeecgframework.poi.handler.inter.IExcelVerifyHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author wangjiahao, zhangqijian, wangshuang
 * @Description: atuo_poi自定义数据校验
 * @company DXC.technology
 * @create  2021-03-29 9:41
 */
@Component
public class OnlgraphImportCheck implements IExcelVerifyHandler {

    @Autowired
    private IOnlCfgraphHeadService onlCfgraphHeadService;

    @Override
    public String[] getNeedVerifyFields() {
        return new String[0];
    }

    @Override
    public void setNeedVerifyFields(String[] arr) {
        //实现接口需要重写此方法
    }

    /**
     * atuo_poi自定义数据校验
     *
     * @param obj   主表对象
     * @param name  列名
     * @param value 属性值
     * @return
     */
    @Override
    public ExcelVerifyHanlderResult verifyHandler(Object obj, String name, Object value) {
        //获取编码 查询数据库校验
        LambdaQueryWrapper<OnlCfgraphHeadDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getGraphCode, value.toString());
        lambdaQueryWrapper.eq(OnlCfgraphHeadDO::getDelFlag, OnlgraphMessageConstant.GRAPH_HEAD_DEL_FLAG_FLASE);
        if (onlCfgraphHeadService.count(lambdaQueryWrapper) > 0) {
            return new ExcelVerifyHanlderResult(false, OnlgraphMessageConstant.GRAPH_HEAD_CODE_CHECK_FALSE);
        }
        return new ExcelVerifyHanlderResult(true, OnlgraphMessageConstant.GRAPH_HEAD_CODE_CHECK_SUCCESS);
    }
}
