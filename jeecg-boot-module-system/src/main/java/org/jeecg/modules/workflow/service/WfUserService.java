package org.jeecg.modules.workflow.service;

import io.swagger.annotations.ApiParam;
import org.apache.poi.ss.formula.functions.T;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.GroupVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;

import java.util.List;

public interface WfUserService {
    /**
     * 增加用户
     * @param userDTO 用户实体类
     * @return
     */
    Result<T> addUser(UserDTO userDTO);

    /**
     * 增加租户
     * @param name 租户名
     * @param id 租户id
     * @return
     */
    Result<T> addTenant(String name, String id);

    /**
     * 删除租户
     * @param name 租户名
     * @param id 租户ID
     * @return
     */
    Result<T> deleteTenant(String name, String id);

    /**
     * 查询用户
     * @param groupId 部门id
     * @return
     */
    Result<List<UserDTO>> queryAllUser(@ApiParam(value = "用户名称",required = false)
                                               String userName,
                                       @ApiParam(value = "部门id",required = false)
                                               String groupId);

    /**
     * 添加组
     * @param groupId  组ID
     * @param name     组名称
     * @param type     组的类型
     * @return
     */
     Result<T> addGroup(String groupId, String name, String type);
    /**
     * 删除组
     * @param groupId  组ID
     * @return
     */
    Result<T> deleteGroup(String groupId);

    /**
     * 添加组和用户之间的关系
     * @param userId    用户id
     * @param groupIds   组id
     * @return
     */
    Result<T> createMembership(String userId, List<String> groupIds);

    /**
     * 添加删除组和用户之间的关系
     * @param userId    用户id
     * @param groupId   组id
     * @return
     */
     Result<T> deleteMembership(String userId, String groupId);


    /**
     * 查询所有组
     * @return
     */
    Result<List<GroupVO>> queryAllGroup();

    /**
     * 添加租户与用户之间的关系
     * @return
     */
    Result<T> createTenantUserMembership(String userId,String tenantId);

    /**
     * 根据id查询用户
     *
     * @param userId
     * @return
     */
    UserDTO selectByUserId(String userId);


    /**
     * 根据用户id批量查询用户头像
     * */
    List<String> queryAvatar(List<String> ids);

}
