package org.jeecg.modules.system.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author 陈亚
 * Company DXC.technology
 * @ClassName SysMemberShipMapper
 * @CreateTime 2021-06-09 15:18
 * @Version 1.0
 * @Description:
 */

@Mapper
public interface SysMemberShipMapper {


    @Delete("delete from act_id_membership where USER_ID_  = #{id} ")
    void deleteMembershipById(@Param("id") String id);

}
