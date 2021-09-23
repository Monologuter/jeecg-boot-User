package org.jeecg.modules.workflow.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.jeecg.modules.workflow.entity.vo.ProcessDevelopmentVO;

import java.util.List;

/**
 * 工作台功能展示
 * @Description: TODO
 * @author: SunBoWen
 * @date: 2021年06月03日 14:21
 */
@Mapper
public interface WorkbenchDisplayMapper {

    /**
     * 得到工作台的所有内容
     * */
    List<String> getWorkbenchDisplayFindAllGroup();

    /**
     * 通过流程key寻找版本
     * */
    List<ProcessDevelopmentVO> findKeyAndVersions(String procDefGroup);

    /**
     * 通过组找到所有工作台内容
     * */
    List<ProcessDevelopmentVO> getWorkbenchDisplayByGroup(String procDefGroup);
}

