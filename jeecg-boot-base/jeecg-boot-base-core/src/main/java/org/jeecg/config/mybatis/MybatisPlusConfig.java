package org.jeecg.config.mybatis;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TenantLineHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.TenantLineInnerInterceptor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.LongValue;
import org.jeecg.common.util.oConvertUtils;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 单数据源配置（jeecg.datasource.open = false时生效）
 * @Author zhoujf
 *
 */
@Configuration
@MapperScan(value={"org.jeecg.modules.**.mapper*", "org.jeecg.generator.**.mapper*"})
public class MybatisPlusConfig {

    /**
     * tenant_id 字段名
     */
    private static final String TENANT_FIELD_NAME = "tenant_id";

    /**
     * 有哪些表需要做多租户 这些表需要添加一个字段 ，字段名和tenant_field对应的值一样
     */
    private static final List<String> tenantTable = new ArrayList<String>();
    /**a
     * ddl 关键字 判断不走多租户的sql过滤
     */
    private static final List<String> DDL_KEYWORD = new ArrayList<String>();

    static {
        tenantTable.add("jee_bug_danbiao");
        //角色
        tenantTable.add("sys_role");
        //菜单
        tenantTable.add("sys_permission");
        //部门
        tenantTable.add("sys_depart");
        //应用
        tenantTable.add("sys_application");
        //职务
        tenantTable.add("sys_position");
        DDL_KEYWORD.add("alter");
        tenantTable.add("onl_cfgraph_head");
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 先 add TenantLineInnerInterceptor 再 add PaginationInnerInterceptor
        interceptor.addInnerInterceptor(new TenantLineInnerInterceptor(new TenantLineHandler() {
            @Override
            public Expression getTenantId() {
                String tenant_id = oConvertUtils.getString(TenantContext.getTenant(), "0");
                return new LongValue(tenant_id);
            }

            @Override
            public String getTenantIdColumn() {
                return TENANT_FIELD_NAME;
            }

            // 返回 true 表示不走租户逻辑
            @Override
            public boolean ignoreTable(String tableName) {
                for (String temp : tenantTable) {
                    if (temp.equalsIgnoreCase(tableName)) {
                        return false;
                    }
                }
                return true;
            }
        }));
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor());
        return interceptor;
    }
}
