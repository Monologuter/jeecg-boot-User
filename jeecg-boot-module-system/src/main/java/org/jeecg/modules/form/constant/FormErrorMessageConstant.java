package org.jeecg.modules.form.constant;

/**
 * 表单错误信息常量类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/18 14:12
 */
public final class FormErrorMessageConstant {
    private FormErrorMessageConstant(){}

    public static final String FORM_SAVE_FAILED = "表单保存失败!";

    public static final String FORM_SAVE_FAILED_BECAUSE_STYLE_CODE_IS_USED = "表单全局样式编码已经存在!";

    public static final String PERMISSION_SAVE_FAILED = "菜单路由信息创建失败!";

    public static final String PERMISSION_UPDATE_FAILED = "菜单路由信息更新失败!";

    public static final String ROLE_PERMISSION_SAVE_FAILED = "菜单授权失败!";

    public static final String FORM_NOT_EXISTS = "表单不存在!";

    public static final String FORM_DELETE_FAILED = "表单删除失败!";

    public static final String RELATIVE_PERMISSIONS_OR_ROLE_PERMISSIONS_DELETE_FAILED = "删除相关菜单或角色授权失败!";

    public static final String FORM_UPDATE_FAILED = "表单更新失败!";

    public static final String FORM_RECOGNIZE_FAILED = "表单识别失败";

    public static final String FORM_NAME_ALL_SPACE = "表单名称不能全为空格";
}
