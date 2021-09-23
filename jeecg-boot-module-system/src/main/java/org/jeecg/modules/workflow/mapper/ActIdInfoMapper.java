package org.jeecg.modules.workflow.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.lettuce.core.dynamic.annotation.Param;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author 陈亚
 * Company DXC.technology
 * @ClassName ActIdInfoMapper
 * @CreateTime 2021-08-04 14:55
 * @Version 1.0
 * @Description:
 */
@Mapper
public interface ActIdInfoMapper extends BaseMapper<T> {
     /**
      * 根据用户id批量查询用户头像
      */
     List<String> selectAvatartByIds(@Param("ids")List<String> ids);


     /**
      * 根据用户id查询用户头像 从sys_user表中获取
      */
     String selectAvatarById(@Param("id") String id);
}
