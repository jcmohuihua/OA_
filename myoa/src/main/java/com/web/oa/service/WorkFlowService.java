package com.web.oa.service;

import com.web.oa.pojo.Baoxiaobill;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/26 0026 - 上午 10:59
 */
public interface WorkFlowService {
    //部署流程
    void saveNewProcess(String processName, InputStream in);

    //查询部署流程
    List<Deployment> findDeploymentList();

    //查询流程定义信息列表
    List<ProcessDefinition> findProcessDefinitionList();

    void deleteDeploymentById(String deploymentId);

    InputStream getImageInputStream(String deploymentId, String imageName);

    void saveStartProcess(Integer id, String username);

    List<Task> findTaskList();

    ProcessDefinition findProcessDefinitionBytaskId(String taskId);

    Map<String, Object> findCoordingBytaskId(String taskId);

    Baoxiaobill findBaoxiaoBillBytastId(String taskId);

    List<Comment> findCommentsBytaskId(String taskId);

    List<String> findOutComeListBytaskId(String taskId);

    void saveSubmitTask(int id, String taskId, String comment, String outcome);
}
