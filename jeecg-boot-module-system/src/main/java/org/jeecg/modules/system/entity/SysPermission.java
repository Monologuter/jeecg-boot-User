package org.jeecg.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecg.common.aspect.annotation.Dict;
import org.jeecg.modules.system.model.SysPermissionTree;
import org.jeecgframework.poi.excel.annotation.Excel;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 菜单权限表
 * </p>
 *
 * @Author scott
 * @since 2018-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class SysPermission implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	@Excel(name="页面编码",width = 15)
	@TableId(type = IdType.ASSIGN_ID)
	private String id;

	/**
	 * 父id
	 */
	private String parentId;

	/**
	 * 菜单名称
	 */
	@Excel(name="页面名称",width = 15)
	private String name;

	/**
	 * 菜单权限编码，例如：“sys:schedule:list,sys:schedule:info”,多个逗号隔开
	 */
	private String perms;
	/**
	 * 权限策略1显示2禁用
	 */
	private String permsType;

	/**
	 * 菜单图标
	 */
	private String icon;

	/**
	 * 组件
	 */
	private String component;
	
	/**
	 * 组件名字
	 */
	private String componentName;

	/**
	 * 路径
	 */
	@Excel(name="页面类型",width = 15)
	private String url;
	/**
	 * 一级菜单跳转地址
	 */
	private String redirect;

	/**
	 * 菜单排序
	 */
	private Double sortNo;

	/**
	 * 类型（0：一级菜单；1：子菜单 ；2：按钮权限）
	 */
	@Dict(dicCode = "menu_type")
	private Integer menuType;

	/**
	 * 是否叶子节点: 1:是  0:不是
	 */
	@TableField(value="is_leaf")
	private boolean leaf;
	
	/**
	 * 是否路由菜单: 0:不是  1:是（默认值1）
	 */
	@TableField(value="is_route")
	private boolean route;


	/**
	 * 是否缓存页面: 0:不是  1:是（默认值1）
	 */
	@TableField(value="keep_alive")
	private boolean keepAlive;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 删除状态 0正常 1已删除
	 */
	private Integer delFlag;
	
	/**
	 * 是否配置菜单的数据权限 1是0否 默认0
	 */
	private Integer ruleFlag;
	
	/**
	 * 是否隐藏路由菜单: 0否,1是（默认值0）
	 */
	private boolean hidden;

	/**
	 * 创建时间
	 */
	private Date createTime;

	/**
	 * 更新人
	 */
	private String updateBy;

	/**
	 * 更新时间
	 */
	private Date updateTime;
	
	/**按钮权限状态(0无效1有效)*/
	private java.lang.String status;
	
	/**alwaysShow*/
    private boolean alwaysShow;

	/*update_begin author:wuxianquan date:20190908 for:实体增加字段 */
	/**
	 * 外链菜单打开方式 0/内部打开 1/外部打开
	 */
	private boolean internalOrExternal;
	/*update_end author:wuxianquan date:20190908 for:实体增加字段 */

	/**
	 * 创建途径
	 */
	private int createWays;

	/**
	 * 多租户字段
	 */
	private Integer tenantId;

	public SysPermission() {

	}

	public SysPermission(SysPermissionTree sysPermissionTree) {
		this.id = sysPermissionTree.getId();
		this.perms = sysPermissionTree.getPerms();
		this.permsType = sysPermissionTree.getPermsType();
		this.component = sysPermissionTree.getComponent();
        this.createBy = sysPermissionTree.getCreateBy();
        this.createTime = sysPermissionTree.getCreateTime();
        this.delFlag = sysPermissionTree.getDelFlag();
        this.description = sysPermissionTree.getDescription();
        this.icon = sysPermissionTree.getIcon();
        this.menuType = sysPermissionTree.getMenuType();
        this.name = sysPermissionTree.getName();
        this.parentId = sysPermissionTree.getParentId();
        this.sortNo = sysPermissionTree.getSortNo();
        this.updateBy = sysPermissionTree.getUpdateBy();
        this.updateTime = sysPermissionTree.getUpdateTime();
        this.redirect = sysPermissionTree.getRedirect();
        this.url = sysPermissionTree.getUrl();
        this.hidden = sysPermissionTree.isHidden();
        this.route = sysPermissionTree.isRoute();
        this.keepAlive = sysPermissionTree.isKeepAlive();
        this.alwaysShow= sysPermissionTree.isAlwaysShow();
        this.createWays = sysPermissionTree.getCreateWays();
        /*update_begin author:wuxianquan date:20190908 for:赋值 */
        this.internalOrExternal = sysPermissionTree.isInternalOrExternal();
        /*update_end author:wuxianquan date:20190908 for:赋值 */
        this.status = sysPermissionTree.getStatus();
    }

    public SysPermission(boolean index) {
    	if(index) {
    		this.id = "9502685863ab87f0ad1134142788a385";
        	this.name="首页";
        	this.component="dashboard/Analysis";
        	this.componentName="dashboard-analysis";
        	this.url="/dashboard/analysis";
        	this.icon="home";
        	this.menuType=0;
        	this.sortNo=0.0;
        	this.ruleFlag=0;
        	this.delFlag=0;
        	this.alwaysShow=false;
        	this.route=true;
        	this.keepAlive=true;
        	this.leaf=true;
        	this.hidden=false;
    	}
    	
    }
}
