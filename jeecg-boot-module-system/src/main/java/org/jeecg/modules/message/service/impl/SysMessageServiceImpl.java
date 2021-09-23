package org.jeecg.modules.message.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.common.system.base.service.impl.JeecgServiceImpl;
import org.jeecg.modules.message.entity.SysMessage;
import org.jeecg.modules.message.mapper.SysMessageMapper;
import org.jeecg.modules.message.service.ISysMessageService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * @Description: 消息
 * @Author: jeecg-boot
 * @Date:  2019-04-09
 * @Version: V1.0
 */
@Service
public class SysMessageServiceImpl extends JeecgServiceImpl<SysMessageMapper, SysMessage> implements ISysMessageService {

    private final SysMessageMapper sysMessageMapper;

    public SysMessageServiceImpl(SysMessageMapper sysMessageMapper) {
        this.sysMessageMapper = sysMessageMapper;
    }

    public IPage<SysMessage> findSysMessage(SysMessage sysMessage, int page, int size){
        return sysMessageMapper.findSysMessageLike(sysMessage, new Page<>(page,size));
    }



}
