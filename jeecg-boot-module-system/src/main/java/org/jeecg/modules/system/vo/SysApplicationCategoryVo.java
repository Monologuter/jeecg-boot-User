package org.jeecg.modules.system.vo;

import lombok.Data;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysCategory;

import java.util.List;

@Data
public class SysApplicationCategoryVo {
    private List<SysApplication> sysApplication;
    private List<SysCategory> sysCategory;
}
