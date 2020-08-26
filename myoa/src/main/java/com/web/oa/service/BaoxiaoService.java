package com.web.oa.service;

import com.web.oa.pojo.Baoxiaobill;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;

import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/26 0026 - 下午 3:34
 */
public interface BaoxiaoService {
    //保存报销信息
    void saveBaoxiao(Baoxiaobill baoxiaobill);

    List<Baoxiaobill> findBaoxiaobillByUserId(int id);

    Baoxiaobill findBaoxiaobillById(int id);

    List<Comment> findCommentsBybillId(int id);

    void deleteBaoxiaoBill(int id);

    ProcessDefinition findProcessDefinitionBybillId(int billId);

    Map<String, Object> findCoordingBybillId(int billId);
}
