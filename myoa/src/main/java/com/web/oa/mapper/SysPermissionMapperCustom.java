package com.web.oa.mapper;

import com.web.oa.pojo.EmployeeCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;
import com.web.oa.pojo.SysRole;

import java.util.List;
import java.util.Set;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:43
 */
public interface SysPermissionMapperCustom {

    //获取主菜单
    List<MenuTree> getMenuTree();
    //获取下一级菜单
    List<SysPermission> getSubMenu();

    //获取用户以及权限信息
    List<EmployeeCustom> findUserAndRoleList();

    //获取用户角色及角色对应的权限信息
    SysRole findRoleAndPermissionsByUserId(String userId);

    List<SysPermission> findRoleAndPermissionsByRoleId(String roleId);

    Set<String> findRoleByUserId(String userId);

    Set<String> findPermissionByUserId(String userId);
}
