package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;
import java.util.List;

/**
 * 最近使用-业务层
 * @author 权计超
 * @date 2021-6-10
 */
public interface WfRecentUserService {
    /**
     * 获取最近使用
     * @param userId
     */
    Result<List<ProcessDevelopmentVO>> getRecentUse(String userId);
}
