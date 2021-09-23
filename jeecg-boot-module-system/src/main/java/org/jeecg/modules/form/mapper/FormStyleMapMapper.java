package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.form.entity.FormCSSDO;
import org.jeecg.modules.form.vo.FormStyleMapVO;

/**
 * 表单样式持久层接口
 *
 * @Author: HuangSn
 * @Date: 2021/5/7 20:11
 */
@InterceptorIgnore(tenantLine = "true")
public interface FormStyleMapMapper extends BaseMapper<FormCSSDO> {

    /**
     * 从表单样式关联表中删除表单样式关联信息
     *
     * @param formId 表单ID
     * @param styleCode 表单样式Code
     */
    @Select("delete from fd_form_style_mapping where form_id=(#{formId}) and style_id=(#{styleCode})")
    void deleteFormStyleMapping(String formId,String styleCode);

    /**
     * 查询与相关表单相关的表单样式ID
     *
     * @param page 分页参数
     * @param formId 表单ID
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.vo.FormCSSVO>
     */
    @Select("select style_id from fd_form_style_mapping where form_id = (#{formId})")
    IPage<FormStyleMapVO> getFormCSSList(Page<FormStyleMapVO> page, String formId);
}
