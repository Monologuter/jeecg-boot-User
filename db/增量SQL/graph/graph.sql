USE `bpt_lowcode_graph`;

/*Table structure for table `graph_report` */

DROP TABLE IF EXISTS `graph_report`;

CREATE TABLE `graph_report` (
  `id` varchar(36) NOT NULL COMMENT '报表ID',
  `graph_report_code` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报表编码',
  `graph_report_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '报表名称',
  `graph_report_type` tinyint(1) DEFAULT '0' COMMENT '报表类型',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建日期',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新日期',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
  `is_template` tinyint(1) DEFAULT '0' COMMENT '是否生成模板',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `graph_report` */

insert  into `graph_report`(`id`,`graph_report_code`,`graph_report_name`,`graph_report_type`,`create_by`,`create_time`,`update_by`,`update_time`,`sys_org_code`,`del_flag`,`is_template`) values ('1435860290578157570','666','测试一',0,'admin','2021-09-09 14:59:05','',NULL,'A01',0,0);

/*Table structure for table `graph_report_cell` */

DROP TABLE IF EXISTS `graph_report_cell`;

CREATE TABLE `graph_report_cell` (
  `id` varchar(36) NOT NULL COMMENT '单元格id',
  `report_id` varchar(36) DEFAULT NULL COMMENT '报表id',
  `sheet_id` int DEFAULT NULL COMMENT 'sheet页id',
  `dataset_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '数据集名称',
  `coordsx` int DEFAULT NULL COMMENT '单元格横坐标',
  `coordsy` int DEFAULT NULL COMMENT '单元格纵坐标',
  `is_merge` tinyint(1) DEFAULT '0' COMMENT '是否是合并单元格 0否 1是',
  `row_span` int DEFAULT '1' COMMENT '合并行数',
  `col_span` int DEFAULT '1' COMMENT '合并列数',
  `cell_classname` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '单元格样式',
  `cell_value` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '单元格的值',
  `cell_value_type` tinyint(1) DEFAULT '1' COMMENT '单元格内容类型 1固定值 2变量',
  `cell_extend` tinyint(1) DEFAULT '1' COMMENT '单元格扩展方向 1不扩展 2纵向扩展 2横向扩展',
  `aggregate_type` varchar(30) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT 'list' COMMENT '聚合类型',
  `cell_function` tinyint(1) DEFAULT '1' COMMENT '函数类型 1求和 2求平均值 3最大值 4最小值',
  `font_color` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '#000000' COMMENT '字体颜色',
  `back_color` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '#FFFFFF' COMMENT '背景颜色',
  `font_size` varchar(6) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT '12' COMMENT '字体大小',
  `is_bold` tinyint(1) DEFAULT '0' COMMENT '是否字体加粗 0否 1是',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `graph_report_cell` */

insert  into `graph_report_cell`(`id`,`report_id`,`sheet_id`,`dataset_name`,`coordsx`,`coordsy`,`is_merge`,`row_span`,`col_span`,`cell_classname`,`cell_value`,`cell_value_type`,`cell_extend`,`aggregate_type`,`cell_function`,`font_color`,`back_color`,`font_size`,`is_bold`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`sys_org_code`) values ('1435869686695768066','',0,'',34,24,0,0,0,'','',0,0,'',0,'','','',0,'admin','2021-09-09 15:36:25','',NULL,0,'A01'),('1435876331920297985','',0,'',7,8,0,0,0,'','',0,0,'',0,'','','',0,'admin','2021-09-09 16:02:49','',NULL,0,'A01');

/*Table structure for table `graph_report_dataset` */

DROP TABLE IF EXISTS `graph_report_dataset`;

CREATE TABLE `graph_report_dataset` (
  `id` varchar(36) NOT NULL COMMENT '主键id',
  `dataset_name` varchar(40) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '数据集名称',
  `report_id` varchar(36) DEFAULT NULL COMMENT '报表id',
  `datasource_id` varchar(36) DEFAULT NULL COMMENT '数据源id',
  `report_data` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT 'sql语句',
  `report_param` text CHARACTER SET utf8 COLLATE utf8_general_ci COMMENT '参数',
  `is_pagination` tinyint DEFAULT '0' COMMENT '是否分页 0否 1是',
  `page_count` int DEFAULT NULL COMMENT '每页显示条数',
  `data_type` tinyint DEFAULT '1' COMMENT '类型 1sql 2api',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(1) DEFAULT '0' COMMENT '逻辑删除 0未删除 1已删除',
  `sys_org_code` varchar(64) DEFAULT NULL COMMENT '所属部门',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

/*Data for the table `graph_report_dataset` */

insert  into `graph_report_dataset`(`id`,`dataset_name`,`report_id`,`datasource_id`,`report_data`,`report_param`,`is_pagination`,`page_count`,`data_type`,`create_by`,`create_time`,`update_by`,`update_time`,`del_flag`,`sys_org_code`) values ('1435879162798682114','eddd','','','','',0,0,0,'admin','2021-09-09 16:14:04','',NULL,0,'A01');

/*Data for the table `sys_dict` */

INSERT INTO `sys_dict` VALUES ('1436157209655320578', '单元格内容类型', 'cell_value_type', '', 0, 'admin', '2021-09-10 10:38:56', NULL, NULL, 0);
INSERT INTO `sys_dict` VALUES ('1436158978095521793', '单元格扩展方向', 'cell_extend', '', 0, 'admin', '2021-09-10 10:45:58', NULL, NULL, 0);
INSERT INTO `sys_dict` VALUES ('1436159344946126849', '函数类型', 'cell_function', '', 0, 'admin', '2021-09-10 10:47:25', NULL, NULL, 0);

/*Data for the table `sys_dict` */

INSERT INTO `sys_dict_item` VALUES ('1436157354413334530', '1436157209655320578', '固定值', '1', '', 1, NULL, 'admin', '2021-09-10 10:39:30', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436157435409539074', '1436157209655320578', '变量', '2', '', 1, NULL, 'admin', '2021-09-10 10:39:50', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159048312365058', '1436158978095521793', '不扩展', '1', '', 1, NULL, 'admin', '2021-09-10 10:46:14', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159154604417026', '1436158978095521793', '纵向扩展', '2', '', 1, NULL, 'admin', '2021-09-10 10:46:40', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159202620809218', '1436158978095521793', '横向扩展', '3', '', 1, NULL, 'admin', '2021-09-10 10:46:51', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159427360006145', '1436159344946126849', '求和', '1', '', 1, NULL, 'admin', '2021-09-10 10:47:45', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159493911027714', '1436159344946126849', '求平均值', '2', '', 1, NULL, 'admin', '2021-09-10 10:48:01', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159548248236034', '1436159344946126849', '最大值', '3', '', 1, NULL, 'admin', '2021-09-10 10:48:14', NULL, NULL);
INSERT INTO `sys_dict_item` VALUES ('1436159601142603777', '1436159344946126849', '最小值', '4', '', 1, NULL, 'admin', '2021-09-10 10:48:26', NULL, NULL);

/*Data for the table `sys_permission` */

INSERT INTO `bpt_lowcode_graph`.`sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`, `create_ways`) VALUES ('1437607110444503041', NULL, '报表设计', '/report/design', 'layouts/RouteView', NULL, NULL, '0', NULL, '1', '1.00', '0', 'table', '1', '0', '0', '0', NULL, 'admin', '2021-09-14 10:40:19', 'admin', '2021-09-14 11:41:42', '0', '0', '1', '0', '1');
INSERT INTO `bpt_lowcode_graph`.`sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`, `create_ways`) VALUES ('1437612132779610113', '1437607110444503041', '报表设计器', '/online/graphReportForm', 'modules/online/graphReportForm/OnlGraphReportFormList', NULL, NULL, '0', NULL, '1', '1.00', '0', NULL, '1', '1', '0', '0', NULL, 'admin', '2021-09-14 11:00:17', NULL, NULL, '0', '0', '1', '0', '1');
INSERT INTO `bpt_lowcode_graph`.`sys_permission` (`id`, `parent_id`, `name`, `url`, `component`, `component_name`, `redirect`, `menu_type`, `perms`, `perms_type`, `sort_no`, `always_show`, `icon`, `is_route`, `is_leaf`, `keep_alive`, `hidden`, `description`, `create_by`, `create_time`, `update_by`, `update_time`, `del_flag`, `rule_flag`, `status`, `internal_or_external`, `create_ways`) VALUES ('1437612640323948545', '1437607110444503041', '报表设计器模板', '/online/graphReportFormTemplate', 'modules/online/graphReportFormTemplate/OnlGraphReportFormTemplateList', NULL, NULL, '0', NULL, '1', '1.00', '0', NULL, '1', '1', '0', '0', NULL, 'admin', '2021-09-14 11:02:18', NULL, NULL, '0', '0', '1', '0', '1');

/*Data for the table `sys_role_permission` */

INSERT INTO `bpt_lowcode_graph`.`sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`) VALUES ('1432942793902264135', 'f6817f48af4fb3af11b9e8bf182f618b', '1437607110444503041', NULL, '2021-09-13 16:17:27', '49.93.131.198');
INSERT INTO `bpt_lowcode_graph`.`sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`) VALUES ('1432942793902264246', 'f6817f48af4fb3af11b9e8bf182f618b', '1437612132779610113', NULL, '2021-09-13 16:17:27', '49.93.131.198');
INSERT INTO `bpt_lowcode_graph`.`sys_role_permission` (`id`, `role_id`, `permission_id`, `data_rule_ids`, `operate_date`, `operate_ip`) VALUES ('1432942793902264789', 'f6817f48af4fb3af11b9e8bf182f618b', '1437612640323948545', NULL, '2021-09-13 16:17:27', '49.93.131.198');
