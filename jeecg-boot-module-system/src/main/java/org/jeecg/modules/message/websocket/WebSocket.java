package org.jeecg.modules.message.websocket;

import lombok.extern.slf4j.Slf4j;
import org.jeecg.boot.starter.redis.client.JeecgRedisClient;
import org.jeecg.common.base.BaseMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author scott
 * @Date 2019/11/29 9:41
 * @Description: 此注解相当于设置访问URL
 */
@Component
@ServerEndpoint(value = "/websocket/{userId}", configurator = CustomSpringConfigurator.class, encoders = MessageEncoder.class, decoders = MessageDecoder.class)
@Slf4j
public class WebSocket {
    public Map<String, Session> sessions = new ConcurrentHashMap<>();

    private static final String REDIS_TOPIC_NAME = "socketHandler";

    @Autowired
    private JeecgRedisClient jeecgRedisClient;

    @OnOpen
    public void onOpen(Session session, @PathParam(value = "userId") String userId) {
        sessions.put(userId, session);
        log.info("【websocket消息】有新的连接，总数为:" + (session.getOpenSessions().size() + 1));//当onOpen结束时，多一个session
    }

    @OnClose
    public void onClose(Session session, @PathParam("userId") String userId) {
        sessions.remove(userId);
        int size = Math.max((session.getOpenSessions().size() - 1), 0);//当onClose结束时，少一个session
        log.info("【websocket消息】连接断开，总数为:" + size);
    }

    @OnError
    public void onError(Session session, @PathParam("userId") String userId, Throwable throwable) {
        sessions.remove(userId);
        log.error("onError", throwable);
//        broadcast("User " + username + " left on error: " + throwable);
    }

    @OnMessage
    public void onMessage(String message, @PathParam("userId") String userId) {
        broadcast(message);
    }


    private void broadcast(String message) {
        sessions.values().forEach(s -> {
            s.getAsyncRemote().sendObject(message, result -> {
                if (result.getException() != null) {
                    log.error("Unable to send message: " + result.getException());
                }
            });
        });
    }


    public void pushMessage(String[] userIds, String message) {
        for (String userId : userIds) {
            pushMessage(userId, message);
        }
    }


    /**
     * 服务端推送消息
     *
     * @param userId
     * @param message
     */
    public void pushMessage(String userId, String message) {
        Session session = sessions.get(userId);
        log.info("-------------------------push message " + userId);
        if (session != null && session.isOpen()) {
            session.getAsyncRemote().sendObject(message, result -> {
                log.error("Unable to send message: " + result.getException());
            });
        }
    }

    /**
     * 服务器端推送消息
     */
    public void pushMessage(String message) {
        broadcast(message);
        log.info(message);
    }


//    @OnMessage
//    public void onMessage(String message) {
//        //todo 现在有个定时任务刷，应该去掉
//        log.debug("【websocket消息】收到客户端消息:" + message);
//        JSONObject obj = new JSONObject();
//        obj.put(WebsocketConst.MSG_CMD, WebsocketConst.CMD_CHECK);//业务类型
//        obj.put(WebsocketConst.MSG_TXT, "心跳响应");//消息内容
//        for (org.jeecg.modules.message.websocket.WebSocket webSocket : webSockets) {
//            webSocket.pushMessage(message);
//        }
//    }

    /**
     * 后台发送消息到redis
     *
     * @param message
     */
    public void sendMessage(String message) {
        log.info("【websocket消息】广播消息:" + message);
        BaseMap baseMap = new BaseMap();
        baseMap.put("userId", "");
        baseMap.put("message", message);
        jeecgRedisClient.sendMessage(REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此为单点消息
     *
     * @param userId
     * @param message
     */
    public void sendMessage(String userId, String message) {
        BaseMap baseMap = new BaseMap();
        baseMap.put("userId", userId);
        baseMap.put("message", message);
        jeecgRedisClient.sendMessage(REDIS_TOPIC_NAME, baseMap);
    }

    /**
     * 此为单点消息(多人)
     *
     * @param userIds
     * @param message
     */
    public void sendMessage(String[] userIds, String message) {
        for (String userId : userIds) {
            sendMessage(userId, message);
        }
    }


}
