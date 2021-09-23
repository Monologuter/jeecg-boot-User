/*
 Navicat Premium Data Transfer

 Source Server         : rm-0jlktt43a028na6qd3o
 Source Server Type    : MySQL
 Source Server Version : 80022
 Source Host           : rm-0jlktt43a028na6qd3o.mysql.rds.aliyuncs.com:3306
 Source Schema         : bpt_lowcode_form

 Target Server Type    : MySQL
 Target Server Version : 80022
 File Encoding         : 65001

 Date: 12/08/2021 17:42:32
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for generator_code
-- ----------------------------
DROP TABLE IF EXISTS `generator_code`;
CREATE TABLE `generator_code`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '唯一标识',
  `template_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '模板ID',
  `table_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '表名称 具有唯一性',
  `table_name_plus` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '创建的表名',
  `is_generator_code` int NULL DEFAULT NULL COMMENT '是否生成代码',
  `code_path` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '代码存放路径',
  `is_generator_load` int NULL DEFAULT NULL COMMENT '是否加载',
  `is_enable_url` int NULL DEFAULT 1 COMMENT '是否启用接口',
  `fields` text CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '所有字段信息',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间 ',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间 ',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人 ',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人 ',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `table_name_union`(`table_name`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
