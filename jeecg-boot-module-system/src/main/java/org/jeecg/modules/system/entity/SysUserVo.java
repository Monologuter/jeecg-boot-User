package org.jeecg.modules.system.entity;

import lombok.Data;

@Data
public class SysUserVo extends SysUser {
    private String selectedroles;
    private String selecteddeparts;


}
