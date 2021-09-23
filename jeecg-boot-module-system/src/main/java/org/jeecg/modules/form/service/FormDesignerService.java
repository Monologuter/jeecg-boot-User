package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.form.dto.FormDTO;
import org.jeecg.modules.form.dto.FormSysPermissionDTO;
import org.jeecg.modules.form.entity.FormDO;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 表单Service层接口类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/3 13:35
 */
public interface FormDesignerService extends IService<FormDO> {

    /**
     * 保存表单
     *
     * @param formDTO 表单对象
     * @return 插入主键后的表单对象
     */
    FormDTO saveFormDTO(FormDTO formDTO);

    /**
     * 根据id删除表单
     *
     * @param id 表单id
     */
    void deleteFormById(String id);

    /**
     * 根据id获取表单DTO对象,包括该表单相关的菜单对象
     *
     * @param id 表单id
     * @return 表单DTO对象
     */
    FormDTO getFormDTOById(String id);

    /**
     * 根据id更新表单
     *
     * @param formDTO 表单DTO对象
     */
    void updateFormDTOById(FormDTO formDTO);

    /**
     * 根据id数组批量删除表单
     *
     * @param ids 表单id数组
     */
    void deleteFormByIdBatch(List<String> ids);

    /**
     * 添加表单的路由菜单
     *
     * @param formSysPermissionDTO 表单路由菜单对象
     */
    void saveFormSysPermission(FormSysPermissionDTO formSysPermissionDTO);

    /**
     * 动态SQL，列出全局样式
     *
     * @param page       页码
     * @param isTemplate 是否为模板
     * @param name       名字
     * @param code       编码
     */
    IPage<FormDTO> getFormList(Page<FormDTO> page, Boolean isTemplate, String name, String code);

    /**
     * 导出
     *
     * @param ids id列表
     */
    ModelAndView exportTemplateToXls(List<String> ids);

    /**
     * 导入
     *
     * @param xlsFile xls格式文件
     */
    List<String> importXlsToTemplate(MultipartFile xlsFile);

    /**
     * 复制表单模板
     *
     * @param code   新表单的编码
     * @param name   新表单的名称
     * @param formId 模板表单的Id
     */
    FormDO copyForm(String code, String name, String formId);

    /**
     * 删除表单的路由菜单
     *
     * @param permissionId 菜单的id
     */
    void deleteFormSysPermission(String permissionId);
}
