package org.jeecg.modules.system.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class CheckAttrUtil {
    /**
     * 判空 输入的可变参数中有任意一项为空，则返回true
     * @param object
     * @param attributes
     * @return
     */
    public static Boolean isNull(Object object,String...attributes){
        Field[] fields = object.getClass().getDeclaredFields();
        try {
            for (int i = 0; i < fields.length; i++) {
                String name = fields[i].getName();
                for (String s: attributes) {
                    if (s == name){
                        name = "get"+name.substring(0, 1).toUpperCase() + name.substring(1);
                        //获取值
                        Method method = object.getClass().getMethod(name);
                        Object value = method.invoke(object);
                        if (value == null || "".equals(value) || "null".equals(value)){
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否存在汉字
     * @param str
     * @return
     */
    public static Boolean includeChineseChar(String str) {
        Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(str);
        if (m.find()) {
            return true;
        }
        return false;
    }

    public static Boolean isAllNull(Object object) {
        Field[] fields = object.getClass().getDeclaredFields();
        int count = 0;
        try {
            for (int i = 0; i < fields.length; i++) {
                int modifiers = fields[i].getModifiers();
                if (modifiers == 26) {
                    count++;
                    continue;
                }
                String name = fields[i].getName();
                name = "get" + name.substring(0, 1).toUpperCase() + name.substring(1);
                Method method = object.getClass().getMethod(name);
                Object value = method.invoke(object);
                if (value == null || "".equals(value) || "null".equals(value)) {
                    count++;
                }
                log.info("第" + i + "个参数");
            }
            if (count == fields.length) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
