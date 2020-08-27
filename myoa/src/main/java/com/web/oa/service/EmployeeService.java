package com.web.oa.service;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeCustom;

import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:21
 */
public interface EmployeeService {
    //根据用户名查询
    Employee findEmployeeByName(String name);

    Employee findManagerByManagerId(long managerId);

    List<Employee> findAllEmployee();

    List<EmployeeCustom> findUserAndRoleList();

    void updateEmployeeRole(String roleId, String userId);

    void saveEmployee(Employee employee);

    List<Employee> findManagerByLevel(int level);
}
