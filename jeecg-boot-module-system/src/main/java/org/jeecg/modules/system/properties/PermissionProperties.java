package org.jeecg.modules.system.properties;

public interface PermissionProperties {
    /**
     * create_ways
     * 应用菜单 0， 系统菜单1
     */
    int CREATE_WAYS_APPLICATION = 0;
    int CREATE_WAYS_SYSTEM = 1;
    /**
     * menu_type
     * 文件夹 0，表单 1. 按钮权限 2，图表 3，工作流 4
     * 系统菜单menu_type都等于0
     */
    int MENU_TYPE_DIRECTORY = 0;
    int MENU_TYPE_BUTTON = 1;
    int MENU_TYPE_FORM = 2;
    int MENU_TYPE_GRAPH = 3;
    int MENU_TYPE_WORKFLOW = 4;

    // special permission
    //首页菜单
    String PERMISSION_NAME_INDEX = "首页";

}
