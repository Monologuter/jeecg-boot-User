package org.jeecg.modules.form.util;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.*;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.modules.common.util.ServiceUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.jeecg.modules.form.constant.GitLabUtilConstant.*;

/**
 * GitLab的工具类，包含登录、上传、删除、修改等功能
 *
 * @author: lk
 * @date: 2021年07月14日 11:10
 */
@Slf4j
public class GitLabUtil {

    private RedisUtil redisUtil;

    /**
     * 用于存储cookie和token
     */
    private String cookie;
    private String token;
    /**
     * gitLab主机地址
     */
    private String url;
    /**
     * 用户名和项目名，如："用户名/项目名"
     */
    private String projectName;
    private String postUrl;
    private String contentType = STR_REQUEST_CONTENT_TYPE_JSON;
    private String branch;
    private String userName;
    private String password;
    private String signInUrl;

    /**
     * 匹配换行符、制表符、回车符，用于将其换成一行
     */
    private final Pattern tabPattern = Pattern.compile("[\\t\\r\\n]");

    /**
     * 匹配网页中的csrf-token，提取它用于登录
     */
    private final Pattern csrfTokenPattern = Pattern.compile("<meta name=\"csrf-token\" content=\"(.*?)\" />");

    /**
     * 提取项目中的authenticity_token，提取它用于访问项目
     */
    private final Pattern authenticityTokenPattern = Pattern
            .compile("<input type=\"hidden\" name=\"authenticity_token\" value=\"(.*?)\" /><div class=\"form-group\">");

    /**
     * 有参构造函数，创建GitLab工具类时调用该有参构造方法，
     * 设置全局参数，同时检测用户是否登录，未登录则进行登录
     *
     * @param userName GitLab用户登录账号名
     * @param password GitLab用户登录密码
     * @param url gitLab服务所在主机IP+端口号，如http://1.2.3.4:8080/
     * @param projectName 项目名  如："用户名/项目名"
     * @param branch 操作分支
     * @param redisUtil 用于存放GitLab登录信息的Redis工具类
     * @author: lk
     */
    public GitLabUtil(String userName, String password, String url, String projectName, String branch, RedisUtil redisUtil) {
        this.userName = userName;
        this.password = password;
        this.branch = branch;
        this.url = url;
        this.signInUrl = url + "/users/sign_in";
        try {
            this.projectName = URLEncoder.encode(projectName, STR_UTF_8);
        } catch (UnsupportedEncodingException e) {
            ServiceUtils.throwException(MESSAGE_ERROR_PORJECT_ENCODE_URL);
        }
        this.redisUtil = redisUtil;
        this.postUrl = url + "/api/v4/projects/" + this.projectName + "/repository/commits";
        // 获取Redis已存储的cookie和token值
        this.cookie = (String) redisUtil.get(SETTING_GITLAB_COOKIE);
        this.token = (String) redisUtil.get(SETTING_GITLAB_TOKEN);
        // 检测cookie与token是否有效
        if(getLastCommitId(STR_CHECK)==null){
            logInGitLab();
        }
    }

