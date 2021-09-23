package org.jeecg.modules.form.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName OperationLogVO
 * @Description:
 * @Author HuangSn
 * @Date 2021-09-10 9:11 上午
 */
@Data
public class OperationLogVO {
    private String operUser;
    private String operType;
    private Date createTime;
}
