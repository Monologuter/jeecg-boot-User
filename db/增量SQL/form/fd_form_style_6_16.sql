/*
Navicat MySQL Data Transfer

Source Server         : 后端-表单环境
Source Server Version : 50733
Source Host           : 39.101.68.161:3306
Source Database       : jeecg-boot

Target Server Type    : MYSQL
Target Server Version : 50733
File Encoding         : 65001

Date: 2021-06-16 10:24:59
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for fd_form_style
-- ----------------------------
DROP TABLE IF EXISTS `fd_form_style`;
CREATE TABLE `fd_form_style` (
  `style_id` varchar(32) CHARACTER SET utf8 NOT NULL COMMENT '全局样式id',
  `style_name` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '全局样式名字',
  `style_content` varchar(255) CHARACTER SET utf8 DEFAULT NULL COMMENT '全局样式内容',
  `style_code` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '全局样式编码',
  `style_type` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '全局样式类型',
  `style_create_by` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '创建者',
  `style_create_time` datetime(6) DEFAULT NULL COMMENT '创建时间',
  `style_update_by` varchar(100) CHARACTER SET utf8 DEFAULT NULL COMMENT '更新者',
  `style_update_time` datetime(6) DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`style_id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of fd_form_style
-- ----------------------------
INSERT INTO `fd_form_style` VALUES ('1399237834254462977', 'hello', '.color{\n  color: red;\n}', 'algum', null, 'admin', '2021-05-31 13:34:30.926000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834254462978', 'hello', '.color{\n  color: red;\n}', 'alias', null, 'admin', '2021-05-31 13:34:30.926000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834296406018', 'hello', '.color{\n  color: red;\n}', 'alien', null, 'admin', '2021-05-31 13:34:30.936000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834296406019', 'hello', '.color{\n  color: red;\n}', 'alibi', null, 'admin', '2021-05-31 13:34:30.936000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834296406020', 'hello', '.color{\n  color: red;\n}', 'algums', null, 'admin', '2021-05-31 13:34:30.936000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834313183234', 'hello', '.color{\n  color: red;\n}', 'alible', null, 'admin', '2021-05-31 13:34:30.940000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834317377538', 'hello', '.color{\n  color: red;\n}', 'algors', null, 'admin', '2021-05-31 13:34:30.941000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834321571841', 'hello', '.color{\n  color: red;\n}', 'alidad', null, 'admin', '2021-05-31 13:34:30.942000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834346737665', 'hello', '.color{\n  color: red;\n}', 'alibis', null, 'admin', '2021-05-31 13:34:30.947000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834665504770', 'hello', '.color{\n  color: red;\n}', 'alines', null, 'admin', '2021-05-31 13:34:31.024000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834724225025', 'hello', '.color{\n  color: red;\n}', 'aligns', null, 'admin', '2021-05-31 13:34:31.038000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834753585153', 'hello', '.color{\n  color: red;\n}', 'alif', null, 'admin', '2021-05-31 13:34:31.045000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834757779458', 'hello', '.color{\n  color: red;\n}', 'aline', null, 'admin', '2021-05-31 13:34:31.046000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834761973761', 'hello', '.color{\n  color: red;\n}', 'align', null, 'admin', '2021-05-31 13:34:31.047000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834774556674', 'hello', '.color{\n  color: red;\n}', 'alined', null, 'admin', '2021-05-31 13:34:31.050000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834787139585', 'hello', '.color{\n  color: red;\n}', 'alight', null, 'admin', '2021-05-31 13:34:31.052000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834787139586', 'hello', '.color{\n  color: red;\n}', 'alike', null, 'admin', '2021-05-31 13:34:31.052000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834791333890', 'hello', '.color{\n  color: red;\n}', 'aliner', null, 'admin', '2021-05-31 13:34:31.053000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237834791333891', 'hello', '.color{\n  color: red;\n}', 'alifs', null, 'admin', '2021-05-31 13:34:31.054000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835143655425', 'hello', '.color{\n  color: red;\n}', 'aliped', null, 'admin', '2021-05-31 13:34:31.138000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835147849730', 'hello', '.color{\n  color: red;\n}', 'aliya', null, 'admin', '2021-05-31 13:34:31.139000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835152044033', 'hello', '.color{\n  color: red;\n}', 'alist', null, 'admin', '2021-05-31 13:34:31.139000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835235930114', 'hello', '.color{\n  color: red;\n}', 'alive', null, 'admin', '2021-05-31 13:34:31.160000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835235930115', 'hello', '.color{\n  color: red;\n}', 'alit', null, 'admin', '2021-05-31 13:34:31.160000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835248513025', 'hello', '.color{\n  color: red;\n}', 'aliyas', null, 'admin', '2021-05-31 13:34:31.163000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835252707330', 'hello', '.color{\n  color: red;\n}', 'aliyah', null, 'admin', '2021-05-31 13:34:31.163000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835265290241', 'hello', '.color{\n  color: red;\n}', 'aliyot', null, 'admin', '2021-05-31 13:34:31.166000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835265290242', 'hello', '.color{\n  color: red;\n}', 'aliyos', null, 'admin', '2021-05-31 13:34:31.167000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835269484545', 'hello', '.color{\n  color: red;\n}', 'alkali', null, 'admin', '2021-05-31 13:34:31.167000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835504365569', 'hello', '.color{\n  color: red;\n}', 'alkane', null, 'admin', '2021-05-31 13:34:31.224000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835550502914', 'hello', '.color{\n  color: red;\n}', 'alkene', null, 'admin', '2021-05-31 13:34:31.234000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835579863041', 'hello', '.color{\n  color: red;\n}', 'alkies', null, 'admin', '2021-05-31 13:34:31.242000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835617611777', 'hello', '.color{\n  color: red;\n}', 'alkyls', null, 'admin', '2021-05-31 13:34:31.251000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835626000386', 'hello', '.color{\n  color: red;\n}', 'alky', null, 'admin', '2021-05-31 13:34:31.253000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835630194689', 'hello', '.color{\n  color: red;\n}', 'alkyl', null, 'admin', '2021-05-31 13:34:31.253000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835638583298', 'hello', '.color{\n  color: red;\n}', 'alkyd', null, 'admin', '2021-05-31 13:34:31.256000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835638583299', 'hello', '.color{\n  color: red;\n}', 'alkine', null, 'admin', '2021-05-31 13:34:31.256000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835735052289', 'hello', '.color{\n  color: red;\n}', 'alkoxy', null, 'admin', '2021-05-31 13:34:31.278000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835739246594', 'hello', '.color{\n  color: red;\n}', 'alkyds', null, 'admin', '2021-05-31 13:34:31.279000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237835877658626', 'hello', '.color{\n  color: red;\n}', 'alkyne', null, 'admin', '2021-05-31 13:34:31.313000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836099956738', 'hello', '.color{\n  color: red;\n}', 'allege', null, 'admin', '2021-05-31 13:34:31.366000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836108345345', 'hello', '.color{\n  color: red;\n}', 'allee', null, 'admin', '2021-05-31 13:34:31.368000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836108345346', 'hello', '.color{\n  color: red;\n}', 'allays', null, 'admin', '2021-05-31 13:34:31.368000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836112539650', 'hello', '.color{\n  color: red;\n}', 'allele', null, 'admin', '2021-05-31 13:34:31.369000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836112539651', 'hello', '.color{\n  color: red;\n}', 'allay', null, 'admin', '2021-05-31 13:34:31.369000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836120928257', 'hello', '.color{\n  color: red;\n}', 'alley', null, 'admin', '2021-05-31 13:34:31.370000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836120928258', 'hello', '.color{\n  color: red;\n}', 'allees', null, 'admin', '2021-05-31 13:34:31.370000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836339032065', 'hello', '.color{\n  color: red;\n}', 'allied', null, 'admin', '2021-05-31 13:34:31.423000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836343226370', 'hello', '.color{\n  color: red;\n}', 'allies', null, 'admin', '2021-05-31 13:34:31.424000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836351614978', 'hello', '.color{\n  color: red;\n}', 'alleys', null, 'admin', '2021-05-31 13:34:31.426000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836569718786', 'hello', '.color{\n  color: red;\n}', 'allot', null, 'admin', '2021-05-31 13:34:31.477000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836573913089', 'hello', '.color{\n  color: red;\n}', 'allod', null, 'admin', '2021-05-31 13:34:31.479000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836573913090', 'hello', '.color{\n  color: red;\n}', 'allow', null, 'admin', '2021-05-31 13:34:31.479000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836578107393', 'hello', '.color{\n  color: red;\n}', 'allods', null, 'admin', '2021-05-31 13:34:31.480000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836586496002', 'hello', '.color{\n  color: red;\n}', 'allium', null, 'admin', '2021-05-31 13:34:31.482000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836599078913', 'hello', '.color{\n  color: red;\n}', 'allots', null, 'admin', '2021-05-31 13:34:31.485000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836599078914', 'hello', '.color{\n  color: red;\n}', 'allows', null, 'admin', '2021-05-31 13:34:31.485000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836783628290', 'hello', '.color{\n  color: red;\n}', 'alloys', null, 'admin', '2021-05-31 13:34:31.529000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836787822593', 'hello', '.color{\n  color: red;\n}', 'alls', null, 'admin', '2021-05-31 13:34:31.530000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237836787822594', 'hello', '.color{\n  color: red;\n}', 'alloy', null, 'admin', '2021-05-31 13:34:31.530000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837031092225', 'hello', '.color{\n  color: red;\n}', 'ally', null, 'admin', '2021-05-31 13:34:31.588000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837035286529', 'hello', '.color{\n  color: red;\n}', 'allure', null, 'admin', '2021-05-31 13:34:31.588000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837035286530', 'hello', '.color{\n  color: red;\n}', 'allude', null, 'admin', '2021-05-31 13:34:31.589000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837047869441', 'hello', '.color{\n  color: red;\n}', 'allyl', null, 'admin', '2021-05-31 13:34:31.592000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837052063745', 'hello', '.color{\n  color: red;\n}', 'allyls', null, 'admin', '2021-05-31 13:34:31.592000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399237837148532737', 'hello', '.color{\n  color: red;\n}', 'almah', null, 'admin', '2021-05-31 13:34:31.615000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399267178720931841', 'redbackground', '.redbackground{\n  background-color: red;\n}', 'red', null, 'admin', '2021-05-31 15:31:07.190000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399267370601951233', 'graybackground', '.graybackground{\n  background-color: gray;\n}', 'graybackground', null, 'admin', '2021-05-31 15:31:52.939000', null, null);
INSERT INTO `fd_form_style` VALUES ('1399548325967421441', '6.1xll', '.h{\n  backgroud-color:red;\n}', 'g6-1', null, 'admin', '2021-06-01 10:08:17.915000', null, null);
INSERT INTO `fd_form_style` VALUES ('1402906668708360193', '      fds', 'ds', 'fdsfds', null, 'admin', '2021-06-10 16:33:09.218000', null, null);
INSERT INTO `fd_form_style` VALUES ('1402906732851851265', 'q fds', 'gre', 'ddddq', null, 'admin', '2021-06-10 16:33:24.515000', null, null);
