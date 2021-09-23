package org.jeecg.modules.form.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.vo.FormDataGraphVO;

import java.util.List;
import java.util.Map;

/**
 * 表单数据持久层接口
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/17 17:05
 */
@DS("form")
@InterceptorIgnore(tenantLine = "true")
public interface FormDataMapper extends BaseMapper<FormDataDO> {

    /**
     * 获取表单数据
     *
     * @param page      分页参数
     * @param formId    表单ID
     * @param roleRules 角色规则
     * @param orderBy   排序字段
     * @param isDesc    排序规则
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.entity.FormDataDO> 返回相关表单数据FormDataDO对象的IPage
     */
    IPage<FormDataDO> getFormDataList(Page<?> page,
                                      @Param("formId") String formId,
                                      @Param("roleRules") List<FormRoleDo> roleRules,
                                      @Param("orderBy") List<String> orderBy,
                                      @Param("isDesc") Boolean isDesc);

    /**
     * @Description
     * @Author huang sn
     * @Date 11:09 2021/9/6
     * @Param [formDataId]
     * @return java.lang.String
     */
    @Select("select form_id from fd_form_data where id =(#{formDataId})")
    String getFormIdByDataId(String formDataId);

    Long getFormDataCount(@Param("formId") String formId,
                          @Param("rules") List<FormRoleDo> rules);

    /**
     * @Description 图表组数据
     * @Author huang sn
     * @Date 4:46 下午 2021/9/16
     * @Param []
     * @return org.jeecg.modules.form.vo.FormDataGraphVO
     */
    Map getGraphData();
}
