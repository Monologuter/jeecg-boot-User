package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.UserDTO;

/**
 * @Author 陈亚
 * Company DXC.technology
 * @ClassName WfActIdUser
 * @CreateTime 2021-07-19 11:28
 * @Version 1.0
 * @Description:
 */
@Mapper
public interface WfActIdUserMapper {

    /**
     * 根据id更新用户
     * */
    void updateById(UserDTO userDTO);
}
