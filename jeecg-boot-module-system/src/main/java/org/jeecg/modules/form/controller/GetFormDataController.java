package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.form.service.GetFormDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@Slf4j
@Api(tags = "图表-获取表单数据")
@RequestMapping(value = "/getFormData")
public class GetFormDataController {
    @Autowired
    private GetFormDataService getFormDataService;


    @RequestMapping(value = "/getFromData", method = RequestMethod.GET)
    @ApiOperation(value = "获取表单数据")
    public Object getFromData(@RequestParam(value = "codeType") String codeType)
    {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        return getFormDataService.getFormData(user.getUsername(), codeType);
    }

    @RequestMapping(value = "/listCode",method = RequestMethod.GET)
    @ApiOperation(value = "根据数据创建的用户列出表单编码")
    public Object getCode(@RequestParam(value = "userName") String userName){
        return getFormDataService.listCode(userName);
    }
}
