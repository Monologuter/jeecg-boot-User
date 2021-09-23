package org.jeecg.modules.form.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.jeecg.common.system.vo.LoginUser;
import org.jeecg.modules.form.util.InitPermissionUtil;
import org.jeecg.modules.form.util.OnlinePythonComplieUtil;
import org.jeecg.modules.form.util.OnlineJavaComplieUtil;
import org.jeecg.modules.form.vo.CompliesVO;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@Api(tags = "Java、Python在线编译")
@RequestMapping(value = "/online")
public class OnlineComplierController {
    OnlineJavaComplieUtil onlineComplieUtil = new OnlineJavaComplieUtil();
    OnlinePythonComplieUtil onlinePythonComplierUtils = new OnlinePythonComplieUtil();
    CompliesVO complieVO = new CompliesVO();
    InitPermissionUtil initPermissionUtil = new InitPermissionUtil();

    @RequestMapping(value = "/java", method = RequestMethod.POST)
    @ApiOperation(value = "Java在线编译")
    @ResponseBody
    public CompliesVO onlineComplier(@RequestParam String code) throws IOException, InterruptedException {
        initPermissionUtil.initPermission();
        complieVO = onlineComplieUtil.execute(code);
        return complieVO;
    }

    @RequestMapping(value = "/Python", method = RequestMethod.POST)
    @ApiOperation(value = "Python在线编译")
    @ResponseBody
    public CompliesVO onlinePython(@RequestParam String code) {
        LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
        try {
            log.info("正在执行python程序");
            complieVO = onlinePythonComplierUtils.execute(onlinePythonComplierUtils.writeIntoFile(code, user.getUsername()));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return complieVO;
    }
}
