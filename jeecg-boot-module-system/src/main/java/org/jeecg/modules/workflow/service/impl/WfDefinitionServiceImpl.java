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
 * ?????????????????????
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
     * ????????????????????????.bpmn???.zip????????????
     * @param file ????????????(.bpmn???.zip)
     * @param userId ?????????
     * @param source ????????????
     * @param tenantId ??????id
     * @return Result<Boolean> ??????????????????
     */
    @Override
    @Transactional
    public Result<List<ProcessDefinitionDTO>> processDefinitionDeployByInputStream(MultipartFile file, String userId,
                                                               String source, String tenantId) throws Exception {
        //1.????????????
        if(StringUtils.isEmpty(file.getName()) || StringUtils.isEmpty(userId)
                || StringUtils.isEmpty(source) || StringUtils.isEmpty(tenantId)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        //???????????????
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        //????????????????????????
        Tenant tenant = identityService.createTenantQuery().tenantId(tenantId).singleResult();
        if(tenant==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_TENANT_NULL);
        }
        //???????????????????????????
        DeploymentBuilder deploymentBuilder = repositoryService.createDeployment()
                .name(userId)
                .source(source)
                .tenantId(tenantId);
        //??????????????????
        String type = FileUtil.getFileType(file.getInputStream());
        //??????????????????
        Deployment deployment = null;
        //???????????????????????????????????????
        Map<String,String> formKeyMap = new HashMap<>();
        //????????????
        if(FileType.ZIP.getValue().equals(type)){
            //???zip????????????
            deployment = deployByZip(file, formKeyMap, deploymentBuilder);
        }else if(FileType.XML.getValue().equals(type)){
            //???bpmn????????????
            deployment = deployByBpmn(file, formKeyMap, deploymentBuilder);
        }else {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_FILE);
        }
        file.getInputStream().close();
        //?????????????????????act_re_procdef???????????????????????????
        List<ProcessDefinition> processDefinition =  repositoryService.createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .list();
        if(processDefinition.isEmpty()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_NONE);
        }
        //??????????????????????????????
        judgeProcDef(processDefinition);
        //???????????????????????????????????????????????????
        addExtendInfo(processDefinition,formKeyMap);
        //????????????????????? ????????????key ?????????????????????????????????????????????
        List<ProcessDefinitionDTO> updateList;
        //??????????????????
        Context context = new Context(new ProcessDefinitionDTOStrategy());
        updateList = (List<ProcessDefinitionDTO>)context.executeMapping(processDefinition);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS,updateList);
    }

    /**
     * ???zip???????????????
     * @param file
     * @param formKeyMap
     * @param deploymentBuilder
     * @return
     * @throws Exception
     */
    private Deployment deployByZip(MultipartFile file,
                             Map<String,String> formKeyMap,
                             DeploymentBuilder deploymentBuilder) throws Exception{
        //??????????????????zip???
        ZipInputStream deployInputStream = new ZipInputStream(file.getInputStream());
        //?????????????????????zip??????????????????
        byte[] temp02 = UtilTools.toByteArray(file.getInputStream());
        InputStream te = new ByteArrayInputStream(temp02);
        ZipInputStream zipInputStream = new ZipInputStream(te);
        //???????????????zip????????????formKey??????
        ZipEntry zipEntry;
        while((zipEntry = zipInputStream.getNextEntry())!=null){
            if (zipEntry.isDirectory()){
                continue;
            }
            //????????????????????????
            byte[] temp = UtilTools.toByteArray(zipInputStream);
            //??????????????????????????????
            InputStream tempis = new ByteArrayInputStream(temp);
            UtilTools.readModelFromStream(tempis,formKeyMap);
        }
        //??????????????????
        deploymentBuilder = deploymentBuilder
                .addZipInputStream(deployInputStream);
        //??????formKey??????????????????????????????
       addForm(formKeyMap.values(),deploymentBuilder);
        //????????????
        deployInputStream.close();
        return deploymentBuilder.deploy();
    }

    /**
     * ??????formKey??????????????????????????????
     * @param formKeyList ??????????????????
     * @param deploymentBuilder ????????????
     */
    private void addForm(Collection<String> formKeyList, DeploymentBuilder deploymentBuilder) throws IOException {
        //??????formKey??????????????????????????????
        for (String formKey:formKeyList){
            if (Strings.isEmpty(formKey)){
                continue;
            }
            String formJson = HttpUtil.getFormJson(HttpUtil.getToken(), formKey);
            //?????????????????? ?????????
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(formJson.getBytes());
            deploymentBuilder.addInputStream(formKey,byteArrayInputStream);
            byteArrayInputStream.close();
        }
    }

    /**
     * ???bpmn????????????
     * @param file
     * @param formKeyMap
     * @param deploymentBuilder
     * @return
     * @throws IOException
     */
    private Deployment deployByBpmn(MultipartFile file,
                              Map<String,String> formKeyMap,
                              DeploymentBuilder deploymentBuilder) throws IOException{
        //??????bpmn????????????????????????
        UtilTools.readModelFromStream(file.getInputStream(),formKeyMap);
        //???????????????
        deploymentBuilder = deploymentBuilder
                .addInputStream(file.getName()+".bpmn",file.getInputStream());
        //??????forKey??????????????????????????????
       addForm(formKeyMap.values(),deploymentBuilder);
        //????????????
        return deploymentBuilder.deploy();
    }

    /**
     * ??????????????????????????????
     * @param processDefinition ??????????????????
     */
    private void judgeProcDef(List<ProcessDefinition> processDefinition){
        for (ProcessDefinition p:processDefinition){
            suspendOrActivateDefinitionById(p.getId(),p.isSuspended()?2:1);
            if (p.getName()==null){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DEPLOY_FAIL_NAME);
            }
            //???????????????????????????????????????
            Result judgeFlow = UtilTools.judgeFlow(p.getId());
            if (!judgeFlow.isSuccess()){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,judgeFlow.getMessage());
            }
            //????????????????????????????????????????????????
            Result judgeForm = UtilTools.judgeForm(p.getId());
            if (!judgeForm.isSuccess()){
                throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,judgeForm.getMessage());
            }
        }
    }

    /**
     * ??????????????????
     * @param processDefinition ??????????????????
     */
    private void addExtendInfo(List<ProcessDefinition> processDefinition,Map<String,String> map){
        for (ProcessDefinition p:processDefinition){
            //????????????????????????????????????????????????icon???????????????????????????
            QueryWrapper<ProcessDevelopmentVO> wrapper = new QueryWrapper<>();
            wrapper.eq("PROC_KEY_",p.getKey());
            ProcessDevelopmentVO processDevelopmentVO = workFlowExtendsMapper.selectOne(wrapper);
            if (processDevelopmentVO==null){
                processDevelopmentVO = new ProcessDevelopmentVO();
                processDevelopmentVO.setProcKey(p.getKey());
                processDevelopmentVO.setIconColour("#FF9E00");
                processDevelopmentVO.setProcDefGroup("????????????");
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
     * ????????????????????????????????????
     * @param pageNo ?????????
     * @param pageSize ???????????????
     * @param definitionName ??????????????????
     * @param definitionKey ????????????key
     * @return
     */
    @Override
    public Result<RecordsListPageVO> queryAllProcessDefinition(Integer pageNo, Integer pageSize, String definitionKey,
                                                               String definitionName){
        //1.????????????
        if(StringUtils.isEmpty(pageNo) || StringUtils.isEmpty(pageSize)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        Long total = workFlowProcessDefinitionMapper.ProcessDefinitionCount(definitionKey, definitionName);
        List<ProcessDefinitionDTO> pdVoList = workFlowProcessDefinitionMapper.ProcessDefinitionQuery(pageSize, (pageNo - 1) * pageSize, definitionKey, definitionName);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS, UtilTools.getListPage(total, pageNo, pageSize, pdVoList));
    }

    /**
     * ??????????????????:??????????????????key
     * @param startVariable ????????????
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
     * ??????????????????:??????????????????id
     * @param startVariable ????????????
     * @return
     */
    @Override
    @Transactional
    public Result<StartProcessInstanceByIdVO> startProcessInstanceById(StartVariable startVariable) {
        String processDefinitionId = startVariable.getProcessDefinitionParameter();
        String userId = startVariable.getUserId();
        String[] copy = startVariable.getCopy();
        String rowData = startVariable.getRowData();

        //1.????????????
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
        //???redis??????????????????????????????
        addRecentUse(userId,processDefinition.getKey());

        RuntimeService runtimeService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRuntimeService();
        IdentityService identityService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getIdentityService();
        //2.???????????????????????????
        User user = identityService.createUserQuery()
                .userId(userId)
                .singleResult();
        if(user==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_USER_NULL);
        }
        //3.???????????????????????????
        List<Tenant> tenantList = identityService.createTenantQuery().userMember(userId).list();
        List<String> list = new ArrayList<>();
        for (Tenant t:tenantList){
            list.add(t.getId());
        }
        Authentication authentication = new Authentication(userId, null,list);
        //4.?????????????????????
        identityService.setAuthentication(authentication);
        //5.??????????????????businessKey
        StringBuilder businessKey = new StringBuilder();
        businessKey.append(processDefinition.getName()).append(":").append(processDefinition.getVersion()).append(":")
                .append("[").append(userId).append("]").append(":").append(UUID.randomUUID());
        //6.????????????????????????????????????
        Map map = new HashMap();
        Map<String, String> allExtensions = UtilTools.getAllExtensions(processDefinitionId);
        //??????????????????
        for (Map.Entry entry:allExtensions.entrySet()){
            if (entry.getKey()==null||entry.getKey().equals("role")||entry.getKey().equals("examine")){
                continue;
            }else {
                String value = (String)entry.getValue();
                Collection<String> strings = Arrays.asList(value.split(","));
                map.put(entry.getKey(),strings);
            }
        }
        //???????????????
        map.put("copy",copy);
        //???????????????
        map.put("starter",userId);
        //?????????????????????
        if (!Strings.isEmpty(rowData)){
            Map rowDataMap = JSON.parseObject(rowData, Map.class);
            rowDataMap.forEach((key,value) -> {
                map.put(key,value);
            });
        }
        //????????????
        ProcessInstance processInstance = runtimeService.startProcessInstanceById(processDefinitionId,businessKey.toString(),map);

        if(processInstance==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_START_FAIL);
        }
        StartProcessInstanceByIdVO startProcessInstanceByIdVO = new StartProcessInstanceByIdVO();
        startProcessInstanceByIdVO.setProcdefId(processDefinitionId);
        startProcessInstanceByIdVO.setProcessInstanceId(processInstance.getId());
        //?????????????????????????????????????????????
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
     * ???????????????????????????????????????
     * @param startVariables ????????????
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
     * ????????????????????????????????????
     * @param userId
     * @param recentProcKey
     * @return
     */
    @Transactional
    public void addRecentUse(String userId,String recentProcKey){
        stringRedisTemplate.expire(userId,60*12*30, TimeUnit.MINUTES);
        //????????????????????????id????????????
        List<String> range = stringRedisTemplate.opsForList().range(userId, 0, -1);
        long index = -1;
        for (int i = 0;i<range.size();i++){
            if (range.get(i).equals(recentProcKey)){
                index = i;
            }
        }
        //?????????????????????????????????id???????????????????????????
        if(index!=-1){
            stringRedisTemplate.opsForList().remove(userId,0,recentProcKey);
            stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
            return;
        }
        //??????????????????8
        if (stringRedisTemplate.opsForList().size(userId)>=8){
            stringRedisTemplate.opsForList().rightPop(userId);
            stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
            return;
        }
        stringRedisTemplate.opsForList().leftPush(userId,recentProcKey);
        return;
    }

    /**
     * ????????????????????????????????????
     * @param processDefinitionIds ??????????????????
     * @param isCascade ????????????
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
     * ????????????????????????key
     * @param processDefinitionKey ????????????key
     * @param isCascade ????????????
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
        return Result.OK("????????????");
    }

    /**
     * ????????????????????????????????????id
     * @param processDefinitionId ????????????id
     * @param isCascade ????????????
     * @return
     */
    @Override
    @Transactional
    public Result deleteProcessDefinitionById(String processDefinitionId, boolean isCascade) {
        //1.????????????
        if(StringUtils.isEmpty(processDefinitionId)||StringUtils.isEmpty(isCascade)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }

        HistoryService historyService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getHistoryService();
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //??????????????????????????????????????????
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionId(processDefinitionId).list();
        if (isCascade==false&&!list.isEmpty()){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_DELETE_FAIL_CASCADE);
        }
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery();
        //2.????????????????????????
        ProcessDefinition processDefinition = processDefinitionQuery.processDefinitionId(processDefinitionId).singleResult();
        if(processDefinition==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PROC_NULL);
        }

        String deploymentId = processDefinition.getDeploymentId();

        //3.????????????
        /*
        ???????????? id ???????????????aProcessDefinitionId?????????????????????????????????????????????????????????
        ascade=true&skipCustomListeners=true&skipIoMappings=true*/
        repositoryService.deleteProcessDefinition(processDefinitionId,isCascade,true,true);

        //??????????????????
        deleteProcResourceMapper.deleteResource(deploymentId);
        //??????????????????
        deleteProcResourceMapper.deleteDeployment(deploymentId);

        //4.??????????????????
        long count = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinition.getKey()).count();
        if (count==0){
            QueryWrapper queryWrapper = new QueryWrapper();
            queryWrapper.eq("proc_key_",processDefinition.getKey());
            workFlowExtendsMapper.delete(queryWrapper);
        }
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_DELETE_SUCCESS);
    }

    /**
     * ??????????????????/??????
     * @param processDefinitionKey ????????????key
     * @param state ???????????? 1:?????? 2?????????
     * @return
     */
    @Override
    @Transactional
    public Result<String> suspendOrActivateDefinition(String processDefinitionKey,Integer state) {
        //1.????????????
        if(StringUtils.isEmpty(processDefinitionKey)||(state!=1&&state!=2)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //2.????????????
        if(state==1){
            //3.1 ????????????
            repositoryService.suspendProcessDefinitionByKey(processDefinitionKey);
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
        }else{
            //3.2 ????????????
            repositoryService.activateProcessDefinitionByKey(processDefinitionKey);
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
        }
    }
    /**
     * ??????????????????/????????????????????????id
     * @param processDefinitionId ????????????id
     * @param state ???????????? 1:?????? 2?????????
     * @return
     */
    @Override
    @Transactional
    public Result suspendOrActivateDefinitionById(String processDefinitionId, Integer state) {
        //1.????????????
        if(StringUtils.isEmpty(processDefinitionId)||(state!=1&&state!=2)){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();
        //2.????????????
        if(state==1){
            //3.1 ????????????
            if(!processDefinition.isSuspended()){
                repositoryService.suspendProcessDefinitionById(processDefinitionId);
                return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
            }
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_SUSPENSION_FAIL);
        }else{
            //3.2 ????????????
            if(processDefinition.isSuspended()){
                repositoryService.activateProcessDefinitionById(processDefinitionId);
                return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_SUCCESS);
            }
            return Result.OK(WorkflowMessageConstant.WORK_FLOW_ACTIVE_FAIL);
        }
    }

    /**
     * ??????bpmn????????????
     * @param definitionIds ????????????id??????
     * @param outputStream ?????????
     */
    @Override
    public void getResourceAsStream(String[] definitionIds, OutputStream outputStream) throws IOException{
        if(definitionIds.length==0){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PARAMETER_NULL);
        }
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        List<ProcessDefinition> pdList =  repositoryService.createProcessDefinitionQuery()
                .processDefinitionIdIn(definitionIds).list();
        //??????zip?????????
        ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream);
        //?????? ??????????????????
        for(ProcessDefinition p:pdList){
            //????????????bpmn?????????
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
        //??????zip???
        zipOutputStream.close();
    }

    /**
     * ????????????????????????key
     * @param processDefinitionKey ????????????key
     * @param pageNO ?????????
     * @param pageSize ???????????????
     * @return
     */
    @Override
    public Result<RecordsListPageVO> queryVersionByKey(String processDefinitionKey,  Integer pageNO, Integer pageSize) {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey);
        Long total = processDefinitionQuery.count();
        //1.???????????????????????????
        List<ProcessDefinition> list = processDefinitionQuery
                .orderByProcessDefinitionVersion()
                .desc()
                .listPage((pageNO-1)*pageSize,pageSize);
        //?????????????????? List<ProcessDefinition> ??? List<DefinitionVersionVO>
        Context context = new Context(new DefinitionVersionVoStarttegy());
        //????????????
        List<DefinitionVersionVO> listVo = (List<DefinitionVersionVO>)context.executeMapping(list);
        //4.??????????????????
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_QUERY_SUCCESS,UtilTools.getListPage(total, pageNO, pageSize, listVo));
    }

    /**
     * ??????????????????????????????id
     * @param outputStream ?????????
     * @param definitionId ????????????id
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
     * ??????????????????Key??????????????????????????????????????????id
     * @param procedefKey : ????????????Key
     * @return ???????????????????????????????????????id
     */
    @Override
    public Map<Integer,String> queryVersionByProcedefKey(String procedefKey){
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        Map<Integer,String> verMap = new HashMap<>();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().processDefinitionKey(procedefKey).list();
        for(ProcessDefinition processDefinition : processDefinitions){
            verMap.put(processDefinition.getVersion(),processDefinition.getId());
        }

        //??????

        return verMap;
    }

    /**
     * ????????????????????????Key???????????????
     * @return : ??????????????????Key ?????????????????? ???????????? Name
     */
    @Override
    public Map<String,String> queryAllProcedefKey() {
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();

        //????????????HashMap??????????????? ????????????Key ??? ???????????? Name
        Map<String,String> map = new HashMap<>();
        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery().list();

        //?????????????????????Key??????????????????
        for(ProcessDefinition processDefinition : processDefinitions){
            if(!map.keySet().contains(processDefinition.getKey())){
                map.put(processDefinition.getKey(),processDefinition.getName());
            }
        }
        return map;
    }

    /**
     * ????????????-??????????????????
     * @param processDefinitionId ????????????id
     * @return
     */
    @Override
    @Transactional
    public Result<String> copyProcessDefinition(String processDefinitionId){
        //???????????????
        RepositoryService repositoryService = ProcessEngineUtil.PROCESS_ENGINE_INSTANCE.getRepositoryService();
        //??????????????????
        BpmnModelInstance bpmnModelInstance = repositoryService.getBpmnModelInstance(processDefinitionId);
        Collection<Process> modelElementsByType = bpmnModelInstance.getModelElementsByType(Process.class);
        for (Process p:modelElementsByType){
            p.setId("Copy_"+System.currentTimeMillis());
        }
        //????????????
        Deployment deployment = repositoryService.createDeployment()
                .addModelInstance("copy.bpmn", bpmnModelInstance.clone())
                .deploy();
        //???????????????
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deployment.getId())
                .singleResult();
        if (processDefinition==null){
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR,WorkflowMessageConstant.WORK_FLOW_PROC_COPY_FAIL);
        }
        //????????????
        suspendOrActivateDefinitionById(processDefinition.getId(),1);
        return Result.OK(WorkflowMessageConstant.WORK_FLOW_PROC_COPY_SUCCESS,processDefinition.getId());
    }
}
