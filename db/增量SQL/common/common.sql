-- 应用表添加多租户字段
ALTER TABLE `lowcode_full_develop`.`sys_application`
    ADD COLUMN `tenant_id` int NULL DEFAULT 0 COMMENT '租户id' AFTER `application_image`;
-- 部门表添加多租户字段
ALTER TABLE `lowcode_full_develop`.`sys_depart`
    ADD COLUMN `tenant_id` int NULL DEFAULT 0 COMMENT '租户id' AFTER `update_time`;
-- 菜单表添加多租户字段
ALTER TABLE `lowcode_full_develop`.`sys_permission`
    ADD COLUMN `tenant_id` int NULL DEFAULT 0 COMMENT '租户id' AFTER `create_ways`;
-- 角色表添加多租户字段
ALTER TABLE `lowcode_full_develop`.`sys_role`
    ADD COLUMN `tenant_id` int NULL DEFAULT 0 COMMENT '租户id' AFTER `update_time`;
-- 职务表添加多租户字段
ALTER TABLE `lowcode_full_develop`.`sys_position`
    ADD COLUMN `tenant_id` int NULL DEFAULT 0 COMMENT '租户id' AFTER `sys_org_code`;
--删除部门表唯一约束
ALTER TABLE `sys_depart` DROP INDEX `uniq_depart_org_code`;