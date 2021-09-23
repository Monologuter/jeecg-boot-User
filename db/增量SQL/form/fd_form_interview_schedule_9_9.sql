/*
 Navicat Premium Data Transfer

 Source Server         : 161
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : rm-0jlktt43a028na6qd3o.mysql.rds.aliyuncs.com:3306
 Source Schema         : bpt_lowcode_form

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 09/09/2021 17:25:36
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for fd_form_interview_schedule
-- ----------------------------
DROP TABLE IF EXISTS `fd_form_interview_schedule`;
CREATE TABLE `fd_form_interview_schedule`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL,
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '事件名称',
  `type` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试方式  video、scene、phone',
  `style_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试方式对应的颜色  type-orange、type-blue、type-green',
  `create_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试创建者',
  `update_by` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试更新者',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '面试创建时间',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '面试更新时间',
  `start` datetime(0) NULL DEFAULT NULL COMMENT '面试开始时间',
  `end` datetime(0) NULL DEFAULT NULL COMMENT '面试结束时间',
  `interviewee` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试者',
  `interviewer` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '面试官',
  `mark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `dep_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门编码',
  `role_id` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '角色ID',
  `form_id` varchar(32) CHARACTER SET latin1 COLLATE latin1_swedish_ci NULL DEFAULT NULL COMMENT '表单ID',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = latin1 COLLATE = latin1_swedish_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
