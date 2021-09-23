package org.jeecg.modules.form.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.form.entity.GetFormDataDO;
import java.util.List;

/**
 * 获取表单数据相关信息  图表组件需要
 *
 * @author HuangSn
 * @date: 2021/7/12
 */
@InterceptorIgnore(tenantLine = "true")
@Mapper
@DS("form")
public interface GetFormDataMapper extends BaseMapper<GetFormDataDO> {

    /**
     * 获取当前登录用户创建的所有表单数据列表
     *
     * @param userName 用户名
     * @param codeType 表单Code
     * @return java.util.List<java.lang.String> 表单数据列表
     */
    @Select("select data.row_data from fd_form_data data inner join fd_form form on data.form_id = form.id where data.create_by = #{userName} and form.code = #{codeType}")
    List<String> getRowData(String userName, String codeType);

    /**
     * 获取当前登录用户创建的所有表单Code列表
     *
     * @param userName 用户名
     * @return java.util.List<java.lang.String> 表单Code列表
     */
    @Select("select DISTINCT F.`code` from fd_form F left join fd_form_data D on F.id=D.form_id where D.create_by=(#{userName})")
    List<String> getCode(String userName);
}
