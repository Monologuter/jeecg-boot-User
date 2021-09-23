package org.jeecg.modules.onlgraph.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.jeecg.config.shiro.JwtToken;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;
import org.jeecg.modules.system.controller.LoginController;
import org.jeecg.modules.system.model.SysLoginModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.WebApplicationContext;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;


/**
 * @author  wangshuang
 * @company dxc
 * @create  2021-06-03 15:40
 */

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IGraphBigscreenServiceTest {

    @Autowired
    IGraphBigscreenService graphBigscreenService;

    @Autowired
    SecurityManager securityManager;

    @Autowired
    LoginController loginController;

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        ThreadContext.bind(securityManager);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        SysLoginModel loginModel = new SysLoginModel();
        loginModel.setUsername("admin");
        loginModel.setPassword("123456");
        mockMvc.perform(MockMvcRequestBuilders
                .post("/sys/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(loginModel)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    String token = json.getJSONObject("result").getString("token");
                    SecurityUtils.getSubject().login(new JwtToken(token));
                });
    }

    @Test
    @Transactional
    @Rollback
    void saveAll() {
        GraphBigscreenVO graphBigscreenVO = new GraphBigscreenVO();
        graphBigscreenVO.setGraphBigscreenName("bsTestSaveAll");
        List<GraphBigscreenCardDO> bigscreenCardList = new ArrayList<>();
        bigscreenCardList.add(new GraphBigscreenCardDO().setBiggreenId("874561465132"));
        graphBigscreenVO.setGraphBigscreenCards(bigscreenCardList);
        log.info("保存一个id不重复的记录");
        Assertions.assertDoesNotThrow(() -> graphBigscreenService.saveAll(graphBigscreenVO));
        graphBigscreenVO.setId("555555555555555");
        graphBigscreenVO.setGraphBigscreenName("bsTestSaveAll1");
        bigscreenCardList.clear();
        graphBigscreenVO.setGraphBigscreenCards(bigscreenCardList);
        log.info("保存一个id重复的记录");
        Assertions.assertThrows(DuplicateKeyException.class, () -> graphBigscreenService.saveAll(graphBigscreenVO));

    }

    @Test
    @Transactional
    @Rollback
    void removeAll() {

        log.info("删除一个存在的记录");
        Assertions.assertDoesNotThrow(() -> graphBigscreenService.removeAll("4444444444444"));

        log.info("删除一个不存在的记录");
        Assertions.assertThrows(HttpServerErrorException.class, () -> graphBigscreenService.removeAll("986465418755"));
    }

    @Test
    @Transactional
    @Rollback
    void updateAllById() {
        GraphBigscreenVO graphBigscreenVO = new GraphBigscreenVO();
        graphBigscreenVO.setId("77777777777777");
        graphBigscreenVO.setGraphBigscreenName("bsTestUpdate");
        List<GraphBigscreenCardDO> bigscreenCardList = new ArrayList<>();
        bigscreenCardList.add(new GraphBigscreenCardDO().setBiggreenId("7878787878"));
        graphBigscreenVO.setGraphBigscreenCards(bigscreenCardList);
        log.info("更新存在数据");
        Assertions.assertDoesNotThrow(() -> graphBigscreenService.updateAllById(graphBigscreenVO));
        graphBigscreenVO.setId("");
        graphBigscreenVO.setGraphBigscreenName("bsTestUpdate1");
        bigscreenCardList.clear();
        graphBigscreenVO.setGraphBigscreenCards(bigscreenCardList);
        log.info("更新不存在的数据");
        Assertions.assertThrows(HttpServerErrorException.class, () -> graphBigscreenService.updateAllById(graphBigscreenVO));


    }

    @Test
    void getBigscreens() {

        log.info("查询存在信息测试");
        Assertions.assertNotNull(graphBigscreenService.getBigscreens("2342423424"));

        log.info("查询不存在信息测试");
        Assertions.assertNull(graphBigscreenService.getBigscreens("46894454445"));
    }
}