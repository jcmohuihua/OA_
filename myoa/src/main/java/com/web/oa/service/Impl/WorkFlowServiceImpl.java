package com.web.oa.service.Impl;

import com.web.oa.mapper.BaoxiaobillMapper;
import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.impl.identity.Authentication;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.PvmTransition;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

/**
 * @author mhh
 * 2020/8/26 0026 - 上午 11:01
 */
@Service
public class WorkFlowServiceImpl implements WorkFlowService {

    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private BaoxiaobillMapper baoxiaobillMapper;

    @Override
    public void saveNewProcess(String processName, InputStream in) {
        ZipInputStream zipIn = new ZipInputStream(in);

        repositoryService
                .createDeployment()
                .name(processName)
                .addZipInputStream(zipIn)
                .deploy();
    }

    @Override
    public List<Deployment> findDeploymentList() {
        List<Deployment> list = repositoryService
                .createDeploymentQuery()
                .orderByDeploymenTime()
                .desc()
                .list();

        return list;
    }

    @Override
    public List<ProcessDefinition> findProcessDefinitionList() {
        List<ProcessDefinition> list = repositoryService
                .createProcessDefinitionQuery()
                .list();

        return list;
    }

    @Override
    public int deleteDeploymentById(String deploymentId) {
        //1. 先判断流程是否正在执行
        int msg;
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .deploymentId(deploymentId)
                .singleResult();
        String processDefinitionId = processDefinition.getId();
        ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        if (pi == null) {
            //流程执行结束
            repositoryService.deleteDeployment(deploymentId);
            msg = 1;
            return msg;
        }else{
            msg = 0;
            return msg;
        }
    }

    @Override
    public int deleteDeploymentAgainById(String deploymentId) {
        //级连删除
        repositoryService.deleteDeployment(deploymentId, true);
        return 1;
    }

    @Override
    public InputStream getImageInputStream(String deploymentId, String imageName) {

        return repositoryService.getResourceAsStream(deploymentId, imageName);
    }

    @Override
    public void saveStartProcess(Integer id, String username) {
        String key = Constants.BAOXIAO_KEY;
        String businessKey = key + "." + id;

        //设置待办人
        Map<String, Object> map = new HashMap<>();
        map.put("inputUser", username);
        //开启流程
        runtimeService
                .startProcessInstanceByKey(key, businessKey, map);
    }

    @Override
    public List<Task> findTaskList() {
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Task> list = taskService
                .createTaskQuery()
                .taskAssignee(activeUser.getUsername())
                .list();

        return list;
    }

    @Override
    public ProcessDefinition findProcessDefinitionBytaskId(String taskId) {
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processDefinitionId = task.getProcessDefinitionId();

        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        return processDefinition;
    }

    @Override
    public Map<String, Object> findCoordingBytaskId(String taskId) {
        //1. 保存坐标
        Map<String, Object> map = new HashMap<>();
        //2. 根据任务id 查询任务
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        //3. 获取流程定义的实体对象
        String processDefinitionId = task.getProcessDefinitionId();
        ProcessDefinitionEntity entity =
                (ProcessDefinitionEntity)repositoryService.getProcessDefinition(processDefinitionId);

        //4. 使用流程实例 id 获取当前活动对应的流程实例对象
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        //5. 获取当前活动 id
        String activityId = pi.getActivityId();
        //6. 获取当前活动对象
        ActivityImpl activity = entity.findActivity(activityId);

        map.put("x", activity.getX());
        map.put("y", activity.getY());
        map.put("width", activity.getWidth());
        map.put("height", activity.getHeight());
        return map;
    }

    @Override
    public Baoxiaobill findBaoxiaoBillBytastId(String taskId) {
        //从 businessKey 中获取 报销单 id
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();

        String processInstanceId = task.getProcessInstanceId();

        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();

        String businessKey = processInstance.getBusinessKey();
        String id = businessKey.split("\\.")[1];

        Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(Integer.parseInt(id));

        return baoxiaobill;
    }

    @Override
    public List<Comment> findCommentsBytaskId(String taskId) {
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();

        List<Comment> comments = taskService.getProcessInstanceComments(processInstanceId);

        return comments;
    }

    @Override
    public List<String> findOutComeListBytaskId(String taskId) {
        //1. 保存连线信息
        List<String> list = new ArrayList<>();
        //2. 获取流程定义的实体对象
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processDefinitionId = task.getProcessDefinitionId();

        ProcessDefinitionEntity entity =
                (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        //3. 获取当前的活动对象
        String processInstanceId = task.getProcessInstanceId();
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();


        String activityId = processInstance.getActivityId();
        ActivityImpl activity = entity.findActivity(activityId);

        //4. 获取活动完成之后连线的名称
        List<PvmTransition> pvmTransitions = activity.getOutgoingTransitions();
        if(pvmTransitions != null && pvmTransitions.size() > 0){
            for (PvmTransition pvmTransition : pvmTransitions) {
                String name = (String) pvmTransition.getProperty("name");
                if(StringUtils.isNotBlank(name)){
                    list.add(name);
                }else {
                    list.add("默认提交");
                }
            }
        }

        return list;
    }

    @Override
    public void saveSubmitTask(int id, String taskId, String comment, String outcome) {
        //1. 添加批注信息
        Task task = taskService
                .createTaskQuery()
                .taskId(taskId)
                .singleResult();
        String processInstanceId = task.getProcessInstanceId();
        //添加当前任务审核人
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Authentication.setAuthenticatedUserId(activeUser.getUsername());

        taskService.addComment(taskId, processInstanceId, comment);

        //2. 如果连线是 默认提交 则不需要设置流程变量，反之，则需要设置 outcome
        if(outcome != null && !outcome.equals("默认提交")){
            Map<String, Object> variables = new HashMap<>();
            variables.put("message", outcome);

            taskService.complete(taskId, variables);
        }else{
            taskService.complete(taskId);
        }

        //3. 若完成任务后，流程结束，需要设置报销表的状态 1 -> 2
        ProcessInstance pi = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .singleResult();
        //流程结束
        if(pi == null){
            Baoxiaobill baoxiaobill = baoxiaobillMapper.selectByPrimaryKey(id);
            baoxiaobill.setState(2);
            baoxiaobillMapper.updateByPrimaryKeySelective(baoxiaobill);
        }
    }

}
