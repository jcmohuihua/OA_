package com.web.oa.service.Impl;

import com.web.oa.mapper.EmployeeMapper;
import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.mapper.SysUserRoleMapper;
import com.web.oa.pojo.*;
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
    @Autowired
    private SysPermissionMapperCustom mapperCustom;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

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

    @Override
    public List<Employee> findAllEmployee() {
        return em.selectByExample(null);
    }

    @Override
    public List<EmployeeCustom> findUserAndRoleList() {
        return mapperCustom.findUserAndRoleList();
    }

    @Override
    public void updateEmployeeRole(String roleId, String userId) {
        //1. 先根据查询
        SysUserRoleExample example = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andSysUserIdEqualTo(userId);

        SysUserRole sysUserRole = sysUserRoleMapper.selectByExample(example).get(0);
        sysUserRole.setSysRoleId(roleId);
        //2. 再修改
        sysUserRoleMapper.updateByPrimaryKey(sysUserRole);
    }

    @Override
    public void saveEmployee(Employee employee) {
        em.insertSelective(employee);
    }

    @Override
    public List<Employee> findManagerByLevel(int level) {
        EmployeeExample example = new EmployeeExample();
        EmployeeExample.Criteria criteria = example.createCriteria();
        criteria.andRoleEqualTo(level);

        List<Employee> list = em.selectByExample(example);
        return list;
    }
}
