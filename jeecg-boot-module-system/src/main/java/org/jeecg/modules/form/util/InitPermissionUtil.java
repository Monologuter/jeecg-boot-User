package org.jeecg.modules.form.util;

import java.security.Permission;

/**
 * 用于JAVA编译接口时的安全检查拦截
 *
 * @ClassName: InitPermissionUtil
 * @author: HuangSn
 * @date: 2021/8/11  9:18
 */
public class InitPermissionUtil {
    public void initPermission() {
        SecurityManager originalSecurityManager = System.getSecurityManager();
        // 创建SecurityManager
        if (originalSecurityManager == null) {
            SecurityManager securityManager = new SecurityManager(){
                private void check(Permission perm) {
                    // 禁止exec
                    if (perm instanceof java.io.FilePermission) {
                        String actions = perm.getActions();
                        if (actions != null && actions.contains("execute") && !perm.getName().equals("<<ALL FILES>>")) {
                            throw new SecurityException("InitPermissionUtil Exception ：execute denied! 禁止使用execute命令代码！");
                        }
                    }
                    // 禁止设置新的SecurityManager，保护自己
                    if (perm instanceof RuntimePermission) {
                        String name = perm.getName();
                        if (name != null && name.contains("setSecurityManager")) {
                            throw new SecurityException("InitPermissionUtil Exception ：System.setSecurityManager denied! 禁止设置系统安全管理！");
                        }
                    }
                }

                @Override
                public void checkPermission(Permission perm) {
                    check(perm);
                }

                @Override
                public void checkPermission(Permission perm, Object context) {
                    check(perm);
                }
            };
            System.setSecurityManager(securityManager);
        }
    }
}