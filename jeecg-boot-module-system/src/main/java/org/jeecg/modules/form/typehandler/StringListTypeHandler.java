package org.jeecg.modules.form.typehandler;

import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * 用于postgre数据库StringList类型数据库处理，在mapper.xml中用到
 *
 * @author XuDeQing
 * @create 2021-08-31 17:26
 * @modify 2021-08-31 17:26
 */
@Slf4j
@MappedTypes(List.class)
public class StringListTypeHandler extends BaseTypeHandler<List<String>> {
    @Override
    public void setNonNullParameter(PreparedStatement preparedStatement, int i, List<String> strings, JdbcType jdbcType) throws SQLException {
        String s;
        if (!strings.isEmpty()) {
            StringBuilder buffer = new StringBuilder();
            for (String string : strings) {
                buffer.append(string).append(",");
            }
            s = buffer.substring(0, buffer.length()-1);
        } else {
            s = "";
        }
        preparedStatement.setString(i, s);
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, String s) throws SQLException {
        return resolveStringList(resultSet.getString(s));
    }

    @Override
    public List<String> getNullableResult(ResultSet resultSet, int i) throws SQLException {
        return resolveStringList(resultSet.getString(i));
    }

    @Override
    public List<String> getNullableResult(CallableStatement callableStatement, int i) throws SQLException {
        return resolveStringList(callableStatement.getString(i));
    }

    private List<String> resolveStringList(String string){
        return Arrays.asList(string.split(","));
    }
}
