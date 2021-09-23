package org.jeecg.modules.form.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.jeecg.modules.form.entity.GeneratorCodeDO;

/**
 * 代码生成器持久层接口
 *
 * @author: HuangSn、LiKun
 * @date: 2021年07月06日 15:03
 */
@Mapper
public interface GeneratorCodeMapper extends BaseMapper<GeneratorCodeDO> {

    /**
     * 指定表名，删除指定的表
     *
     * @param tableName 表名
     * @return void
     * @Author HuangSn
     */
    @Update("drop table if exists ${tableName}")
    void dropTable(String tableName);

    /**
     * 根据给定的sql语句穿件表
     *
     * @param sql SQL建表语句
     * @return void
     * @Author HuangSn
     */
    @Update("${sql}")
    void createTable(String sql);

    /**
     * 给定表名，从数据库中查询是否存在该表，返回表名
     *
     * @param tableName 表名
     * @return void
     * @Author HuangSn
     */
    @Select("select TABLE_NAME from information_schema.tables where table_name =(#{tableName})")
    String ifTableIsCreated(String tableName);

    /**
     * 给定sql语句，删除除相关表中的字段
     *
     * @param sql SQL删除表字段语句
     * @return void
     * @Author LiKun
     */
    @Delete("${sql}")
    void deleteFields(String sql);

    /**
     * 给定sql语句，添加除相关表中的字段
     *
     * @param sql SQL添加表字段语句
     * @return void
     * @Author LiKun
     */
    @Insert("${sql}")
    void addFields(String sql);

    /**
     * 给定sql语句，修改除相关表中的字段
     *
     * @param sql SQL修改表字段语句
     * @return void
     * @Author LiKun
     */
    @Update("${sql}")
    void updateFields(String sql);
}
