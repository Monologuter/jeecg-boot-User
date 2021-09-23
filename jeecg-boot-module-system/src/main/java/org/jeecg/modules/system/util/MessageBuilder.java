package org.jeecg.modules.system.util;

import org.jeecg.common.constant.CommonConstant;
import org.jeecg.modules.system.entity.SysAnnouncement;

import java.util.Date;

/**
 * MessageBuilder是用于方便构建{@code SysAnnouncement}推送消息对象的工具类，该类对不常用的消息参数使用默认配置，
 * 具体参见类实现。
 *
 * @author liujiabao
 * @see SysAnnouncement
 */
public  class MessageBuilder {
    //required field;

    /**
     * 消息标题
     */
    private String title;
    /**
     * 消息内容
     */
    private String content;
    /**
     * 消息的发送人，这里应该指定用户的用户名(username)
     */
    private String sender;

    //optional field

    private Date startTime;
    private Date endTime;
    private Date cancelTime;


    /**
     * 消息优先级
     */
    private String priority = CommonConstant.PRIORITY_L;

    /**
     * 消息种类，通告/系统消息
     */
    private String msgCategory = CommonConstant.MSG_CATEGORY_1;

    /**
     *消息类型，发送给指定用户还是全体全体
     */
    private String msgType = CommonConstant.MSG_TYPE_UESR;

    /**
     * 消息的发布状态， CommonConstant.NO_SEND表示消息未发布， CommonConstant.HAS_SEND表示消息已发送，
     * CommonConstant.HAS_SEND表示已撤销
     */
    private String sendStatus = CommonConstant.NO_SEND;

    /**
     * 消息的发送时间
     */
    private Date sendTime = new Date();

    /**
     * 消息是否删除的的标志
     */
    private String delFlag = String.valueOf(CommonConstant.DEL_FLAG_0);

    /**
     * 消息目标用户的id
     **/
    private java.lang.String userIds;
    /**
     * 业务类型(email:邮件 bpm:流程)
     */
    private java.lang.String busType;
    /**
     * 业务id
     */
    private java.lang.String busId;
    /**
     * 打开方式 组件：component 路由：url
     */
    private java.lang.String openType;
    /**
     * 组件/路由 地址
     */
    private java.lang.String openPage;
    /**
     * 消息摘要
     */
    private java.lang.String msgAbstract;



    public MessageBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public MessageBuilder setContent(String content) {
        this.content = content;
        return this;
    }

    public MessageBuilder setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public MessageBuilder setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public MessageBuilder setCancelTime(Date cancelTime) {
        this.cancelTime = cancelTime;
        return this;
    }

    public MessageBuilder fromUsername(String sender) {
        this.sender = sender;
        return this;
    }

    public MessageBuilder toUserIds(String userIds) {
        this.userIds = userIds;
        this.msgType = CommonConstant.MSG_TYPE_ALL;
        return this;
    }

    public MessageBuilder broadcast() {
        this.msgType = CommonConstant.MSG_TYPE_ALL;
        return this;
    }

    public MessageBuilder setPriority(String priority) {
        this.priority = priority;
        return this;
    }

    public MessageBuilder setMsgCategory(String msgCategory) {
        this.msgCategory = msgCategory;
        return this;
    }

    public MessageBuilder setMsgType(String msgType) {
        this.msgType = msgType;
        return this;
    }

    public MessageBuilder setSendStatus(String sendStatus) {
        this.sendStatus = sendStatus;
        return this;
    }

    public MessageBuilder setSendTime(Date sendTime) {
        this.sendTime = sendTime;
        return this;
    }

    public MessageBuilder setDelFlag(String delFlag) {
        this.delFlag = delFlag;
        return this;
    }


    public MessageBuilder setBusType(String busType) {
        this.busType = busType;
        return this;
    }

    public MessageBuilder setBusId(String busId) {
        this.busId = busId;
        return this;
    }

    public MessageBuilder setOpenType(String openType) {
        this.openType = openType;
        return this;
    }

    public MessageBuilder setOpenPage(String openPage) {
        this.openPage = openPage;
        return this;
    }

    public MessageBuilder setMsgAbstract(String msgAbstract) {
        this.msgAbstract = msgAbstract;
        return this;
    }



    public SysAnnouncement build() {
        SysAnnouncement sysAnnouncement = new SysAnnouncement();
        sysAnnouncement.setTitile(title);
        sysAnnouncement.setBusId(busId);
        sysAnnouncement.setBusType(busType);
        sysAnnouncement.setCancelTime(cancelTime);
        sysAnnouncement.setMsgContent(content);
        sysAnnouncement.setDelFlag(delFlag);
        sysAnnouncement.setEndTime(endTime);
        sysAnnouncement.setMsgAbstract(msgAbstract);
        sysAnnouncement.setMsgCategory(msgCategory);
        sysAnnouncement.setMsgType(msgType);
        sysAnnouncement.setOpenType(openType);
        sysAnnouncement.setOpenPage(openPage);
        sysAnnouncement.setPriority(priority);
        sysAnnouncement.setSender(sender);
        sysAnnouncement.setSendStatus(sendStatus);
        sysAnnouncement.setSendTime(sendTime);
        sysAnnouncement.setStartTime(startTime);
        sysAnnouncement.setUserIds(userIds);
        return sysAnnouncement;
    }
}
