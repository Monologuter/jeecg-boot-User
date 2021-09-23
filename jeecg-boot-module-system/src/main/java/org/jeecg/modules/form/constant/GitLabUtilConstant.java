package org.jeecg.modules.form.constant;

/**
 * GitLab工具类的常量
 *
 * @Author: HuQi
 * @Date: 2021年09月03日 17:01
 */
public class GitLabUtilConstant {

    private GitLabUtilConstant(){}

    public static final String STR_UTF_8 = "UTF-8";
    public static final String STR_STATUS_CODE = "statusCode";
    public static final String STR_UTF8 = "utf8";
    public static final String STR_SET_COOKIE = "Set-Cookie";
    public static final String STR_CHECK = "check";
    public static final String STR_2F = "%2F";
    public static final String STR_RESPONSE = "response";
    public static final String STR_ENTITY = "entity";
    public static final String STR_PATH = "path";
    public static final String STR_FILE_PATH = "file_path";
    public static final String STR_COMMIT_MESSAGE = "commit_message";
    public static final String STR_CONTENT = "content";

    public static final String STR_REQUEST_METHOD_GET = "GET";
    public static final String STR_REQUEST_METHOD_POST = "POST";

    public static final String STR_REQUEST_CONTENT_TYPE_JSON = "application/json";
    public static final String STR_REQUEST_CONTENT_TYPE_URL = "application/x-www-form-urlencoded";

    public static final int STR_RESPONSE_CODE_200 = 200;
    public static final int STR_RESPONSE_CODE_201 = 201;
    public static final int STR_RESPONSE_CODE_404 = 404;
    public static final int STR_RESPONSE_CODE_302 = 302;

    public static final String SETTING_GITLAB_COOKIE = "GitLabCookie";
    public static final String SETTING_GITLAB_TOKEN = "GitLabToken";

    public static final String MESSAGE_ERROR_PORJECT_ENCODE_URL = "GitLab项目名URL解码出错！";
    public static final String MESSAGE_ERROR_REQUEST_HTTP_DATA = "请求获取网页数据失败！";
    public static final String MESSAGE_ERROR_HTTP_CLIENT_CLOSE = "httpClient关闭失败！";
    public static final String MESSAGE_ERROR_RESPONSE_CLOSE = "response关闭失败！";
    public static final String MESSAGE_ERROR_UPDATE_FILE = "更新文件失败！";
    public static final String MESSAGE_ERROR_ADD_FILE = "新增GitLab文件失败！";
    public static final String MESSAGE_ERROR_DELETE_FILE = "删除文件失败！";
    public static final String MESSAGE_ERROR_DELETE_DIR = "删除GitLab文件夹失败！文件夹：";
    public static final String MESSAGE_ERROR_PROJECT_NAME = "项目名称错误！";
    public static final String MESSAGE_ERROR_AUTHENTICITY_TOKEN_IS_NOT_EXIST = "authenticityToken为空！";
    public static final String MESSAGE_ERROR_LOGIN_INFOMATION = "账号或密码错误！";
    public static final String MESSAGE_ERROR_GITLAB_INFORMATION_ENCODE_URL = "GitLab账号、密码、TOKEN URL解码出错！";

    public static final String MESSAGE_SUCCESS_UPDATE_FILE = "更新文件成功！";
    public static final String MESSAGE_SUCCESS_DELETE_FILE = "删除文件成功！";
}
