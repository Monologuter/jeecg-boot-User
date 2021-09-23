package org.jeecg.modules.form.service.imp;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.form.mapper.GetFormDataMapper;
import org.jeecg.modules.form.service.GetFormDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 实现图表组件需要的功能
 *
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName GetFormDataServicelmpl
 * @CreateTime 2021-08-30 16:23
 * @Version 1.0
 */
@Service
@Slf4j
public class GetFormDataServiceImpl implements GetFormDataService {

    @Autowired
    private GetFormDataMapper getFormDataMapper;

    /**
     * 前端codetype转换
     * 获取当前登录用户创建的所有表单数据列表
     *
     * @param userName 用户名
     * @param codeType 表单类型
     * @return java.util.List<java.lang.String> 返回表单数据
     */
    @Override
    public List<String> getFormData(String userName, String codeType) {
        String codeT = " ";
        switch (codeType) {
            case "日常报销单":
                codeT = "reimburse-daily";
                break;
            case "差旅报销单":
                codeT = "reimburse-travel";
                break;
            case "备用金报销":
                codeT = "reimburse-reserve";
                break;
            case "出差申请":
                codeT = "application-travel";
                break;
            case "备用金申请":
                codeT = "application-reserve";
                break;
            default:
                break;
        }
        return getFormDataMapper.getRowData(userName, codeT);
    }

    /**
     * 获取当前登录用户创建的所有表单Code列表
     *
     * @param userName 用户名
     * @return java.util.List<java.lang.String> 表单Code列表
     */
    @Override
    public List<String> listCode(String userName) {
        return getFormDataMapper.getCode(userName);
    }
}
