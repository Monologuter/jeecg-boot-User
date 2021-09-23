package org.jeecg.modules.form.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.modules.form.dto.FormStyleDTO;
import org.jeecg.modules.form.entity.FormStyleDO;
import org.jeecg.modules.form.service.FormStyleService;
import org.jeecg.modules.form.vo.FormStyleVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.beans.BeanCopier;
import org.jeecg.common.api.vo.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @Author: HuangSn
 * @Date: 2021/5/7 9:36
 */
@RestController
@Controller
@RequestMapping("/form/style/")
@Api(tags="表单全局样式")
public class FormStyleController extends JeecgController<FormStyleDO, FormStyleService> {
    private final BeanCopier formCopier = BeanCopier.create(FormStyleVO.class, FormStyleDTO.class, false);

    @Autowired
    private FormStyleService formStyleService;

    /**
    *保存表单样式
    * @param formStyleVO FormStyleVO对象
    * @return Result
    */
    @RequestMapping(value="/save", method = RequestMethod.POST)
    @ApiOperation("保存全局样式")
    public Result<FormStyleDO> saveFormStyle(@RequestBody FormStyleVO formStyleVO)
    {
        FormStyleDTO formStyleDTO=new FormStyleDTO();
        formCopier.copy(formStyleVO,formStyleDTO,null);
        return Result.OK(formStyleService.saveFormStyle(formStyleDTO));
    }

    /**
     * 获取表单列表
     * @param pageNo 当前页数
     * @param pageSize 页容量
     * @param name 名字
     * @param code 编码
     * @return 表单列表
     */
    @GetMapping("/list")
    @ApiOperation("列出全局样式")
    public Result<IPage<FormStyleVO>> getFormStyleList(
            @ApiParam("当前页数，可选，默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
            @ApiParam("页容量，可选，默认为5") @RequestParam(defaultValue = "5") Integer pageSize,
            @ApiParam("样式名称，可选") @RequestParam(required = false,defaultValue = "") String name,
            @ApiParam("样式编码，可选") @RequestParam(required = false,defaultValue = "") String code)
    {
        Page<FormStyleVO> formPage = new Page<>(pageNo, pageSize);
        formStyleService.getFormStyleList(formPage, "%" + name + "%", "%" + code + "%");
        return Result.OK(formPage);
    }

    @GetMapping("/selectList")
    @ApiOperation("样式表高级查询")
    public Result<IPage<FormStyleDO>> queryPageList(FormStyleDO formStyleDO, @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                                    @RequestParam(name="pageSize", defaultValue="10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<FormStyleDO>> result = new Result<>();
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, String[]> map = new HashMap<>(parameterMap);
        String[] column = {"create_time"};
        map.put("column",column);
        QueryWrapper<FormStyleDO> queryWrapper = QueryGenerator.initQueryWrapper(formStyleDO,map);
        Page<FormStyleDO> page = new Page<>(pageNo,pageSize);
        IPage<FormStyleDO> pageList = formStyleService.page(page,queryWrapper);
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 全局名字是否可用
     * @param styleCode 编码
     * @return Result
     */
    @GetMapping("/is-used")
    @ApiOperation("全局名字是否可用")
    public Result<Object> formStyleCodeIsUsed(@RequestParam String styleCode){
        if(!formStyleService.formStyleCodeIsUsed(styleCode)){
            return Result.Error("style code已被使用！");
        }
        else {
            return Result.OK("style code可以使用");
        }
    }

    /**
     * 根据类型列出全局样式
     * @param type 类型
     * @param pageNo 页码
     * @param pageSize 页面大小
     */
    @GetMapping("/listByType")
    @ApiOperation("根据类型列出全局样式")
    public Result<IPage<FormStyleDTO>> listFormStyleSheetByType(
        @ApiParam("表单类型, 必选， 默认为table") @RequestParam(defaultValue ="table") String type,
        @ApiParam("当前页数, 可选, 默认为1") @RequestParam(defaultValue = "1") Integer pageNo,
        @ApiParam("页容量, 可选, 默认为5") @RequestParam(defaultValue = "5") Integer pageSize)
    {
        Page<FormStyleDTO> formPage = new Page<>(pageNo, pageSize);
        formStyleService.listFormStyleByType(formPage,type);
        return Result.OK(formPage);
    }

    /**
     * 修改全局样式
     * @param formStyleVO 全局样式VO
     */
    @PutMapping("/update")
    @ApiOperation("根据样式id修改全局样式")
    public Result<Object> updateFormStyleById(@RequestBody FormStyleVO formStyleVO) {
        FormStyleDTO formStyle = new FormStyleDTO();
        formCopier.copy(formStyleVO,formStyle,null);
        formStyleService.updateFormStyleById(formStyle);
        return Result.OK();
    }

    /**
     * 删除全局样式
     * @param id 样式id
     */
    @DeleteMapping("/delete")
    @ApiOperation("根据样式id删除全局样式")
    public Result<Object> deleteFormStyleById(@RequestParam String id) {
        if(formStyleService.deleteFormStyleByStyleId(id) == 1){
            return Result.OK(200,"操作成功");
        }
        //前端要求这么写的
        return Result.error(200,"该样式正在被使用,无法删除!");
    }

    /**
     * 批量删除全局样式
     * @param ids 样式id数组
     */
    @DeleteMapping("/delete-batch")
    @ApiOperation("根据样式id批量删除全局样式")
    public Result<Object> deleteFormStyleByIds(@RequestParam List<String> ids) {
        formStyleService.deleteFormStyleByStyleIds(ids);
        return Result.OK();
    }
}
