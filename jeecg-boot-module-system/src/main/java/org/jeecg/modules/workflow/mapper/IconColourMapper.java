package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import java.util.List;

/**
 * @Description: Icon颜色实体类
 * @author: SunBoWen
 * @date: 2021年06月01日 10:45
 */

@Mapper
public interface IconColourMapper {

    /**
     * 得到图标的颜色集合
     * */
    List<String> getIconColour();
}
