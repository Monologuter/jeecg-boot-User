package ${package.Controller};

import ${package.Service}.${table.serviceName};
import ${package.Entity}.${entity};
<#if swagger2>
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
</#if>
<#if superControllerClassPackage??>
import ${superControllerClassPackage};
</#if>
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
<#if cfg.result??>
import ${cfg.result};
</#if>
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
*  ${table.entityPath}前端控制器  RestController注解 将结果以JSON形式返回
* </p>
*
* @author ${author}
* @since ${date}
*/
@RestController
@RequestMapping("/${table.entityPath}")
@Api(tags = "自动生成的接口")
<#if superControllerClass??>
public class ${entity}Controller extends ${superControllerClass} {
<#else>
public class ${entity}Controller {
</#if>

    @Autowired
    private ${table.serviceName} ${table.entityPath}Service;

    /**
    * 保存修改公用 POST请求方式
    * @param ${table.entityPath} 修改或保存的对象
    * @return Result
    */
    @PostMapping("/save")
    @ApiOperation("${table.entityPath}信息保存修改接口")
    public Result save(@RequestBody ${entity} ${table.entityPath}) {
        if (${table.entityPath}.getId() != null){
            try {
                if (${table.entityPath}Service.updateById(${table.entityPath})){
                    return Result.OK("修改成功！", ${table.entityPath});
                }else {
                    return Result.Error("修改失败，不存在相关数据！");
                }
            } catch (Exception e) {
                e.printStackTrace();
                return Result.Error("修改失败！" + e.getMessage());
            }
        }
        try {
            if (${table.entityPath}Service.save(${table.entityPath})){
                return Result.OK("添加成功！", ${table.entityPath});
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
    @ApiOperation("${table.entityPath}信息批量删除接口")
    public Result delete(@RequestBody List<String> ids) {
        try {
            if (${table.entityPath}Service.removeByIds(ids)){
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
    @ApiOperation("${table.entityPath}信息一个查询接口")
    public Result findOne(String id) {
        try {
            ${entity} ${table.entityPath} = ${table.entityPath}Service.getById(id);
            return Result.OK("查询成功！", ${table.entityPath});
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
    @ApiOperation("${table.entityPath}信息查询所有接口")
    public Result findAll(Integer pageNum, Integer pageSize) {
        if (pageNum != null && pageSize != null) {
            try {
                IPage<${entity}> page = ${table.entityPath}Service.page(new Page<>(pageNum, pageSize));
                return Result.OK("查询成功！", page);
            } catch (Exception e) {
                e.printStackTrace();
                return Result.Error("查询失败！" + e.getMessage());
            }
        }
        try {
            IPage<${entity}> page = ${table.entityPath}Service.page(new Page<>());
            return Result.OK("查询成功！", page);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.Error("查询失败！" + e.getMessage());
        }
    }
}