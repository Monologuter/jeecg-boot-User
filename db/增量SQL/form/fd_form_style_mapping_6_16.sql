/*
Navicat MySQL Data Transfer

Source Server         : 后端-表单环境
Source Server Version : 50733
Source Host           : 39.101.68.161:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50733
File Encoding         : 65001

Date: 2021-06-16 10:25:10
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fd_form_style_mapping
-- ----------------------------
DROP TABLE IF EXISTS `fd_form_style_mapping`;
CREATE TABLE `fd_form_style_mapping` (
  `id` varchar(32) NOT NULL COMMENT '表单样式映射id',
  `form_id` varchar(32) NOT NULL COMMENT '表单id',
  `style_id` varchar(32) NOT NULL COMMENT '样式id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records of fd_form_style_mapping
-- ----------------------------
