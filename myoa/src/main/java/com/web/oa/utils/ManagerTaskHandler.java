package com.web.oa.utils;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.service.EmployeeService;
import org.activiti.engine.delegate.DelegateTask;
import org.activiti.engine.delegate.TaskListener;
import org.apache.shiro.SecurityUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

/**
 * @author mhh
 * 2020/8/26 0026 - 下午 8:01
 */
public class ManagerTaskHandler implements TaskListener {
    @Override
    public void notify(DelegateTask delegateTask) {
        //1. 获取 spring 容器
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        //2. 获取 bean
        EmployeeService employeeService = (EmployeeService) context.getBean("employeeService");
        //3. 获取用户信息
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        //4. 查询上级的信息
        Employee manager = employeeService.findManagerByManagerId(activeUser.getManagerId());
        //5. 设置个人任务待办人
        delegateTask.setAssignee(manager.getName());
    }
}
