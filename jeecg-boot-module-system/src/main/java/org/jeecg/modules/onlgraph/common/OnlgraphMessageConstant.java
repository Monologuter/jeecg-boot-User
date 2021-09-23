package org.jeecg.modules.onlgraph.common;

/**
 * @author wangjiahao, zhangqijian
 * @company dxc
 * @create  2021-03-24 15:36
 */
public final class OnlgraphMessageConstant {

    //私有构造方法
    private OnlgraphMessageConstant() {
    }

    //查询成功
    public static final String GRAPH_HEAD_QUERY_SUCCESS = "查询成功";
    //添加成功
    public static final String GRAPH_HEAD_ADD_SUCCESS = "添加成功";
    //该记录为空!
    public static final String GRAPH_HEAD_QUERY_FAILED = "该记录为空!";
    //批量删除成功
    public static final String GRAPH_HEAD_DELETE_BATCH_SUCCESS = "批量删除成功";
    //已通过检验
    public static final String GRAPH_HEAD_CHECK_SUCCESS = "已通过检验";
    //该值不可用系统中已经存在!
    public static final String GRAPH_HEAD_CHECK_FAILED = "该值不可用系统中已经存在!";
    //路由菜单生成成功
    public static final String GRAPH_HEAD_MENU_ROUTING_SUCCESS = "路由菜单生成成功";
    //路由菜单已存在
    public static final String GRAPH_HEAD_MENU_ROUTING_EXITS = "路由菜单已存在";
    //ID不能为空！
    public static final String GRAPH_HEAD_UPDATE_ID_EMPTY = "ID不能为空！";
    //更新成功
    public static final String GRAPH_HEAD_UPDATE_SUCCESS = "更新成功";
    //删除成功
    public static final String GRAPH_HEAD_DELETE_SUCCESS = "删除成功";
    //编码校验未通过
    public static final String GRAPH_HEAD_CODE_CHECK_FALSE = "编码校验未通过!";
    //编码成功
    public static final String GRAPH_HEAD_CODE_CHECK_SUCCESS = "校验成功";
    //文件导入成功！数据行数：
    public static final String GRAPH_HEAD_FILE_IMPORT_SUCCESS = "文件导入成功！数据行数：";
    //文件导入失败!
    public static final String GRAPH_HEAD_FILE_IMPORT_FALSE = "请导入正确的文件格式!";
    //菜单路由配置地址
    public static final String GRAPH_HEAD_PERMISSION_URL = "/online/graphreport/";
    //菜单路由组件
    public static final String GRAPH_HEAD_PERMISSION_COMPONENT = "modules/online/auto-chart/index";
    //菜单路由名字
    public static final String GRAPH_HEAD_PERMISSION_NAME = "在线图表配置-";
    //head图表删除状态(删除)
    public static final Integer GRAPH_HEAD_DEL_FLAG_SUCCESS = 1;
    //head图表删除状态(未删除)
    public static final Integer GRAPH_HEAD_DEL_FLAG_FLASE = 0;
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
