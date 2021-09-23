package org.jeecg.modules.onlgraph.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.jeecg.config.shiro.JwtToken;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;
import org.jeecg.modules.system.controller.LoginController;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.model.SysLoginModel;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
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
 * @author  zhangqijain wangjiahao
 * @company dxc
 * @create  2021-04-02 10:23
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IOnlCfgraphHeadServiceTest {

    @Autowired
    IOnlCfgraphHeadService onlCfgraphHeadService;
    @Autowired
    SecurityManager securityManager;
    @Autowired
    LoginController loginController;
    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeAll
    void setUp() throws Exception {
        ThreadContext.bind(securityManager);
        mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        SysLoginModel loginModel = new SysLoginModel();
        loginModel.setUsername("sprint1");
        loginModel.setPassword("Ww123456.");
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
        OnlCfgraphHeadVO onlCfgraphHeadVO = new OnlCfgraphHeadVO();
        onlCfgraphHeadVO.setGraphName("单元测试saveAll");
        List<OnlCfgraphFieldDO> fieldList = new ArrayList<>();
        fieldList.add(new OnlCfgraphFieldDO().setDbFieldName("单元测试field-update"));
        onlCfgraphHeadVO.setOnlCfgraphFields(fieldList);
        log.info("保存一个id不重复的记录");
        Assertions.assertDoesNotThrow(() -> onlCfgraphHeadService.saveAll(onlCfgraphHeadVO));
        onlCfgraphHeadVO.setId("1376827731850485761");
        onlCfgraphHeadVO.setGraphName("单元测试update-error");
        fieldList.clear();
        onlCfgraphHeadVO.setOnlCfgraphFields(fieldList);
        log.info("保存一个id重复的记录");
        Assertions.assertThrows(DuplicateKeyException.class, () -> onlCfgraphHeadService.saveAll(onlCfgraphHeadVO));
    }

    @Test
    @Transactional
    @Rollback
    void removeAll() {
        log.info("删除一个存在的记录");
        Assertions.assertDoesNotThrow(() -> onlCfgraphHeadService.removeAll("1376827731850485761"));
        log.info("删除一个不存在的记录");
        Assertions.assertDoesNotThrow(() -> onlCfgraphHeadService.removeAll("1111111"));
    }

    @Test
    void getOnlCfgraphHeads() {
        log.info("查询存在信息测试");
        Assertions.assertNotNull(onlCfgraphHeadService.getOnlCfgraphHeads("1376827731850485761"));
        log.info("查询不存在存在信息测试");
        Assertions.assertNull(onlCfgraphHeadService.getOnlCfgraphHeads("1111111"));
    }

    @Test
    @Transactional
    @Rollback
    void updateAllById() {
        OnlCfgraphHeadVO onlCfgraphHeadVO = new OnlCfgraphHeadVO();
        onlCfgraphHeadVO.setId("1376827731850485761");
        onlCfgraphHeadVO.setGraphName("单元测试update");
        List<OnlCfgraphFieldDO> fieldList = new ArrayList<>();
        fieldList.add(new OnlCfgraphFieldDO().setDbFieldName("单元测试field-update"));
        onlCfgraphHeadVO.setOnlCfgraphFields(fieldList);
        log.info("更新存在的数据");
        Assertions.assertDoesNotThrow(() -> onlCfgraphHeadService.updateAllById(onlCfgraphHeadVO));
        onlCfgraphHeadVO.setId("");
        onlCfgraphHeadVO.setGraphName("单元测试update-error");
        fieldList.clear();
        onlCfgraphHeadVO.setOnlCfgraphFields(fieldList);
        log.info("更新不存在的数据");
        Assertions.assertThrows(HttpServerErrorException.class,
                () -> onlCfgraphHeadService.updateAllById(onlCfgraphHeadVO));
    }

    @Test
    void menuRouting() {
        SysPermission sysPermission=new SysPermission();
        sysPermission.setId("1377830699331084289");
        sysPermission.setUrl("/online/graphreport/1377470266830675970");
        //TODO 设置对象参数
        log.info("插入一条表单id存在的记录");
        Assertions.assertDoesNotThrow(() -> onlCfgraphHeadService.menuRouting(sysPermission));
    }
}