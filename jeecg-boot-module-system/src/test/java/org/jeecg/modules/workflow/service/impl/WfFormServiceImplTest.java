// package org.jeecg.modules.workflow.service.impl;

// import com.alibaba.fastjson.JSON;
// import lombok.extern.slf4j.Slf4j;
// import org.camunda.bpm.engine.IdentityService;
// import org.jeecg.JeecgSystemApplication;
// import org.jeecg.common.api.vo.Result;
// import org.jeecg.common.constant.CommonConstant;
// import org.jeecg.common.util.PasswordUtil;
// import org.jeecg.common.util.oConvertUtils;
// import org.jeecg.modules.system.entity.SysUser;
// import org.jeecg.modules.system.service.ISysUserService;
// import org.jeecg.modules.workflow.entity.vo.UserDTO;
// import org.jeecg.modules.workflow.service.WfUserService;
// import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
// import org.junit.jupiter.api.AfterEach;
// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.junit.runner.RunWith;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.stereotype.Component;
// import org.springframework.test.context.junit4.SpringRunner;

// import java.util.Date;

// import static org.junit.jupiter.api.Assertions.*;

// @Slf4j
// @RunWith(SpringRunner.class)
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = JeecgSystemApplication.class)
// @SuppressWarnings({"FieldCanBeLocal", "SpringJavaAutowiredMembersInspection"})
// class WfFormServiceImplTest {
//     @Value("${server.port}")
//     private Integer port;


//     @Value("${spring.application.name}")
//     private String name;


//     @Autowired
//     private ISysUserService sysUserService;

//     @Autowired
//     private WfUserService wfUserService;

//     @Autowired
//     private IdentityService identityService;

//     @BeforeEach
//     void setUp() {
//     }

//     @AfterEach
//     void tearDown() {
//     }

//     @Test
//     void showFormByTaskId() {
//     }

//     @Test
//     void submitForm() {
//     }

//     @Test
//     void refuseForm() {
//     }


// /**
//  * 测试内容: 用户同步测试
//  */
//     @Test
//     public void testAddUser() {

//         UserDTO user1 = new UserDTO();

//         SysUser user = new SysUser();

//         user.setCreateTime(new Date());//设置创建时间
//         String salt = oConvertUtils.randomGen(8);
//         user.setUsername("战三");
//         user.setEmail("243@qq.com");
//         user.setRealname("小战三");

//         user.setSalt(salt);
//         String passwordEncode = PasswordUtil.encrypt(user.getUsername(), "Cyy1234567!", salt);
//         user.setPassword(passwordEncode);
//         user.setStatus(1);
//         user.setDelFlag(CommonConstant.DEL_FLAG_0);


//         sysUserService.addUserWithRole(user, "1");

//         user1.setUserId(user.getUsername());
//         user1.setEmail(user.getEmail());
//         user1.setPassword(user.getPassword());
//         user1.setFirstName(user.getRealname());
//         user1.setLastName(user.getRealname());

//         wfUserService.addUser(user1);
//     }

//     /**
//      * 测试内容:获取项目名以及端口
//      */
//     @Test
//     public void test() {
//         System.out.println("当前项目端口号为 = " + port);
//         System.out.println("当前项目应用名为 = " + name);
//     }
// }
