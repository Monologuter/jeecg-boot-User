package org.jeecg.modules.workflow.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.util.Strings;
import org.camunda.bpm.engine.*;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.identity.Tenant;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.impl.identity.Authentication;
import org.camunda.bpm.engine.repository.*;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.common.FileType;
import org.jeecg.modules.workflow.common.WorkflowMessageConstant;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.mapper.DeleteProcResourceMapper;
import org.jeecg.modules.workflow.mapper.WorkFlowExtendsMapper;
import org.jeecg.modules.workflow.mapper.WorkFlowProcessDefinitionMapper;
import org.jeecg.modules.workflow.service.WfDefinitionService;
import org.jeecg.modules.workflow.strategy.Context;
import org.jeecg.modules.workflow.strategy.impl.DefinitionVersionVoStarttegy;
import org.jeecg.modules.workflow.strategy.impl.ProcessDefinitionDTOStrategy;
import org.jeecg.modules.workflow.utils.FileUtil;
import org.jeecg.modules.workflow.utils.HttpUtil;
import org.jeecg.modules.workflow.utils.ProcessEngineUtil;
import org.jeecg.modules.workflow.utils.UtilTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 流程定义业务层
 * @author qjc
 * @date 2021-3-18 9.00
 */
@Service
@Slf4j
public class WfDefinitionServiceImpl implements WfDefinitionService {

    @Autowired
    private WorkFlowProcessDefinitionMapper workFlowProcessDefinitionMapper;

    @Autowired
    private WorkFlowExtendsMapper workFlowExtendsMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeleteProcResourceMapper deleteProcResourceMapper;

