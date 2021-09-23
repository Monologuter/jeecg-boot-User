/*
 Navicat Premium Data Transfer

 Source Server         : jeecg-boot
 Source Server Type    : MySQL
 Source Server Version : 50612
 Source Host           : localhost:3306
 Source Schema         : jeecg-boot

 Target Server Type    : MySQL
 Target Server Version : 50612
 File Encoding         : 65001

 Date: 01/08/2021 21:05:35
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for generator_templates
-- ----------------------------
DROP TABLE IF EXISTS `generator_templates`;
CREATE TABLE `generator_templates`  (
  `id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '唯一标识',
  `template_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '模板名称',
  `is_use_controller` int(11) NULL DEFAULT NULL COMMENT '是否使用Controller模板',
  `is_use_entity` int(11) NULL DEFAULT NULL COMMENT '是否使用Entity模板',
  `is_use_mapper` int(11) NULL DEFAULT NULL COMMENT '是否使用Mapper模板',
  `is_use_service` int(11) NULL DEFAULT NULL COMMENT '是否使用Service模板',
  `is_use_service_impl` int(11) NULL DEFAULT NULL COMMENT '是否使用ServiceImpl模板',
  `controller_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Controller内容',
  `entity_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Entity内容',
  `mapper_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Mapper内容',
  `service_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'Service内容',
  `service_impl_content` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT 'ServiceImpl内容',
  `templates_var_json` longtext CHARACTER SET utf8 COLLATE utf8_general_ci NULL COMMENT '自定义变量',
  `is_use_templates_var` int(11) NULL DEFAULT NULL COMMENT '是否使用自定义变量',
  `is_public` int(11) NULL DEFAULT NULL COMMENT '是否公开',
  `create_time` datetime(0) NULL DEFAULT NULL COMMENT '创建时间 ',
  `update_time` datetime(0) NULL DEFAULT NULL COMMENT '更新时间 ',
  `create_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人 ',
  `update_by` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人 ',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci ROW_FORMAT = Compact;

SET FOREIGN_KEY_CHECKS = 1;
