package org.jeecg.modules.onlgraph.controller;

import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.service.IOnlCfgraphFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;


/**
 * @author wangjiahao, zhangqijian
 * @Description: online_configration_graph_field图表配置表单汇总表
 * @company DXC.technology
 * @create 2021-03-01 13:58
 */
@Api(tags = "onl图表配置表单汇总表")
@RestController
@RequestMapping("/onlgraph/onl-cfgraph-field")
@Slf4j
public class OnlCfgraphFieldController extends JeecgController<OnlCfgraphFieldDO, IOnlCfgraphFieldService> {
}
