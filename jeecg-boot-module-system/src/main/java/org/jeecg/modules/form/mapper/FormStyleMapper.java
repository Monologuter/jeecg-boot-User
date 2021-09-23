package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.form.dto.FormStyleDTO;
import org.jeecg.modules.form.entity.FormStyleDO;
import org.jeecg.modules.form.vo.FormStyleVO;

/**
 * 表单样式持久层接口
 *
 * @Author: HuangSn
 * @Date: 2021/5/6 21:59
 */
@InterceptorIgnore(tenantLine = "true")
@Mapper
public interface FormStyleMapper extends BaseMapper<FormStyleDO> {

    /**
     * 查询表单样式是否被使用
     *
     * @param code 表单样式Code
     * @return int 结果不为0则表示已被使用
     */
    @Select("select count(1) from fd_form_style where code=(#{code})")
    int formStyleCodeIsUsed(String code);

    /**
     * 根据表单样式类型查询表单样式数据列表
     *
     * @param page 分页参数
     * @param type 样式类型
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.dto.FormStyleDTO> 返回表单样式FormStyleDTO对象的IPage
     */
    @Select("select * from fd_form_style where type=(#{type}) order by create_time desc")
    IPage<FormStyleDTO> listFormStyleByType(Page<FormStyleDTO> page, String type);

    /**
     * 删除相关的表单样式
     *
     * @param code 表单样式Code
     */
    @Select("delete from fd_form_style where code=(#{code})")
    void deleteFormStyleByStyleCode(String code);

    /**
     * 根据表单样式Code更改表单样式内容
     *
     * @param content 表单样式内容
     * @param code 表单样式Code
     */
    @Select("update fd_form_style set content = (#{content}) where code=(#{code})")
    void updateFormStyleByFormId(String content,String code);

    /**
     * 根据表单样式名称或表单样式Code查询表单样式数据
     *
     * @param page 分页参数
     * @param name 表单样式名称
     * @param code 表单样式Code
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.vo.FormStyleVO> 返回表单样式FormStyleVO对象的IPage
     */
    IPage<FormStyleVO> findByNameOrCode(Page<FormStyleVO> page, String name, String code);
}
