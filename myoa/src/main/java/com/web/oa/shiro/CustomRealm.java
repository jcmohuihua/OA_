package com.web.oa.shiro;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Employee;
import com.web.oa.pojo.MenuTree;
import com.web.oa.service.EmployeeService;
import com.web.oa.service.SysService;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Set;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 6:42
 */
public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private SysService sysService;

    //授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principal) {
        //1. 获取用户身份信息
        ActiveUser activeUser = (ActiveUser) principal.getPrimaryPrincipal();
        //2. 查询数据库，获取用户的 角色信息 和 权限信息
        Set<String> roles = sysService.findRoleByUsername(activeUser.getUsername());
        Set<String> permissions = sysService.findPermissionByUsername(activeUser.getUsername());

        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.addRoles(roles);
        info.addStringPermissions(permissions);

        return info;
    }

    //认证
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        System.out.println("正在认证用户.....");
        String username = token.getPrincipal().toString();

        //根据用户名查询数据库
        Employee employee = employeeService.findEmployeeByName(username);
        if (employee == null){
            return null;// UnknownAccountException
        }

        //获取菜单
        List<MenuTree> menuTree = sysService.loadMenuTree();

        //把用户的身份信息重新封装
        ActiveUser activeUser = new ActiveUser();
        activeUser.setId(employee.getId());
        activeUser.setUsername(employee.getName());
        activeUser.setManagerId(employee.getManagerId());
        activeUser.setMenuTree(menuTree);

        //查询出的 密码与盐
        String password_db = employee.getPassword();
        String salt = employee.getSalt();

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(
                activeUser, password_db, ByteSource.Util.bytes(salt), "自定义realm");
        return info;
    }
}
