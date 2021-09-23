package org.jeecg.modules.form.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.jeecg.modules.form.entity.OperationLog;
import org.jeecg.modules.form.vo.OpeartionLogListVO;
import org.jeecg.modules.form.vo.OperationLogVO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 表单数据操作日志Service层接口类
 *
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName OperationLogService
 * @CreateTime 2021-08-30 16:22
 * @Version 1.0
 */
@Service
public interface OperationLogService {

    /**
     * 按照参数查询日志
     *
     * @param page     分页
     * @param user     用户名
     * @param type     类型
     * @param platform 平台
     * @return IPage<OperationLog> 返回表单数据操作日志对象的IPage
     */
    IPage<OperationLog> listLogByParam(Page<OperationLog> page, String user, String type, String platform);

    /**
     * 添加表单数据操作日志
     *
     * @param operationLog 表单数据操作日志对象
     */
    void addOperationLog(OperationLog operationLog);

    /**
     * 根据表单id查询日志
     * @param formId 表单id
     * @return list
     */
    List<OpeartionLogListVO> getLogByFormId(String formId);
}
