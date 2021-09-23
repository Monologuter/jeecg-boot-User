package org.jeecg.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.system.entity.SysOperationRecord;

import java.util.List;

public interface ISysOperationRecordService extends IService<SysOperationRecord> {

    /**
     * 还原操作
     * @param operationRecordId 操作表id
     * @return
     */
    Boolean reduction(String operationRecordId);

    /**
     * 展示操作记录
     * @param applicationId
     * @return
     */
    List<SysOperationRecord> displayOperationRecord(String applicationId);
}
