package com.web.oa.controller;

import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.UnknownAccountException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 6:57
 */
@Controller
public class UserController {

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
}
