package org.jeecg.modules.onlgraph.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.UncategorizedSQLException;

import javax.annotation.Resource;

/**
 * @author  zhagnqijian wangjiahao
 * @company dxc
 * @create  2021-04-02 10:23
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OnlCfgraphHeadMapperTest {

    @Resource
    OnlCfgraphHeadMapper onlCfgraphHeadMapper;

    @Test
    void getOnlCfgraphHeads() {
        log.info("查询存在信息测试");
        Assertions.assertNotNull(onlCfgraphHeadMapper.getOnlCfgraphHeads("1377813000676806657"));
        log.info("查询不存在存在信息测试");
        Assertions.assertNull(onlCfgraphHeadMapper.getOnlCfgraphHeads("1111111"));
    }

    @Test
    void executeSeleteBySql() {

        log.info("sql注入成功");
        Assertions.assertNotNull(onlCfgraphHeadMapper.executeSeleteBySql("select * from onl_cfgraph_head"));
        log.info("sql注入失败,敏感表");
        Assertions.assertThrows(UncategorizedSQLException.class, ()->onlCfgraphHeadMapper.executeSeleteBySql("update * from sys_user"));
        }
        }