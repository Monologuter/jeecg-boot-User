package org.jeecg.modules.system.util;

import org.jeecg.common.api.vo.Result;

import javax.servlet.http.HttpServletResponse;

public final class ResultUtil {
    private ResultUtil() {
    }

    public static <T> Result<T> badRequest() {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(HttpServletResponse.SC_BAD_REQUEST);
        r.setMessage("原数据有误");
        return r;
    }

    public static <T> Result<T> ok() {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(HttpServletResponse.SC_OK);
        r.setMessage("成功");
        return r;
    }

    public static <T> Result<T> ok(T data) {
        Result<T> r = new Result<>();
        r.setSuccess(true);
        r.setCode(HttpServletResponse.SC_OK);
        r.setMessage("成功");
        r.setResult(data);
        return r;
    }

    public static <T> Result<T> notFound() {
        Result<T> r = new Result<>();
        r.setSuccess(false);
        r.setCode(HttpServletResponse.SC_NOT_FOUND);
        r.setMessage("对应资源未找到");
        return r;
    }


}
