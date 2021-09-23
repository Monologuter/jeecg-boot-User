package org.jeecg.modules.form.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.form.dto.FormCSSDTO;
import org.jeecg.modules.form.entity.FormCSSDO;
import org.jeecg.modules.form.service.FormStyleMapService;
import org.jeecg.modules.form.vo.FormStyleMapVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: HuangSn
 * @Date: 2021/5/10 14:14
 */
@RestController
@RequestMapping("/form/styleMapping/")
@Api(tags = "表单全局样式映射")
public class FormStyleMapController extends JeecgController<FormCSSDO, FormStyleMapService> {
    @Autowired
    private FormStyleMapService formStyleMapService;

    private final BeanCopier formCopier = BeanCopier.create(FormStyleMapVO.class, FormCSSDTO.class, false);

    /**
     * 添加映射关系
     *
     * @param formCSSVO FormCSSVO对象
     */
    @PostMapping("/save")
    @ApiOperation("添加映射关系")
    public Result<FormCSSDTO> saveFormStyleSheet(FormStyleMapVO formCSSVO) {
        FormCSSDTO formCSSDTO = new FormCSSDTO();
        formCopier.copy(formCSSVO, formCSSDTO, null);
        return Result.OK(formStyleMapService.saveFormStyleMapping(formCSSDTO));
    }

    /**
     * 删除映射关系
     *
     * @param formId    表单Id
     * @param styleCode 表单编码
     * @return Result
     */
    @DeleteMapping("/delete")
    @ApiOperation("删除映射关系")
    public Result<Object> deleteStyleMappingByStyleCode(@RequestParam String formId, @RequestParam String styleCode) {
        formStyleMapService.deleteFormStyleMapping(formId, styleCode);
        return Result.OK("删除成功！");
    }

    /**
     * 列出样式id
     *
     * @param pageNo
     * @param pageSize
     * @param formId
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ApiOperation(value = "列出样式id", notes = "仅仅列出style_id")
    public Result<IPage<FormStyleMapVO>> listCSSMapping(
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为10") @RequestParam(defaultValue = "10") Integer pageSize,
            @ApiParam("表单id，必选") @RequestParam(required = true) String formId) {
        Page<FormStyleMapVO> page = new Page<>(pageNo, pageSize);
        formStyleMapService.getFormCSSList(page, formId);
        return Result.OK(page);
    }
}
