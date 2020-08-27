package com.web.oa.controller;

import com.web.oa.pojo.*;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.SysService;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 6:57
 */
@Controller
public class UserController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SysService sysService;

    @RequestMapping("/login")
    public String login(HttpServletRequest request, Model model){

        String exceptionName = (String) request.getAttribute("shiroLoginFailure");

        if(exceptionName != null){
            if(UnknownAccountException.class.getName().equalsIgnoreCase(exceptionName)){
                model.addAttribute("errorMsg", "用户不存在");

            }else if(IncorrectCredentialsException.class.getName().equalsIgnoreCase(exceptionName)){
                model.addAttribute("errorMsg", "密码不正确");

            }else {
                model.addAttribute("errorMsg", "未知错误");
            }
        }
        return "login";
    }

    /**
     * 显示用户信息列表
     * @param model
     * @return
     */
    @RequestMapping("/findUserList")
    public String findUserList(Model model){
        //1. 查询用户与角色信息
        List<EmployeeCustom> userList = employeeService.findUserAndRoleList();
        //2. 查询所有角色信息
        List<SysRole> allRoles = sysService.findAllRole();
        model.addAttribute("userList", userList);
        model.addAttribute("allRoles", allRoles);

        return "userlist";
    }

    /**
     * 修改用户角色 来分配权限
     * @param roleId
     * @param userId
     * @return
     */
    @RequestMapping("/assignRole")
    @ResponseBody
    public Map<String, String> assignRole(String roleId, String userId){
        //1. 更新用户角色信息
        Map<String, String> map = new HashMap<>();
        try {
            employeeService.updateEmployeeRole(roleId, userId);
            //2. 添加返回信息
            map.put("msg", "分配权限成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "分配权限失败");
        }

        return map;
    }

    /**
     * 查询用户的角色和角色对应的权限信息
     * @param userName
     * @return
     */
    @RequestMapping("/viewPermissionByUser")
    @ResponseBody
    public SysRole viewPermissionByUser(String userName){

        SysRole sysRole = sysService.findRoleAndPermissionsByUserId(userName);

        return sysRole;
    }

    /**
     * 保存添加的用户信息
     * @param employee
     * @return
     */
    @RequestMapping("/saveUser")
    public String saveUser(Employee employee, String roleId){
        sysService.saveEmployeeAndRole(employee, roleId);

        return "redirect:/findUserList";
    }

    /**
     * 查询直接上级
     * @param level
     * @return
     */
    @RequestMapping("/findNextManager")
    @ResponseBody
    public List<Employee> findNextManager(int level){
        List<Employee> managerList = employeeService.findManagerByLevel(++level);

        return managerList;
    }

    /**
     * 角色列表中 查询权限信息
     * @return
     */
    @RequestMapping("/findRoles")
    public ModelAndView findRoles(){
        //1. 查询所有角色信息
        List<SysRole> allRoles = sysService.findAllRole();
        //2. 查询所有权限信息
        List<MenuTree> allMenuAndPermissions = sysService.loadMenuTree();

        ModelAndView mv = new ModelAndView();
        mv.addObject("allRoles", allRoles);
        mv.addObject("allMenuAndPermissions", allMenuAndPermissions);

        mv.setViewName("permissionlist");
        return mv;
    }

    /**
     * 查询某一角色的权限信息
     * @param roleId
     * @return
     */
    @RequestMapping("/loadMyPermissions")
    @ResponseBody
    public List<SysPermission> loadMyPermissions(String roleId){

        //1. 查询某一角色的权限信息
        List<SysPermission> permissionList = sysService.findRoleAndPermissionsByRoleId(roleId);

        return permissionList;
    }

    @RequestMapping("/updateRoleAndPermission")
    public String updateRoleAndPermission(String roleId, String[] permissionIds){
        //1. 更新某一角色的权限信息
        sysService.updatePermissionByRoleId(roleId, permissionIds);
        return "redirect:/findRoles";
    }

    /**
     * 用户添加
     * @return
     */
    @RequestMapping("/toAddRole")
    public ModelAndView toAddRole(){
        //1. 获取权限分配列表
        List<MenuTree> allPermissions = sysService.loadMenuTree();
        //2. 获得权限的父节点
        final List<SysPermission> menuTypes = new ArrayList<>();
        allPermissions.forEach(p -> {
            SysPermission sysPermission = new SysPermission();
            sysPermission.setId((long) p.getId());
            sysPermission.setName(p.getName());
            menuTypes.add(sysPermission);
        });

        ModelAndView mv = new ModelAndView();
        mv.addObject("allPermissions", allPermissions);
        mv.addObject("menuTypes", menuTypes);

        mv.setViewName("rolelist");
        return mv;
    }

    //保存新加的权限
    @RequestMapping("/saveSubmitPermission")
    public String saveSubmitPermission(SysPermission sysPermission){
        //1. 保存新添加的权限信息
        sysService.saveNewPermission(sysPermission);

        return "redirect:/toAddRole";
    }

    //保存新加的角色，及角色的权限信息
    @RequestMapping("/saveRoleAndPermissions")
    public String saveRoleAndPermissions(SysRole role, String[] permissionIds){
        //1. 添加新角色, 添加角色与权限关联的信息
        sysService.saveNewRole(role, permissionIds);

        return "redirect:/findRoles";
    }
}
