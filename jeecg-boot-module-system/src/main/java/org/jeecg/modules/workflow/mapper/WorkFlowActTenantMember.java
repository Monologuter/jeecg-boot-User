package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
/**
 * @Author 陈亚
 * Company DXC.technology
 * @ClassName WorkFlowActTenantMember
 * @CreateTime 2021-07-16 09:44
 * @Version 1.0
 * @Description:
 */
@Mapper
public interface WorkFlowActTenantMember {

    /**
     * 根据用户id删除Tenat表数据
     * */
    void deleteTenantById(@Param("id") String id);
}
