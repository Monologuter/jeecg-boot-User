package org.jeecg.modules.workflow.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.common.api.vo.Result;
import org.jeecg.modules.form.controller.FormDesignerController;
import org.jeecg.modules.workflow.entity.vo.*;
import org.jeecg.modules.workflow.service.WfDefinitionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author qjc
 * 流程定义前端控制器
 * @date 2021-3-18 9.00
 */
@RestController
@RequestMapping("workflow/workflow-definition")
@Api(tags="工作流")
@Slf4j
public class WfDefinitionController {
    @Autowired
    private WfDefinitionService wfDefinitionService;
    @Autowired
    private FormDesignerController formDesignerController;
    /**
     * 流程定义部署：文件方式（.bpmn或.zip文件）
     * @param file 文件
     * @param userId 创建人
     * @param source 来源
     * @param tenantId 租户id
     * @return
     */
    @PostMapping("/deploy-stream")
    @ApiOperation(value = "流程定义部署(.bpmn或.zip)",notes = "流程定义部署(.bpmn或.zip)")
    public Result<List<ProcessDefinitionDTO>> processDefinitionDeployByInputStream(@ApiParam(value = ".bpmn或.zip文件",required = false)
                                                            MultipartFile file,
                                                                                   @ApiParam(value = "创建人",required = true)
                                                               @RequestParam(required = true) String userId,
                                                                                   @ApiParam(value = "来源",required = true)
                                                           @RequestParam(required = true) String source,
                                                                                   @ApiParam(value = "租户id",required = true)
                                                           @RequestParam(required = true) String tenantId
                                                                                   )
            throws Exception {
        return wfDefinitionService.processDefinitionDeployByInputStream(file,userId,source,tenantId);
    }


    /**
     * 查询所有最新版本流程定义
     * @param pageNo 起始页
     * @param pageSize 每页多少条
     * @param definitionKey 流程定义key
     * @param definitionName 流程定义id
     * @return
     */
    @GetMapping("/query-definition")
    @ApiOperation(value = "流程定义列表",notes = "流程定义列表")
    public Result<RecordsListPageVO> queryAllProcessDefinition(@ApiParam(value = "当前页",required = true)
                                                                           @RequestParam Integer pageNo,
                                                               @ApiParam(value = "显示多少页",required = true)
                                                                            @RequestParam Integer pageSize,
                                                               @ApiParam(value = "筛选条件：流程定义key",required = false)
                                                                           @RequestParam(required = false)
                                                                                   String definitionKey,
                                                               @ApiParam(value = "筛选条件：流程定义名称",required = false)
                                                                           @RequestParam(required = false)
                                                                                   String definitionName){
        return wfDefinitionService.queryAllProcessDefinition(pageNo, pageSize, definitionKey, definitionName);
    }

    /**
     * 启动流程实例:按流程定义的key
     * @param startVariable 流程实例参数
     */
    @PostMapping("/start-process-instance-by-key")
    @ApiOperation(value = "启动流程实例(按key)",notes = "启动流程实例(按key)")
    public Result<StartProcessInstanceByIdVO> startProcessInstanceByKey(@ApiParam(value = "启动流程实例参数",required = true) @RequestBody
                                                        StartVariable startVariable){
        return wfDefinitionService.startProcessInstanceByKey(startVariable);
    }

    /**
     * 启动流程实例：按流程定义的id
     * @param startVariable 流程实例参数
     * @return
     */
    @PostMapping("/start-process-instance-by-id")
    @ApiOperation(value = "启动流程实例(按id)", notes = "启动流程实例(按id)")
    public Result<StartProcessInstanceByIdVO> startProcessInstanceById(@ApiParam(value = "启动流程实例参数",required = true) @RequestBody
                                                       StartVariable startVariable){
        return wfDefinitionService.startProcessInstanceById(startVariable);
    }

    @PostMapping("/start-process-instance-by-array")
    @ApiOperation(value = "以不同参数批量启动某一流程" ,notes = "以不同参数批量启动某一流程")
    public Result startProcessInstanceByArray(@ApiParam(value = "启动流程实例参数",required = true) @RequestBody
                                                   StartVariables startVariables
                                           ){
        return wfDefinitionService.startProcessInstanceByArray(startVariables);
    }

    /**
     * 删除流程定义：按定义id
     * @param processDefinitionId 流程定义id
     * @param isCascade 是否级联
     * @return
     */
    @DeleteMapping("/delete-definition-by-id")
    @ApiOperation(value = "删除流程定义（按定义id）",notes = "删除流程定义（按定义id）")
    public Result delProcessDefinitionById(@ApiParam(value = "流程定义id",required = true)
                                               @RequestParam String processDefinitionId,
                                           @ApiParam(value = "是否级联",required = true)
                                           @RequestParam boolean isCascade){
        return wfDefinitionService.deleteProcessDefinitionById(processDefinitionId,isCascade);
    }

    /**
     * 流程定义挂起/激活：按key
     * @param processDefinitionKey 流程定义key
     * @param state 当前状态 1:激活 2：挂起
     * @return
     */
    @GetMapping("/suspend-or-activate-definition")
    @ApiOperation(value = "流程定义上线或下线：按key",notes = "流程定义上线或下线：按key(暂未使用)")
    public Result suspendOrActivateDefinition(@ApiParam(value = "流程定义key",required = true) @RequestParam
                                                          String processDefinitionKey,
            @ApiParam(value = "当前状态 1:激活 2：挂起",required = true) @RequestParam Integer state){
        return wfDefinitionService.suspendOrActivateDefinition(processDefinitionKey,state);
    }

