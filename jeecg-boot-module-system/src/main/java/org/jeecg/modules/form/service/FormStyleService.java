package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.dto.FormStyleDTO;
import org.jeecg.modules.form.entity.FormStyleDO;
import org.jeecg.modules.form.vo.FormStyleVO;

import java.util.List;

/**
 * 表单样式Service层接口类
 *
 * @Description: 表单样式
 * @Author: HuangSn
 * @Date: 2021/5/6 22:01
 */
public interface FormStyleService extends IService<FormStyleDO> {
    /**
     * 保存全局样式
     *
     * @param formStyleDTO 样式表
     * @return FormStyleDTO
     */
    FormStyleDTO saveFormStyle(FormStyleDTO formStyleDTO);

    /**
     * 列出全局样式
     *
     * @param page 分页
     * @param name 名字
     * @param code 编码
     * @return IPage<FormStyleVO> 分页
     */
    IPage<FormStyleVO> getFormStyleList(Page<FormStyleVO> page, String name, String code);

    /**
     * 判断StyleCode是否可用
     *
     * @param styleCode 样式编码
     * @return boolean
     */
    boolean formStyleCodeIsUsed(String styleCode);

    /**
     * 根据StyleType查找表单样式
     *
     * @param page 分页
     * @param type 类型
     * @return IPage<FormStyleDTO> 分页结果
     */
    IPage<FormStyleDTO> listFormStyleByType(Page<FormStyleDTO> page, String type);

    /**
     * 根据StyleId删除表单样式
     *
     * @param styleId 样式Id
     */
    int deleteFormStyleByStyleId(String styleId);

    /**
     * 根据StyleId修改表单样式
     *
     * @param formStyle 样式风格
     */
    void updateFormStyleById(FormStyleDTO formStyle);

    /**
     * 根据ID批量删除表单样式
     *
     * @param ids Id列表
     */
    void deleteFormStyleByStyleIds(List<String> ids);
}
