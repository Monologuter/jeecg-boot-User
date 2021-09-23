package org.jeecg.modules.system.controller;


import cn.hutool.core.date.DateTime;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.aspect.annotation.PermissionData;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.system.entity.SysTenant;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.jeecg.modules.system.service.ISysTenantService;
import org.jeecg.modules.system.util.TenantContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 租户配置信息
 */
@Slf4j
@RestController
@Api(tags = "租户管理")
@RequestMapping("/sys/tenant")
public class SysTenantController {

    @Autowired
    private ISysTenantService sysTenantService;

    @Autowired
    private ISysPermissionService sysPermissionService;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 获取列表数据
     *
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @ApiOperation("请求租户列表")
    @PermissionData(pageComponent = "system/TenantList")
    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public Result<IPage<SysTenant>> queryPageList(@ApiParam("租户信息") SysTenant sysTenant, @ApiParam("页数") @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
                                                  @ApiParam("页面显示条数") @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
        Result<IPage<SysTenant>> result = new Result<IPage<SysTenant>>();
        QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
        Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
        IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
        for (SysTenant record : pageList.getRecords()) {
            if (record.getEndDate().before(new DateTime())) {
                //过期了
                record.setStatus(0);
                this.sysTenantService.updateById(record);
            }
        }
        result.setSuccess(true);
        result.setResult(pageList);
        return result;
    }

    /**
     * 添加
     *
     * @param
     * @return
     */
    @ApiOperation("添加租户")
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysTenant> add(@ApiParam("租户信息") @RequestBody SysTenant sysTenant) {
        Result<SysTenant> result = new Result<SysTenant>();
        if (sysTenantService.getById(sysTenant.getId()) != null) {
            return result.error500("该编号已存在!");
        }
        try {
            sysTenantService.saveSysTenant(sysTenant);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失败");
        }
        return result;
    }

    /**
     * 编辑
     *
     * @param
     * @return
     */
    @ApiOperation("编辑租户信息")
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysTenant> edit(@ApiParam("租户信息") @RequestBody SysTenant tenant) {
        Result<SysTenant> result = new Result<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if (sysTenant == null) {
            result.error500("未找到对应实体");
        } else {
            boolean ok = sysTenantService.updateById(tenant);
            if (ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }

    /**
     * 通过id删除
     *
     * @param id
     * @return
     */
    @ApiOperation("通过id删除")
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<String> delete(@ApiParam("id") @RequestParam(name = "id", required = true) String id) {

        if (sysTenantService.countUserLinkTenant(id) == 0) {
            sysTenantService.removeTenantById(id);
            return Result.OK("删除成功");
        }
        return Result.Error("当前租户下存在关联用户，不能删除");
    }

    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @ApiOperation("批量删除")
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@ApiParam("ids") @RequestParam(name = "ids", required = true) String ids) {
        Result<String> result = new Result<>();
        if (oConvertUtils.isEmpty(ids)) {
            result.error500("未选中租户！");
        } else {
            String[] ls = ids.split(",");
            // 过滤掉已被引用的租户
            List<String> idList = new ArrayList<>();
            for (String id : ls) {
                int userCount = sysTenantService.countUserLinkTenant(id);
                if (userCount == 0) {
                    idList.add(id);
                }
            }
            if (idList.size() > 0) {
                sysTenantService.removeByIds(idList);
                if (ls.length == idList.size()) {
                    result.success("删除成功！");
                } else {
                    result.success("部分删除成功！（被引用的租户无法删除）");
                }
            } else {
                result.error500("选择的租户都已被引用，无法删除！");
            }
        }
        return result;
    }

    /**
     * 通过id查询
     *
     * @param id
     * @return
     */
    @ApiOperation("通过id查询")
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysTenant> queryById(@ApiParam(value = "id", required = true) @RequestParam(name = "id", required = true) String id) {
        Result<SysTenant> result = new Result<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(id);
        if (sysTenant == null) {
            result.error500("未找到对应实体");
        } else {
            result.setResult(sysTenant);
            result.setSuccess(true);
        }
        return result;
    }

    /**
     * 查询有效的 租户数据
     *
     * @return
     */
    @ApiOperation("批量查询")
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Result<List<SysTenant>> queryList(@ApiParam("ids") @RequestParam(name = "ids", required = false) String ids) {
        Result<List<SysTenant>> result = new Result<List<SysTenant>>();
        LambdaQueryWrapper<SysTenant> query = new LambdaQueryWrapper<>();
        if (oConvertUtils.isNotEmpty(ids)) {
            query.in(SysTenant::getId, ids.split(","));
            query.eq(SysTenant::getStatus, 1);
        }
        List<SysTenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }

    @ApiOperation("生成租户邀请码")
    @RequestMapping(value = "/getTenantInviteCode", method = RequestMethod.GET)
    public Result<String> getTenantInviteCode() {
        //获取当前租户id
        String tenantId = TenantContext.getTenant();
        if (tenantId == null) {
            tenantId = "0";
        }
        //查看之前邀请码是否过期
        Object object = redisUtil.get(tenantId);
        if (object != null) {
            return Result.OK("邀请码未过期: " + object);
        }
        //生成随机码
        String captcha = UUID.randomUUID().toString().replace("-", "");
        //邀请码暂时只存租户id且12小时
        redisUtil.set(tenantId, captcha, 60 * 60 * 12);
        redisUtil.set(captcha, tenantId, 60 * 60 * 12);
        return Result.OK(captcha);
    }

    @ApiOperation("根据邀请码获取权限")
    @RequestMapping(value = "/parseInviteCode", method = RequestMethod.GET)
    public Result<String> parseInviteCode(String inviteCode) {
        Object object = redisUtil.get(inviteCode);
        if (object == null) {
            return Result.Error("邀请码失效，请联系管理员");
        }
        return Result.OK(object.toString());
    }

//    @ApiOperation("租户demo导出")
//    @RequestMapping(value = "/jsonDemo", method = RequestMethod.GET)
//    public Result<String> jsonDemo() {
//        List<SysPermission> ls = sysPermissionService.list(new LambdaQueryWrapper<SysPermission>()
//                .eq(SysPermission::getBaseFlag, 1)
//                .eq(SysPermission::getTenantId, 0));
//        JSONArray array = new JSONArray();
//        for(SysPermission p: ls){
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("name", p.getName());
//            map.put("id", p.getId());
//            map.put("parentId", p.getParentId());
//            map.put("component", p.getComponent());
//            map.put("componentName", p.getComponentName());
//            map.put("url", p.getUrl());
//            map.put("route", p.isRoute());
//            map.put("delFlag", p.getDelFlag());
//            map.put("sortNo", p.getSortNo());
//            map.put("icon", p.getIcon());
//            map.put("alwaysShow", p.isAlwaysShow());
//            map.put("hidden", p.isHidden());
//            map.put("internalOrExternal", p.isInternalOrExternal());
//            map.put("keepAlive", p.isKeepAlive());
//            map.put("leaf", p.isLeaf());
//            map.put("menuType", p.getMenuType());
//            map.put("ruleFlag", p.getRuleFlag());
//            JSONObject json = new JSONObject(map);
//            array.add(json);
//        }
//        System.out.println("===========================");
//        System.out.println(array.toJSONString());
//        System.out.println("===========================");
//        return null;
//    }
}
