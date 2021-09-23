package org.jeecg.modules.system.controller;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;

//import net.sf.json.JSONObject;
//import org.apache.http.HttpEntity;
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.entity.StringEntity;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.util.EntityUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.util.ThreadContext;
import org.jeecg.JeecgSystemApplication;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.service.ISysUserService;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

//import java.io.UnsupportedEncodingException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;


/**
 * @author wlx
 * @company DXC.technology
 * @create 2021-04-22
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = JeecgSystemApplication.class)
public class LoginControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private ISysUserService sysUserService;
    @Autowired
    SecurityManager securityManager;


    /*
      云帆短信发送，已欠费
     */
//    @Test
//    public void sendTestyunfan() throws NoSuchAlgorithmException, UnsupportedEncodingException{
//
//        log.info("发送云帆短信");
//        HttpClient httpClient = null;
//        HttpPost httppost = null;
//        try{
//            httpClient = new DefaultHttpClient();
//            httppost = new HttpPost("http://www.yunfansms.com:9090/sms/batch/v1");
//
//            Map<String,String> map = new HashMap<String,String>();
//            String appkey = "N09321";
//            String appsecret = "53DP31";
//            map.put("appkey", appkey);
//            map.put("appcode", "1000");
//
//            String timestamp = System.currentTimeMillis()+"";
//            map.put("timestamp", timestamp);
//            map.put("phone", phone);
//            int i = (int) ((Math.random() * 9 + 1) * 100000);  // 随机六位数字
//            String code = String.valueOf(i);  // 六位数字转字符串
//            //String code="1234";
//
//            map.put("msg", "【云帆】您的验证码是:"+code+"");
//            String sign = md5(appkey+appsecret+timestamp);
//
//            map.put("sign", sign);
//            //map.put("extend", "123");
//
//            String json = JSONObject.fromObject(map).toString();
//
//
//            StringEntity formEntity = new StringEntity(json, "utf-8");
//            httppost.setEntity(formEntity);
//            HttpResponse httpresponse = null;
//            httpresponse = httpClient.execute(httppost);
//            HttpEntity httpEntity = httpresponse.getEntity();
//            String response = EntityUtils.toString(httpEntity, "utf-8");
//            System.out.println(response);
//
//            redisUtil.set(phone, code, 600);
//
//        }catch(Exception e){
//            e.printStackTrace();
//        }finally{
//            if(httppost!=null){
//                httppost.abort();
//            }
//            if(httpClient!=null){
//                httpClient.getConnectionManager().shutdown();
//            }
//        }
//    }


    @BeforeEach
    public void setup() {

        log.info("开始测试");
    }
    @AfterEach
    void tearDown() {
        log.info("结束测试");
    }
    /**
     * 榛子云短信发送
     */
    @Test
    @Ignore
    public void sms1() throws Exception {
        String url = "/sys/sms";
        String S = "{\"mobile\":\"18198618235\",\"smsmode\":\"0\"}";  //手机号码  0为登录模板 111
        ThreadContext.bind(securityManager);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();  //初始化MockMvc对象
        log.info("发送验证码");
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(S))
                .andDo(MockMvcResultHandlers.print())
                ;
    }

    /**
     * 手机验证码登录
     */
    @Test
    public void phoneLogin1() throws Exception{
        String url = "/sys/phoneLogin";
        String S = "{\"mobile\":\"18198618235\",\"captcha\":\"921860\"}";  //captcha为收到的验证码
        ThreadContext.bind(securityManager);
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();  //初始化MockMvc对象
        log.info("手机验证码登录");
        mockMvc.perform(MockMvcRequestBuilders
                .post(url)
                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                .contentType(MediaType.APPLICATION_JSON)
                .content(S))
                .andDo(MockMvcResultHandlers.print())
                .andExpect(mvcResult -> {
                    com.alibaba.fastjson.JSONObject json = JSON.parseObject(mvcResult.getResponse().getContentAsString());
                    Assertions.assertTrue(json.getBoolean("success"));
                });



    }

/*
  云帆验证码发送用的方法
 */

//    private  String md5(String param) throws NoSuchAlgorithmException, UnsupportedEncodingException {
//        MessageDigest md5 = MessageDigest.getInstance("MD5");
//        byte[] md5Byte = md5.digest(param.getBytes("utf8"));
//        String result = byteToHex(md5Byte);
//        return result;
//    }
//
//
//    private  String byteToHex(byte[] md5Byte) {
//        String result = "";
//        StringBuilder sb = new StringBuilder();
//        for(byte each : md5Byte){
//            int value = each&0xff;
//            String hex = Integer.toHexString(value);
//            if(value<16){
//                sb.append("0");
//            }
//            sb.append(hex);
//        }
//        result = sb.toString();
//        return result;
//    }


}
