package org.jeecg.modules.form.constant;

/**
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName OperationPlatform
 * @CreateTime 2021-08-30 13:13
 * @Version 1.0
 * @Description:
 * TODO: 因为还不能通过“user-agent”有效判断请求来源，所以切面那里直接用的“user-agent”串，没有转化为下列枚举类型
 */
public enum OperationPlatform {
    // 可取值：web/app/wechat/api
    WEB,
    APP,
    WECHAT,
    API;
}
