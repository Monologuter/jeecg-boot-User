package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.UserDTO;

/**
 * @Author 孙博文
 * @date: 2021年07月26日 13:40
 */
@Mapper
public interface WorkFlowUserMapper extends BaseMapper {
    UserDTO selectByPrimaryKey(String id);
}
