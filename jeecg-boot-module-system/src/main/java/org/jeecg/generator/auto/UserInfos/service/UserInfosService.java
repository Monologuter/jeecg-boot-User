package org.jeecg.generator.UserInfos.service;

import org.jeecg.generator.UserInfos.entity.UserInfos;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;
/**
 * <p>
 *  服务类
 * </p>
 *
 * @author system
 * @since 2021-08-04
 */
//public interface UserInfosService extends IService<UserInfos> {
public interface UserInfosService {

    boolean updateById(UserInfos entity);

    boolean save(UserInfos entity);

    boolean removeByIds(List<Long> ids);

    UserInfos getById(Long id);

    IPage<UserInfos> page(Page<UserInfos> page);

}
