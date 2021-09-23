package org.jeecg.modules.onlgraph.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.poifs.nio.DataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.jeecg.config.shiro.JwtToken;
import org.jeecg.modules.common.exception.MultiDatasourceException;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphFieldDO;
import org.jeecg.modules.onlgraph.entity.OnlCfgraphHeadDO;
import org.jeecg.modules.onlgraph.vo.OnlCfgraphHeadVO;
import org.jeecg.modules.system.entity.SysPermission;
import org.jeecg.modules.system.model.SysLoginModel;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author wangjiahao, zhangqijian
 * @company DXC.technology
 * @create 2021-04-02 10:05
 */
@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class OnlCfgraphHeadControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private SecurityManager securityManager;
    private Object MultiDatasourceException;

    @BeforeEach
    public void setUp() throws Exception {
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

        log.info("分页查询数据");
        String url = "/onlgraph/onl-cfgraph-head/list";
        OnlCfgraphHeadDO onlCfgraphHeadDO = new OnlCfgraphHeadDO();
        onlCfgraphHeadDO.setGraphCode("111wwss");
        onlCfgraphHeadDO.setGraphName("菜单一");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param("pageNo", "1")
                .param("pageSize", "10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(onlCfgraphHeadDO)))
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

        log.info("数据关联图表不存在");
        String url = "/onlgraph/onl-cfgraph-head/save-all";
        OnlCfgraphHeadVO onlCfgraphHeadVO = new OnlCfgraphHeadVO();
        onlCfgraphHeadVO.setGraphName("单元测试一");
        onlCfgraphHeadVO.setGraphCode("2342424");
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(onlCfgraphHeadVO)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("数据关联图表存在");
        OnlCfgraphFieldDO onlCfgraphFieldDO = new OnlCfgraphFieldDO();
        onlCfgraphFieldDO.setDbFieldName("asdas");
        ArrayList<OnlCfgraphFieldDO> onlCfgraphFieldDOS = new ArrayList<>();
        onlCfgraphFieldDOS.add(onlCfgraphFieldDO);
        onlCfgraphHeadVO.setOnlCfgraphFields(onlCfgraphFieldDOS);
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(onlCfgraphHeadVO)))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }

    @Test
    void queryId() throws Exception {

        log.info("根据ID查询存在");
        String url = "/onlgraph/onl-cfgraph-head/query-id";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param("id", "1377830900158554114")
                .accept(MediaType.ALL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNotNull(json.getJSONObject("result"));
                });

        log.info("根据ID查询不存在");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param("id", "121312313")
                .accept(MediaType.ALL))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNull(json.getJSONObject("result"));
                });
    }

    @Test
    @Transactional
    @Rollback
    void editAll() throws Exception {

        log.info("更新单表数据");
        String url = "/onlgraph/onl-cfgraph-head/edit-all";
        OnlCfgraphHeadVO onlCfgraphHeadVO = new OnlCfgraphHeadVO();
        onlCfgraphHeadVO.setId("1377813000676806657");
        onlCfgraphHeadVO.setCreateBy("vxcvxv");
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(onlCfgraphHeadVO)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("更新图表head和图表field数据");
        OnlCfgraphFieldDO onlCfgraphFieldDO = new OnlCfgraphFieldDO();
        onlCfgraphHeadVO.setOnlCfgraphFields(Arrays.asList(onlCfgraphFieldDO));
        mockMvc.perform(MockMvcRequestBuilders.put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(onlCfgraphHeadVO)))
                .andDo(MockMvcResultHandlers.print())
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
        String url = "/onlgraph/onl-cfgraph-head/delete-all";
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("id", "1377813000676806657"))
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
        String url = "/onlgraph/onl-cfgraph-head/delete-batch";
        String[] ids = {"1380371178241134594", "1377830900158554114"};
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("ids", ids))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("数据不存在");
        ids = new String[]{"0", "0"};
        mockMvc.perform(MockMvcRequestBuilders.delete(url)
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
    void codeCheck() throws Exception {

        log.info("数据校验存在");
        String url = "/onlgraph/onl-cfgraph-head/graph-code/131wrss";
        mockMvc.perform(MockMvcRequestBuilders
                .get(url))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("数据校验不存在");
        url = "/onlgraph/onl-cfgraph-head/graph-code/13asdadss";
        mockMvc.perform(MockMvcRequestBuilders
                .get(url))
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }

    @Test
    @Transactional
    @Rollback
    void menuRouting() throws Exception {
        String url = "/onlgraph/onl-cfgraph-head/menu_routing";
        SysPermission sysPermission = new SysPermission();
        sysPermission.setId("1377830699331084289");
        log.info("配置过");
        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(sysPermission)))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
        log.info("未配置过");

        mockMvc.perform(MockMvcRequestBuilders.post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(new SysPermission())))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertEquals("500", json.getString("code"));
                });
    }

    @Test
    void queryPermission() throws Exception {

        log.info("菜单ID存在");
        String url = "/onlgraph/onl-cfgraph-head/query-permission";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param("id", "1380351542778982401")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNotNull(json.getJSONObject("result"));
                });

        log.info("菜单ID不存在");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .param("id", "138035154277")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertNull(json.getJSONObject("result"));
                });
    }

    @Test
    void exportXls() throws Exception {
        log.info("导出开始");
        String url = "/onlgraph/onl-cfgraph-head/export-xls";
        mockMvc.perform(MockMvcRequestBuilders.get(url).header("USER-AGENT", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.90 Safari/537.36 Edg/89.0.774.63")
                .param("pageNo", "1")
                .param("pageSize", "10")
                .contentType("application/vnd.ms-excel")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    MockHttpServletResponse response = mvcResult.getResponse();
                    Assertions.assertEquals(200, response.getStatus());
                });
        log.info("导出结束");
    }

    @Test
    @Transactional
    @Rollback
    void importExcel() throws Exception {

        log.info("导入开始");
        String url = "/onlgraph/onl-cfgraph-head/import-xls";
        mockMvc.perform(MockMvcRequestBuilders.fileUpload(url).
                file(new MockMultipartFile("file", "在线图表 ", "application/ms-excel", new FileInputStream(new File("D:\\图表数据.xls"))))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
        log.info("导入结束");
    }

    @Test
    void executeSelectBySql() throws Exception {
        log.info("其他数据源连接成功");
        String url = "/onlgraph/onl-cfgraph-head/sql-parsing";
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("sql", "select * from attack")
                .param("dbKey", "test01"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
        log.info("其他数据源连接失败");
        mockMvc.perform(MockMvcRequestBuilders.get("/onlgraph/onl-cfgraph-head/sql-parsing")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON_UTF8)
                .param("sql", "select * from attack")
                .param("dbKey", "23234234"))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                });


        log.info("系统数据源存在");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("sql", "select * from onl_cfgraph_head")
                .param("dbKey", ""))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });

        log.info("sql拦截");
        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .accept(MediaType.APPLICATION_JSON)
                .param("sql", "select * from sys_ser")
                .param("dbKey", ""))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertFalse(json.getBoolean("success"));
                });
    }

    @Test
    void queryOptions() throws Exception {

        log.info("数据字典列表");
        mockMvc.perform(MockMvcRequestBuilders.get("/sys/dataSource/options")
                .param("value", "dataSource.getCode()")
                .param("label", "dataSource.getName()")
                .param("text", "dataSource.getName()")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });
    }}




