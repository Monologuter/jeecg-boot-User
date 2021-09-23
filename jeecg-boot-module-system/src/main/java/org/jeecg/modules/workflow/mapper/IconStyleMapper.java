package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: Icon样式实体类
 * @author: SunBoWen
 * @date: 2021年06月01日 10:50
 */
@Mapper
public interface IconStyleMapper  {

    /**
     * 得到图标样式集合
     * */
    List<String> getIconStyle();
}
