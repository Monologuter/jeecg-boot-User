package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.jeecg.modules.common.util.BeanCheckUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.dto.FormCSSDTO;
import org.jeecg.modules.form.entity.FormCSSDO;
import org.jeecg.modules.form.mapper.FormStyleMapMapper;
import org.jeecg.modules.form.service.FormStyleMapService;
import org.jeecg.modules.form.vo.FormStyleMapVO;
import org.springframework.stereotype.Service;

/**
 * 表单全局样式关系操作Service层实现类
 *
 * @Author: HuangSn
 * @Date: 2021/5/10 14:10
 */
@DS("form")
@Service
public class FormStyleMapServiceImpl extends ServiceImpl<FormStyleMapMapper, FormCSSDO> implements FormStyleMapService {

    /**
     * 保存表单全局样式关联关系
     *
     * @param formCSSDTO 表单样式关联DTO
     * @return FormCSSDTO 返回修改后的表单样式映射关系DTO
     */
    @Override
    public FormCSSDTO saveFormStyleMapping(FormCSSDTO formCSSDTO) {
        BeanCheckUtils.beanIsEmpty(formCSSDTO, FormCSSDTO::getFormId, FormCSSDTO::getStyleId);
        ServiceUtils.throwIfSaveFailed(() -> save(formCSSDTO));
        return formCSSDTO;
    }

    /**
     * 根据表单id、样式id删除单全局样式关联关系
     *
     * @param formId  表单id
     * @param styleId 样式id
     */
    @Override
    public void deleteFormStyleMapping(String formId, String styleId) {
        getBaseMapper().deleteFormStyleMapping(formId, styleId);
    }

    /**
     * 根据表单ID查询表单样式
     *
     * @param page 分页参数
     * @param formId 表单ID
     * @return 返回FormCSSVO对象的列表
     */
    @Override
    public IPage<FormStyleMapVO> getFormCSSList(Page<FormStyleMapVO> page, String formId) {
        return getBaseMapper().getFormCSSList(page, formId);
    }
}
