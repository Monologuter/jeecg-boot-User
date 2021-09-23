/*
Navicat MySQL Data Transfer

Source Server         : 后端-表单环境
Source Server Version : 50733
Source Host           : 39.101.68.161:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50733
File Encoding         : 65001

Date: 2021-06-16 10:24:49
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fd_form_data
-- ----------------------------
DROP TABLE IF EXISTS `fd_form_data`;
CREATE TABLE `fd_form_data` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `form_id` varchar(32) NOT NULL COMMENT '表单id',
  `row_data` text COMMENT '表单数据',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(100) DEFAULT NULL COMMENT '创建时间',
  `create_time` datetime DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fd_form_data
-- ----------------------------
INSERT INTO `fd_form_data` VALUES ('1402863784011755521', '1402856246736023554', '{\"imgupload_1623303276667\":[{\"key\":\"1623303748339_89487\",\"url\":\"http://tcdn.form.making.link/1623303748339_89487\",\"percent\":100,\"status\":\"success\"}],\"editor_1623303372523\":\"<p>官方</p>\"}', 'admin', null, '2021-06-10 13:42:45', null, '0');
