package com.web.oa.service;

import com.web.oa.pojo.Employee;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:21
 */
public interface EmployeeService {
    //根据用户名查询
    Employee findEmployeeByName(String name);
}
