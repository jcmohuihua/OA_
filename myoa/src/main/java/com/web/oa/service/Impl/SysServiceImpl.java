package com.web.oa.service.Impl;

import com.web.oa.mapper.SysPermissionMapperCustom;
import com.web.oa.pojo.MenuTree;
import com.web.oa.service.SysService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 8:29
 */
@Service
public class SysServiceImpl implements SysService {

    @Autowired
    private SysPermissionMapperCustom permissionMapperCustom;

    @Override
    public List<MenuTree> loadMenuTree() {
        List<MenuTree> menuTree = permissionMapperCustom.getMenuTree();
        return menuTree;
    }
}
