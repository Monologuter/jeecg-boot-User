/*
Navicat MySQL Data Transfer

Source Server         : 后端-表单环境
Source Server Version : 50733
Source Host           : 39.101.68.161:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50733
File Encoding         : 65001

Date: 2021-07-5 10:27:27
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fd_form
-- ----------------------------
DROP TABLE IF EXISTS `fd_form`;
CREATE TABLE `fd_form` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `code` varchar(100) NOT NULL COMMENT '表单编码',
  `name` varchar(100) NOT NULL COMMENT '表单名',
  `json` text COMMENT '表单Json',
  `department` varchar(100) DEFAULT NULL COMMENT '部门',
  `dynamic_data_source` text COMMENT '数据源配置',
  `is_template` tinyint(4) DEFAULT '0' COMMENT '是否为模板',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(100) DEFAULT NULL COMMENT '更新者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `code` (`code`, `is_template`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
