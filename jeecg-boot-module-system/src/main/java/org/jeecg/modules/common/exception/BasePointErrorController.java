package org.jeecg.modules.common.exception;

import org.jeecg.common.api.vo.Result;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.ServletWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * 错误处理器
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/8 16:44
 */
@RestController
public class BasePointErrorController implements ErrorController {

    private static final String ERROR_PATH = "/error";
    private final ErrorAttributes errorAttributes;

    public BasePointErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    @Override
    public String getErrorPath() {
        return ERROR_PATH;
    }

    /**
     * 处理ControllerAdvice无法捕获的异常
     * @param request 请求
     * @param response 相应
     * @return 错误详细说明
     */
    @RequestMapping(ERROR_PATH)
    public ResponseEntity<Result<Object>> handler(HttpServletRequest request, HttpServletResponse response) {
        ServletWebRequest webRequest = new ServletWebRequest(request);
        ErrorAttributeOptions options = ErrorAttributeOptions.of(ErrorAttributeOptions.Include.MESSAGE,
                ErrorAttributeOptions.Include.EXCEPTION);
        Map<String, Object> attributes = this.errorAttributes.getErrorAttributes(webRequest, options);
        Object message = attributes.get("message");
        Object error = attributes.get("error");
        return ResponseEntity.status(response.getStatus()).body(Result.error(response.getStatus(), (!message.equals("No message available") ? message : error).toString()));
    }
}
