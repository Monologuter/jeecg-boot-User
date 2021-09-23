package org.jeecg.modules.onlgraph.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.jeecg.config.shiro.JwtToken;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenCardDO;
import org.jeecg.modules.onlgraph.entity.GraphBigscreenDO;
import org.jeecg.modules.onlgraph.vo.GraphBigscreenVO;
import org.jeecg.modules.system.model.SysLoginModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * @author wangshuang
 * @company DXC.technology
 * @create 2021-06-03 09:10
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GraphBigscreenControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    //安全管理器
    @Autowired
    private SecurityManager securityManager;


    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        SecurityUtils.setSecurityManager(securityManager);
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
        log.info("开始测试");
    }

    @AfterEach
    void tearDown() {
        log.info("结束测试");
    }

    @Test
    void queryPageList() throws Exception {

        log.info("分页列表查询");
        String url = "/onlgraph/graph-bigscreen/list";
        GraphBigscreenDO graphBigscreenDO = new GraphBigscreenDO();
        graphBigscreenDO.setGraphBigscreenName("菜单一");
        graphBigscreenDO.setGraphBigscreenCode("123123");
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("pageNo", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(graphBigscreenDO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNotNull(json.getJSONObject("result"));
                });
    }

    @Test
    @Transactional
    @Rollback
    void saveAll() throws Exception {

        log.info("大屏设计器不存在");
        String url = "/onlgraph/graph-bigscreen/save-all";
        GraphBigscreenVO graphBigscreenVO = new GraphBigscreenVO();
        graphBigscreenVO.setGraphBigscreenName("Bigscreen");
        graphBigscreenVO.setGraphBigscreenCode("12345");
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(graphBigscreenVO)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("大屏设计器存在");
        GraphBigscreenCardDO graphBigscreenCardDO = new GraphBigscreenCardDO();
        graphBigscreenCardDO.setBiggreenId("132131321321321321").setCardCode("jksdfhuf");
        ArrayList<GraphBigscreenCardDO> graphBigscreenCardDOS = new ArrayList<>();
        graphBigscreenCardDOS.add(graphBigscreenCardDO);
        graphBigscreenVO.setGraphBigscreenCards(graphBigscreenCardDOS);
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(graphBigscreenVO)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }

    @Test
    @Transactional
    @Rollback
    void editAll() throws Exception {

        log.info("更新大屏设计器数据");
        String url = "/onlgraph/graph-bigscreen/edit-all";
        GraphBigscreenVO graphBigscreenVO = new GraphBigscreenVO();
        graphBigscreenVO.setId("222222222222");
        graphBigscreenVO.setCreateBy("admin").setGraphBigscreenName("sadadasd");
        mockMvc.perform(MockMvcRequestBuilders
                .put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(graphBigscreenVO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
        log.info("更新图表Bigscreen和图表Card数据");
        GraphBigscreenCardDO graphBigscreenCardDO = new GraphBigscreenCardDO();
        graphBigscreenVO.setGraphBigscreenCards(Arrays.asList(graphBigscreenCardDO));
        mockMvc.perform(MockMvcRequestBuilders
                .put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(graphBigscreenVO)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }


    @Test
    @Transactional
    @Rollback
    void deleteAll() throws Exception {

        log.info("数据存在");
        String url = "/onlgraph/graph-bigscreen/delete-all";
        mockMvc.perform(MockMvcRequestBuilders
                .delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("id", "6666666666666666"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }


    @Test
    @Transactional
    @Rollback
    void deleteBatch() throws Exception {

        log.info("数据存在");
        String url = "/onlgraph/graph-bigscreen/delete-batch";
        String[] ids = {"77777777777777", "8888888888888"};
        mockMvc.perform(MockMvcRequestBuilders
                .delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("ids", ids))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    assertTrue(json.getBoolean("success"));
                });

        log.info("数据不存在");
        ids = new String[]{"0", "0"};
        mockMvc.perform(MockMvcRequestBuilders
                .delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("ids", ids))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertFalse(json.getBoolean("success"));
                });
    }


    @Test
    void queryId() throws Exception {

        log.info("根据ID查询存在");
        String url = "/onlgraph/graph-bigscreen/query-id";
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("id", "4444444444444")
                .accept(MediaType.ALL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNotNull(json.getJSONObject("result"));
                });

        log.info("根据ID查询不存在");
        mockMvc.perform(MockMvcRequestBuilders
                .get(url)
                .param("id", "6435456548654")
                .accept(MediaType.ALL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNull(json.getJSONObject("result"));

                });
    }
}