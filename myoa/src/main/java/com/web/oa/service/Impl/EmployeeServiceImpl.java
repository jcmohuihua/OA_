package com.web.oa.service.Impl;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.EmployeeExample;
import com.web.oa.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:22
 */
@Service("employeeService")
public class EmployeeServiceImpl implements EmployeeService {

    @Autowired
    private EmployeeMapper em;

    @Override
    public Employee findEmployeeByName(String name) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andNameEqualTo(name);

        List<Employee> employees = em.selectByExample(example);
        if(employees.size() > 0){
            return employees.get(0);
        }
        return null;
    }

    @Override
    public Employee findManagerByManagerId(long managerId) {
        return em.selectByPrimaryKey(managerId);
    }
}
