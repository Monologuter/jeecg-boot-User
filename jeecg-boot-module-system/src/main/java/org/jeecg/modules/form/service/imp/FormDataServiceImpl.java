package org.jeecg.modules.form.service.imp;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jnr.ffi.annotations.In;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.api.CommonAPI;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.form.dto.FormDataDTO;
import org.jeecg.modules.form.entity.FormDataDO;
import org.jeecg.modules.form.entity.FormRoleDo;
import org.jeecg.modules.form.entity.PermissionRuleDo;
import org.jeecg.modules.form.mapper.FormDataMapper;
import org.jeecg.modules.form.service.FormDataService;
import org.jeecg.modules.form.service.FormRoleService;
import org.jeecg.modules.form.service.PermissionRuleService;
import org.jeecg.modules.form.vo.FormDataJsonVO;
import org.jeecg.modules.form.vo.FormDataToWorkflowVO;
import org.jeecg.modules.system.entity.SysRole;
import org.jeecg.modules.system.service.ISysRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 表单数据操作Service层实现类
 *
 * @author XuDeQing
 * @date 2021-03-2021/3/17 17:10
 */

@DS("form")
@Service
public class FormDataServiceImpl extends ServiceImpl<FormDataMapper, FormDataDO> implements FormDataService {

    @Autowired(required = false)
    FormDataMapper formDataMapper;

    private ArrayList<FormDataToWorkflowVO.params> paramsList;

    private final CommonAPI commonAPI;
    private final ISysRoleService sysRoleService;
    private final FormRoleService formRoleService;
    private final PermissionRuleService permissionRuleService;

    @Autowired
    public FormDataServiceImpl(CommonAPI commonAPI, ISysRoleService sysRoleService, FormRoleService formRoleService, PermissionRuleService permissionRuleService) {
        this.commonAPI = commonAPI;
        this.sysRoleService = sysRoleService;
        this.formRoleService = formRoleService;
        this.permissionRuleService = permissionRuleService;
    }

    /**
     * 给工作流返回表单的数据
     *
     * @param dataJson 表单的数据JSON字符串
     * @return java.util.ArrayList<org.jeecg.modules.form.vo.FormDataToWorkflowVO.params> 返回表单数据的列表
     */
    @Override
    public ArrayList<FormDataToWorkflowVO.params> getDataJsonToWorkflow(String dataJson) throws JSONException {
        paramsList = new ArrayList<>();
        // 这里转换json字符串失败会抛出JSONException异常
        FormDataJsonVO dataJsonVO = JSONObject.parseObject(dataJson, FormDataJsonVO.class);
        JSONArray list = dataJsonVO.getList();
        // 进入递归判断
        list.forEach(item -> judgeType((JSONObject) item));
        return paramsList;
    }

    /**
     * 保存表单数据
     *
     * @param formData 表单数据
     * @return org.jeecg.modules.form.dto.FormDataDTO 返回报错后的表单数据
     */
    @Override
    public FormDataDTO saveFormData(FormDataDTO formData) {
        return null;
    }

    /**
     * 获取表单数据列表
     *
     * @param page        分页参数
     * @param formId      表单ID
     * @param searchRules 查询的规则
     * @param orderBy     排序的字段
     * @param isDesc      排序方式
     * @return com.baomidou.mybatisplus.core.metadata.IPage<org.jeecg.modules.form.entity.FormDataDO> FormDataDO对象的IPage
     */
    @DS("master")
    @Override
    public IPage<FormDataDO> getFormDataList(Page<FormDataDO> page,
                                             String formId,
                                             String permissionId,
                                             List<FormRoleDo> searchRules,
                                             List<String> orderBy,
                                             boolean isDesc) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        Set<String> roleCodes = commonAPI.queryUserRoles(user.getUsername());
        List<SysRole> roleList = sysRoleService.lambdaQuery()
                .in(SysRole::getRoleCode, roleCodes)
                .list();
        List<FormRoleDo> rules = formRoleService.lambdaQuery()
                .eq(FormRoleDo::getFormId, formId)
                .in(FormRoleDo::getRoleId, roleList.stream()
                        .map(SysRole::getId)
                        .collect(Collectors.toList()))
                .list();
        rules.addAll(searchRules);
        if (StringUtils.hasText(permissionId)) {
            List<FormRoleDo> permissionRules = permissionRuleService.lambdaQuery()
                    .eq(PermissionRuleDo::getPermissionId, permissionId)
                    .list()
                    .stream()
                    .map(pr -> FormRoleDo
                            .builder()
                            .ruleKey(pr.getRuleKey())
                            .ruleValue(pr.getRuleValue())
                            .build())
                    .collect(Collectors.toList());
            rules.addAll(permissionRules);
        }
        return getBaseMapper().getFormDataList(page, formId, rules, orderBy, isDesc);
    }

    /**
     * 图表组件需要  获取表单统计数据
     *
     * @param formId 表单数据
     * @param rules  统计规则
     * @return java.lang.String 返回统计数据
     */
    @Override
    public Long getFormDataCount(String formId, List<FormRoleDo> rules) {
        return getBaseMapper().getFormDataCount(formId, rules);
    }

    /**
     * @param formDataId
     * @return java.lang.String
     * @Description 表单日志需要：根据表单数据id查询表单id
     * @Author huang sn
     * @Date 11:05 2021/9/6
     */
    @Override
    public String getFormIdByFormDataId(String formDataId) {
        return formDataMapper.getFormIdByDataId(formDataId);
    }

    @Override
    public Map<String, Integer> getGraphData() {
        Map<String, Integer> map = formDataMapper.getGraphData();
        return map;
    }

    /**
     * 判断布局属于什么
     */
    private void judgeType(JSONObject object) {
        String type = object.getString("type");
        if ("tabs".equals(type)) {
            // 布局是 tabs
            tabsLayout(object);
        } else if ("grid".equals(type)) {
            // 布局是 grid
            gridLayout(object);
        } else {
            // 无布局
            paramsList.add(getParams(object));
        }
    }

    /**
     * grid 布局
     */
    private void gridLayout(JSONObject object) {
        object.getJSONArray("columns").forEach(grid ->
                // 调用判断布局函数进行递归, 用于处理 grid 布局中还会出现其他布局的情况
                ((JSONObject) grid).getJSONArray("list").forEach(gridItem -> judgeType((JSONObject) gridItem))
        );
    }

    /**
     * tabs 布局
     */
    private void tabsLayout(JSONObject object) {
        object.getJSONArray("tabs").forEach(tabs ->
                // 调用判断布局函数进行递归,用于处理 tabs 布局中还会出现其他布局的情况
                ((JSONObject) tabs).getJSONArray("list").forEach(tabsItem -> judgeType((JSONObject) tabsItem))
        );
    }

    /**
     * 获取输入框信息
     */
    private FormDataToWorkflowVO.params getParams(JSONObject object) {
        return new FormDataToWorkflowVO.params(object.getString("model"), object.getString("type")
                , object.getString("name"));
    }
}
