package org.jeecg.modules.form.util;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

import java.net.URI;

/**
 * 重写HttpGet与HttpPost方法，整合成一个HttpRequest方法继承HttpEntityEnclosingRequestBase，方便Java网络请求
 *
 * @author: HuQi
 * @date: 2021年08月02日 10:32
 */
public class HttpRequest extends HttpEntityEnclosingRequestBase {

    private String METHOD_NAME;

    @Override
    public String getMethod() {
        return METHOD_NAME;
    }

    HttpRequest(String uri) {
        this.setURI(URI.create(uri));
        this.METHOD_NAME = "GET";
    }

    HttpRequest(String uri, String method) {
        this.setURI(URI.create(uri));
        this.METHOD_NAME = method;
    }
}
