package org.jeecg.modules.message.mapper;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.jeecg.modules.message.entity.SysMessage;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
public interface SysMessageMapper extends BaseMapper<SysMessage> {

    IPage<SysMessage> findSysMessageLike(SysMessage sysMessage,Page<?> page);




}
