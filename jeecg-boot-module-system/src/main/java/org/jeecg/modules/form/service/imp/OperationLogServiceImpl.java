package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.form.entity.OperationLog;
import org.jeecg.modules.form.mapper.OperationLogMapper;
import org.jeecg.modules.form.service.OperationLogService;
import org.jeecg.modules.form.vo.OpeartionLogListVO;
import org.jeecg.modules.form.vo.OperationLogVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 表单数据操作日志记录
 *
 * @Author HuangSn
 * Company DXC.technology
 * @ClassName OperationLogServicelmpl
 * @CreateTime 2021-08-30 16:23
 * @Version 1.0
 */
@Service
@Slf4j
@DS("form")
public class OperationLogServiceImpl implements OperationLogService {

    @Autowired
    OperationLogMapper operationLogMapper;

    /**
     * 按照参数查询日志
     *
     * @param page     分页
     * @param user     用户名
     * @param type     类型
     * @param platform 平台
     * @return IPage<OperationLog> 返回表单数据操作日志对象的IPage
     */
    @Override
    public IPage<OperationLog> listLogByParam(Page<OperationLog> page, String user, String type, String platform) {
        return operationLogMapper.listByParam(page, user, type, platform);
    }

    /**
     * 添加表单数据操作日志
     *
     * @param operationLog 表单数据操作日志对象
     */
    @Override
    public void addOperationLog(OperationLog operationLog) {
        operationLogMapper.addOperationLog(operationLog);
    }

    /**
     * 查询接口：图标组可能需要的接口
     * 根据表单id去查询日志数据
     *
     * @param formId 表单id
     * @return page
     */
    @Override
    public List<OpeartionLogListVO> getLogByFormId(String formId) {
        List<OperationLogVO> objects = operationLogMapper.getLogByFormId(formId);
        log.info("object length"+objects.size());
        // 返回String结果
        List<OpeartionLogListVO> result = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
        OpeartionLogListVO opeartionLogListVO=new OpeartionLogListVO();
        String operation = "";
        for (OperationLogVO o : objects) {
            if ("INSERT".equals(o.getOperType())) {
                operation = "新增";
            } else if ("UPDATE".equals(o.getOperType())) {
                operation = "修改";
            } else if ("DELETE".equals(o.getOperType())) {
                operation = "删除";
            }
            opeartionLogListVO.setInformation("information");
            opeartionLogListVO.setValue(simpleDateFormat.format(o.getCreateTime()) + " " + o.getOperUser() + " " + operation + "了一条招聘信息");
            result.add(opeartionLogListVO);
        }
        return result;
    }
}
