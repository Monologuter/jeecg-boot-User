package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.GroupVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;
import org.jeecg.modules.workflow.service.WfUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author qjc, lb , lz
 * @company DXC.technology
 * @date 2021-4-13
 */
@RestController
@RequestMapping("workflow/workflow-user")
@Api(tags="工作流")
@Slf4j
public class WfUserController {

    @Autowired
    private WfUserService wfUserService;

    /**
     * 增加新用户
     * @param userDTO 用户实体类
     * @return
     */
    @PutMapping("/add-user")
    @ApiOperation(value = "增加新用户", notes = "增加新用户")
    public Result addUser(@RequestBody UserDTO userDTO){
        return wfUserService.addUser(userDTO);
    }

    /**
     * 增加新租户
     *
     * @return
     */
    @PutMapping("/add-tenant")
    @ApiOperation(value = "新增新租户", notes = "新增新租户")
    public Result addTennat(String name, String id){
        return wfUserService.addTenant(name, id);
    }

    /**
     * 删除租户
     * @param name
     * @param id
     * @return
     */
    @DeleteMapping("/delete-tenant")
    @ApiOperation(value = "删除租户", notes = "删除租户")
    public Result deleteTenant(String name, String id){ return wfUserService.deleteTenant(name, id); }

    /**
     * 查询所有用户
     * @return
     */
    @GetMapping("/query-all-User")
    @ApiOperation(value = "查询所有用户", notes = "查询所有用户")
    public Result<List<UserDTO>> queryAllUser(@ApiParam(value = "用户名称",required = false)
                                              @RequestParam(required = false) String userName,
                                              @ApiParam(value = "部门id",required = false)
                                              @RequestParam(required = false) String groupId
                                              ){ return wfUserService.queryAllUser(userName,groupId); }

    /**
     * 添加组
     * @param groupId
     * @param name
     * @param type
     * @return
     */
    @PutMapping("/add-group")
    @ApiOperation(value = "添加组", notes = "添加组")
    public Result addGroup(@ApiParam(value = "组id", required = true) @RequestParam String groupId,
                           @ApiParam(value = "组的名字", required = true) @RequestParam String name,
                           @ApiParam(value = "组的类型", required = false) @RequestParam String type){
        return wfUserService.addGroup(groupId, name, type);
    }

    /**
     * 删除组
     * @param groupId
     * @return
     */
    @DeleteMapping("/delete-group")
    @ApiOperation(value = "删除组", notes = "删除组")
    public Result adeleteGroup(@ApiParam(value = "组id", required = true) @RequestParam String groupId){
        return wfUserService.deleteGroup(groupId);
    }

    /**
     * 添加组和用户之间的关系
     * @param userId
     * @param groupIds
     * @return
     */
    @PutMapping("/create-Membership")
    @ApiOperation(value = "添加组和用户之间的关系", notes = "添加组和用户之间的关系")
    public Result createMembership(@ApiParam(value = "用户id", required = true) @RequestParam String userId,
                                   @ApiParam(value = "组id集合", required = true) @RequestParam ArrayList<String> groupIds){
        return wfUserService.createMembership(userId, groupIds);
    }

    /**
     * 删除组和用户之间的关系
     * @param userId
     * @param groupId
     * @return
     */
    @DeleteMapping("/delete-Membership")
    @ApiOperation(value = "删除组和用户之间的关系", notes = "删除组和用户之间的关系")
    public Result deleteMembership(@ApiParam(value = "用户id", required = true) @RequestParam String userId,
                                   @ApiParam(value = "组Id", required = true) @RequestParam String groupId){
        return wfUserService.deleteMembership(userId, groupId);
    }

    /**
     * 查询所有组
     * @return
     */
    @GetMapping("query-all-group")
    @ApiOperation(value = "查询所有组", notes = "查询所有组")
    public Result<List<GroupVO>> queryAllGroup(){
        return wfUserService.queryAllGroup();
    }
}
