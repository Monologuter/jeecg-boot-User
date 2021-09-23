package org.jeecg.modules.message.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class CheckAttrIsNullUtil {

    public static boolean checkObjFieldIsAllNull(Object obj){
//        for(int i = 0; i < obj.getClass().getDeclaredFields().length; i++){
//
//        }
//
//
//
        boolean flag = true;
        for(Field f : obj.getClass().getDeclaredFields()){
            f.setAccessible(true);
            try {
                if(f.get(obj) != null){
                    flag = false;
                    return flag;
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }

    public static Boolean checkSomeAttrIsNull(Object object,String...attributes){
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

}
