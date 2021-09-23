package org.jeecg.modules.form.vo;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

/**
 * 表单设计器VO
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/18 15:46
 */
@Data
public class FormVO {
    private String id;
    private String name;
    private String code;
    private JSONObject json;
    private String department;
    private String dynamicDataSource;
    private String autoCountCollection;
    private Boolean isTemplate;
}
