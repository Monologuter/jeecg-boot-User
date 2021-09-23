package org.jeecg.modules.form.service.imp;

import com.baomidou.dynamic.datasource.annotation.DS;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.form.constant.FormErrorMessageConstant;
import org.jeecg.modules.form.dto.FormSysPermissionDTO;
import org.jeecg.modules.form.entity.FormSysPermissionDO;
import org.jeecg.modules.form.service.FormAdapterService;
import org.jeecg.modules.form.service.FormSysPermissionService;
import org.jeecg.modules.system.service.ISysPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.util.List;

/**
 * 表单关联菜单栏操作Service层实现类
 *
 * @author XuDeQing
 * @create 2021-08-27 9:57
 * @modify 2021-08-27 9:57
 */
@Service
@Slf4j
public class FormAdapterServiceImpl implements FormAdapterService {
    private final ISysPermissionService sysPermissionService;
    private final FormSysPermissionService formSysPermissionService;

    @Autowired
    public FormAdapterServiceImpl(ISysPermissionService sysPermissionService, FormSysPermissionService formSysPermissionService) {
        this.sysPermissionService = sysPermissionService;
        this.formSysPermissionService = formSysPermissionService;
    }

    /**
     * 删除所有关联的菜单
     *
     * @param list 关联的菜单对象的列表
     */
    @Override
    @DS("master")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void deleteSysPermissionsOfForm(List<FormSysPermissionDO> list) {
        if (!list.isEmpty()) {
            log.info("删除所有关联的菜单");
            list.forEach(formSysPermissionDO -> {
                try {
                    sysPermissionService.deletePermission(formSysPermissionDO.getSysPermissionId());
                } catch (JeecgBootException e) {
                    throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,
                            FormErrorMessageConstant.RELATIVE_PERMISSIONS_OR_ROLE_PERMISSIONS_DELETE_FAILED);
                }
            });
        }
    }

    /**
     * 保存关联的菜单信息
     *
     * @param formSysPermissionDTO 关联的菜单对象
     */
    @Override
    @DS("form")
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void saveFormSysPermission(FormSysPermissionDTO formSysPermissionDTO) {
        formSysPermissionService.save(formSysPermissionDTO);
    }
}
