package org.jeecg.modules.workflow.mapper;

/**
 * @Author 权计超
 * Company DXC.technology
 * @ClassName DeleteProcResource
 * @CreateTime 2021-09-02 10:10
 * @Version 1.0
 * @Description: 删除流程定义部署信息
 */
public interface DeleteProcResourceMapper {
    /**
     * 删除部署信息
     * @param deploymentId
     */
    void deleteDeployment(String deploymentId);

    /**
     * 删除资源文件信息
     * @param deploymentId
     */
    void deleteResource(String deploymentId);
}
