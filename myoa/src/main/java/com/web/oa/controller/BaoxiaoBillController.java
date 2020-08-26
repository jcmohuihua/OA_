package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.MenuTree;
import com.web.oa.service.BaoxiaoService;
import jdk.nashorn.internal.ir.ReturnNode;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/25 0025 - 下午 8:43
 */
@Controller
public class BaoxiaoBillController {

    @Autowired
    private BaoxiaoService baoxiaoService;

    @RequestMapping("/index")
    public String main(Model model){
        Subject subject = SecurityUtils.getSubject();
        ActiveUser activeUser = (ActiveUser) subject.getPrincipal();

        model.addAttribute("activeUser", activeUser);
        return "index";
    }

    /**
     * 查看我的报销单
     * @param model
     * @return
     */
    @RequestMapping("/myBaoxiaoBill")
    public String myBaoxiaoBill(Model model){
        //查询当前登录人的报销列表
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        List<Baoxiaobill> baoxiaoList = baoxiaoService.findBaoxiaobillByUserId((int) activeUser.getId());

        model.addAttribute("baoxiaoList", baoxiaoList);

        return "baoxiaobill";
    }

    /**
     * 查看审核记录
     * @param id
     * @return
     */
    @RequestMapping("/viewHisComment")
    public ModelAndView viewHisComment(int id){

        //1. 根据 id 获取到 报销单
        Baoxiaobill baoxiaoBill = baoxiaoService.findBaoxiaobillById(id);
        //2. 获得历史批注信息
        List<Comment> commentList = baoxiaoService.findCommentsBybillId(id);

        ModelAndView mv = new ModelAndView();
        mv.addObject("baoxiaoBill", baoxiaoBill);
        mv.addObject("commentList", commentList);

        mv.setViewName("workflow_commentlist");
        return mv;
    }

    /**
     * 根据报销单 id 显示当前流程图
     * @param billId
     * @return
     */
    @RequestMapping("/viewCurrentImageByBill")
    public ModelAndView viewCurrentImageByBill(int billId){
        //1. 获取到 流程定义对象，通过流程定义对象 获取 部署流程id 和 资源名称
        ProcessDefinition processDefinition = baoxiaoService.findProcessDefinitionBybillId(billId);
        //2. 获取流程执行的位置 Map<String, Object>
        Map<String, Object> acs = baoxiaoService.findCoordingBybillId(billId);

        ModelAndView mv = new ModelAndView();
        mv.addObject("deploymentId", processDefinition.getDeploymentId());
        mv.addObject("imageName", processDefinition.getDiagramResourceName());
        mv.addObject("acs", acs);

        mv.setViewName("viewimage");
        return mv;
    }

    /**
     * 删除报销记录，思考：又不要同时删除历史批注信息呢？
     * @param id
     * @return
     */
    @RequestMapping("/deleteBaoxiaoBill")
    public String deleteBaoxiaoBill(int id){
        baoxiaoService.deleteBaoxiaoBill(id);
        return "redirect:/myBaoxiaoBill";
    }
}
