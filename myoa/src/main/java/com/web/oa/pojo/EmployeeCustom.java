package com.web.oa.pojo;

/**
 * @author mhh
 * 2020/8/27 0027 - 上午 9:20
 */
public class EmployeeCustom extends Employee{
    private String roleId;
    private String rolename;
    private String manager;

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getRolename() {
        return rolename;
    }

    public void setRolename(String rolename) {
        this.rolename = rolename;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
}