    /**
     * 统一请求接口
     *
     * @param url 请求页面的地址
     * @param method 请求方法 包含GET、POST
     * @param body 请求体，POST的时候使用
     * @return java.util.Map<java.lang.String,java.lang.Object> 返回请求后获取到的结果
     */
    private Map<String, Object> myRequest(String url, String method, String body) {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse response = null;
        Map<String, Object> result = new HashMap<>(3);
        String entity;
        HttpRequest http;
        if (STR_REQUEST_METHOD_GET.equals(method)){
            http = new HttpRequest(url);
        }else {
            http = new HttpRequest(url, STR_REQUEST_METHOD_POST);
            http.setEntity(new StringEntity(body, StandardCharsets.UTF_8));
        }
        http.setHeader("Cookie", cookie);
        http.setHeader("User-Agent",
                "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/92.0.4515.107 Safari/537.36");
        http.setHeader("X-CSRF-Token", token);
        http.setHeader("Content-Type", contentType);

        try {
            response = httpClient.execute(http);
            // 过滤cookie或token失效问题
            if(response.getStatusLine().getStatusCode() == STR_RESPONSE_CODE_404){
                if (!StringUtils.isEmpty(body) && STR_CHECK.equals(body)){
                    return null;
                }
                // 二次判断，当cookie过期导致GetLastCommitId()返回为null时再次登录，防止出现因为项目名称错误而一直登录
                if(getLastCommitId(STR_CHECK)==null){
                    logInGitLab();
                }
            }
            entity = EntityUtils.toString(response.getEntity(), STR_UTF8);
            result.put(STR_RESPONSE, response);
            result.put(STR_STATUS_CODE, response.getStatusLine().getStatusCode());
            result.put(STR_ENTITY, entity);
        } catch (Exception e) {
            log.error(MESSAGE_ERROR_REQUEST_HTTP_DATA + http + e.getMessage());
        } finally {
            try {
                httpClient.close();
            } catch (IOException e) {
                log.error(MESSAGE_ERROR_HTTP_CLIENT_CLOSE + e.getMessage());
            }
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException e) {
                log.error(MESSAGE_ERROR_RESPONSE_CLOSE + e.getMessage());
            }
        }
        return result;
    }

    /**
     * 获取最新操作的id
     *
     * @param check 检测登录标识
     * @author: lk
     * @Return: java.lang.String
     */
    private String getLastCommitId(String check) {
        // 获取某个文件的ID：  last_commit_sha
        // http://39.101.64.141:8090/bpt-lowcode/bpt-lowcode-backend/-/blob/form/jeecg-boot/README.md?format=json&viewer=rich
        String commitUrl = url + "/api/v4/projects/" + projectName + "/repository/branches/" + branch;
        Map<String, Object> result = myRequest( commitUrl, STR_REQUEST_METHOD_GET, check);
        if(result==null){
            return null;
        }
        String content = (String) result.get(STR_ENTITY);
        return content.substring(content.indexOf("id\":\"") + 5, content.indexOf("\",\"short_id"));
    }

    /**
     * 更新文件
     *
     * @param info 请求体 包括 commit_message action(update) file_path content encoding last_commit_id
     * @author: lk
     * @Return: java.lang.String
     */
    public String updateFile(Map<String, Object> info) {
        String body = "{" +
                    "\"branch\": \""+branch+"\"," +
                    "\"commit_message\": \"" + info.get(STR_COMMIT_MESSAGE) + "\"," +
                    "\"actions\":[{\"action\": \"update\", " +
                    "\"file_path\": \"" + info.get(STR_FILE_PATH) + "\", " +
                    "\"content\": \"" + info.get(STR_CONTENT) + "\", " +
                    "\"encoding\": \"text\", " +
                    "\"last_commit_id\": \"" + getLastCommitId(null) + "\"" +
                    "}]}";
        Map<String, Object> result = myRequest(postUrl, STR_REQUEST_METHOD_POST, body);
        if ((int) Objects.requireNonNull(result).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_201) {
            return MESSAGE_SUCCESS_UPDATE_FILE;
        }
        return MESSAGE_ERROR_UPDATE_FILE + info.get(STR_FILE_PATH);
    }

    /**
     * 新增文件
     *
     * @param infos 请求体 包括 file_path content 的列表
     * @param commitMessage Commit信息
     * @author: lk
     * @Return: java.lang.String
     */
    public Boolean addFile(List<Map<String, Object>> infos, String commitMessage) {

        StringBuilder body = new StringBuilder("{\"branch\":\"" + branch + "\", \"commit_message\":\""
                + commitMessage + "\", \"actions\":[");
        for (Map<String, Object> info : infos) {
            body.append("{\"action\":\"create\", \"file_path\":\"").append(info.get(STR_FILE_PATH))
                    .append("\", \"content\":\"").append(info.get(STR_CONTENT)).append("\", \"encoding\":\"text\"},");
        }
        body.setLength(body.length() - 1);
        body.append("]}");

        Map<String, Object> result = myRequest(postUrl,STR_REQUEST_METHOD_POST, body.toString());
        if ((int) Objects.requireNonNull(result).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_201){
            return true;
        }else {
            log.error(MESSAGE_ERROR_ADD_FILE + result);
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param info 请求体 包括 commit_message action(delete) file_path encoding
     * @author: lk
     * @Return: java.lang.String
     */
    public String deleteFile(Map<String, String> info) {
        String body = "{" +
                "\"branch\": \"" + branch + "\"," +
                "\"commit_message\": \"" + info.get(STR_COMMIT_MESSAGE) + "\"," +
                "\"actions\":[{\"action\": \"delete\", " +
                "\"file_path\": \"" + info.get(STR_FILE_PATH) + "\", " +
                "\"encoding\": \"text\"" +
                "}]}";
        Map<String, Object> result = myRequest(postUrl,STR_REQUEST_METHOD_POST, body);
        if ((int) Objects.requireNonNull(result).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_201) {
            return MESSAGE_SUCCESS_DELETE_FILE;
        }
        return MESSAGE_ERROR_DELETE_FILE + info.get(STR_FILE_PATH);
    }

    /**
     * 获取文件夹下的子文件路径
     *
     * @param packageRepositoryPath 文件夹的相对路径
     * @author: lk
     * @Return: java.lang.String
     */
    private String getSonFiles(String packageRepositoryPath) {
        String getSonFilesUrl = url + projectName + "/-/refs/" + branch + "/logs_tree/"
                + packageRepositoryPath + "?format=json&offset=0";
        getSonFilesUrl = getSonFilesUrl.replace(STR_2F, "/");
        Map<String, Object> result = myRequest(getSonFilesUrl, STR_REQUEST_METHOD_GET, null);
        List<GetSonFilesEntity> list = JSON.parseArray((String) Objects.requireNonNull(result).get(STR_ENTITY)
                , GetSonFilesEntity.class);
        StringBuilder stringBuilder = new StringBuilder();
        for (GetSonFilesEntity getSonFilesEntity : list) {
            if ("tree".equals(getSonFilesEntity.getType())) {
                stringBuilder.append(getSonFiles(packageRepositoryPath + STR_2F
                        + getSonFilesEntity.getFile_name()));
            } else {
                stringBuilder.append(packageRepositoryPath.replace(STR_2F, "/")).append("/")
                        .append(getSonFilesEntity.getFile_name()).append(",");
                return stringBuilder.toString();
            }
        }
        return stringBuilder.toString();
    }

    /**
     * 删除文件夹
     *
     * @param packageRepositoryPath 要删除的git文件夹
     * @author: lk
     * @Return: java.lang.String
     */
    public Boolean deletePackage(String packageRepositoryPath) {
        String filePaths = getSonFiles(packageRepositoryPath);
        String[] filePath = filePaths.split(",");
        StringBuilder body = new StringBuilder("{\"branch\":\"" + branch + "\", \"commit_message\":\"AutoDeleted GeneratorCode："
                + packageRepositoryPath + "\", \"actions\":[");
        for (String item : filePath) {
            body.append("{\"action\":\"delete\", \"file_path\":\"").append(item).append("\", \"encoding\":\"text\"},");
        }
        body.setLength(body.length() - 1);
        body.append("]}");
        Map<String, Object> result = myRequest(postUrl, STR_REQUEST_METHOD_POST, body.toString());

        if ((int) Objects.requireNonNull(result).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_201){
            return true;
        }else {
            log.error(MESSAGE_ERROR_DELETE_DIR + packageRepositoryPath + "    " + result);
            return false;
        }
    }

    /**
     * 登录GitLab获取持久化cookie和token
     *
     * @author: lk
     * @Return: java.util.HashMap<java.lang.String, java.lang.String>
     */
    private void logInGitLab() {

        // 1、第一次访问GitLab登录页面，拿到登录所需的token
        firstVisit();

        // 2、将拿到的Token与用户给的用户名和密码一并请求登录页面进行登录，拿到登陆后的Cookie
        userLogin();

        // 3、使用登陆后的Cookie访问该用户有权限访问的项目，拿到相关的Token，完成登录操作
        getProjectToken();
    }

    /**
     * 1、第一次访问GitLab登录页面，拿到登录所需的token
     *
     * @Author lk
     */
    private void firstVisit(){
        Map<String, Object> result = myRequest(signInUrl, STR_REQUEST_METHOD_GET, null);
        setLoginCookie(result);
        // 获取HTML
        String content = (String) Objects.requireNonNull(result).get(STR_ENTITY);
        // 获取临时Token
        if (content != null) {
            // 去掉行符、制表符、回车符，使HTML变成一行
            Matcher m = tabPattern.matcher(content);
            content = m.replaceAll("");
            // 匹配authenticity_token
            Matcher matcher = authenticityTokenPattern.matcher(content);
            while (matcher.find()) {
                token = matcher.group(1);
            }
        }
    }

    /**
     * 2、将拿到的Token与用户给的用户名和密码一并请求登录页面进行登录，拿到登陆后的Cookie
     *
     * @Author lk
     */
    private void userLogin(){
        // 使用cookie，token，username，password登录
        String body = "";
        // 临时设置contentType为application/x-www-form-urlencoded
        contentType = STR_REQUEST_CONTENT_TYPE_URL;
        if (token != null) {
            try {
                body = "utf8=%E2%9C%93&" +
                        "authenticity_token=" + URLEncoder.encode(token, STR_UTF_8) + "&" +
                        "user%5Blogin%5D=" + URLEncoder.encode(userName, STR_UTF_8) + "&" +
                        "user%5Bpassword%5D=" + URLEncoder.encode(password, STR_UTF_8) + "&" +
                        "user%5Bremember_me%5D=0";
            }catch (Exception e){
                ServiceUtils.throwException(MESSAGE_ERROR_GITLAB_INFORMATION_ENCODE_URL);
            }
        } else {
            ServiceUtils.throwException(MESSAGE_ERROR_AUTHENTICITY_TOKEN_IS_NOT_EXIST);
        }
        Map<String, Object> secondResult = myRequest(signInUrl, STR_REQUEST_METHOD_POST, body);
        if((int) Objects.requireNonNull(secondResult).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_200){
            ServiceUtils.throwException(MESSAGE_ERROR_LOGIN_INFOMATION);
        }else if ((int) secondResult.get(STR_STATUS_CODE) == STR_RESPONSE_CODE_302) {
            setLoginCookie(secondResult);
            // 将cookie存入redis
            redisUtil.set(SETTING_GITLAB_COOKIE, cookie);
        }
    }

    /**
     * 3、使用登陆后的Cookie访问该用户有权限访问的项目，拿到相关的Token，完成登录操作
     *
     * @Author lk
     */
    private void getProjectToken(){
        // 重置contentType
        contentType = STR_REQUEST_CONTENT_TYPE_JSON;
        // 拿到持久化的token
        String finalUrl = (url+projectName).replace(STR_2F,"/");
        Map<String, Object> thirdResult = myRequest(finalUrl, STR_REQUEST_METHOD_GET, null);
        if((int) Objects.requireNonNull(thirdResult).get(STR_STATUS_CODE) == STR_RESPONSE_CODE_404){
            ServiceUtils.throwException(MESSAGE_ERROR_PROJECT_NAME);
        }
        if ((int) thirdResult.get(STR_STATUS_CODE) == STR_RESPONSE_CODE_200) {
            String contentFinal = (String) thirdResult.get(STR_ENTITY);
            if (contentFinal != null) {
                Matcher m = tabPattern.matcher(contentFinal);
                contentFinal = m.replaceAll("");
                Matcher matcher = csrfTokenPattern.matcher(contentFinal);
                while (matcher.find()) {
                    // 拿到持久化token
                    token = matcher.group(1);
                    // 将token存入redis
                    redisUtil.set(SETTING_GITLAB_TOKEN, token);
                }
            }
        }
    }

    /**
     * 设置请求的Cookie
     *
     * @param result 请求返回的Map结果数据
     * @Author lk
     */
    private void setLoginCookie(Map<String, Object> result){
        // 获取返回头中的Cookie
        String setCookie = Arrays.toString(((CloseableHttpResponse) Objects.requireNonNull(result).get(STR_RESPONSE))
                .getHeaders(STR_SET_COOKIE));
        // 获取experimentation_subject_id
        String cookieOne = setCookie.substring(setCookie.indexOf(STR_SET_COOKIE) + 11, setCookie.indexOf(STR_PATH));
        // 获取_gitlab_session
        String cookieTwo = setCookie.substring(setCookie.lastIndexOf(STR_SET_COOKIE) + 11, setCookie.lastIndexOf("; "
                + STR_PATH));
        // 生成登录使用的Cookie   或   拼接experimentation_subject_id和_gitlab_session
        cookie = cookieOne + "event_filter=all; sidebar_collapsed=false; " + cookieTwo;
    }

}
