package org.jeecg.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.constant.WebsocketConst;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.message.websocket.WebSocket;
import org.jeecg.modules.system.entity.SysAnnouncement;
import org.jeecg.modules.system.entity.SysAnnouncementSend;
import org.jeecg.modules.system.entity.SysUser;
import org.jeecg.modules.system.mapper.SysAnnouncementMapper;
import org.jeecg.modules.system.mapper.SysAnnouncementSendMapper;
import org.jeecg.modules.system.mapper.SysUserMapper;
import org.jeecg.modules.system.service.IPushService;
import org.jeecg.modules.system.util.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class PushService implements IPushService {

    private final SysAnnouncementMapper sysAnnouncementMapper;
    private final SysAnnouncementSendMapper sysAnnouncementSendMapper;
    private final SysUserMapper userMapper;
    private final WebSocket webSocket;

    public PushService(SysAnnouncementMapper sysAnnouncementMapper, SysAnnouncementSendMapper sysAnnouncementSendMapper, SysUserMapper userMapper, WebSocket webSocket) {
        this.sysAnnouncementMapper = sysAnnouncementMapper;
        this.sysAnnouncementSendMapper = sysAnnouncementSendMapper;
        this.userMapper = userMapper;
        this.webSocket = webSocket;


    }

    @Override
    public void unicast(String fromUsername, String title, String content, String toUserIds) {
        SysAnnouncement sysAnnouncement = new MessageBuilder()
                .fromUsername(fromUsername)
                .setTitle(title).setContent(content)
                .toUserIds(toUserIds).build();
        generalPush(sysAnnouncement);
    }

    @Override
    public void multicast() {
        // TODO: 2021/7/16 组播
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    @Override
    public void broadcast(String fromUser, String title, String content) {
        // TODO: 2021/7/16 广播
//        SysAnnouncement sysAnnouncement = new MessageBuilder()
//                .from(fromUser)
//                .setTitle(title).setContent(content)
//                .broadcast().build();
//        generalPush(sysAnnouncement);
        throw new UnsupportedOperationException("Not implemented, yet");
    }

    @Override
    public void generalPush(SysAnnouncement sysAnnouncement) {
        sysAnnouncement.setSendStatus(CommonConstant.HAS_SEND);
        sysAnnouncement.setSendTime(new Date());
        sysAnnouncementMapper.insert(sysAnnouncement);
        // 2.插入用户通告阅读标记表记录
        String userId = sysAnnouncement.getUserIds();
        String[] userIds = userId.split(",");
        String anntId = sysAnnouncement.getId();
        for (String id : userIds) {
            if (oConvertUtils.isNotEmpty(id)) {
                SysUser sysUser = userMapper.selectById(id);
                if (sysUser == null) {
                    continue;
                }
                SysAnnouncementSend announcementSend = new SysAnnouncementSend();
                announcementSend.setAnntId(anntId);
                announcementSend.setUserId(sysUser.getId());
                announcementSend.setReadFlag(CommonConstant.NO_READ_FLAG);
                sysAnnouncementSendMapper.insert(announcementSend);
                JSONObject obj = new JSONObject();
                obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_USER);
                obj.put(WebsocketConst.MSG_USER_ID, sysUser.getId());
                obj.put(WebsocketConst.MSG_ID, sysAnnouncement.getId());
                obj.put(WebsocketConst.MSG_TXT, sysAnnouncement.getTitile());
                webSocket.sendMessage(sysUser.getId(), obj.toJSONString());
            }
        }

    }

}
