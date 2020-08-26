package com.web.oa.mapper;

import com.web.oa.pojo.MenuTree;
import com.web.oa.pojo.SysPermission;

import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 7:43
 */
public interface SysPermissionMapperCustom {

    //获取主菜单
    List<MenuTree> getMenuTree();
    //获取下一级菜单
    List<SysPermission> getSubMenu();
}
