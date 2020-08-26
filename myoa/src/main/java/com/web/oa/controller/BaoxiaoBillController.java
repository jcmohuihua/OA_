package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.MenuTree;
import jdk.nashorn.internal.ir.ReturnNode;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 8:43
 */
@Controller
public class BaoxiaoBillController {

    @RequestMapping("/index")
    public String main(Model model){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();

        model.addAttribute("activeUser", activeUser);
        return "index";
    }
}
