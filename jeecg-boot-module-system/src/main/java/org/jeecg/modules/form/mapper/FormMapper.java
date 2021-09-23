package org.jeecg.modules.form.mapper;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.form.dto.FormDTO;
import org.jeecg.modules.form.entity.FormDO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.provider.FormDTOProvider;

import java.util.List;

/**
 * 表单持久层接口
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/3 13:35
 */
@DS("form")
@InterceptorIgnore(tenantLine = "true")
public interface FormMapper extends BaseMapper<FormDO> {

    /**
     * 根据id查询表单及该表单相关的菜单信息
     *
     * @param id 表单id
     * @return 返回表单DTO对象
     */
    @Select("SELECT id,code,name,json,department,is_template,dynamic_data_source,auto_count_collection " +
            "FROM fd_form WHERE id = #{id}")
    FormDTO getFormDTOById(@Param("id") String id);

    /**
     * 查询所有表单设计器表单数据，不包含模板数据
     *
     * @param page 分页数据
     * @param isTemplate 是否为模板
     * @param name 表单名
     * @param code 表单编码
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.dto.FormDTO> 返回表单DTO对象的IPage
     */
    @SelectProvider(type = FormDTOProvider.class,method = "getFormDTOListByIsTemplate")
    IPage<FormDTO> getFormDTOListByIsTemplate(Page<FormDTO> page, @Param("isTemplate") Boolean isTemplate,
                                              @Param("name") String name, @Param("code") String code);

    /**
     * 根据表单id使用相关子查询获取相关联的菜单列表
     *
     * @param formId 表单id
     * @return 菜单列表FormSysPermissionDO对象的list
     */
    List<FormSysPermissionDO> getFormSysPermissionByFormId(@Param("formId") String formId);

    /**
     *
     * @param formId 表单id
     * @return JSONObject对象
     */
    @Select("select json from fd_form where id=(#{dormId})")
    JSONObject selectJsonById(String formId);

    /**
     * 查询表单设计器表单代码是否已被使用
     *
     * @param code 表单代码
     * @return int 结果不为0则表示已被使用
     */
    @Select("select count(1) from fd_form where code=(#{code})")
    int ifCOdeIsUsed(String code);
}