    /**
     * 流程定义挂起/激活：按流程定义id
     * @param processDefinitionId 流程定义id
     * @param state 当前状态 1：激活 2：挂起
     * @return
     */
    @GetMapping("/suspend-or-activate-definition-by-id")
    @ApiOperation(value = "流程定义上线或下线：按id",notes = "流程定义上线或下线：按id")
    public Result suspendOrActivateDefinitionById(@ApiParam(value = "流程定义id",required = true) @RequestParam
                                                  String processDefinitionId,
                                                  @ApiParam(value = "当前状态 1：激活 2：挂起",required = true)
                                                  @RequestParam Integer state){
        return wfDefinitionService.suspendOrActivateDefinitionById(processDefinitionId,state);
    }

    /**
     * 下载资源文件
     * @param definitionIds 流程定义列表
     * @param response 响应
     * @return
     * @throws IOException
     */
    @PostMapping("/download-resource-file")
    @ApiOperation(value = "批量下载资源文件", notes = "批量下载资源文件")
    public void downloadResourceFile(@ApiParam(value = "流程定义id",required = true) @RequestBody String[] definitionIds,
                                     HttpServletResponse response) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename=bpmns.zip");
        wfDefinitionService.getResourceAsStream(definitionIds,response.getOutputStream());
    }

    /**
     * 流程定义版本列表：按key
     * @param processKey 流程定义key
     * @return
     */
    @GetMapping("/query-version-by-Key")
    @ApiOperation(value = "流程定义版本列表(按key查询)", notes = "流程定义版本列表(按key查询)")
    public Result<RecordsListPageVO> queryVersionByKey(
            @ApiParam(value = "流程定义key",required = true) @RequestParam String processKey,
            @ApiParam(value = "当前页",required = true) @RequestParam Integer pageNo,
            @ApiParam(value = "每页多少条",required = true) @RequestParam Integer pageSize){
        return wfDefinitionService.queryVersionByKey(processKey, pageNo, pageSize);
    }

    /**
     * 获取bpmn资源:按定义id
     * @param response
     * @param definitionId
     * @throws IOException
     */
    @GetMapping("/download-resource-file-url")
    @ApiOperation(value = "获取bpmn资源:按定义id",notes = "获取bpmn资源:按定义id")
    public void downloadResourceFileByUrl(HttpServletResponse response,@ApiParam(value = "流程定义id",required = true) @RequestParam String definitionId) throws IOException {
        response.setHeader("Content-Disposition", "attachment;filename="+definitionId+".bpmn");
        wfDefinitionService.getResourceAsUrl(response.getOutputStream(), definitionId);
    }


    /**
     * 删除流程定义：批量删除
     * @param processDefinitionIds 流程定义列表
     * @return
     */
    @PostMapping("/delete-definition-by-array")
    @ApiOperation(value = "流程定义：批量删除", notes = "流程定义：批量删除")
    public Result deleteProcessDefinition(@ApiParam(value = "流程定义id列表",required = true) @RequestBody
                                                      String[] processDefinitionIds){
        return wfDefinitionService.deleteProcessDefinitionByArray(processDefinitionIds,true);
    }

    /**
     * 删除看流程定义：按key
     * @param processDefinitionKey 流程定义key
     * @return
     */
    @PostMapping("/delete-definition-by-key")
    @ApiOperation(value = "删除流程定义：按key", notes = "删除流程定义：按key")
    public Result deleteProcessDefinitionByKey(@ApiParam(value = "流程定义key",required = true)
                                                 @RequestParam String processDefinitionKey,
    @ApiParam(value = "是否级联",required = true) @RequestParam boolean isCascade
    ){
        return wfDefinitionService.deleteProcessDefinitionByKey(processDefinitionKey,isCascade);
    }

    /**
     * 根据流程定义Key获取所有版本号和对应流程定义id
     * @param procedefKey : 流程定义Key
     * @return ：所有版本号和是否挂起信息
     */
    @GetMapping("/get-version-by-procedefKey")
    @ApiOperation(value = "根据流程Key获取版本号和流程定义id", notes = "根据流程Key获取版本号和流程定义id")
    public Result<Map<Integer,String>> queryVersionByProcedefKey(@ApiParam(value = "流程定义Key", required = true) @RequestParam String procedefKey){
        Map<Integer,String> verMap = wfDefinitionService.queryVersionByProcedefKey(procedefKey);
        return Result.OK(verMap);
    }

    /**
     * 获取所有流程定义Key（不重复）
     * @return : 所有流程定义Key （不重复）和 流程定义 Name
     */
    @GetMapping("/get-all-procedefKey")
    @ApiOperation(value = "获取所有流程定义Key（不重复）和 流程定义 Name", notes = "获取所有流程定义Key（不重复）和 流程定义 Name")
    public Result<Map<String,String>> queryAllProcedefKey(){
        Map<String,String> map = wfDefinitionService.queryAllProcedefKey();

        return Result.OK(map);
    }

    /**
     * 应用复制-流程定义复制
     * @param processDefinitionId 流程定义id
     * @return
     */
    @GetMapping("/cop-process-definition")
    @ApiOperation(value = "应用复制-流程定义复制",notes = "应用复制-流程定义复制")
    public Result<String> copyProcessDefinition(@ApiParam(value = "流程定义id",required = true)
                                                @RequestParam String processDefinitionId,String name){
        return wfDefinitionService.copyProcessDefinition(processDefinitionId);
    }
}