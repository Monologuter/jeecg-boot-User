package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 流程定义分类实体类
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年06月02日 09:50
 */
@Mapper
public interface ProcClassMapper {

    /**
     * 流程定义分类实体类
     * */
    List<String> getProcClass();
}
