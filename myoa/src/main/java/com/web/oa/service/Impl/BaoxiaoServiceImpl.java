package com.web.oa.service.Impl;

import com.web.oa.mapper.BaoxiaobillMapper;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.BaoxiaobillExample;
import com.web.oa.service.BaoxiaoService;
import com.web.oa.utils.Constants;
import org.activiti.engine.HistoryService;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.RuntimeService;
import org.activiti.engine.TaskService;
import org.activiti.engine.history.HistoricProcessInstance;
import org.activiti.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.activiti.engine.impl.pvm.process.ActivityImpl;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.runtime.ProcessInstance;
import org.activiti.engine.task.Comment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/26 0026 - 下午 3:35
 */
@Service
public class BaoxiaoServiceImpl implements BaoxiaoService {

    @Autowired
    private BaoxiaobillMapper baoxiaobillMapper;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;
    @Autowired
    private RuntimeService runtimeService;

    @Override
    public void saveBaoxiao(Baoxiaobill baoxiaobill) {
        baoxiaobillMapper.insert(baoxiaobill);
    }

    @Override
    public List<Baoxiaobill> findBaoxiaobillByUserId(int id) {
        BaoxiaobillExample example = new BaoxiaobillExample();
        BaoxiaobillExample.Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(id);

        return baoxiaobillMapper.selectByExample(example);
    }

    @Override
    public Baoxiaobill findBaoxiaobillById(int id) {

        return baoxiaobillMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<Comment> findCommentsBybillId(int id) {
        //1. 拼接 businessKey
        String businessKey = Constants.BAOXIAO_KEY + "." + id;
        //2. 获取到 历史流程实例对象
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        //3. 根据历史流程实例对象的 id 获取 历史批注信息
        List<Comment> commentList = taskService.getProcessInstanceComments(historicProcessInstance.getId());

        return commentList;
    }

    @Override
    public void deleteBaoxiaoBill(int id) {
        baoxiaobillMapper.deleteByPrimaryKey(id);
        System.out.println("报销记录删除成功......");
    }

    @Override
    public ProcessDefinition findProcessDefinitionBybillId(int billId) {
        //1. 拼接 businessKey
        String businessKey = Constants.BAOXIAO_KEY + "." + billId;
        //2. 获取历史流程定义对象
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        //3. 获取流程定义对象
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        ProcessDefinition processDefinition = repositoryService
                .createProcessDefinitionQuery()
                .processDefinitionId(processDefinitionId)
                .singleResult();

        return processDefinition;
    }

    @Override
    public Map<String, Object> findCoordingBybillId(int billId) {
        //1. 拼接 businessKey
        String businessKey = Constants.BAOXIAO_KEY + "." + billId;
        //2. 获取到 流程定义 id
        HistoricProcessInstance historicProcessInstance = historyService
                .createHistoricProcessInstanceQuery()
                .processInstanceBusinessKey(businessKey)
                .singleResult();
        String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
        //3. 获得流程定义的实体对象
        ProcessDefinitionEntity entity =
                (ProcessDefinitionEntity) repositoryService.getProcessDefinition(processDefinitionId);

        //4.获取流程实例对象
        ProcessInstance processInstance = runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(historicProcessInstance.getId())
                .singleResult();
        //5. 获取当期活动 id
        String activityId = processInstance.getActivityId();
        //6. 获取当前的活动对象
        ActivityImpl activity = entity.findActivity(activityId);
        //7. 保存坐标
        Map<String, Object> map = new HashMap<>();
        map.put("x", activity.getX());
        map.put("y", activity.getY());
        map.put("width", activity.getWidth());
        map.put("height", activity.getHeight());

        return map;
    }

}
