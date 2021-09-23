/*
 Navicat Premium Data Transfer

 Source Server         : mysql8@12345678
 Source Server Type    : MySQL
 Source Server Version : 80023
 Source Host           : localhost:3306
 Source Schema         : workflow-temp

 Target Server Type    : MySQL
 Target Server Version : 80023
 File Encoding         : 65001

 Date: 17/09/2021 13:32:31
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for act_ge_bytearray
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_bytearray`;
CREATE TABLE `act_ge_bytearray` (
                                    `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `BYTES_` longblob,
                                    `GENERATED_` tinyint DEFAULT NULL,
                                    `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `TYPE_` int DEFAULT NULL,
                                    `CREATE_TIME_` datetime DEFAULT NULL,
                                    `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `REMOVAL_TIME_` datetime DEFAULT NULL,
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_FK_BYTEARR_DEPL` (`DEPLOYMENT_ID_`) USING BTREE,
                                    KEY `ACT_IDX_BYTEARRAY_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                    KEY `ACT_IDX_BYTEARRAY_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                    KEY `ACT_IDX_BYTEARRAY_NAME` (`NAME_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_BYTEARR_DEPL` FOREIGN KEY (`DEPLOYMENT_ID_`) REFERENCES `act_re_deployment` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ge_bytearray
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ge_property
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_property`;
CREATE TABLE `act_ge_property` (
                                   `NAME_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `VALUE_` varchar(300) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `REV_` int DEFAULT NULL,
                                   PRIMARY KEY (`NAME_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of act_ge_property
-- ----------------------------
BEGIN;
INSERT INTO `act_ge_property` VALUES ('deployment.lock', '0', 1);
INSERT INTO `act_ge_property` VALUES ('history.cleanup.job.lock', '0', 1);
INSERT INTO `act_ge_property` VALUES ('historyLevel', '3', 1);
INSERT INTO `act_ge_property` VALUES ('installationId.lock', '0', 1);
INSERT INTO `act_ge_property` VALUES ('next.dbid', '1', 1);
INSERT INTO `act_ge_property` VALUES ('schema.history', 'create(fox)', 1);
INSERT INTO `act_ge_property` VALUES ('schema.version', 'fox', 1);
INSERT INTO `act_ge_property` VALUES ('startup.lock', '0', 1);
INSERT INTO `act_ge_property` VALUES ('telemetry.lock', '0', 1);
COMMIT;

-- ----------------------------
-- Table structure for act_ge_schema_log
-- ----------------------------
DROP TABLE IF EXISTS `act_ge_schema_log`;
CREATE TABLE `act_ge_schema_log` (
                                     `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                     `TIMESTAMP_` datetime DEFAULT NULL,
                                     `VERSION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- ----------------------------
-- Records of act_ge_schema_log
-- ----------------------------
BEGIN;
INSERT INTO `act_ge_schema_log` VALUES ('0', '2021-03-16 16:23:17', '7.14.0');
COMMIT;

-- ----------------------------
-- Table structure for act_hi_actinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_actinst`;
CREATE TABLE `act_hi_actinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `PARENT_ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CALL_CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `ASSIGNEE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `START_TIME_` datetime NOT NULL,
                                  `END_TIME_` datetime DEFAULT NULL,
                                  `DURATION_` bigint DEFAULT NULL,
                                  `ACT_INST_STATE_` int DEFAULT NULL,
                                  `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACTINST_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_START_END` (`START_TIME_`,`END_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_END` (`END_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_PROCINST` (`PROC_INST_ID_`,`ACT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_COMP` (`EXECUTION_ID_`,`ACT_ID_`,`END_TIME_`,`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_STATS` (`PROC_DEF_ID_`,`PROC_INST_ID_`,`ACT_ID_`,`END_TIME_`,`ACT_INST_STATE_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                  KEY `ACT_IDX_HI_AI_PDEFID_END_TIME` (`PROC_DEF_ID_`,`END_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_ACT_INST_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_actinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_attachment
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_attachment`;
CREATE TABLE `act_hi_attachment` (
                                     `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                     `REV_` int DEFAULT NULL,
                                     `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `DESCRIPTION_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `URL_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `CONTENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `CREATE_TIME_` datetime DEFAULT NULL,
                                     `REMOVAL_TIME_` datetime DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ATTACHMENT_CONTENT` (`CONTENT_ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ATTACHMENT_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ATTACHMENT_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ATTACHMENT_TASK` (`TASK_ID_`) USING BTREE,
                                     KEY `ACT_IDX_HI_ATTACHMENT_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_attachment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_batch
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_batch`;
CREATE TABLE `act_hi_batch` (
                                `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `TOTAL_JOBS_` int DEFAULT NULL,
                                `JOBS_PER_SEED_` int DEFAULT NULL,
                                `INVOCATIONS_PER_JOB_` int DEFAULT NULL,
                                `SEED_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `MONITOR_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `BATCH_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `CREATE_USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `START_TIME_` datetime NOT NULL,
                                `END_TIME_` datetime DEFAULT NULL,
                                `REMOVAL_TIME_` datetime DEFAULT NULL,
                                PRIMARY KEY (`ID_`) USING BTREE,
                                KEY `ACT_HI_BAT_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_batch
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_caseactinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_caseactinst`;
CREATE TABLE `act_hi_caseactinst` (
                                      `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                      `PARENT_ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                      `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                      `CASE_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                      `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CALL_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CALL_CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CASE_ACT_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CASE_ACT_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      `CREATE_TIME_` datetime NOT NULL,
                                      `END_TIME_` datetime DEFAULT NULL,
                                      `DURATION_` bigint DEFAULT NULL,
                                      `STATE_` int DEFAULT NULL,
                                      `REQUIRED_` tinyint(1) DEFAULT NULL,
                                      `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                      PRIMARY KEY (`ID_`) USING BTREE,
                                      KEY `ACT_IDX_HI_CAS_A_I_CREATE` (`CREATE_TIME_`) USING BTREE,
                                      KEY `ACT_IDX_HI_CAS_A_I_END` (`END_TIME_`) USING BTREE,
                                      KEY `ACT_IDX_HI_CAS_A_I_COMP` (`CASE_ACT_ID_`,`END_TIME_`,`ID_`) USING BTREE,
                                      KEY `ACT_IDX_HI_CAS_A_I_CASEINST` (`CASE_INST_ID_`,`CASE_ACT_ID_`) USING BTREE,
                                      KEY `ACT_IDX_HI_CAS_A_I_TENANT_ID` (`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_caseactinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_caseinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_caseinst`;
CREATE TABLE `act_hi_caseinst` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `CREATE_TIME_` datetime NOT NULL,
                                   `CLOSE_TIME_` datetime DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `STATE_` int DEFAULT NULL,
                                   `CREATE_USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `SUPER_CASE_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `SUPER_PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   UNIQUE KEY `CASE_INST_ID_` (`CASE_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_CAS_I_CLOSE` (`CLOSE_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_CAS_I_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_CAS_I_TENANT_ID` (`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_caseinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_comment
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_comment`;
CREATE TABLE `act_hi_comment` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TIME_` datetime NOT NULL,
                                  `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACTION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `MESSAGE_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `FULL_MSG_` longblob,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_COMMENT_TASK` (`TASK_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_COMMENT_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_COMMENT_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_COMMENT_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_comment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_dec_in
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_dec_in`;
CREATE TABLE `act_hi_dec_in` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `DEC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `CLAUSE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CLAUSE_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `VAR_TYPE_` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `DOUBLE_` double DEFAULT NULL,
                                 `LONG_` bigint DEFAULT NULL,
                                 `TEXT_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TEXT2_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CREATE_TIME_` datetime DEFAULT NULL,
                                 `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `REMOVAL_TIME_` datetime DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DEC_IN_INST` (`DEC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DEC_IN_CLAUSE` (`DEC_INST_ID_`,`CLAUSE_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DEC_IN_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DEC_IN_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_dec_in
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_dec_out
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_dec_out`;
CREATE TABLE `act_hi_dec_out` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `DEC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `CLAUSE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CLAUSE_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `RULE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `RULE_ORDER_` int DEFAULT NULL,
                                  `VAR_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `VAR_TYPE_` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DOUBLE_` double DEFAULT NULL,
                                  `LONG_` bigint DEFAULT NULL,
                                  `TEXT_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TEXT2_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CREATE_TIME_` datetime DEFAULT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_OUT_INST` (`DEC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_OUT_RULE` (`RULE_ORDER_`,`CLAUSE_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_OUT_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_OUT_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_dec_out
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_decinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_decinst`;
CREATE TABLE `act_hi_decinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `DEC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `DEC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `DEC_DEF_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `EVAL_TIME_` datetime NOT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  `COLLECT_VALUE_` double DEFAULT NULL,
                                  `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ROOT_DEC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DEC_REQ_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DEC_REQ_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_ID` (`DEC_DEF_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_KEY` (`DEC_DEF_KEY_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_PI` (`PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_CI` (`CASE_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_ACT` (`ACT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_ACT_INST` (`ACT_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_TIME` (`EVAL_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_ROOT_ID` (`ROOT_DEC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_REQ_ID` (`DEC_REQ_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_REQ_KEY` (`DEC_REQ_KEY_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_DEC_INST_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_decinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_detail
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_detail`;
CREATE TABLE `act_hi_detail` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `VAR_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `VAR_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `REV_` int DEFAULT NULL,
                                 `TIME_` datetime NOT NULL,
                                 `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `DOUBLE_` double DEFAULT NULL,
                                 `LONG_` bigint DEFAULT NULL,
                                 `TEXT_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TEXT2_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                                 `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `OPERATION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `REMOVAL_TIME_` datetime DEFAULT NULL,
                                 `INITIAL_` tinyint(1) DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_PROC_INST` (`PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_ACT_INST` (`ACT_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_CASE_INST` (`CASE_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_CASE_EXEC` (`CASE_EXECUTION_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TIME` (`TIME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_NAME` (`NAME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TASK_ID` (`TASK_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_BYTEAR` (`BYTEARRAY_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_TASK_BYTEAR` (`BYTEARRAY_ID_`,`TASK_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_DETAIL_VAR_INST_ID` (`VAR_INST_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_detail
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_ext_task_log
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_ext_task_log`;
CREATE TABLE `act_hi_ext_task_log` (
                                       `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `TIMESTAMP_` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       `EXT_TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `RETRIES_` int DEFAULT NULL,
                                       `TOPIC_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `WORKER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PRIORITY_` bigint NOT NULL DEFAULT '0',
                                       `ERROR_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ERROR_DETAILS_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `STATE_` int DEFAULT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `REMOVAL_TIME_` datetime DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_EXTTASKLOG_ERRORDET` (`ERROR_DETAILS_ID_`) USING BTREE,
                                       KEY `ACT_HI_EXT_TASK_LOG_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_ext_task_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_identitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_identitylink`;
CREATE TABLE `act_hi_identitylink` (
                                       `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `TIMESTAMP_` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                       `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `OPERATION_TYPE_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ASSIGNER_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `REMOVAL_TIME_` datetime DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_USER` (`USER_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_GROUP` (`GROUP_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LINK_TASK` (`TASK_ID_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LINK_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                       KEY `ACT_IDX_HI_IDENT_LNK_TIMESTAMP` (`TIMESTAMP_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_identitylink
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_incident
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_incident`;
CREATE TABLE `act_hi_incident` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CREATE_TIME_` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   `END_TIME_` timestamp NULL DEFAULT NULL,
                                   `INCIDENT_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `INCIDENT_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `ACTIVITY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `FAILED_ACTIVITY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CAUSE_INCIDENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ROOT_CAUSE_INCIDENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `HISTORY_CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `INCIDENT_STATE_` int DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `REMOVAL_TIME_` datetime DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_CREATE_TIME` (`CREATE_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_INCIDENT_END_TIME` (`END_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_incident
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_job_log
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_job_log`;
CREATE TABLE `act_hi_job_log` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `TIMESTAMP_` datetime NOT NULL,
                                  `JOB_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `JOB_DUEDATE_` datetime DEFAULT NULL,
                                  `JOB_RETRIES_` int DEFAULT NULL,
                                  `JOB_PRIORITY_` bigint NOT NULL DEFAULT '0',
                                  `JOB_EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `JOB_EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `JOB_STATE_` int DEFAULT NULL,
                                  `JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `JOB_DEF_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `JOB_DEF_CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `FAILED_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROCESS_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROCESS_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `HOSTNAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_PROCINST` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_PROCDEF` (`PROCESS_DEF_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_JOB_DEF_ID` (`JOB_DEF_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_PROC_DEF_KEY` (`PROCESS_DEF_KEY_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_EX_STACK` (`JOB_EXCEPTION_STACK_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_JOB_LOG_JOB_CONF` (`JOB_DEF_CONFIGURATION_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_job_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_op_log
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_op_log`;
CREATE TABLE `act_hi_op_log` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `JOB_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `BATCH_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TIMESTAMP_` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 `OPERATION_TYPE_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `OPERATION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ENTITY_TYPE_` varchar(30) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROPERTY_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ORG_VALUE_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `NEW_VALUE_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `REMOVAL_TIME_` datetime DEFAULT NULL,
                                 `CATEGORY_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `EXTERNAL_TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ANNOTATION_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_TASK` (`TASK_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_TIMESTAMP` (`TIMESTAMP_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_USER_ID` (`USER_ID_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_OP_TYPE` (`OPERATION_TYPE_`) USING BTREE,
                                 KEY `ACT_IDX_HI_OP_LOG_ENTITY_TYPE` (`ENTITY_TYPE_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_op_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_procinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_procinst`;
CREATE TABLE `act_hi_procinst` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `START_TIME_` datetime NOT NULL,
                                   `END_TIME_` datetime DEFAULT NULL,
                                   `REMOVAL_TIME_` datetime DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `START_USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `START_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `END_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `SUPER_PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `SUPER_CASE_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `STATE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   UNIQUE KEY `PROC_INST_ID_` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_END` (`END_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_I_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_PROC_TIME` (`START_TIME_`,`END_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PI_PDEFID_END_TIME` (`PROC_DEF_ID_`,`END_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_PRO_INST_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_procinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_taskinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_taskinst`;
CREATE TABLE `act_hi_taskinst` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `DESCRIPTION_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `OWNER_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ASSIGNEE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `START_TIME_` datetime NOT NULL,
                                   `END_TIME_` datetime DEFAULT NULL,
                                   `DURATION_` bigint DEFAULT NULL,
                                   `DELETE_REASON_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PRIORITY_` int DEFAULT NULL,
                                   `DUE_DATE_` datetime DEFAULT NULL,
                                   `FOLLOW_UP_DATE_` datetime DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `REMOVAL_TIME_` datetime DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASKINST_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASKINST_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASKINSTID_PROCINST` (`ID_`,`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_START` (`START_TIME_`) USING BTREE,
                                   KEY `ACT_IDX_HI_TASK_INST_END` (`END_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_taskinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_hi_varinst
-- ----------------------------
DROP TABLE IF EXISTS `act_hi_varinst`;
CREATE TABLE `act_hi_varinst` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `VAR_TYPE_` varchar(100) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `CREATE_TIME_` datetime DEFAULT NULL,
                                  `REV_` int DEFAULT NULL,
                                  `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DOUBLE_` double DEFAULT NULL,
                                  `LONG_` bigint DEFAULT NULL,
                                  `TEXT_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TEXT2_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `STATE_` varchar(20) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `REMOVAL_TIME_` datetime DEFAULT NULL,
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VARINST_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_PROC_INST` (`PROC_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_PROCVAR_NAME_TYPE` (`NAME_`,`VAR_TYPE_`) USING BTREE,
                                  KEY `ACT_IDX_HI_CASEVAR_CASE_INST` (`CASE_INST_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VAR_INST_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VAR_INST_PROC_DEF_KEY` (`PROC_DEF_KEY_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VARINST_BYTEAR` (`BYTEARRAY_ID_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VARINST_RM_TIME` (`REMOVAL_TIME_`) USING BTREE,
                                  KEY `ACT_IDX_HI_VAR_PI_NAME_TYPE` (`PROC_INST_ID_`,`NAME_`,`VAR_TYPE_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_hi_varinst
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_id_group
-- ----------------------------
DROP TABLE IF EXISTS `act_id_group`;
CREATE TABLE `act_id_group` (
                                `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `REV_` int DEFAULT NULL,
                                `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_group
-- ----------------------------
BEGIN;
INSERT INTO `act_id_group` VALUES ('1432985793290375170', 1, 'workflow', NULL);
INSERT INTO `act_id_group` VALUES ('1432985860772532225', 1, 'from', NULL);
INSERT INTO `act_id_group` VALUES ('1432986750053060609', 1, 'graph', NULL);
INSERT INTO `act_id_group` VALUES ('1432989677178130433', 1, 'common', NULL);
INSERT INTO `act_id_group` VALUES ('f6817f48af4fb3af11b9e8bf182f618b', 1, '管理员', NULL);
COMMIT;

-- ----------------------------
-- Table structure for act_id_info
-- ----------------------------
DROP TABLE IF EXISTS `act_id_info`;
CREATE TABLE `act_id_info` (
                               `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `USER_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `TYPE_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `VALUE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PASSWORD_` longblob,
                               `PARENT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_info
-- ----------------------------
BEGIN;
INSERT INTO `act_id_info` VALUES ('9c0244f8-0b01-11ec-bc6b-80fa5b8a737a', 1, '1432989134980452354', 'userinfo', 'avatar', NULL, NULL, NULL);
INSERT INTO `act_id_info` VALUES ('b58b97aa-0b01-11ec-bc6b-80fa5b8a737a', 1, '1432989314979008514', 'userinfo', 'avatar', NULL, NULL, NULL);
INSERT INTO `act_id_info` VALUES ('cc552e1c-0b01-11ec-bc6b-80fa5b8a737a', 1, '1432989475314667522', 'userinfo', 'avatar', NULL, NULL, NULL);
INSERT INTO `act_id_info` VALUES ('e06a04ce-0b01-11ec-bc6b-80fa5b8a737a', 1, '1432989616675295233', 'userinfo', 'avatar', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for act_id_membership
-- ----------------------------
DROP TABLE IF EXISTS `act_id_membership`;
CREATE TABLE `act_id_membership` (
                                     `USER_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                     `GROUP_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                     PRIMARY KEY (`USER_ID_`,`GROUP_ID_`) USING BTREE,
                                     KEY `ACT_FK_MEMB_GROUP` (`GROUP_ID_`) USING BTREE,
                                     CONSTRAINT `ACT_FK_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                     CONSTRAINT `ACT_FK_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_membership
-- ----------------------------
BEGIN;
INSERT INTO `act_id_membership` VALUES ('1432989616675295233', '1432989677178130433');
INSERT INTO `act_id_membership` VALUES ('e9ca23d68d884d4ebb19d07889727dae', 'f6817f48af4fb3af11b9e8bf182f618b');
COMMIT;

-- ----------------------------
-- Table structure for act_id_tenant
-- ----------------------------
DROP TABLE IF EXISTS `act_id_tenant`;
CREATE TABLE `act_id_tenant` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `REV_` int DEFAULT NULL,
                                 `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_tenant
-- ----------------------------
BEGIN;
INSERT INTO `act_id_tenant` VALUES ('A', NULL, 'A系统');
COMMIT;

-- ----------------------------
-- Table structure for act_id_tenant_member
-- ----------------------------
DROP TABLE IF EXISTS `act_id_tenant_member`;
CREATE TABLE `act_id_tenant_member` (
                                        `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                        `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                        `USER_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        `GROUP_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        PRIMARY KEY (`ID_`) USING BTREE,
                                        UNIQUE KEY `ACT_UNIQ_TENANT_MEMB_USER` (`TENANT_ID_`,`USER_ID_`) USING BTREE,
                                        UNIQUE KEY `ACT_UNIQ_TENANT_MEMB_GROUP` (`TENANT_ID_`,`GROUP_ID_`) USING BTREE,
                                        KEY `ACT_FK_TENANT_MEMB_USER` (`USER_ID_`) USING BTREE,
                                        KEY `ACT_FK_TENANT_MEMB_GROUP` (`GROUP_ID_`) USING BTREE,
                                        CONSTRAINT `ACT_FK_TENANT_MEMB` FOREIGN KEY (`TENANT_ID_`) REFERENCES `act_id_tenant` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_TENANT_MEMB_GROUP` FOREIGN KEY (`GROUP_ID_`) REFERENCES `act_id_group` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                        CONSTRAINT `ACT_FK_TENANT_MEMB_USER` FOREIGN KEY (`USER_ID_`) REFERENCES `act_id_user` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_tenant_member
-- ----------------------------
BEGIN;
INSERT INTO `act_id_tenant_member` VALUES ('9bfdff37-0b01-11ec-bc6b-80fa5b8a737a', 'A', '1432989134980452354', NULL);
INSERT INTO `act_id_tenant_member` VALUES ('b58a1109-0b01-11ec-bc6b-80fa5b8a737a', 'A', '1432989314979008514', NULL);
INSERT INTO `act_id_tenant_member` VALUES ('cc53ce8b-0b01-11ec-bc6b-80fa5b8a737a', 'A', '1432989475314667522', NULL);
INSERT INTO `act_id_tenant_member` VALUES ('e069417d-0b01-11ec-bc6b-80fa5b8a737a', 'A', '1432989616675295233', NULL);
INSERT INTO `act_id_tenant_member` VALUES ('wdadwa', 'A', 'e9ca23d68d884d4ebb19d07889727dae', NULL);
INSERT INTO `act_id_tenant_member` VALUES ('wdwd', 'A', NULL, 'f6817f48af4fb3af11b9e8bf182f618b');
COMMIT;

-- ----------------------------
-- Table structure for act_id_user
-- ----------------------------
DROP TABLE IF EXISTS `act_id_user`;
CREATE TABLE `act_id_user` (
                               `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `FIRST_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `LAST_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `EMAIL_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PWD_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `SALT_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `LOCK_EXP_TIME_` datetime DEFAULT NULL,
                               `ATTEMPTS_` int DEFAULT NULL,
                               `PICTURE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_id_user
-- ----------------------------
BEGIN;
INSERT INTO `act_id_user` VALUES ('1432989134980452354', 1, 'graph', 'graph', NULL, '{SHA-512}5048Mg8ZGqQELbdn+d0rj3awMLCrTCmAS2oXNw8MEYic0+zMz2X9AyyJyhHFS00oX6aw3qIbrN7cvWJ4Z+TLFg==', 'HSjztIbj0n427Dx13K5m4w==', NULL, NULL, NULL);
INSERT INTO `act_id_user` VALUES ('1432989314979008514', 1, 'workflow', 'workflow', NULL, '{SHA-512}I7hNcbTaaD/08/vRGcmPoyMJNhHYpZOea8BGchc7GDi/ZCVyj5PGPW5YM9j0w5vfppmTwDe38G5/V2Ra/am2pQ==', 'hW5nD3L6clBb45VVgDtojw==', NULL, NULL, NULL);
INSERT INTO `act_id_user` VALUES ('1432989475314667522', 1, 'from', 'from', NULL, '{SHA-512}7VBlSz6ODbOnZugIRkPAYa0ZtIP4dxFEbioI6TobmJ4zgpLqTEE9p1650Dpvke0T2NR9VIVaiNt8id0GlRvLow==', 'MztFUMO+ntMvh9Y8TYzLCQ==', NULL, NULL, NULL);
INSERT INTO `act_id_user` VALUES ('1432989616675295233', 1, 'common', 'common', NULL, '{SHA-512}AzFpahd4v/d5CrMXRTCkcT1XeJ+z7uLN5o1zAwo127rvuLVaScWpHlhRzdolxuE41Yl3Tg2FhSHe101vwVduXQ==', '91MNm+Nyfc6jMSyY+Ve//Q==', NULL, NULL, NULL);
INSERT INTO `act_id_user` VALUES ('e9ca23d68d884d4ebb19d07889727dae', 1, 'admin', 'admin', 'admin@qq.com', '{SHA-512}Ew18VTmTOojALas2HyCjdWQ/lWZ1WaxRSzJ4dIOYHbWzF07SvAvxOisvGUUW7XV7rBCYbhkU9MNnyuEyaD1W6g==', 'EWXbP9h7Ro2aBj/TclxYvg==', NULL, NULL, NULL);
COMMIT;

-- ----------------------------
-- Table structure for act_proc_classification
-- ----------------------------
DROP TABLE IF EXISTS `act_proc_classification`;
CREATE TABLE `act_proc_classification` (
                                           `PROC_CLASS_` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程分类',
                                           PRIMARY KEY (`PROC_CLASS_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_proc_classification
-- ----------------------------
BEGIN;
INSERT INTO `act_proc_classification` VALUES ('人事管理');
INSERT INTO `act_proc_classification` VALUES ('关键业务');
INSERT INTO `act_proc_classification` VALUES ('客户服务');
INSERT INTO `act_proc_classification` VALUES ('特殊服务');
INSERT INTO `act_proc_classification` VALUES ('行政管理');
INSERT INTO `act_proc_classification` VALUES ('财务相关');
COMMIT;

-- ----------------------------
-- Table structure for act_proc_expand
-- ----------------------------
DROP TABLE IF EXISTS `act_proc_expand`;
CREATE TABLE `act_proc_expand` (
                                   `PROC_KEY_` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义KEY',
                                   `PROC_DEF_GROUP_` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程定义分组',
                                   `ICON_COLOUR_` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'icon颜色',
                                   `ICON_STYLE_` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'icon图标',
                                   `PROC_DESC_` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流程定义描述',
                                   `PROC_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '流程定义名称',
                                   `PROC_FORM_KEY_` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci DEFAULT NULL COMMENT '绑定外置表单',
                                   PRIMARY KEY (`PROC_KEY_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_proc_expand
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_proc_icon_colour
-- ----------------------------
DROP TABLE IF EXISTS `act_proc_icon_colour`;
CREATE TABLE `act_proc_icon_colour` (
                                        `ICON_COLOUR_` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '颜色',
                                        PRIMARY KEY (`ICON_COLOUR_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_proc_icon_colour
-- ----------------------------
BEGIN;
INSERT INTO `act_proc_icon_colour` VALUES ('#00B4A4');
INSERT INTO `act_proc_icon_colour` VALUES ('#4A6DB5');
INSERT INTO `act_proc_icon_colour` VALUES ('#DDB47F');
INSERT INTO `act_proc_icon_colour` VALUES ('#FF5A22');
INSERT INTO `act_proc_icon_colour` VALUES ('#FF9E00');
COMMIT;

-- ----------------------------
-- Table structure for act_proc_icon_style
-- ----------------------------
DROP TABLE IF EXISTS `act_proc_icon_style`;
CREATE TABLE `act_proc_icon_style` (
                                       `ICON_STYLE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT 'icon样式',
                                       PRIMARY KEY (`ICON_STYLE_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_proc_icon_style
-- ----------------------------
BEGIN;
INSERT INTO `act_proc_icon_style` VALUES ('icon-cost');
INSERT INTO `act_proc_icon_style` VALUES ('icon-form-plane');
INSERT INTO `act_proc_icon_style` VALUES ('icon-meeting');
INSERT INTO `act_proc_icon_style` VALUES ('icon-offer');
INSERT INTO `act_proc_icon_style` VALUES ('icon-out');
INSERT INTO `act_proc_icon_style` VALUES ('icon-overtime');
INSERT INTO `act_proc_icon_style` VALUES ('icon-quit');
INSERT INTO `act_proc_icon_style` VALUES ('icon-recruit');
INSERT INTO `act_proc_icon_style` VALUES ('icon-signet');
INSERT INTO `act_proc_icon_style` VALUES ('icon-time-plane');
INSERT INTO `act_proc_icon_style` VALUES ('icon-travel');
COMMIT;

-- ----------------------------
-- Table structure for act_re_case_def
-- ----------------------------
DROP TABLE IF EXISTS `act_re_case_def`;
CREATE TABLE `act_re_case_def` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `REV_` int DEFAULT NULL,
                                   `CATEGORY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `VERSION_` int NOT NULL,
                                   `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `HISTORY_TTL_` int DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_re_case_def
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_re_decision_def
-- ----------------------------
DROP TABLE IF EXISTS `act_re_decision_def`;
CREATE TABLE `act_re_decision_def` (
                                       `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `CATEGORY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `VERSION_` int NOT NULL,
                                       `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `DEC_REQ_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `DEC_REQ_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `HISTORY_TTL_` int DEFAULT NULL,
                                       `VERSION_TAG_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_DEC_DEF_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                       KEY `ACT_IDX_DEC_DEF_REQ_ID` (`DEC_REQ_ID_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_DEC_REQ` FOREIGN KEY (`DEC_REQ_ID_`) REFERENCES `act_re_decision_req_def` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_re_decision_def
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_re_decision_req_def
-- ----------------------------
DROP TABLE IF EXISTS `act_re_decision_req_def`;
CREATE TABLE `act_re_decision_req_def` (
                                           `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                           `REV_` int DEFAULT NULL,
                                           `CATEGORY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                           `VERSION_` int NOT NULL,
                                           `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           PRIMARY KEY (`ID_`) USING BTREE,
                                           KEY `ACT_IDX_DEC_REQ_DEF_TENANT_ID` (`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_re_decision_req_def
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_re_deployment
-- ----------------------------
DROP TABLE IF EXISTS `act_re_deployment`;
CREATE TABLE `act_re_deployment` (
                                     `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                     `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `DEPLOY_TIME_` datetime DEFAULT NULL,
                                     `SOURCE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                     PRIMARY KEY (`ID_`) USING BTREE,
                                     KEY `ACT_IDX_DEPLOYMENT_NAME` (`NAME_`) USING BTREE,
                                     KEY `ACT_IDX_DEPLOYMENT_TENANT_ID` (`TENANT_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_re_deployment
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_re_procdef
-- ----------------------------
DROP TABLE IF EXISTS `act_re_procdef`;
CREATE TABLE `act_re_procdef` (
                                  `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `REV_` int DEFAULT NULL,
                                  `CATEGORY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                  `VERSION_` int NOT NULL,
                                  `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `DGRM_RESOURCE_NAME_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `HAS_START_FORM_KEY_` tinyint DEFAULT NULL,
                                  `SUSPENSION_STATE_` int DEFAULT NULL,
                                  `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `VERSION_TAG_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                  `HISTORY_TTL_` int DEFAULT NULL,
                                  `STARTABLE_` tinyint(1) NOT NULL DEFAULT '1',
                                  PRIMARY KEY (`ID_`) USING BTREE,
                                  KEY `ACT_IDX_PROCDEF_DEPLOYMENT_ID` (`DEPLOYMENT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_PROCDEF_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                  KEY `ACT_IDX_PROCDEF_VER_TAG` (`VERSION_TAG_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_re_procdef
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_authorization
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_authorization`;
CREATE TABLE `act_ru_authorization` (
                                        `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                        `REV_` int NOT NULL,
                                        `TYPE_` int NOT NULL,
                                        `GROUP_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        `RESOURCE_TYPE_` int NOT NULL,
                                        `RESOURCE_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        `PERMS_` int DEFAULT NULL,
                                        `REMOVAL_TIME_` datetime DEFAULT NULL,
                                        `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                        PRIMARY KEY (`ID_`) USING BTREE,
                                        UNIQUE KEY `ACT_UNIQ_AUTH_USER` (`USER_ID_`,`TYPE_`,`RESOURCE_TYPE_`,`RESOURCE_ID_`) USING BTREE,
                                        UNIQUE KEY `ACT_UNIQ_AUTH_GROUP` (`GROUP_ID_`,`TYPE_`,`RESOURCE_TYPE_`,`RESOURCE_ID_`) USING BTREE,
                                        KEY `ACT_IDX_AUTH_GROUP_ID` (`GROUP_ID_`) USING BTREE,
                                        KEY `ACT_IDX_AUTH_RESOURCE_ID` (`RESOURCE_ID_`) USING BTREE,
                                        KEY `ACT_IDX_AUTH_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                        KEY `ACT_IDX_AUTH_RM_TIME` (`REMOVAL_TIME_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_authorization
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_batch
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_batch`;
CREATE TABLE `act_ru_batch` (
                                `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                `REV_` int NOT NULL,
                                `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `TOTAL_JOBS_` int DEFAULT NULL,
                                `JOBS_CREATED_` int DEFAULT NULL,
                                `JOBS_PER_SEED_` int DEFAULT NULL,
                                `INVOCATIONS_PER_JOB_` int DEFAULT NULL,
                                `SEED_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `BATCH_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `MONITOR_JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `SUSPENSION_STATE_` int DEFAULT NULL,
                                `CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                `CREATE_USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                PRIMARY KEY (`ID_`) USING BTREE,
                                KEY `ACT_IDX_BATCH_SEED_JOB_DEF` (`SEED_JOB_DEF_ID_`) USING BTREE,
                                KEY `ACT_IDX_BATCH_MONITOR_JOB_DEF` (`MONITOR_JOB_DEF_ID_`) USING BTREE,
                                KEY `ACT_IDX_BATCH_JOB_DEF` (`BATCH_JOB_DEF_ID_`) USING BTREE,
                                CONSTRAINT `ACT_FK_BATCH_JOB_DEF` FOREIGN KEY (`BATCH_JOB_DEF_ID_`) REFERENCES `act_ru_jobdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `ACT_FK_BATCH_MONITOR_JOB_DEF` FOREIGN KEY (`MONITOR_JOB_DEF_ID_`) REFERENCES `act_ru_jobdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                CONSTRAINT `ACT_FK_BATCH_SEED_JOB_DEF` FOREIGN KEY (`SEED_JOB_DEF_ID_`) REFERENCES `act_ru_jobdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_batch
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_case_execution
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_case_execution`;
CREATE TABLE `act_ru_case_execution` (
                                         `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                         `REV_` int DEFAULT NULL,
                                         `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `SUPER_CASE_EXEC_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `SUPER_EXEC_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `PARENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         `PREV_STATE_` int DEFAULT NULL,
                                         `CURRENT_STATE_` int DEFAULT NULL,
                                         `REQUIRED_` tinyint(1) DEFAULT NULL,
                                         `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                         PRIMARY KEY (`ID_`) USING BTREE,
                                         KEY `ACT_IDX_CASE_EXEC_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                         KEY `ACT_IDX_CASE_EXE_CASE_INST` (`CASE_INST_ID_`) USING BTREE,
                                         KEY `ACT_FK_CASE_EXE_PARENT` (`PARENT_ID_`) USING BTREE,
                                         KEY `ACT_FK_CASE_EXE_CASE_DEF` (`CASE_DEF_ID_`) USING BTREE,
                                         CONSTRAINT `ACT_FK_CASE_EXE_CASE_DEF` FOREIGN KEY (`CASE_DEF_ID_`) REFERENCES `act_re_case_def` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                         CONSTRAINT `ACT_FK_CASE_EXE_CASE_INST` FOREIGN KEY (`CASE_INST_ID_`) REFERENCES `act_ru_case_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
                                         CONSTRAINT `ACT_FK_CASE_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_case_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_case_execution
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_case_sentry_part
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_case_sentry_part`;
CREATE TABLE `act_ru_case_sentry_part` (
                                           `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                           `REV_` int DEFAULT NULL,
                                           `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `CASE_EXEC_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `SENTRY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `SOURCE_CASE_EXEC_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `STANDARD_EVENT_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `SOURCE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `VARIABLE_EVENT_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `VARIABLE_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           `SATISFIED_` tinyint(1) DEFAULT NULL,
                                           `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                           PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_case_sentry_part
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_event_subscr
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_event_subscr`;
CREATE TABLE `act_ru_event_subscr` (
                                       `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `EVENT_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `EVENT_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `ACTIVITY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `CREATED_` datetime NOT NULL,
                                       `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_EVENT_SUBSCR_CONFIG_` (`CONFIGURATION_`) USING BTREE,
                                       KEY `ACT_IDX_EVENT_SUBSCR_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                       KEY `ACT_FK_EVENT_EXEC` (`EXECUTION_ID_`) USING BTREE,
                                       KEY `ACT_IDX_EVENT_SUBSCR_EVT_NAME` (`EVENT_NAME_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_EVENT_EXEC` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_event_subscr
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_execution
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_execution`;
CREATE TABLE `act_ru_execution` (
                                    `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `REV_` int DEFAULT NULL,
                                    `ROOT_PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `BUSINESS_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `PARENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `SUPER_EXEC_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `SUPER_CASE_EXEC_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `IS_ACTIVE_` tinyint DEFAULT NULL,
                                    `IS_CONCURRENT_` tinyint DEFAULT NULL,
                                    `IS_SCOPE_` tinyint DEFAULT NULL,
                                    `IS_EVENT_SCOPE_` tinyint DEFAULT NULL,
                                    `SUSPENSION_STATE_` int DEFAULT NULL,
                                    `CACHED_ENT_STATE_` int DEFAULT NULL,
                                    `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                                    `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_IDX_EXEC_ROOT_PI` (`ROOT_PROC_INST_ID_`) USING BTREE,
                                    KEY `ACT_IDX_EXEC_BUSKEY` (`BUSINESS_KEY_`) USING BTREE,
                                    KEY `ACT_IDX_EXEC_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PARENT` (`PARENT_ID_`) USING BTREE,
                                    KEY `ACT_FK_EXE_SUPER` (`SUPER_EXEC_`) USING BTREE,
                                    KEY `ACT_FK_EXE_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                                    CONSTRAINT `ACT_FK_EXE_PARENT` FOREIGN KEY (`PARENT_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_EXE_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                    CONSTRAINT `ACT_FK_EXE_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
                                    CONSTRAINT `ACT_FK_EXE_SUPER` FOREIGN KEY (`SUPER_EXEC_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_execution
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_ext_task
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_ext_task`;
CREATE TABLE `act_ru_ext_task` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `REV_` int NOT NULL,
                                   `WORKER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TOPIC_NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `RETRIES_` int DEFAULT NULL,
                                   `ERROR_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ERROR_DETAILS_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `LOCK_EXP_TIME_` datetime DEFAULT NULL,
                                   `SUSPENSION_STATE_` int DEFAULT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ACT_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PRIORITY_` bigint NOT NULL DEFAULT '0',
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_EXT_TASK_TOPIC` (`TOPIC_NAME_`) USING BTREE,
                                   KEY `ACT_IDX_EXT_TASK_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_EXT_TASK_PRIORITY` (`PRIORITY_`) USING BTREE,
                                   KEY `ACT_IDX_EXT_TASK_ERR_DETAILS` (`ERROR_DETAILS_ID_`) USING BTREE,
                                   KEY `ACT_IDX_EXT_TASK_EXEC` (`EXECUTION_ID_`) USING BTREE,
                                   CONSTRAINT `ACT_FK_EXT_TASK_ERROR_DETAILS` FOREIGN KEY (`ERROR_DETAILS_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_EXT_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_ext_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_filter
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_filter`;
CREATE TABLE `act_ru_filter` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `REV_` int NOT NULL,
                                 `RESOURCE_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `OWNER_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `QUERY_` longtext CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `PROPERTIES_` longtext CHARACTER SET utf8 COLLATE utf8_bin,
                                 PRIMARY KEY (`ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_filter
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_identitylink
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_identitylink`;
CREATE TABLE `act_ru_identitylink` (
                                       `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                       `REV_` int DEFAULT NULL,
                                       `GROUP_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `USER_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                       PRIMARY KEY (`ID_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_USER` (`USER_ID_`) USING BTREE,
                                       KEY `ACT_IDX_IDENT_LNK_GROUP` (`GROUP_ID_`) USING BTREE,
                                       KEY `ACT_IDX_ATHRZ_PROCEDEF` (`PROC_DEF_ID_`) USING BTREE,
                                       KEY `ACT_FK_TSKASS_TASK` (`TASK_ID_`) USING BTREE,
                                       CONSTRAINT `ACT_FK_ATHRZ_PROCEDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                       CONSTRAINT `ACT_FK_TSKASS_TASK` FOREIGN KEY (`TASK_ID_`) REFERENCES `act_ru_task` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_identitylink
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_incident
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_incident`;
CREATE TABLE `act_ru_incident` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `REV_` int NOT NULL,
                                   `INCIDENT_TIMESTAMP_` datetime NOT NULL,
                                   `INCIDENT_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `INCIDENT_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ACTIVITY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `FAILED_ACTIVITY_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CAUSE_INCIDENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `ROOT_CAUSE_INCIDENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_CONFIGURATION` (`CONFIGURATION_`) USING BTREE,
                                   KEY `ACT_IDX_INC_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_JOB_DEF` (`JOB_DEF_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_CAUSEINCID` (`CAUSE_INCIDENT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_EXID` (`EXECUTION_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_PROCDEFID` (`PROC_DEF_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_PROCINSTID` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_IDX_INC_ROOTCAUSEINCID` (`ROOT_CAUSE_INCIDENT_ID_`) USING BTREE,
                                   CONSTRAINT `ACT_FK_INC_CAUSE` FOREIGN KEY (`CAUSE_INCIDENT_ID_`) REFERENCES `act_ru_incident` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE,
                                   CONSTRAINT `ACT_FK_INC_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_INC_JOB_DEF` FOREIGN KEY (`JOB_DEF_ID_`) REFERENCES `act_ru_jobdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_INC_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_INC_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_INC_RCAUSE` FOREIGN KEY (`ROOT_CAUSE_INCIDENT_ID_`) REFERENCES `act_ru_incident` (`ID_`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_incident
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_job
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_job`;
CREATE TABLE `act_ru_job` (
                              `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                              `REV_` int DEFAULT NULL,
                              `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                              `LOCK_EXP_TIME_` datetime DEFAULT NULL,
                              `LOCK_OWNER_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `EXCLUSIVE_` tinyint(1) DEFAULT NULL,
                              `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `PROCESS_INSTANCE_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `PROCESS_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `PROCESS_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `RETRIES_` int DEFAULT NULL,
                              `EXCEPTION_STACK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `EXCEPTION_MSG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `FAILED_ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `DUEDATE_` datetime DEFAULT NULL,
                              `REPEAT_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `REPEAT_OFFSET_` bigint DEFAULT '0',
                              `HANDLER_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `HANDLER_CFG_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `SUSPENSION_STATE_` int NOT NULL DEFAULT '1',
                              `JOB_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `PRIORITY_` bigint NOT NULL DEFAULT '0',
                              `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                              `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                              `CREATE_TIME_` datetime DEFAULT NULL,
                              PRIMARY KEY (`ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_EXECUTION_ID` (`EXECUTION_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_HANDLER` (`HANDLER_TYPE_`(100),`HANDLER_CFG_`(155)) USING BTREE,
                              KEY `ACT_IDX_JOB_PROCINST` (`PROCESS_INSTANCE_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_JOB_DEF_ID` (`JOB_DEF_ID_`) USING BTREE,
                              KEY `ACT_FK_JOB_EXCEPTION` (`EXCEPTION_STACK_ID_`) USING BTREE,
                              KEY `ACT_IDX_JOB_HANDLER_TYPE` (`HANDLER_TYPE_`) USING BTREE,
                              CONSTRAINT `ACT_FK_JOB_EXCEPTION` FOREIGN KEY (`EXCEPTION_STACK_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_job
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_jobdef
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_jobdef`;
CREATE TABLE `act_ru_jobdef` (
                                 `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `REV_` int DEFAULT NULL,
                                 `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `PROC_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `ACT_ID_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `JOB_TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                 `JOB_CONFIGURATION_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `SUSPENSION_STATE_` int DEFAULT NULL,
                                 `JOB_PRIORITY_` bigint DEFAULT NULL,
                                 `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 `DEPLOYMENT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                 PRIMARY KEY (`ID_`) USING BTREE,
                                 KEY `ACT_IDX_JOBDEF_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                 KEY `ACT_IDX_JOBDEF_PROC_DEF_ID` (`PROC_DEF_ID_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_jobdef
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_meter_log
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_meter_log`;
CREATE TABLE `act_ru_meter_log` (
                                    `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `NAME_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                    `REPORTER_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                    `VALUE_` bigint DEFAULT NULL,
                                    `TIMESTAMP_` datetime DEFAULT NULL,
                                    `MILLISECONDS_` bigint DEFAULT '0',
                                    PRIMARY KEY (`ID_`) USING BTREE,
                                    KEY `ACT_IDX_METER_LOG_MS` (`MILLISECONDS_`) USING BTREE,
                                    KEY `ACT_IDX_METER_LOG_NAME_MS` (`NAME_`,`MILLISECONDS_`) USING BTREE,
                                    KEY `ACT_IDX_METER_LOG_REPORT` (`NAME_`,`REPORTER_`,`MILLISECONDS_`) USING BTREE,
                                    KEY `ACT_IDX_METER_LOG_TIME` (`TIMESTAMP_`) USING BTREE,
                                    KEY `ACT_IDX_METER_LOG` (`NAME_`,`TIMESTAMP_`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_meter_log
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_task
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_task`;
CREATE TABLE `act_ru_task` (
                               `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                               `REV_` int DEFAULT NULL,
                               `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `CASE_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PARENT_TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `DESCRIPTION_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `TASK_DEF_KEY_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `OWNER_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `ASSIGNEE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `DELEGATION_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               `PRIORITY_` int DEFAULT NULL,
                               `CREATE_TIME_` datetime DEFAULT NULL,
                               `DUE_DATE_` datetime DEFAULT NULL,
                               `FOLLOW_UP_DATE_` datetime DEFAULT NULL,
                               `SUSPENSION_STATE_` int DEFAULT NULL,
                               `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                               PRIMARY KEY (`ID_`) USING BTREE,
                               KEY `ACT_IDX_TASK_CREATE` (`CREATE_TIME_`) USING BTREE,
                               KEY `ACT_IDX_TASK_ASSIGNEE` (`ASSIGNEE_`) USING BTREE,
                               KEY `ACT_IDX_TASK_OWNER` (`OWNER_`) USING BTREE,
                               KEY `ACT_IDX_TASK_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                               KEY `ACT_FK_TASK_EXE` (`EXECUTION_ID_`) USING BTREE,
                               KEY `ACT_FK_TASK_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                               KEY `ACT_FK_TASK_PROCDEF` (`PROC_DEF_ID_`) USING BTREE,
                               CONSTRAINT `ACT_FK_TASK_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `ACT_FK_TASK_PROCDEF` FOREIGN KEY (`PROC_DEF_ID_`) REFERENCES `act_re_procdef` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                               CONSTRAINT `ACT_FK_TASK_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_task
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for act_ru_variable
-- ----------------------------
DROP TABLE IF EXISTS `act_ru_variable`;
CREATE TABLE `act_ru_variable` (
                                   `ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `REV_` int DEFAULT NULL,
                                   `TYPE_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `NAME_` varchar(255) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `PROC_DEF_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_EXECUTION_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `CASE_INST_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TASK_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `BATCH_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `BYTEARRAY_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `DOUBLE_` double DEFAULT NULL,
                                   `LONG_` bigint DEFAULT NULL,
                                   `TEXT_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `TEXT2_` varchar(4000) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   `VAR_SCOPE_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin NOT NULL,
                                   `SEQUENCE_COUNTER_` bigint DEFAULT NULL,
                                   `IS_CONCURRENT_LOCAL_` tinyint DEFAULT NULL,
                                   `TENANT_ID_` varchar(64) CHARACTER SET utf8 COLLATE utf8_bin DEFAULT NULL,
                                   PRIMARY KEY (`ID_`) USING BTREE,
                                   UNIQUE KEY `ACT_UNIQ_VARIABLE` (`VAR_SCOPE_`,`NAME_`) USING BTREE,
                                   KEY `ACT_IDX_VARIABLE_TASK_ID` (`TASK_ID_`) USING BTREE,
                                   KEY `ACT_IDX_VARIABLE_TENANT_ID` (`TENANT_ID_`) USING BTREE,
                                   KEY `ACT_IDX_VARIABLE_TASK_NAME_TYPE` (`TASK_ID_`,`NAME_`,`TYPE_`) USING BTREE,
                                   KEY `ACT_FK_VAR_EXE` (`EXECUTION_ID_`) USING BTREE,
                                   KEY `ACT_FK_VAR_PROCINST` (`PROC_INST_ID_`) USING BTREE,
                                   KEY `ACT_FK_VAR_BYTEARRAY` (`BYTEARRAY_ID_`) USING BTREE,
                                   KEY `ACT_IDX_BATCH_ID` (`BATCH_ID_`) USING BTREE,
                                   CONSTRAINT `ACT_FK_VAR_BATCH` FOREIGN KEY (`BATCH_ID_`) REFERENCES `act_ru_batch` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_VAR_BYTEARRAY` FOREIGN KEY (`BYTEARRAY_ID_`) REFERENCES `act_ge_bytearray` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_VAR_EXE` FOREIGN KEY (`EXECUTION_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT,
                                   CONSTRAINT `ACT_FK_VAR_PROCINST` FOREIGN KEY (`PROC_INST_ID_`) REFERENCES `act_ru_execution` (`ID_`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of act_ru_variable
-- ----------------------------
BEGIN;
COMMIT;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment` (
                           `comment_id` int NOT NULL AUTO_INCREMENT COMMENT '评论id',
                           `user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户ID',
                           `proc_ins_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '流程实例ID',
                           `reply_user_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '0' COMMENT '回复人ID',
                           `pid` int NOT NULL DEFAULT '0' COMMENT '父评论ID',
                           `comment_msg` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '评论内容',
                           `creat_time` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL DEFAULT '' COMMENT '创建时间',
                           PRIMARY KEY (`comment_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records of comment
-- ----------------------------
BEGIN;
COMMIT;

SET FOREIGN_KEY_CHECKS = 1;
