package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.jeecg.modules.common.util.ServiceUtils;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.dto.FormStyleDTO;
import org.jeecg.modules.form.entity.FormCSSDO;
import org.jeecg.modules.form.entity.FormStyleDO;
import org.jeecg.modules.form.mapper.FormStyleMapper;
import org.jeecg.modules.form.service.FormStyleMapService;
import org.jeecg.modules.form.service.FormStyleService;
import org.jeecg.modules.form.vo.FormStyleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 表单样式Service层实现类
 *
 * @Author: HuangSn
 * @Date: 2021/5/6 22:07
 */

@DS("form")
@Service
public class FormStyleServiceImpl extends ServiceImpl<FormStyleMapper, FormStyleDO> implements FormStyleService {

    @Autowired(required = false)
    private FormStyleMapper formStyleMapper;

    @Autowired
    private FormStyleMapService formStyleMapService;

    /**
     * 保存表单全局样式
     *
     * @param formStyleDTO 表单样式DTO对象
     * @return FormStyleDTO 返回保存后的表单样式DTO对象
     */
    @Override
    @Transactional
    public FormStyleDTO saveFormStyle(FormStyleDTO formStyleDTO) {
        //name和style code不含有空格
        ServiceUtils.throwIfFailed(() -> StringUtils.isNotBlank(formStyleDTO.getName()) && StringUtils.isNotBlank(formStyleDTO.getCode()),
                FormErrorMessageConstant.FORM_SAVE_FAILED
        );
        //保存全局样式、异常处理
        if (formStyleCodeIsUsed(formStyleDTO.getCode())) {
            ServiceUtils.throwIfFailed(() -> this.save(formStyleDTO), FormErrorMessageConstant.FORM_SAVE_FAILED);
            return formStyleDTO;
        } else {
            ServiceUtils.throwException(FormErrorMessageConstant.FORM_SAVE_FAILED_BECAUSE_STYLE_CODE_IS_USED);
            return formStyleDTO;
        }
    }

    /**
     * 列出表单全局样式
     *
     * @param page 分页参数
     * @param name 表单名称
     * @param code 表单Code
     */
    @Override
    public IPage<FormStyleVO> getFormStyleList(Page<FormStyleVO> page, String name, String code) {
        return getBaseMapper().findByNameOrCode(page, name, code);
    }

    /**
     * 判断style code是否被使用过
     *
     * @param code 表单Code
     * @return boolean 返回布尔值结果
     */
    @Override
    public boolean formStyleCodeIsUsed(String code) {
        //code = 1，表示已经有写入，返回false
        //code = 0, 表示还未有写入，返回true
        if (formStyleMapper.formStyleCodeIsUsed(code) == 1) { return false; }
        else { return formStyleMapper.formStyleCodeIsUsed(code) == 0; }
    }

    /**
     * 根据type类型查询表单全局样式
     *
     * @param page 分页参数
     * @param type 表单样式类型
     */
    @Override
    public IPage<FormStyleDTO> listFormStyleByType(Page<FormStyleDTO> page, String type) {
        return formStyleMapper.listFormStyleByType(page, type);
    }

    /**
     * 通过style code删除全局样式
     *
     * @param styleId 表单样式ID
     */
    @Override
    public int deleteFormStyleByStyleId(String styleId) {
        //先判断style是否被使用, > 0 ，被使用过，不能删除
        if (formStyleMapService.lambdaQuery().eq(FormCSSDO::getStyleId, styleId).count() > 0) {
            return 0;
        }
        ServiceUtils.throwIfDeleteFailed(() -> removeById(styleId));
        return 1;
    }

    /**
     * 根据Id进行更改表单全局样式
     *
     * @param formStyle 表单样式DTO对象
     */
    @Override
    public void updateFormStyleById(FormStyleDTO formStyle) {
        ServiceUtils.throwIfUpdateFailed(() -> {
            if (StringUtils.isBlank(formStyle.getId())) { return false; }
            if (StringUtils.isBlank(formStyle.getName() + formStyle.getContent())) { return false; }
            return lambdaUpdate()
                    .set(StringUtils.isNotBlank(formStyle.getName()), FormStyleDO::getName, formStyle.getName())
                    .set(StringUtils.isNotBlank(formStyle.getContent()), FormStyleDO::getContent, formStyle.getContent())
                    .eq(FormStyleDO::getId, formStyle.getId())
                    .update(new FormStyleDO());
        });
    }

    /**
     * 根据ID批量删除表单全局样式
     *
     * @param ids id集合
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteFormStyleByStyleIds(List<String> ids) {
        ServiceUtils.throwIfDeleteFailed(() -> removeByIds(ids));
    }
}
