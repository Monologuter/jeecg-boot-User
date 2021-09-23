package org.jeecg.modules.form.vo;

import lombok.Data;

import java.util.ArrayList;

/**
 * 工作流表单返回数据
 *
 * @author 张源
 * @date 15:46 2021/5/31
 **/
@Data
public class FormDataToWorkflowVO {
    private String formKey;
    private String name;
    private ArrayList<params> parameters;

    /**
     * 返回表单模板中所有输入框的列表
     *
     * @param formKey    表单模板编码
     * @param name       表单模板名称
     * @param parameters 表单模板json数据
     */
    public FormDataToWorkflowVO(String formKey, String name, ArrayList<params> parameters) {
        this.formKey = formKey;
        this.name = name;
        this.parameters = parameters;
    }

    @Data
    public static class params {
        private String id;
        private String type;
        private String label;

        public params(String id, String type, String label) {
            this.id = id;
            this.type = type;
            this.label = label;
        }
    }
}
