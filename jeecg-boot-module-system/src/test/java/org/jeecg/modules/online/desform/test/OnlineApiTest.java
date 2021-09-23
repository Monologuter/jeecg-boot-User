package org.jeecg.modules.online.desform.test;

import com.alibaba.fastjson.JSONObject;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.UserTask;
import org.jeecg.JeecgSystemApplication;
import org.jeecg.common.constant.CommonConstant;
import org.jeecg.common.system.util.JwtUtil;
import org.jeecg.common.util.RedisUtil;
import org.jeecg.common.util.RestUtil;
import org.jeecg.modules.workflow.service.WfDefinitionService;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * online api online表单单元测试
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,classes = JeecgSystemApplication.class)
@SuppressWarnings({"FieldCanBeLocal", "SpringJavaAutowiredMembersInspection"})
public class OnlineApiTest {
    @Autowired
    private RedisUtil redisUtil;
    /**
     * 测试地址：实际使用时替换成你自己的地址
     */
    private final String BASE_URL = "http://localhost:8080/jeecg-boot/online/cgform/api/";

    // 请实际使用时替换成你自己的用户名和密码
    private final String USERNAME = "admin";
    private final String PASSWORD = "123456";

    /**
     * online表单code，实际使用时可以替换成你要测试的表单code
     * （测试表test_demo）
     */
    private final String ONLINE_CODE = "d35109c3632c4952a19ecc094943dd71";

    /**
     * 测试用例：新增
     */
    @Test
    public void testAdd() {
        // 用户Token
        String token = this.getToken();
        // 请求地址
        String url = BASE_URL + "form/" + ONLINE_CODE;
        // 请求 Header （用于传递Token）
        HttpHeaders headers = this.getHeaders(token);
        // 请求方式是 POST 代表提交新增数据
        HttpMethod method = HttpMethod.POST;

        System.out.println("请求地址：" + url);
        System.out.println("请求方式：" + method);
        System.out.println("请求Token：" + token);

        JSONObject params = new JSONObject();
        params.put("name", "张三");
        params.put("sex", "1");
        params.put("age",15);
        params.put("descc", "<p>富文本编辑</p>");

        System.out.println("请求参数：" + params.toJSONString());

        // 利用 RestUtil 请求该url
        ResponseEntity<JSONObject> result = RestUtil.request(url, method, headers, null, params, JSONObject.class);
        if (result != null && result.getBody() != null) {
            System.out.println("返回结果：" + result.getBody().toJSONString());
        } else {
            System.out.println("查询失败");
        }
    }


    /**
     * 测试用例：修改
     */
    @Test
    public void testEdit() {
        // dataId
        String dataId = "1331797913880883201";
        // 用户Token
        String token = this.getToken();
        // 请求地址
        String url = BASE_URL + "form/" + ONLINE_CODE;
        // 请求 Header （用于传递Token）
        HttpHeaders headers = this.getHeaders(token);
        // 请求方式是 PUT 代表提交修改数据
        HttpMethod method = HttpMethod.PUT;

        System.out.println("请求地址：" + url);
        System.out.println("请求方式：" + method);
        System.out.println("请求Token：" + token);

        JSONObject params = new JSONObject();
        params.put("id", dataId);
        params.put("name", "张三");
        params.put("sex", "1");
        params.put("age",30);
        params.put("descc", "<p>富文本编辑，重新编辑</p>");

        System.out.println("请求参数：" + params.toJSONString());

        // 利用 RestUtil 请求该url
        ResponseEntity<JSONObject> result = RestUtil.request(url, method, headers, null, params, JSONObject.class);
        if (result != null && result.getBody() != null) {
            System.out.println("返回结果：" + result.getBody().toJSONString());
        } else {
            System.out.println("查询失败");
        }
    }


    /**
     * 测试用例：删除
     */
    @Test
    public void testDelete() {
        // 数据id
        String dataId = "1331797913880883201";
        // 用户Token
        String token = this.getToken();
        // 请求地址
        String url = BASE_URL + "form/" + ONLINE_CODE + "/" + dataId;
        // 请求 Header （用于传递Token）
        HttpHeaders headers = this.getHeaders(token);
        // 请求方式是 DELETE 代表删除数据
        HttpMethod method = HttpMethod.DELETE;

        System.out.println("请求地址：" + url);
        System.out.println("请求方式：" + method);
        System.out.println("请求Token：" + token);

        // 利用 RestUtil 请求该url
        ResponseEntity<JSONObject> result = RestUtil.request(url, method, headers, null, null, JSONObject.class);
        if (result != null && result.getBody() != null) {
            System.out.println("返回结果：" + result.getBody().toJSONString());
        } else {
            System.out.println("查询失败");
        }
    }

