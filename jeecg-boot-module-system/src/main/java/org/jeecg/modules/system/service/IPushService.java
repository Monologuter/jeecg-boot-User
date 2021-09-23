package org.jeecg.modules.system.service;

import org.jeecg.modules.system.entity.SysAnnouncement;
import org.jeecg.modules.system.util.MessageBuilder;

/**
 * IPushService是其他模块调用消息模块推送服务的外部接口。
 * <p>
 *     {@code unicast}、{@code multicast}和{@code broadcast}是提供给调用者的方便调用的外部接口，
 *     其具体实现都放在{@code generalPush}中。通过{@code generalPush}可以实现对消息更详细的自定义配置。
 *     创建{@code SysAnnouncement}消息对象，推荐使用{@link MessageBuilder}对象来构建。
 * </p>
 * <p>
 *     使用方法：
 *     <pre>{@code
 *     @Autowired
 *     private IPushService pushService;
 *
 *     public static final String fromUser = "admin";
 *     public static final String toUserIds = "dummyId";//这里换成真正的用户id
 *     public static final String title = "title";
 *     public static final String content = "content";
 *
 *
 *     public void unicast(){
 *         pushService.unicast(fromUser,title,content,toUserIds);
 *     }
 *
 *
 *     public void  generalPush(){
 *         SysAnnouncement sysAnnouncement =
 *                 new MessageBuilder()
 *                     .fromUsername(fromUser)
 *                         .setTitle(title)
 *                         .setContent(content)
 *                     .toUserIds(toUserIds).build();
 *        pushService.generalPush(sysAnnouncement);
 *     }
 *
 *     }</pre>
 * </p>
 * @author liujiabao
 * @see MessageBuilder
 * @see SysAnnouncement
 *
 *
 *
 *
 */
public interface IPushService {
    /**
     * 推送给指定用户消息
     * @param fromUsername 消息的发送人的用户名(username)
     * @param title 消息的标题
     * @param content 消息的内容
     * @param toUserIds 消息的目标，具体来时这里要传递的是消息目标用户的id，当传递给多个用户时，以;分隔
     */
    void unicast(String fromUsername, String title, String content, String toUserIds);

    /**
     * 推送给组用户消息
     */
    void multicast();

    /**
     * 广播消息
     * @param fromUser 消息的发送人的用户名(username)
     * @param title 消息的标题
     * @param content 消息的内容
     */
    void broadcast(String fromUser, String title, String content);

    /**
     * 消息推送，通过参数实现更详细的自定义配置
     * @param sysAnnouncement 消息的具体配置
     */
    void generalPush(SysAnnouncement sysAnnouncement);

}
