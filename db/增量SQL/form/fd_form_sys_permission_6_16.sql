/*
Navicat MySQL Data Transfer

Source Server         : 后端-表单环境
Source Server Version : 50733
Source Host           : 39.101.68.161:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50733
File Encoding         : 65001

Date: 2021-06-16 10:25:19
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fd_form_sys_permission
-- ----------------------------
DROP TABLE IF EXISTS `fd_form_sys_permission`;
CREATE TABLE `fd_form_sys_permission` (
  `id` varchar(32) NOT NULL COMMENT '主键',
  `form_id` varchar(32) NOT NULL COMMENT '表单id',
  `sys_permission_id` varchar(32) NOT NULL COMMENT '菜单id',
  `create_by` varchar(100) DEFAULT NULL COMMENT '创建者',
  `update_by` varchar(100) DEFAULT NULL COMMENT '创建时间',
  `create_time` datetime DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `del_flag` tinyint(4) DEFAULT '0' COMMENT '逻辑删除',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fd_form_sys_permission
-- ----------------------------
INSERT INTO `fd_form_sys_permission` VALUES ('1402140233096896513', '1402140201761251330', '1402140233088507906', 'admin', null, '2021-06-08 13:47:37', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402140302978195457', '1402140291783598082', '1402140302974001154', 'admin', null, '2021-06-08 13:47:53', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402186697663717377', '1402163063154941954', '1402186697655328769', 'admin', null, '2021-06-08 16:52:15', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402436153566961665', '1402200119625261057', '1402436153558573058', 'admin', null, '2021-06-09 09:23:30', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402438399553839105', '1402438316003303425', '1402438399545450498', 'admin', null, '2021-06-09 09:32:25', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402454445691932674', '1402440954484760577', '1402454445364776962', 'admin', null, '2021-06-09 10:36:11', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402830704364187650', '1402484170236469249', '1402830703999283201', 'admin', null, '2021-06-10 11:31:18', null, '0');
INSERT INTO `fd_form_sys_permission` VALUES ('1402896182028005378', '1402856246736023554', '1402896182002839554', 'admin', null, '2021-06-10 15:51:29', null, '0');
