package org.jeecg.modules.workflow.service;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.workflow.entity.vo.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

/**
 * 流程定义业务层
 * @author qjc
 * @date 2021-3-18 9.00
 */
public interface WfDefinitionService {

    /**
     * 流程定义部署：以.bpmn或.zip文件部署
     * @param file 资源文件(.bpmn或.zip)
     * @param userId 创建人
     * @param source 部署来源
     * @param tenantId 租户id
     * @return Result<Boolean> 是否部署成功
     */

    Result<List<ProcessDefinitionDTO>> processDefinitionDeployByInputStream(MultipartFile file, String userId,
                                                                                   String source, String tenantId
            ) throws Exception;

    /**
     * 查询所有最新版本流程定义
     * @param pageNo 起始页
     * @param pageSize 每页多少条
     * @param definitionName 流程定义名称
     * @param definitionKey 流程定义key
     * @return
     */
    Result<RecordsListPageVO> queryAllProcessDefinition(Integer pageNo, Integer pageSize, String definitionKey, String definitionName);

    /**
     * 启动流程实例:按流程定义的key
     * @param startVariable 启动参数
     */
    Result<StartProcessInstanceByIdVO> startProcessInstanceByKey(StartVariable startVariable);

    /**
     * 启动流程实例:按流程定义的id
     * @param startVariable
     * @return
     */
    Result<StartProcessInstanceByIdVO> startProcessInstanceById(StartVariable startVariable);

    /**
     * 批量启动某一流程
     * @param startVariables 启动参数
     * @return
     */
    Result startProcessInstanceByArray(StartVariables startVariables);


    /**
     * 删除流程定义：按key
     * @param processDefinitionKey 流程定义key
     * @param isCascade 是否级联
     * @return
     */
    Result deleteProcessDefinitionByKey(String processDefinitionKey, boolean isCascade);

    /**
     * 删除看流程定义：批量删除
     * @param processDefinitionIds 流程定义列表
     * @param isCascade 是否级联
     * @return
     */
    Result deleteProcessDefinitionByArray(String[] processDefinitionIds,boolean isCascade);

    /**
     * 删除流程定义：按流程定义id
     * @param processDefinitionId 流程定义id
     * @param isCascade 是否级联
     * @return
     */
    Result deleteProcessDefinitionById(String processDefinitionId,boolean isCascade);

    /**
     * 流程定义挂起/激活:按流程定义key
     * @param processDefinitionKey 流程定义key
     * @param state 当前状态 1:激活 2：挂起
     * @return
     */
    Result suspendOrActivateDefinition(String processDefinitionKey,Integer state);

    /**
     * 流程定义挂起/激活：按流程定义id
     * @param processDefinitionId 流程定义id
     * @param state 当前状态 1:激活 2：挂起
     * @return
     */
    Result suspendOrActivateDefinitionById(String processDefinitionId,Integer state);

    /**
     * 获取bpmn资源文件
     * @param definitionIds 流程定义id列表
     * @param outputStream 输出流
     */
    void getResourceAsStream(String[] definitionIds, OutputStream outputStream) throws IOException;

    /**
     * 查询所有版本：按key
     * @param processDefinitionKey 流程定义key
     * @param pageNO 当前页
     * @param pageSize 每页多少条
     * @return
     */
    Result<RecordsListPageVO> queryVersionByKey(String processDefinitionKey, Integer pageNO, Integer pageSize);

    /**
     * 获取资源文件：按定义id
     * @param outputStream 输出流
     * @param definitionId 流程定义id
     * @throws IOException
     */
    void getResourceAsUrl(OutputStream outputStream,String definitionId) throws IOException;

    /**
     * 根据流程定义Key获取所有版本号和对应流程定义id
     * @param procedefKey : 流程定义Key
     * @return ：所有版本号和对应流程定义id
     */
    Map<Integer,String> queryVersionByProcedefKey(String procedefKey);

    /**
     * 获取所有流程定义Key（不重复）
     * @return : 所有流程定义Key （不重复）和 流程定义 Name
     */
    Map<String,String> queryAllProcedefKey();

    /**
     * 应用复制——流程定义复制
     * @return
     */
    Result<String> copyProcessDefinition(String processDefinitionId);
}
