package org.jeecg.modules.onlgraph.mapper;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author  wangshuang
 * @company dxc
 * @create  2021-06-03 15:07
 */

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphBigscreenMapperTest {

    @Resource
    GraphBigscreenMapper graphBigscreenMapper;

    @Test
    void getBigscreen() {
        log.info("查询存在信息测试");
        Assertions.assertNotNull(graphBigscreenMapper.getBigscreen("1111111111111"));
        log.info("查询不存在存在信息测试");
        Assertions.assertNull(graphBigscreenMapper.getBigscreen("64354988654"));
    }
}