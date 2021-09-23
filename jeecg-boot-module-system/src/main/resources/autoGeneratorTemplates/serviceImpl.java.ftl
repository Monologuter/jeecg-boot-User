package ${package.ServiceImpl};

import ${package.Entity}.${entity};
import ${package.Mapper}.${table.mapperName};
import ${package.Service}.${table.serviceName};
import ${superServiceImplClassPackage};
import org.springframework.stereotype.Service;
import org.springframework.stereotype.Repository;
import org.springframework.beans.factory.annotation.Autowired;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
/**
 * <p>
 * ${table.comment!} 服务实现类
 * </p>
 *
 * @author ${author}
 * @since ${date}
 */
@Service
<#if kotlin>
open class ${table.serviceImplName} : ${superServiceImplClass}<${table.mapperName}, ${entity}>(), ${table.serviceName} {

}
<#else>
//public class ${table.serviceImplName} extends ${superServiceImplClass}<${table.mapperName}, ${entity}> implements ${table.serviceName} {
public class ${table.serviceImplName} implements ${table.serviceName} {

    @Autowired
    private ${entity}Mapper ${table.entityPath}Mapper;

    @Override
    public boolean updateById(${entity} entity) {
        return ${table.entityPath}Mapper.updateById(entity) == 1;
    }

    @Override
    public boolean save(${entity} entity) {
        return ${table.entityPath}Mapper.insert(entity) == 1;
    }

    @Override
    public boolean removeByIds(List<String> ids) {
        return ${table.entityPath}Mapper.deleteBatchIds(ids) >= 1;
    }

    @Override
    public ${entity} getById(String id) {
        return ${table.entityPath}Mapper.selectById(id);
    }

    @Override
    public IPage<${entity}> page(Page<${entity}> page) {
        return ${table.entityPath}Mapper.selectPage(page, null);
    }

}
</#if>
