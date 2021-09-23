package org.jeecg.modules.form.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.form.entity.OperationLog;
import org.jeecg.modules.form.vo.OperationLogVO;

import javax.xml.crypto.Data;
import java.util.List;

/**
 * @ClassName: OperationLogMapper
 * @Description:
 * @author: HuangSn
 * @date: 2021/8/30  10:42
 */
@Mapper
@DS("form")
@InterceptorIgnore(tenantLine = "true")
public interface OperationLogMapper extends BaseMapper<OperationLog> {
    /**
     * 条件查询
     *
     * @param page     分页
     * @param user     用户名
     * @param type     日志类型
     * @param platform 操作平台
     * @return
     */
    IPage<OperationLog> listByParam(Page<OperationLog> page, String user, String type, String platform);

    /**
     * 添加操作日志
     *
     * @param operationLog 日志
     */
    void addOperationLog(OperationLog operationLog);

    /**
     * 根据表单id查询日志
     * @param formId 表单id
     * @return page
     */
    List<OperationLogVO> getLogByFormId(String formId);

    /**
     * 返回指定时间段的数据个数
     * @param start 开始时间
     * @param end 结束时间
     * @return string
     */
    String countInsertDuringTime(Data start, Data end);
}
