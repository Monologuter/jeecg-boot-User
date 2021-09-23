package org.jeecg.modules.form.entity;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * 表单数据操作日志记录DO
 *
 * @ClassName: OperationLog
 * @author: HuangSn
 * @date: 2021/8/30  9:55
 */
@Data
@TableName(value = "fd_form_data_oper_log")
public class OperationLog{
    @TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键id")
    private String id;

    @ApiModelProperty(value = "新增的业务数据id需要返填回oper_log表")
    private String formDataId;

    @ApiModelProperty(value = "表单id")
    private String formId;

    @ApiModelProperty(value = "可取值：insert/update/delete/read")
    private String operType;

    @ApiModelProperty(value = "可取值：web/app/wechat/api")
    private String operPlatform;

    @ApiModelProperty(value = "登录账号")
    private String operUser;

    @ApiModelProperty(value = "可为空，代表账号没设置部门")
    private String operDeptId;

    @ApiModelProperty(value = "主要用于监控远程客户端IP")
    private String operIp;

    @ApiModelProperty(value = "主要用于监控远程客户端端口")
    private String operPort;

    @ApiModelProperty(value = "旧的表单业务数据--对新增操作为空，json格式")
    private JSONObject rawOldData;

    @ApiModelProperty(value = "新的表单业务数据，json格式")
    private JSONObject rawNewData;

    @ApiModelProperty(value = "可取值：0--失败；1--成功")
    private String operResult;

    @ApiModelProperty(value = "加入时间")
    private Date createTime;
}
