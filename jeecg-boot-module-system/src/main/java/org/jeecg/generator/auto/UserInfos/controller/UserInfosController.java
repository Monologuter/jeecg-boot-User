package org.jeecg.generator.UserInfos.controller;

import org.jeecg.generator.UserInfos.service.UserInfosService;
import org.jeecg.generator.UserInfos.entity.UserInfos;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.modules.form.controller.BaseController;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.api.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
* <p>
*  userInfos前端控制器  RestController注解 将结果以JSON形式返回
* </p>
*
* @author system
* @since 2021-08-04
*/
@RestController
@RequestMapping("/userInfos")
@Api(tags = "自动生成的接口")
public class UserInfosController extends BaseController {

    @Autowired
    private UserInfosService userInfosService;

    /**
    * 保存修改公用 POST请求方式
    * @param userInfos 修改或保存的对象
    * @return Result
    */
    @PostMapping("/save")
    @ApiOperation("userInfos信息保存修改接口")
    public Result save(@RequestBody UserInfos userInfos) {
        if (userInfos.getId() != null){
            try {
                if (userInfosService.updateById(userInfos)){
                    return Result.OK("修改成功！", userInfos);
                }else {
                    return Result.Error("修改失败，不存在相关数据！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.Error("修改失败！" + e.getMessage());
            }
        }
        try {
            if (userInfosService.save(userInfos)){
                return Result.OK("添加成功！", userInfos);
            }else {
                return Result.Error("添加失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.Error("添加失败！" + e.getMessage());
        }

    }

    /**批量删除 支持POST
    * @param ids Long 类型 List 集合
    * @return Result
    */
    @PostMapping("remove")
    @ApiOperation("userInfos信息批量删除接口")
    public Result delete(@RequestBody List<Long> ids) {
        try {
            if (userInfosService.removeByIds(ids)){
                return Result.OK("删除成功！", ids);
            }else {
                return Result.Error("删除失败，不存在相关数据！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.Error("删除失败！" + e.getMessage());
        }
    }

    /**
    * 查询一个  支持GET
    *
    * @param id 查找对象的主键ID
    * @return Result
    */
    @GetMapping("findOne")
    @ApiOperation("userInfos信息一个查询接口")
    public Result findOne(Long id) {
        try {
            UserInfos userInfos = userInfosService.getById(id);
            return Result.OK("查询成功！", userInfos);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.Error("查询失败！" + e.getMessage());
        }
    }


    /**查询所有 支持GET ,未传当前页以及分页长度 则默认1页 10条数据
    * @param pageNum 当前页
    * @param pageSize 每页最大数据数
    * @return Result
    */
    @GetMapping("findAll")
    @ApiOperation("userInfos信息查询所有接口")
    public Result findAll(Integer pageNum, Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            try {
                IPage<UserInfos> page = userInfosService.page(new Page<>(pageNum, pageSize));
                return Result.OK("查询成功！", page);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.Error("查询失败！" + e.getMessage());
            }
        }
        try {
            IPage<UserInfos> page = userInfosService.page(new Page<>());
            return Result.OK("查询成功！", page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.Error("查询失败！" + e.getMessage());
        }
    }
}