    /**
     * 测试用例：查询记录
     */
    @Test
    public void testQuery() {
        // 用户Token
        String token = this.getToken();
        // 请求地址
        String url = BASE_URL + "getData/"+ ONLINE_CODE ;
        // 请求 Header （用于传递Token）
        HttpHeaders headers = this.getHeaders(token);
        // 请求方式是 GET 代表获取数据
        HttpMethod method = HttpMethod.GET;

        System.out.println("请求地址：" + url);
        System.out.println("请求方式：" + method);
        System.out.println("请求Token：" + token);

        // 利用 RestUtil 请求该url
        ResponseEntity<JSONObject> result = RestUtil.request(url, method, headers, null, null, JSONObject.class);
        if (result != null && result.getBody() != null) {
            System.out.println("返回结果：" + result.getBody().toJSONString());
        } else {
            System.out.println("查询失败");
        }
    }

    private String getToken() {
        String token = JwtUtil.sign(USERNAME, PASSWORD);
        redisUtil.set(CommonConstant.PREFIX_USER_TOKEN + token, token);
        redisUtil.expire(CommonConstant.PREFIX_USER_TOKEN + token, 60);
        return token;
    }

    private HttpHeaders getHeaders(String token) {
        HttpHeaders headers = new HttpHeaders();
        String mediaType = MediaType.APPLICATION_JSON_UTF8_VALUE;
        headers.setContentType(MediaType.parseMediaType(mediaType));
        headers.set("Accept", mediaType);
        headers.set("X-Access-Token", token);
        return headers;
    }

    @Autowired
    WfDefinitionService wfDefinitionService;

    //查询可认领任务
    @Test
    @Rollback
    public void get() {
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        Task task = taskService.createTaskQuery()
                .taskCandidateUser("demo")
                .singleResult();
        System.out.println(task.getName());
//        for (Task task:list){
//            System.out.println(task.getId());
//            System.out.println(task.getName());
//        }
        taskService.claim(task.getId(),"demo");
    }

    //退还待认领任务
    @Test
    public void get02(){
        TaskService taskService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService();
        //查询候选人的代办任务
        Task task1 = taskService.createTaskQuery()
                .taskAssignee("demo")
                .singleResult();
        System.out.println(task1.getName());
        taskService.setAssignee(task1.getId(),null);
    }
    @Test
    public void get03(){
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        InputStream inputStream = repositoryService.getResourceAsStream("6afdd026-afcc-11eb-af12-70b5e8662050","file.bpmn");
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(inputStream);
        Collection<UserTask> modelElements = bpmnModelInstance.getModelElementsByType(UserTask.class);
        for (UserTask userTask:modelElements){
            System.out.println(".....");
            System.out.println(userTask.getCamundaAssignee());
            System.out.println(userTask.getCamundaCandidateUsers());
            System.out.println(userTask.getCamundaCandidateUsersList());
            System.out.println(userTask.getCamundaCandidateGroups());
            System.out.println(userTask.getCamundaCandidateGroupsList());
        }
    }
    //获取上一个节点
    @Test
    public void get04() throws IOException {
        String currentNode = "Activity_05aru06";
        List<FlowNode> currentList = new ArrayList<>();
        List<FlowNode> resultList = new ArrayList<>();
        InputStream inputStream1 = new FileInputStream("C:\\Users\\qjc\\Desktop\\Test\\diagram_1.bpmn");
        BpmnModelInstance bpmnModelInstance = Bpmn.readModelFromStream(inputStream1);
        Collection<FlowNode> list = bpmnModelInstance.getModelElementsByType(FlowNode.class);
        for (FlowNode flowNode:list){
            if (flowNode.getId().equals(currentNode)){
                currentList.add(flowNode);
            }
        }
        while ((currentList = getProNode(currentList)).size()!=0){
            resultList.addAll(currentList);
        }
        for (FlowNode flowNode:resultList){
            System.out.println(flowNode.getId()+flowNode.getName());
        }
    }
    public List<FlowNode> getProNode(List<FlowNode> flowNode){
        List<FlowNode> resultList = new ArrayList<>();
        for (FlowNode tempNode:flowNode){
            resultList.addAll(tempNode.getPreviousNodes().list());
        }
        return resultList;
    }
//    @Test
//    public void getdd() throws IOException{
//        ProcessDefinition processDefinition = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService().createProcessDefinitionQuery().processDefinitionId("Process_17yuu08:8:fe1c74a7-afd3-11eb-af12-70b5e8662050").singleResult();
//        List<String> list = new ArrayList<>();
//        list.add("223358eb-afd4-11eb-af12-70b5e8662050");
//        List<FlowNode> allProNode = UtilTools.getAllProNode(processDefinition, list);
//        for (FlowNode flowNode:allProNode){
//            System.out.println(flowNode.getName());
//        }
//    }

//    @Autowired
//    private StateListener stateListener;
//    @Test
//    public void m(){
//        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
//        System.out.println(stateListener);
//    }
}
