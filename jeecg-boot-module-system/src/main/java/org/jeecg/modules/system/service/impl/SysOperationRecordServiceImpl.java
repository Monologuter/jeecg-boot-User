package org.jeecg.modules.system.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.system.entity.*;
import org.jeecg.modules.system.mapper.*;
import org.jeecg.modules.system.properties.ApplicationProperties;
import org.jeecg.modules.system.properties.OperationProperties;
import org.jeecg.modules.system.service.ISysOperationRecordService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SysOperationRecordServiceImpl extends ServiceImpl<SysOperationRecordMapper, SysOperationRecord> implements ISysOperationRecordService {
    @Resource
    private SysOperationRecordMapper sysOperationRecordMapper;

    @Resource
    private SysApplicationMapper sysApplicationMapper;

    @Resource
    private SysDepartApproleApplicationMapper sysDepartApproleApplicationMapper;

    @Resource
    private SysUserApproleApplicationMapper sysUserApproleApplicationMapper;

    @Resource
    private SysRoleApproleApplicationMapper sysRoleApproleApplicationMapper;

    /**
     * @param operationRecordId 操作表id
     * @return
     */
    @Override
    @Transactional
    public Boolean reduction(String operationRecordId) {
        SysOperationRecord sysOperationRecord = sysOperationRecordMapper.selectById(operationRecordId);
        int operationType = sysOperationRecord.getOperationType();
        Boolean success = false;
        if (operationType == OperationProperties.OPERATION_TYPE_BASE_INFO) {
            success = this.applicationBaseInfoRestore(sysOperationRecord);
        } else if (operationType == OperationProperties.OPERATION_APPLICATION_ROLE_INFO) {
            success = this.applicationRoleUpdateRestore(sysOperationRecord);
        }
        return success;
    }

    /**
     * 查询当前应用id下的操作记录
     * @param applicationId
     * @return
     */
    @Override
    @Transactional
    public  List<SysOperationRecord> displayOperationRecord(String applicationId){
        List<SysOperationRecord> sysOperationRecords=sysOperationRecordMapper.selectList(new LambdaQueryWrapper<SysOperationRecord>()
        .eq(SysOperationRecord::getApplicationId,applicationId));
        getOperationContain(sysOperationRecords);
        return sysOperationRecords;
    }

    /**
     * 判断操作内容
     * @param sysOperationRecordList
     * @return
     */
    public List<SysOperationRecord> getOperationContain(List<SysOperationRecord> sysOperationRecordList){
        for (SysOperationRecord sysOperationRecord:sysOperationRecordList){
            String data=sysOperationRecord.getData();
            if(sysOperationRecord.getOperationType()==OperationProperties.OPERATION_TYPE_BASE_INFO){
                sysOperationRecord.setData("修改了应用基础设置的信息");
            }
            else if(sysOperationRecord.getOperationType()==OperationProperties.OPERATION_APPLICATION_ROLE_INFO){
                sysOperationRecord.setData("修改了成员管理的配置信息");
            }
        }
        return sysOperationRecordList;
    }

    /**
     * 应用基础信息还原方法
     *
     * @param sysOperationRecord
     * @return
     */
    public Boolean applicationBaseInfoRestore(SysOperationRecord sysOperationRecord) {
        SysApplication sysApplication = JSONObject.parseObject(sysOperationRecord.getData(), SysApplication.class);
        int i = sysApplicationMapper.updateById(sysApplication);
        return i < 1;
    }

    /**
     * 应用角色还原方法
     *
     * @param sysOperationRecord
     * @return
     */
    public Boolean applicationRoleUpdateRestore(SysOperationRecord sysOperationRecord) {
        //解析json数据
        Map<String, Object> map = JSONObject.parseObject(sysOperationRecord.getData(), Map.class);
        String applicationId = sysOperationRecord.getApplicationId();
        String appRoleId = (String) map.get("appRoleId");
        List<String> ids = new ArrayList<String>((List<String>) map.get("ids"));
        int selectFlag = (int) map.get("selectFlag");
        if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_USER) {
            sysUserApproleApplicationMapper.delete(new LambdaQueryWrapper<SysUserApproleApplication>()
                    .eq(SysUserApproleApplication::getApplicationId, applicationId)
                    .eq(SysUserApproleApplication::getApproleId, appRoleId));
            for (String userId : ids) {
                sysUserApproleApplicationMapper.insert(new SysUserApproleApplication(userId, appRoleId, applicationId));
            }
        } else if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_ROLE) {
            sysRoleApproleApplicationMapper.delete(new LambdaQueryWrapper<SysRoleApproleApplication>()
                    .eq(SysRoleApproleApplication::getApplicationId, applicationId)
                    .eq(SysRoleApproleApplication::getApproleId, appRoleId));
            for (String roleId : ids) {
                sysRoleApproleApplicationMapper.insert(new SysRoleApproleApplication(roleId, appRoleId, applicationId));
            }
        } else if (selectFlag == ApplicationProperties.APPLICATION_SELECT_FLAG_DEPART) {
            sysDepartApproleApplicationMapper.delete(new LambdaQueryWrapper<SysDepartApproleApplication>()
                    .eq(SysDepartApproleApplication::getApplicationId, applicationId)
                    .eq(SysDepartApproleApplication::getApproleId, appRoleId));
            for (String departId : ids) {
                sysDepartApproleApplicationMapper.insert(new SysDepartApproleApplication(departId, appRoleId, applicationId));
            }
        } else {
            return false;
        }
        return true;
    }
}
