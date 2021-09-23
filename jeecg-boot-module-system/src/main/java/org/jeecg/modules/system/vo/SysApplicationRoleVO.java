package org.jeecg.modules.system.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jeecg.modules.system.entity.SysApplication;
import org.jeecg.modules.system.entity.SysRole;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysApplicationRoleVO {
    private SysApplication sysApplication;
    private List<String> roleIds;
}
