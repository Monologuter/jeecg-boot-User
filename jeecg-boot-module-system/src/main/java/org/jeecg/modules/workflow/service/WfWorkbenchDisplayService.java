package org.jeecg.modules.workflow.service;

import org.jeecg.common.api.vo.Result;

public interface WfWorkbenchDisplayService {
    Result<Object> workbenchDisplayFindAll(String userId);
}

