package com.web.oa.service.Impl;

import com.web.oa.mapper.*;
import com.web.oa.pojo.*;
import com.web.oa.service.SysService;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 8:29
 */
@Service
public class SysServiceImpl implements SysService {

    @Autowired
    private SysPermissionMapperCustom permissionMapperCustom;
    @Autowired
    private SysRoleMapper sysRoleMapper;
    @Autowired
    private SysPermissionMapper sysPermissionMapper;
    @Autowired
    private SysRolePermissionMapper sysRolePermissionMapper;
    @Autowired
    private EmployeeMapper employeeMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @Override
    public List<MenuTree> loadMenuTree() {
        List<MenuTree> menuTree = permissionMapperCustom.getMenuTree();
        return menuTree;
    }

    @Override
    public List<SysRole> findAllRole() {
        return sysRoleMapper.selectByExample(null);
    }

    @Override
    public SysRole findRoleAndPermissionsByUserId(String userId) {
        // 若用户没有角色信息，意味着也不存在权限信息
        //1. 先查询用户是否拥有角色信息
        SysUserRoleExample example = new SysUserRoleExample();
        SysUserRoleExample.Criteria criteria = example.createCriteria();
        criteria.andSysUserIdEqualTo(userId);

        List<SysUserRole> sysUserRoles = sysUserRoleMapper.selectByExample(example);
        if(sysUserRoles.size() > 0){
            //2. 用户存在角色信息，查询角色的权限信息
            return permissionMapperCustom.findRoleAndPermissionsByUserId(userId);
        }else{
            //3. 用户不存在角色信息，则返回一个空内容的对象
            return new SysRole();
        }
    }

    @Override
    public List<SysPermission> findRoleAndPermissionsByRoleId(String roleId) {
        return permissionMapperCustom.findRoleAndPermissionsByRoleId(roleId);
    }

    @Override
    public Set<String> findRoleByUsername(String username) {
        return permissionMapperCustom.findRoleByUserId(username);
    }

    @Override
    public Set<String> findPermissionByUsername(String username) {
        return permissionMapperCustom.findPermissionByUserId(username);
    }

    @Override
    public void saveNewPermission(SysPermission sysPermission) {
        sysPermissionMapper.insertSelective(sysPermission);
        System.out.println("添加新权限成功......");
    }

    @Override
    public void saveNewRole(SysRole role, String[] permissionIds) {
        //1. 保存新角色到角色表中
        String roleId = UUID.randomUUID().toString();//随机生成一个id
        role.setId(roleId);
        role.setAvailable("1");
        sysRoleMapper.insertSelective(role);
        //2. 保存角色与权限的关联信息到中间表中
        if(permissionIds != null){
            for (int i = 0; i < permissionIds.length; i++) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setId(UUID.randomUUID().toString());
                rolePermission.setSysRoleId(roleId);
                rolePermission.setSysPermissionId(permissionIds[i]);

                sysRolePermissionMapper.insertSelective(rolePermission);
            }
        }
    }

    @Override
    public void updatePermissionByRoleId(String roleId, String[] permissionIds) {
        //1. 先删除
        SysRolePermissionExample example = new SysRolePermissionExample();
        SysRolePermissionExample.Criteria criteria = example.createCriteria();
        criteria.andSysRoleIdEqualTo(roleId);

        sysRolePermissionMapper.deleteByExample(example);
        //2. 再添加
        if(permissionIds != null){
            for (int i = 0; i < permissionIds.length; i++) {
                SysRolePermission rolePermission = new SysRolePermission();
                rolePermission.setId(UUID.randomUUID().toString());
                rolePermission.setSysRoleId(roleId);
                rolePermission.setSysPermissionId(permissionIds[i]);

                sysRolePermissionMapper.insert(rolePermission);
            }
        }
    }

    @Override
    public void saveEmployeeAndRole(Employee employee) {
        //1. 保存新用户信息
        //加密
        Md5Hash hash = new Md5Hash(employee.getPassword(), "eteokues", 2);
        employee.setPassword(hash.toString());
        employee.setSalt("eteokues");

        employeeMapper.insert(employee);
    }

    @Override
    public void deleteRoleAndPermissionByRoleId(String roleId) {
        sysRoleMapper.deleteByPrimaryKey(roleId);
    }

    @Override
    public List<MenuTree> findAllMenuAndPermission() {
        return permissionMapperCustom.getAllMenuAndPermission();
    }
}
