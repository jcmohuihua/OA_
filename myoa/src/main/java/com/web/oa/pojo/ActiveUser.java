package com.web.oa.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:27
 *
 * 用户身份信息存入 session ，由于 Tomcat 会将 session 序列化到本地硬盘上，所以使用 Serializable 接口
 */
public class ActiveUser implements Serializable {

    private long id;
    private String username;
    private long managerId;

    private List<SysPermission> menus;//菜单
    private List<SysPermission> permissions;//权限
    private List<MenuTree> menuTree;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getManagerId() {
        return managerId;
    }

    public void setManagerId(long managerId) {
        this.managerId = managerId;
    }

    public List<SysPermission> getMenus() {
        return menus;
    }

    public void setMenus(List<SysPermission> menus) {
        this.menus = menus;
    }

    public List<SysPermission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<SysPermission> permissions) {
        this.permissions = permissions;
    }

    public List<MenuTree> getMenuTree() {
        return menuTree;
    }

    public void setMenuTree(List<MenuTree> menuTree) {
        this.menuTree = menuTree;
    }
}
