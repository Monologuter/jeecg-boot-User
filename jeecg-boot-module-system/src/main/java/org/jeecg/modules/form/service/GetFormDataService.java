package org.jeecg.modules.form.service;

import java.util.List;


/**
 * 图表组需要的获取表单数据的接口
 *
 * @author: HuangSn
 * @date: 2021年07月17日 18:41
 */
public interface GetFormDataService {

    /**
     * 获取表单数据
     *
     * @param userName 用户名
     * @param codeType 编码类型
     * @return java.util.List<java.lang.String> 返回表单数据
     */
    List<String> getFormData(String userName, String codeType);

    /**
     * 查询表单编码
     *
     * @param userName 用户名
     * @return java.util.List<java.lang.String> 表单Code列表
     */
    List<String> listCode(String userName);
}
