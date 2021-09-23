package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.dto.FormCSSDTO;
import org.jeecg.modules.form.entity.FormCSSDO;
import org.jeecg.modules.form.vo.FormStyleMapVO;

/**
 * 表单全局样式关联关系Service层接口类
 *
 * @Author: HuangSn
 * @Date: 2021/5/10 10:57
 */
public interface FormStyleMapService extends IService<FormCSSDO> {


    /**
     * 保存表单全局样式关联关系
     *
     * @param formCSSDTO 表单样式关联DTO
     * @return FormCSSDTO 返回修改后的表单样式映射关系DTO
     */
    FormCSSDTO saveFormStyleMapping(FormCSSDTO formCSSDTO);

    /**
     * 根据表单id、样式id删除单全局样式关联关系
     *
     * @param formId  表单id
     * @param styleId 样式id
     */
    void deleteFormStyleMapping(String formId, String styleId);

    /**
     * 根据表单ID查询表单样式
     *
     * @param page 分页参数
     * @param formId 表单ID
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.vo.FormCSSVO> 返回FormCSSVO对象的列表
     * @Author huang sn
     * @date 17:10 2021/9/2
     */
    IPage<FormStyleMapVO> getFormCSSList(Page<FormStyleMapVO> page, String formId);
}
