package org.jeecg.modules.system.model;

import lombok.Data;
import org.jeecg.modules.system.entity.SysDataSource;

import java.util.List;

/**
 * @author  zhangqijian
 * @company dxc
 * @create  2021-05-19 11:13
 */
@Data
public class SysDataSourceModel extends SysDataSource {
    //次级
    List<SysDataSourceModel> children=null;

}
