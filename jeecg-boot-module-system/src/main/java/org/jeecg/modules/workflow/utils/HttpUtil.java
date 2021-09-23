package org.jeecg.modules.workflow.utils;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.JsonObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import java.io.IOException;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName HttpUtil
 * @CreateTime 2021-09-09 11:46
 * @Version 1.0
 * @Description: httpClient工具类
 */
@Component
public class HttpUtil {
    /**
     * 私有化构造方法
     */
    private HttpUtil(){}

    private static Integer port;

    private static String host = "localhost";

    @Value("${server.port}")
    public void setreturnPort(Integer returnPort){
        port = returnPort;
    }

    //模拟获取token
    public static String getToken() throws IOException {

        //创建连接
        CloseableHttpClient client = HttpClients.createDefault();
        //创建http post请求
        HttpPost httpPost = new HttpPost("http://"+host+":"+port+"/jeecg-boot/sys/login");
        //设置post参数
        JsonObject j = new JsonObject();
        j.addProperty("username","admin");
        j.addProperty("password","Admin1234@");

        //构造一个form表单式的实体
        StringEntity stringEntity = new StringEntity(j.toString(),"utf-8");
        //将请求实体设置到httpPost对象中
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept","application/json");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try{
            //执行请求
            response = client.execute(httpPost);
            //判断返回状态是否为200
            String context = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(context);
            String token = jsonObject.getJSONObject("result").getString("token");
            return token;
        }finally {
            if (response!=null){
                response.close();
            }
            client.close();
        }
    }

    //获取外置表单json
    public static String getFormJson(String token, String formKey) throws IOException {
        //创建连接
        CloseableHttpClient client = HttpClients.createDefault();
        //拼接url
        StringBuilder url = new StringBuilder();
        url.append("http://"+host+":"+port+"/jeecg-boot/form/list?token="+token);
        url.append("&isTemplate=0&pageNo=1&pageSize=1");
        url.append("&code="+formKey);
        //创建http get请求
        HttpGet httpGet
                = new HttpGet(url.toString());
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            String context = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(context);
            String s = jsonObject.getJSONObject("result").getJSONArray("records").getJSONObject(0).getJSONObject("json").toJSONString();
            return s;
        }catch (Exception e){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FORM);
        }
        finally {
            if(response!=null){
                response.close();
            }
            client.close();
        }
    }

    //保存日历信息
    public static void saveInterViewSchedule(String token, String interviewee, String interviewer, String strat, String title) throws IOException {
        //创建连接
        CloseableHttpClient client = HttpClients.createDefault();
        //拼接url
        StringBuilder url = new StringBuilder();
        url.append("http://"+host+":"+port+"/jeecg-boot/form/interviewSchedule/save?token="+token);
        //创建http get请求
        HttpPost httpPost
                = new HttpPost(url.toString());
        //设置post参数
        JsonObject j = new JsonObject();
        j.addProperty("interviewee",interviewee);
        j.addProperty("interviewer",interviewer);
        j.addProperty("start",strat);
        j.addProperty("end",strat);
        j.addProperty("type","video");
        j.addProperty("className","type-orange");
        //构造一个form表单构造器
        StringEntity stringEntity = new StringEntity(j.toString(),"utf-8");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept","application/json");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            String context = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(context);
            if(!jsonObject.getBoolean("success")){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, jsonObject.getString("message"));
            }
        }
        finally {
            client.close();
        }
    }

    //获取外置表单-动态数据源
    public static String getFormDataSource(String token, String formKey) throws IOException {
        //创建连接
        CloseableHttpClient client = HttpClients.createDefault();
        //拼接url
        StringBuilder url = new StringBuilder();
        url.append("http://"+host+":"+port+"/jeecg-boot/form/list?token="+token);
        url.append("&isTemplate=0&pageNo=1&pageSize=1");
        url.append("&code="+formKey);
        //创建http get请求
        HttpGet httpGet
                = new HttpGet(url.toString());
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            String context = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(context);
            if(!jsonObject.getBoolean("success")){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, jsonObject.getString("message"));
            }
            String s = jsonObject.getJSONObject("result").getJSONArray("records").getJSONObject(0).getString("dynamicDataSource");
            return s;
        }catch (Exception e){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FORM);
        }
        finally {
            if(response!=null){
                response.close();
            }
            client.close();
        }
    }


//    /**
//     * @方法描述:获取外置表单
//     *
//     * @param token
//     * @param formName
//     * @param pageNo
//     * @param pageSize
//     * @返回值 : java.lang.String
//     * @作者 : DXC.Technology@ 陈亚
//     * @时间:2021/9/17 11:40
//    */
//    public static String getExtendFrom(String token, String formName, Integer pageNo,Integer pageSize) throws IOException {
//        //创建连接
//        CloseableHttpClient client = HttpClients.createDefault();
//        //拼接url
//        StringBuilder url = new StringBuilder();
//        url.append("http://"+host+":"+port+"/jeecg-boot/form/data/getFormList?token="+token);
//        url.append("&formName=&pageNo=1&pageSize=100");
//
//        //创建http get请求
//        HttpGet httpGet
//                = new HttpGet(url.toString());
//        CloseableHttpResponse response = null;
//
//        try {
//            response = client.execute(httpGet);
//            String context = EntityUtils.toString(response.getEntity(),"utf-8");
//            JSONObject jsonObject = JSONObject.parseObject(context);
//            if(!jsonObject.getBoolean("success")){
//                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, jsonObject.getString("message"));
//            }
//            String s = jsonObject.getJSONObject("result").getJSONArray("records").getJSONObject(0).getString("dynamicDataSource");
//            return s;
//        }catch (Exception e){
//            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FORM);
//        }
//        finally {
//            if(response!=null){
//                response.close();
//            }
//            client.close();
//        }
//    }

    //保存会议日程信息
    public static void saveOrder(String token, String title,String booker,String team,String status,String start,String end) throws IOException {
        //创建连接
        CloseableHttpClient client = HttpClients.createDefault();
        //拼接url
        StringBuilder url = new StringBuilder();
        url.append("http://"+host+":"+port+"/jeecg-boot/form/meetingSchedule/saveOrUpdate?token="+token);
        //创建http get请求
        HttpPost httpPost
                = new HttpPost(url.toString());
        //设置post参数
        JsonObject j = new JsonObject();
        j.addProperty("createBy","admin");
        j.addProperty("title",title);
        j.addProperty("booker",booker);
        j.addProperty("team",team);
        j.addProperty("status",status);
        j.addProperty("start",start);
        j.addProperty("end",end);
        //构造一个form表单构造器
        StringEntity stringEntity = new StringEntity(j.toString(),"utf-8");
        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept","application/json");
        httpPost.setEntity(stringEntity);
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpPost);
            String context = EntityUtils.toString(response.getEntity(),"utf-8");
            JSONObject jsonObject = JSONObject.parseObject(context);
            if(!jsonObject.getBoolean("success")){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, jsonObject.getString("message"));
            }
        }
        finally {
            client.close();
        }
    }

}

