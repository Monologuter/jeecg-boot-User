package org.jeecg.modules.onlgraph.common;

/**
 * @author wangshuang
 * @company dxc
 * @create  2021-06-01 10:41
 */
public class OnlgraphConstant {

    // 私有构造方法
    private OnlgraphConstant() {
    }

    /**
     * 删除状态(删除)
     */
    public static final Integer GRAPH_DEL_FLAG_SUCCESS = 1;
    /**
     * 删除状态(未删除)
     */
    public static final Integer GRAPH_DEL_FLAG_FLASE = 0;
    /**
     * 模板状态(是)
     */
    public static final Integer GRAPH_TEMPLATE_FLAG_TRUE = 1;
    /**
     * 模板状态(否)
     */
    public static final Integer GRAPH_TEMPLATE_FLAG_FLASE = 0;
    /**
     * PermissionStatus状态为1(Integer)
     */
    public static final Integer GRAPH_PERMISSION_STATUS_STATE = 1;
    /**
     * PermissionStatus状态为1(String)
     */
    public static final String GRAPH_STATUS_STATE = "1";
    /**
     * 判断数据库类型 - oracle
     */
    public static final String GRAPH_DATABASE_TYPE_ORACLE = "2";
    /**
     * 判断数据库类型 - sqlServer
     */
    public static final String GRAPH_DATABASE_TYPE_SQLSERVER = "3";
    /**
     * SqlServer判断匹配正则" order\\s+by "
     */
    public static final String GRAPH_DATABASE_REGEX_MATCH = " order\\s+by ";
    /**
     * 编码校验正则"^[a-zA-Z][a-zA-Z0-9_-]*$"
     */
    public static final String GRAPH_CODE_CHECK_REGEX = "^[a-zA-Z][a-zA-Z0-9_-]*$";
    /**
     * 替换字符TABLENAME
     */
    public static final String GRAPH_REPLACE_TABLENAME = "TABLENAME";
    /**
     * 替换字符SCHEMA
     */
    public static final String GRAPH_REPLACE_SCHEMA = "SCHEMA";
    /**
     * 查询成功
     */
    public static final String GRAPH_QUERY_SUCCESS = "查询成功";
    /**
     * 更新成功
     */
    public static final String GRAPH_UPDATE_SUCCESS = "更新成功";
    /**
     * 添加成功
     */
    public static final String GRAPH_ADD_SUCCESS = "添加成功";
    /**
     * 删除成功
     */
    public static final String GRAPH_DELETE_SUCCESS = "删除成功";
    /**
     * 批量删除成功
     */
    public static final String GRAPH_DELETE_BATCH_SUCCESS = "批量删除成功";
    /**
     * 菜单路由配置地址
     */
    public static final String GRAPH_BIGSCREEN_PERMISSION_URL = "/online/graphBigscreen/";
    /**
     * 菜单路由组件
     */
    public static final String GRAPH_BIGSCREEN_PERMISSION_COMPONENT = "modules/online/auto-chart/index";
    /**
     * 菜单路由名字
     */
    public static final String GRAPH_BIGSCREEN_PERMISSION_NAME = "在线图表配置-";
    /**
     * 路由菜单生成成功
     */
    public static final String GRAPH_MENU_ROUTING_SUCCESS = "路由菜单生成成功";
    /**
     * 菜单路径不存在
     */
    public static final String GRAPH_MENU_ROUTING_FLASE = "菜单路径不存在";
    /**
     * 已通过检验
     */
    public static final String GRAPH_CHECK_SUCCESS = "已通过校验";
    /**
     * 该值不可用系统中已经存在!
     */
    public static final String GRAPH_CHECK_FAILED = "该值不可用系统中已经存在!";
    /**
     * 已通过编码校验
     */
    public static final String GRAPH_CODE_CHECK_SUCCESS = "已通过编码校验";
    /**
     * 未通过编码校验
     */
    public static final String GRAPH_CODE_CHECK_FALSE = "未通过编码校验";
    /**
     * 文件导入成功！数据行数：
     */
    public static final String GRAPH_FILE_IMPORT_SUCCESS = "文件导入成功！数据行数：";
    /**
     * Excel文件读取失败
     */
    public static final String GRAPH_FILE_READ_FALSE = "Excel文件读取失败!";
    /**
     * ID不能为空！
     */
    public static final String GRAPH_UPDATE_ID_EMPTY = "ID不能为空！";
    /**
     * 该记录为空!
     */
    public static final String GRAPH_QUERY_FAILED = "该记录为空!";
    /**
     * 该卡片记录为空!
     */
    public static final String GRAPH_CARD_QUERY_FAILED = "该卡片记录为空!";
    /**
     * 多数据源-该数据源不存在
     */
    public static final String MUTIDATASOURCE_NOT_FOUND = "该数据源不存在!";
    /**
     * 多数据源-该数据源不支持内排序
     */
    public static final String MUTIDATASOURCE_SQLSERVER_SORTINSQL_NOTSUPPOT = "SQLSERVER数据源不支持sql内排序!";
    /**
     * 数据源表名列表sql mysql
     */
    public static final String MUTIDATASOURCE_TABLES_SQL = "SELECT table_name " +
            "FROM information_schema.tables " +
            "WHERE table_schema='";
    /**
     * 数据源表内字段查询sql mysql
     */
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL = "SELECT Column_Name AS ColumnName,COLUMN_COMMENT AS Comment " +
            "FROM information_schema.columns " +
            "WHERE table_schema = 'SCHEMA' AND table_name = 'TABLENAME' " +
            "ORDER BY ORDINAL_POSITION ASC;";
    /**
     * 数据源表名列表sql oracle
     */
    public static final String MUTIDATASOURCE_TABLES_SQL_ORACLE = "SELECT table_name AS \"table_name\" " +
            "FROM user_tables " + "WHERE user='";
    /**
     * 数据源表内字段查询sql oracle
     */
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL_ORACLE = "SELECT column_name AS \"ColumnName\",COMMENTS AS \"Comment\"\n" +
            "FROM user_col_comments " +
            "WHERE table_name = 'TABLENAME'";
    /**
     * 数据源表名列表sql sqlServer
     */
    public static final String MUTIDATASOURCE_TABLES_SQL_SQLSERVER = "SELECT NAME AS table_name " +
            "FROM SYSOBJECTS  " +
            "WHERE TYPE='U';";
    /**
     * 数据源表内字段查询sql sqlServer
     */
    public static final String MUTIDATASOURCE_TABLE_ITEMS_SQL_SQLSERVER = "SELECT cast(N.name as varchar(100)) AS ColumnName,cast(D.value as varchar(100)) AS Comment " +
            "FROM sys.tables T " +
            "INNER JOIN sys.columns N ON N.object_id = T.object_id " +
            "LEFT JOIN sys.extended_properties D ON D.major_id = N.object_id AND D.minor_id = N.column_id " +
            "WHERE T.name = 'TABLENAME';";

}
