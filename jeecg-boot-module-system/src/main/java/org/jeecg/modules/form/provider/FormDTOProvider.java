package org.jeecg.modules.form.provider;

import org.apache.ibatis.jdbc.SQL;

import java.util.Map;

/**
 * 构建SQL，通过FormMapper中的@SelectProvider进行调用
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/25 11:45
 */
public class FormDTOProvider {
    FormDTOProvider() {
    }

    /**
     * 构建查询所有表单设计器表单数据的SQL语句
     *
     * @param params 查询条件参数，存储类型为Map
     * @return java.lang.String 返回查询语句构建的结果
     */
    public static String getFormDTOListByIsTemplate(Map<String, Object> params) {
        String name = (String) params.get("name");
        String code = (String) params.get("code");
        boolean nameIsEmpty = "%%".equals(name);
        boolean codeIsEmpty = "%%".equals(code);

        SQL sql = new SQL()
                .SELECT("id,code,name,json,dynamic_data_source,update_time,create_time")
                .FROM("fd_form")
                .WHERE("is_template=#{isTemplate}");
        if (!nameIsEmpty || !codeIsEmpty) {
            sql.AND();
            sql.WHERE((!nameIsEmpty ? "name like #{name}" : "") + (!nameIsEmpty && !codeIsEmpty ? " AND " : "") + (!codeIsEmpty ? "code like #{code}" : ""));
        }
        sql.ORDER_BY("create_time desc");
        return sql.toString();
    }
}
