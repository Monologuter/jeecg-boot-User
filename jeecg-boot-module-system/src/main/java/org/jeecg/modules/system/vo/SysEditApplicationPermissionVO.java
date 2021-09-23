package org.jeecg.modules.system.vo;

import lombok.Data;
import org.jeecg.modules.system.model.SysPermissionTree;

import java.util.List;

@Data
public class SysEditApplicationPermissionVO {
    private String applicationId ;
    private List<SysPermissionTree> sysPermissionTreeList ;
}