    /**
     * 流程定义部署：以.bpmn或.zip文件部署
     * @param file 资源文件(.bpmn或.zip)
     * @param userId 创建人
     * @param source 部署来源
     * @param tenantId 租户id
     * @return Result<Boolean> 是否部署成功
     */
    @Override
    @Transactional
    public Result<List<ProcessDefinitionDTO>> processDefinitionDeployByInputStream(MultipartFile file, String userId,
                                                               String source, String tenantId) throws Exception {
        //1.入参校验
        if(StringUtils.isEmpty(file.getName()) || StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(source) || StringUtils.isEmpty(tenantId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        //获取服务类
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        //判断租户是否存在
        Tenant tenant = identityService.createTenantQuery().tenantId(tenantId).singleResult();
        if(tenant==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_TENANT_NULL);
        }
        //创建部署构造器对象
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name(userId)
                .source(source)
                .tenantId(tenantId);
        //获取文件类型
        String type = FileUtil.getFileType(file.getInputStream());
        //创建部署对象
        Deployment deployment = null;
        //创建流程定义与外置表单关联
        Map<String,String> formKeyMap = new HashMap<>();
        //部署文件
        if(FileType.ZIP.getValue().equals(type)){
            //以zip形式部署
            deployment = deployByZip(file, formKeyMap, deploymentBuilder);
        }else if(FileType.XML.getValue().equals(type)){
            //以bpmn形式部署
            deployment = deployByBpmn(file, formKeyMap, deploymentBuilder);
        }else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FILE);
        }
        file.getInputStream().close();
        //部署完成后查询act_re_procdef表中是否插入了信息
        List<ProcessDefinition> processDefinition =  repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list();
        if(processDefinition.isEmpty()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_NONE);
        }
        //判断流程定义是否合理
        judgeProcDef(processDefinition);
        //添加扩展信息（图标、描述、分类等）
        addExtendInfo(processDefinition,formKeyMap);
        //创建被修改了的 流程定义key 列表，用于“新版本待查看”功能
        List<ProcessDefinitionDTO> updateList;
        //加载映射策略
        Context context = new Context(new ProcessDefinitionDTOStrategy());
        updateList = (List<ProcessDefinitionDTO>)context.executeMapping(processDefinition);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS,updateList);
    }

    /**
     * 以zip报形式部署
     * @param file
     * @param formKeyMap
     * @param deploymentBuilder
     * @return
     * @throws Exception
     */
    private Deployment deployByZip(MultipartFile file,
                             Map<String,String> formKeyMap,
                             DeploymentBuilder deploymentBuilder) throws Exception{
        //创建要部署的zip流
        ZipInputStream deployInputStream = new ZipInputStream(file.getInputStream());
        //拷贝一个相同的zip流，用于解析
        byte[] temp02 = UtilTools.toByteArray(file.getInputStream());
        InputStream te = new ByteArrayInputStream(temp02);
        ZipInputStream zipInputStream = new ZipInputStream(te);
        //解析拷贝的zip流并填充formKey列表
        ZipEntry zipEntry;
        while((zipEntry = zipInputStream.getNextEntry())!=null){
            if (zipEntry.isDirectory()){
                continue;
            }
            //原始流抽取出数组
            byte[] temp = UtilTools.toByteArray(zipInputStream);
            //用数组创建一个临时流
            InputStream tempis = new ByteArrayInputStream(temp);
            UtilTools.readModelFromStream(tempis,formKeyMap);
        }
        //增加部署信息
        deploymentBuilder = deploymentBuilder
                .addZipInputStream(deployInputStream);
        //根据formKey列表获取外置表单资源
       addForm(formKeyMap.values(),deploymentBuilder);
        //执行部署
        deployInputStream.close();
        return deploymentBuilder.deploy();
    }

    /**
     * 根据formKey列表获取外置表单资源
     * @param formKeyList 外置表单列表
     * @param deploymentBuilder 部署对象
     */
    private void addForm(Collection<String> formKeyList, DeploymentBuilder deploymentBuilder) throws IOException {
        //根据formKey列表获取外置表单资源
        for (String formKey:formKeyList){
            if (Strings.isEmpty(formKey)){
                continue;
            }
            String formJson = HttpUtil.getFormJson(HttpUtil.getToken(), formKey);
            //添加外置表单 流信息
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(formJson.getBytes());
            deploymentBuilder.addInputStream(formKey,byteArrayInputStream);
            byteArrayInputStream.close();
        }
    }

    /**
     * 以bpmn形式部署
     * @param file
     * @param formKeyMap
     * @param deploymentBuilder
     * @return
     * @throws IOException
     */
    private Deployment deployByBpmn(MultipartFile file,
                              Map<String,String> formKeyMap,
                              DeploymentBuilder deploymentBuilder) throws IOException{
        //解析bpmn填充外置表单列表
        UtilTools.readModelFromStream(file.getInputStream(),formKeyMap);
        //添加部署流
        deploymentBuilder = deploymentBuilder
                .addInputStream(file.getName()+".bpmn",file.getInputStream());
        //遍历forKey列表获取外置表单资源
       addForm(formKeyMap.values(),deploymentBuilder);
        //执行部署
        return deploymentBuilder.deploy();
    }

    /**
     * 判断流程定义是否合理
     * @param processDefinition 流程定义列表
     */
    private void judgeProcDef(List<ProcessDefinition> processDefinition){
        for (ProcessDefinition p:processDefinition){
            suspendOrActivateDefinitionById(p.getId(),p.isSuspended()?2:1);
            if (p.getName()==null){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_NAME);
            }
            //判断流程定义过程是否能走通
            Result judgeFlow = UtilTools.judgeFlow(p.getId());
            if (!judgeFlow.isSuccess()){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,judgeFlow.getMessage());
            }
            //判断流程定义的用户节点是否有参数
            Result judgeForm = UtilTools.judgeForm(p.getId());
            if (!judgeForm.isSuccess()){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,judgeForm.getMessage());
            }
        }
    }

    /**
     * 添加附属信息
     * @param processDefinition 流程定义列表
     */
    private void addExtendInfo(List<ProcessDefinition> processDefinition,Map<String,String> map){
        for (ProcessDefinition p:processDefinition){
            //为每条流程定义添加默认配置信息：icon、颜色、描述、分类
            QueryWrapper<ProcessDevelopmentVO> wrapper = new QueryWrapper<>();
            wrapper.eq("PROC_KEY_",p.getKey());
            ProcessDevelopmentVO processDevelopmentVO = workFlowExtendsMapper.selectOne(wrapper);
            if (processDevelopmentVO==null){
                processDevelopmentVO = new ProcessDevelopmentVO();
                processDevelopmentVO.setProcKey(p.getKey());
                processDevelopmentVO.setIconColour("#FF9E00");
                processDevelopmentVO.setProcDefGroup("关键业务");
                processDevelopmentVO.setProcDesc("");
                processDevelopmentVO.setIconStyle("icon-travel");
                processDevelopmentVO.setProcFormKey(map.get(p.getKey()));
                processDevelopmentVO.setProcName(p.getName());
                workFlowExtendsMapper.insert(processDevelopmentVO);
            }else {
                processDevelopmentVO.setProcName(p.getName());
                processDevelopmentVO.setProcFormKey(map.get(p.getKey()));
                workFlowExtendsMapper.updateById(processDevelopmentVO);
            }
        }
    }

    /**
     * 查询所有最新版本流程定义
     * @param pageNo 起始页
     * @param pageSize 每页多少条
     * @param definitionName 流程定义名称
     * @param definitionKey 流程定义key
     * @return
     */
    @Override
    public Result<RecordsListPageVO> queryAllProcessDefinition(Integer pageNo, Integer pageSize, String definitionKey,
                                                               String definitionName){
        //1.入参校验
        if(StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        Long total = workFlowProcessDefinitionMapper.ProcessDefinitionCount(definitionKey, definitionName);
        List<ProcessDefinitionDTO> pdVoList = workFlowProcessDefinitionMapper.ProcessDefinitionQuery(pageSize, (pageNo - 1) * pageSize, definitionKey, definitionName);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS, UtilTools.getListPage(total, pageNo, pageSize, pdVoList));
    }

    /**
     * 启动流程实例:按流程定义的key
     * @param startVariable 启动参数
     */
    @Override
    @Transactional
    public Result<StartProcessInstanceByIdVO> startProcessInstanceByKey(StartVariable startVariable) {
        ProcessDefinition processDefinition = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService()
                .createProcessDefinitionQuery()
                .processDefinitionKey(startVariable.getProcessDefinitionParameter())
                .latestVersion().singleResult();
        startVariable.setProcessDefinitionParameter(processDefinition.getId());
        return startProcessInstanceById(startVariable);
    }

    /**
     * 启动流程实例:按流程定义的id
     * @param startVariable 启动参数
     * @return
     */
    @Override
    @Transactional
    public Result<StartProcessInstanceByIdVO> startProcessInstanceById(StartVariable startVariable) {
        String processDefinitionId = startVariable.getProcessDefinitionParameter();
        String userId = startVariable.getUserId();
        String[] copy = startVariable.getCopy();
        String rowData = startVariable.getRowData();

        //1.入参校验
        if(StringUtils.isEmpty(processDefinitionId) || StringUtils.isEmpty(userId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL+processDefinitionId+userId);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        if (processDefinition.isSuspended()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_SUSPENSION);
        }
        //向redis增加【最近使用】记录
        addRecentUse(userId,processDefinition.getKey());

        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //2.检验发起人是否存在
        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();
        if(user==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_USER_NULL);
        }
        //3.获取发起人租户信息
        List<Tenant> tenantList = identityService.createTenantQuery().userMember(userId).list();
        List<String> list = new ArrayList<>();
        for (Tenant t:tenantList){
            list.add(t.getId());
        }
        Authentication authentication = new Authentication(userId, null,list);
        //4.设置当前发起人
        identityService.setAuthentication(authentication);
        //5.生成业务标题businessKey
        StringBuilder businessKey = new StringBuilder();
        businessKey.append(processDefinition.getName()).append(":").append(processDefinition.getVersion()).append(":")
                .append("[").append(userId).append("]").append(":").append(UUID.randomUUID());
        //6.设置抄送人并启动流程实例
        Map map = new HashMap();
        Map<String, String> allExtensions = UtilTools.getAllExtensions(processDefinitionId);
        //存入会签属性
        for (Map.Entry entry:allExtensions.entrySet()){
            if (entry.getKey()==null||entry.getKey().equals("role")||entry.getKey().equals("examine")){
                continue;
            }else {
                String value = (String)entry.getValue();
                Collection<String> strings = Arrays.asList(value.split(","));
                map.put(entry.getKey(),strings);
            }
        }
        //存入抄送人
        map.put("copy",copy);
        //存入发起人
        map.put("starter",userId);
        //存入初始化参数
        if (!Strings.isEmpty(rowData)){
            Map rowDataMap = JSON.parseObject(rowData, Map.class);
            rowDataMap.forEach((key,value) -> {
                map.put(key,value);
            });
        }
        //启动流程
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId,businessKey.toString(),map);

        if(processInstance==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_START_FAIL);
        }
        StartProcessInstanceByIdVO startProcessInstanceByIdVO = new StartProcessInstanceByIdVO();
        startProcessInstanceByIdVO.setProcdefId(processDefinitionId);
        startProcessInstanceByIdVO.setProcessInstanceId(processInstance.getId());
        //现阶段只考虑开始后一个申请节点
        List<Task> taskList = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getTaskService().createTaskQuery()
                .processInstanceId(processInstance.getId())
                .active()
                .list();
        if (taskList.size()==1){
            startProcessInstanceByIdVO.setTaskId(taskList.get(0).getId());
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS,startProcessInstanceByIdVO);
    }

    /**
     * 以不同参数批量启动某一流程
     * @param startVariables 启动参数
     * @return
     */
    @Override
    @Transactional
    public Result startProcessInstanceByArray(StartVariables startVariables){
        List<String> rowDatas = startVariables.getRowDatas();
        if (rowDatas.isEmpty()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_START_FAIL_PARAMETER);
        }
        String processDefinitionKey = startVariables.getProcessDefinitionParameter();
        String userId = startVariables.getUserId();
        String[] copy = startVariables.getCopy();

        for (String rowData:rowDatas){
            StartVariable startVariable = new StartVariable();
            startVariable.setProcessDefinitionParameter(processDefinitionKey);
            startVariable.setUserId(userId);
            startVariable.setCopy(copy);
            startVariable.setRowData(rowData);
            startProcessInstanceByKey(startVariable);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
    }

    /**
     * 增加【最近使用】流程定义
     * @param userId
     * @param recentProcKey
     * @return
     */
    @Transactional
    public void addRecentUse(String userId,String recentProcKey){
        stringRedisTemplate.expire(userId,60*12*30, TimeUnit.MINUTES);
        //如果这个流程定义id已经存在
        List<String> range = stringRedisTemplate.opsForList().range(userId, 0, -1);
        long index = -1;
        for (int i = 0;i<range.size();i++){
            if (range.get(i).equals(recentProcKey)){
                index = i;
            }
        }
        //如果已存在这个流程定义id，将这个值移到头部
        if(index!=-1){
            stringRedisTemplate.opsForList().remove(userId,0,recentProcKey);
            stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
            return;
        }
        //如果长度超过8
        if (stringRedisTemplate.opsForList().size(userId)>=8){
            stringRedisTemplate.opsForList().rightPop(userId);
            stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
            return;
        }
        stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
        return;
    }

    /**
     * 删除看流程定义：批量删除
     * @param processDefinitionIds 流程定义列表
     * @param isCascade 是否级联
     * @return
     */
    @Override
    @Transactional
    public Result deleteProcessDefinitionByArray(String[] processDefinitionIds, boolean isCascade) {
        Result result = new Result();
        for(String processDefinitionId:processDefinitionIds){
            Result tempResult = deleteProcessDefinitionById(processDefinitionId, isCascade);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }

    /**
     * 删除流程定义：按key
     * @param processDefinitionKey 流程定义key
     * @param isCascade 是否级联
     * @return
     */
    @Override
    @Transactional
    public Result deleteProcessDefinitionByKey(String processDefinitionKey, boolean isCascade) {
        List<String> collect = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService().createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey)
                .list().stream()
                .map(processDefinition -> processDefinition.getId())
                .collect(Collectors.toList());
        if (collect.size()==0){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PROC_NULL);
        }
        for (String tempId: collect){
            deleteProcessDefinitionById(tempId,isCascade);
        }
        return Result.OK("删除成功");
    }

    /**
     * 删除流程定义：按流程定义id
     * @param processDefinitionId 流程定义id
     * @param isCascade 是否级联
     * @return
     */
    @Override
    @Transactional
    public Result deleteProcessDefinitionById(String processDefinitionId, boolean isCascade) {
        //1.入参校验
        if(StringUtils.isEmpty(processDefinitionId)||StringUtils.isEmpty(isCascade)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }

        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //查询此流程定义是否发起过实例
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).list();
        if (isCascade==false&&!list.isEmpty()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DELETE_FAIL_CASCADE);
        }
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //2.查此定义是否存在
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionId(processDefinitionId).singleResult();
        if(processDefinition==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PROC_NULL);
        }

        String deploymentId = processDefinition.getDeploymentId();

        //3.执行删除
        /*
        删除带有 id 的流程定义aProcessDefinitionId并级联删除流程实例、历史流程实例和作业
        ascade=true&skipCustomListeners=true&skipIoMappings=true*/
        repositoryService.deleteProcessDefinition(processDefinitionId,isCascade,true,true);

        //删除资源信息
        deleteProcResourceMapper.deleteResource(deploymentId);
        //删除部署信息
        deleteProcResourceMapper.deleteDeployment(deploymentId);

        //4.删除扩展属性
        long count = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinition.getKey()).count();
        if (count==0){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("proc_key_",processDefinition.getKey());
            workFlowExtendsMapper.delete(queryWrapper);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }

    /**
     * 流程定义挂起/激活
     * @param processDefinitionKey 流程定义key
     * @param state 当前状态 1:激活 2：挂起
     * @return
     */
    @Override
    @Transactional
    public Result<String> suspendOrActivateDefinition(String processDefinitionKey,Integer state) {
        //1.入参校验
        if(StringUtils.isEmpty(processDefinitionKey)||(state!=1&&state!=2)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //2.状态判断
        if(state==1){
            //3.1 执行挂起
            repositoryService.suspendProcessDefinitionByKey(processDefinitionKey);
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
        }else{
            //3.2 执行激活
            repositoryService.activateProcessDefinitionByKey(processDefinitionKey);
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
        }
    }
    /**
     * 流程定义挂起/激活：按流程定义id
     * @param processDefinitionId 流程定义id
     * @param state 当前状态 1:激活 2：挂起
     * @return
     */
    @Override
    @Transactional
    public Result suspendOrActivateDefinitionById(String processDefinitionId, Integer state) {
        //1.入参校验
        if(StringUtils.isEmpty(processDefinitionId)||(state!=1&&state!=2)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        //2.状态判断
        if(state==1){
            //3.1 执行挂起
            if(!processDefinition.isSuspended()){
                repositoryService.suspendProcessDefinitionById(processDefinitionId);
                return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
            }
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_SUSPENSION_FAIL);
        }else{
            //3.2 执行激活
            if(processDefinition.isSuspended()){
                repositoryService.activateProcessDefinitionById(processDefinitionId);
                return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
            }
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_FAIL);
        }
    }

    /**
     * 获取bpmn资源文件
     * @param definitionIds 流程定义id列表
     * @param outputStream 输出流
     */
    @Override
    public void getResourceAsStream(String[] definitionIds, OutputStream outputStream) throws IOException{
        if(definitionIds.length==0){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        List<ProcessDefinition> pdList =  repositoryService.createProcessDefinitionQuery()
                .processDefinitionIdIn(definitionIds).list();
        //创建zip输出流
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        //遍历 流程定义列表
        for(ProcessDefinition p:pdList){
            //获取单个bpmn输入流
            InputStream inputStream = repositoryService.getResourceAsStream(p.getDeploymentId(),p.getResourceName());
            int temp = 0;
            byte[] buffer = new byte[1024];
            ZipEntry zipEntry = new ZipEntry(p.getId()+".bpmn");
            zipOutputStream.putNextEntry(zipEntry);
            while ((temp = inputStream.read(buffer))!=-1){
                zipOutputStream.write(buffer,0,temp);
            }
            inputStream.close();
        }
        //关闭zip流
        zipOutputStream.close();
    }

    /**
     * 查询所有版本：按key
     * @param processDefinitionKey 流程定义key
     * @param pageNO 当前页
     * @param pageSize 每页多少条
     * @return
     */
    @Override
    public Result<RecordsListPageVO> queryVersionByKey(String processDefinitionKey,  Integer pageNO, Integer pageSize) {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey);
        Long total = processDefinitionQuery.count();
        //1.获取原生实体类列表
        List<ProcessDefinition> list = processDefinitionQuery
                .orderByProcessDefinitionVersion()
                .desc()
                .listPage((pageNO-1)*pageSize,pageSize);
        //加载映射策略 List<ProcessDefinition> 转 List<DefinitionVersionVO>
        Context context = new Context(new DefinitionVersionVoStarttegy());
        //执行映射
        List<DefinitionVersionVO> listVo = (List<DefinitionVersionVO>)context.executeMapping(list);
        //4.返回视图数据
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,UtilTools.getListPage(total, pageNO, pageSize, listVo));
    }

    /**
     * 获取资源文件：按定义id
     * @param outputStream 输出流
     * @param definitionId 流程定义id
     * @throws IOException
     */
    @Override
    public void getResourceAsUrl(OutputStream outputStream, String definitionId) throws IOException {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(definitionId)
                .singleResult();
        InputStream inputStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId()
                ,processDefinition.getResourceName());
        IOUtils.copy(inputStream,outputStream);
        inputStream.close();
        outputStream.close();
    }

    /**
     * 根据流程定义Key获取所有版本号和对应流程定义id
     * @param procedefKey : 流程定义Key
     * @return ：所有版本号和对应流程定义id
     */
    @Override
    public Map<Integer,String> queryVersionByProcedefKey(String procedefKey){
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        Map<Integer,String> verMap = new HashMap<>();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionKey(procedefKey).list();
        for(ProcessDefinition processDefinition : processDefinitions){
            verMap.put(processDefinition.getVersion(),processDefinition.getId());
        }

        //排序

        return verMap;
    }

    /**
     * 获取所有流程定义Key（不重复）
     * @return : 所有流程定义Key （不重复）和 流程定义 Name
     */
    @Override
    public Map<String,String> queryAllProcedefKey() {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //创建一个HashMap集合来存储 流程定义Key 和 流程定义 Name
        Map<String,String> map = new HashMap<>();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        //查询（流程定义Key不可以重复）
        for(ProcessDefinition processDefinition : processDefinitions){
            if(!map.keySet().contains(processDefinition.getKey())){
                map.put(processDefinition.getKey(),processDefinition.getName());
            }
        }
        return map;
    }

    /**
     * 应用复制-流程定义复制
     * @param processDefinitionId 流程定义id
     * @return
     */
    @Override
    @Transactional
    public Result<String> copyProcessDefinition(String processDefinitionId){
        //获取服务类
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //获取定义模型
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<Process> modelElementsByType = bpmnModelInstance.getModelElementsByType(Process.class);
        for (Process p:modelElementsByType){
            p.setId("Copy_"+System.currentTimeMillis());
        }
        //定义部署
        Deployment deployment = repositoryService.createDeployment()
                .addModelInstance("copy.bpmn", bpmnModelInstance.clone())
                .deploy();
        //获取新定义
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        if (processDefinition==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PROC_COPY_FAIL);
        }
        //默认挂起
        suspendOrActivateDefinitionById(processDefinition.getId(),1);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_PROC_COPY_SUCCESS,processDefinition.getId());
    }
}
