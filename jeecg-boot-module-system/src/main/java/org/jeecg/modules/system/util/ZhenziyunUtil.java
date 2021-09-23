package org.jeecg.modules.system.util;

import com.alibaba.fastjson.JSONObject;
import com.zhenzi.sms.ZhenziSmsClient;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: TODO
 * @author: scott
 * @date: 2021年04月09日 15:40
 */
public class ZhenziyunUtil {

    public  static boolean sendSms (String phone, JSONObject templateParamJson) throws Exception {
        //初始化Client
        ZhenziSmsClient client = new ZhenziSmsClient("https://sms_developer.zhenzikj.com",
                "108623","M2NiNTBjNjEtNzA1Zi00NzBhLTllNTMtNjMzMTNjMjIyNWM0");
        Map<String, Object> params = new HashMap<String, Object>();
        //手机号码
        params.put("number", phone);
        //模板标号
        params.put("templateId", "4520");
        String[] templateParams = new String[2];
        String code = String.valueOf(templateParamJson.get("code"));
        //验证码
        templateParams[0] = code;
        //时间
        templateParams[1] = "10分钟";

        params.put("templateParams", templateParams);
        String result = client.send(params);

        boolean res=true;

        return res;

    }

}
