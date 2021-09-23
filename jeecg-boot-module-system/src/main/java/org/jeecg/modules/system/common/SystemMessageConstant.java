package org.jeecg.modules.system.common;

/**
 * @author zhangqijian, wangshuang
 * @company dxc
 * @create  2021-07-12 14:22
 */
public final class SystemMessageConstant {
    //私有构造方法
    private SystemMessageConstant() {
    }

    //查询成功
    public static final String GRAPH_HEAD_QUERY_SUCCESS = "查询成功";
    //多数据源-该数据源不存在
    public static final String MUTIDATASOURCE_NOT_FOUND = "该数据源不存在!";
    //多数据源-该数据源不存在
    public static final String MUTIDATASOURCE_SQLSERVER_SORTINSQL_NOTSUPPOT = "SQLSERVER数据源不支持sql内排序!";
    //数据源表名列表sql mysql
    public static final String MUTIDATASOURCE_TABLES_SQL = "SELECT table_name " +
            "FROM information_schema.tables " +
            "WHERE table_schema='";
    //数据源表内字段查询sql mysql
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL = "SELECT Column_Name AS ColumnName,COLUMN_COMMENT AS Comment " +
            "FROM information_schema.columns " +
            "WHERE table_schema = 'SCHEMA' AND table_name = 'TABLENAME' " +
            "ORDER BY ORDINAL_POSITION ASC;";
    //数据源表名列表sql oracle
    public static final String MUTIDATASOURCE_TABLES_SQL_ORACLE = "SELECT table_name AS \"table_name\" " +
            "FROM user_tables " +
            "WHERE user='";
    //数据源表内字段查询sql oracle
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL_ORACLE = "SELECT column_name AS \"ColumnName\",COMMENTS AS \"Comment\"\n" +
            "FROM user_col_comments " +
            "WHERE table_name = 'TABLENAME'";
    //数据源表名列表sql sqlServer
    public static final String MUTIDATASOURCE_TABLES_SQL_SQLSERVER = "SELECT NAME AS table_name " +
            "FROM SYSOBJECTS  " +
            "WHERE TYPE='U';";
    //数据源表内字段查询sql sqlServer
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL_SQLSERVER = "SELECT cast(N.name as varchar(100)) AS ColumnName,cast(D.value as varchar(100)) AS Comment " +
            "FROM sys.tables T " +
            "INNER JOIN sys.columns N ON N.object_id = T.object_id " +
            "LEFT JOIN sys.extended_properties D ON D.major_id = N.object_id AND D.minor_id = N.column_id " +
            "WHERE T.name = 'TABLENAME';";
}
