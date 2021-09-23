package org.jeecg.modules.form.vo;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 表单数据视图
 *
 * @author 张源
 * @date 17:29 2021/5/31
 **/
@Data
public class FormDataJsonVO {
    private JSONObject config;
    private JSONArray list;
}
