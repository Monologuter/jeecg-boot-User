package ${package.Service};

import ${package.Entity}.${entity};
import ${superServiceClassPackage};
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
/**
 * <p>
 * ${table.comment!} 服务类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
<#if kotlin>
interface ${table.serviceName} : ${superServiceClass}<${entity}>
<#else>
//public interface ${table.serviceName} extends ${superServiceClass}<${entity}> {
public interface ${table.serviceName} {

    boolean updateById(${entity} entity);

    boolean save(${entity} entity);

    boolean removeByIds(List<String> ids);

    ${entity} getById(String id);

    IPage<${entity}> page(Page<${entity}> page);

}
</#if>
