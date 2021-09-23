package org.jeecg.modules.workflow.service.impl;
import org.apache.logging.log4j.util.Strings;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.camunda.bpm.engine.impl.persistence.entity.GroupEntity;
import org.camunda.bpm.engine.impl.persistence.entity.TenantEntity;
import org.camunda.bpm.engine.impl.persistence.entity.UserEntity;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.GroupVO;
import org.jeecg.modules.workflow.entity.vo.UserDTO;
import org.jeecg.modules.workflow.mapper.ActIdInfoMapper;
import org.jeecg.modules.workflow.mapper.WorkFlowUserMapper;
import org.jeecg.modules.workflow.service.WfUserService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpServerErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qjc, lb , lz ， sunbowen
 * 用户业务层
 */
@Service
public class WfUserServiceImpl implements WfUserService {

    @Autowired
    private WorkFlowUserMapper workFlowUserMapper;

    @Autowired
    private ActIdInfoMapper actIdInfoMapper;


    /**
     * 增加用户
     * @param userDTO 用户实体类
     * @return
     */
    @Override
    public Result addUser(UserDTO userDTO) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //1.判断用户是否已经存在
        User tempUser = identityService.createUserQuery().userId(userDTO.getUserId()).singleResult();
        if(tempUser!=null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_USER_ALREADY);
        }
        User user = new UserEntity();
        user.setId(userDTO.getUserId());
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        if(userDTO.getEmail()!=null){
            user.setEmail(userDTO.getEmail());
        }
        identityService.saveUser(user);
        //增加租户与用户之间的关系
        createTenantUserMembership(user.getId(),"A");
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    /**
     * 增加租户
     * @param name 租户名
     * @param id 租户id
     * @return
     */
    @Override
    public Result addTenant(String name, String id) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        Tenant tempTenant = identityService.createTenantQuery().tenantId(id).singleResult();
        if(tempTenant!=null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_TENANT_ALREADY);
        }
        Tenant tenant = new TenantEntity();
        tenant.setId(id);
        tenant.setName(name);
        identityService.saveTenant(tenant);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    /**
     * 删除租户
     * @param name 租户名
     * @param id 租户ID
     * @return
     */
    @Override
    public Result deleteTenant(String name, String id) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        Tenant tempTenant = identityService.createTenantQuery().tenantId(id).tenantName(name).singleResult();
        if(tempTenant == null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_TENANT_NULL);
        }
        identityService.deleteTenant(id);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }

    /**
     * 查询所有用户
     * @return  List<UserDTO>
     */
    @Override
    public Result<List<UserDTO>> queryAllUser(String userName,String groupId) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //createUserQuery获取所有user
        UserQuery userQuery = identityService.createUserQuery();
        if (!Strings.isEmpty(userName)){
            userQuery = userQuery.userFirstNameLike("%"+userName+"%");
        }
        if (!Strings.isEmpty(groupId)){
            userQuery = userQuery.memberOfGroup(groupId);
        }
        List<User> userList = userQuery
                .orderByUserId()
                .asc()
                .list();

        //自定义实体类集合userDTOS
        List<UserDTO> userDTOSList = new ArrayList<UserDTO>();


        //遍历将数据存入到userDTOSList中
        for (User user : userList
        ) {
            //将数据进行封装
            UserDTO userDTO = new UserDTO();
            //获得用户id
            userDTO.setUserId(user.getId());
            //获得姓
            userDTO.setFirstName(user.getFirstName());
            //获得名
            userDTO.setLastName(user.getLastName());
            //获得邮箱
            userDTO.setEmail(user.getEmail());
            //获取头像
            userDTO.setAvatar(actIdInfoMapper.selectAvatarById(user.getId()));
            userDTOSList.add(userDTO);
        }

        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS, userDTOSList);
    }

    /**
     * 添加组
     * @param groupId  组ID
     * @param name     组名称
     * @param type     组的类型
     * @return
     */
    @Override
    public Result addGroup(String groupId, String name, String type) {
        //获取identityService
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //检验groupId是否存在
        Group groupEntity = identityService.createGroupQuery().groupId(groupId).singleResult();
        if(groupEntity != null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_GROUP_ALREADY);
        }
        Group group = new GroupEntity();
        group.setId(groupId);
        group.setName(name);
        group.setType(type);
        identityService.saveGroup(group);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }
    /**
     * 删除组
     * @param groupId  组ID
     * @return
     */
    @Override
    public Result deleteGroup(String groupId) {
        //获取identityService
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //检验groupId是否存在
        Group groupEntity = identityService.createGroupQuery().groupId(groupId).singleResult();
        if(groupEntity == null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_TENANT_NULL);
        }

        identityService.deleteGroup(groupId);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }

    /**
     * 添加组和用户之间的关系
     * @param userId    用户id
     * @param groupIds   组id
     * @return
     */
    @Override
    public Result createMembership(String userId, List<String> groupIds) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        for(String groupIdOne: groupIds) {
            identityService.createMembership(userId, groupIdOne);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    /**
     * 添加租户与用户之间的关系
     * @return
     */
    @Override
    public Result createTenantUserMembership(String userId,String tenantId){
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        identityService.createTenantUserMembership(tenantId,userId);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    @Override
    public UserDTO selectByUserId(String userId) {
        return workFlowUserMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<String> queryAvatar(List<String> ids) {
        return actIdInfoMapper.selectAvatartByIds(ids);
    }

    /**
     * 删除组和用户之间的关系
     * @param userId    用户id
     * @param groupId   组id
     * @return
     */
    @Override
    public Result deleteMembership(String userId, String groupId) {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        identityService.deleteMembership(userId, groupId);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }
    /**
     * 查询所有组
     * @return
     */
    @Override
    public Result<List<GroupVO>> queryAllGroup() {
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        List<GroupVO> groupVOList = new ArrayList<>();
        List<Group> groupList = identityService.createGroupQuery().orderByGroupId().asc().list();
        for (Group group:groupList) {
            GroupVO groupVO=new GroupVO();
            groupVO.setGroupId(group.getId());
            groupVO.setGroupName(group.getName());
            groupVOList.add(groupVO);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS, groupVOList);
    }

}
