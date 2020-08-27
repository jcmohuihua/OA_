package com.web.oa.service;

import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;
import java.util.Set;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:18
 */
public interface SysService {
    //获取菜单列表
    List<MenuTree> loadMenuTree();

    List<SysRole> findAllRole();

    SysRole findRoleAndPermissionsByUserId(String userId);

    List<SysPermission> findRoleAndPermissionsByRoleId(String roleId);

    Set<String> findRoleByUsername(String username);

    Set<String> findPermissionByUsername(String username);

    void saveNewPermission(SysPermission sysPermission);

    void saveNewRole(SysRole role, String[] permissionIds);

    void updatePermissionByRoleId(String roleId, String[] permissionIds);

    void saveEmployeeAndRole(Employee employee, String roleId);
}
