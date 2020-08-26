package com.web.oa.controller;

import com.web.oa.pojo.ActiveUser;
import com.web.oa.pojo.Baoxiaobill;
import com.web.oa.pojo.Employee;
import com.web.oa.service.BaoxiaoService;
import com.web.oa.service.WorkFlowService;
import com.web.oa.utils.Constants;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.activiti.engine.task.Comment;
import org.activiti.engine.task.Task;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/26 0026 - 上午 10:52
 */
@Controller
public class WorkFlowController {

    @Autowired
    private WorkFlowService workFlowService;
    @Autowired
    private BaoxiaoService baoxiaoService;

    /**
     * 部署流程
     * @param processName
     * @param fileName
     * @return
     */
    @RequestMapping("/deployProcess")
    public String deployProcess(String processName, MultipartFile fileName){

        try {
            workFlowService.saveNewProcess(processName, fileName.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "redirect:/processDefinitionList";
    }

    /**
     * 查询流程定义列表
     * @param model
     * @return
     */
    @RequestMapping("/processDefinitionList")
    public String processDefinitionList(Model model){

        List<Deployment> depList = workFlowService.findDeploymentList();
        List<ProcessDefinition> pdList = workFlowService.findProcessDefinitionList();

        model.addAttribute("depList", depList);
        model.addAttribute("pdList", pdList);

        return "workflow_list";
    }

    /**
     * 删除部署的流程
     * @param deploymentId
     * @return
     */
    @RequestMapping("/delDeployment")
    public String delDeployment(String deploymentId){
        workFlowService.deleteDeploymentById(deploymentId);

        return "redirect:/processDefinitionList";
    }

    /**
     * 输出流程图
     * @param deploymentId
     * @param imageName
     * @param response
     */
    @RequestMapping("/viewImage")
    public void viewImage(String deploymentId, String imageName, HttpServletResponse response){

        //获取流程图片
        InputStream in = workFlowService.getImageInputStream(deploymentId, imageName);

        //通过IO流的方式将图片输出到前端
        try {
            ServletOutputStream out = response.getOutputStream();
            for (int read = -1; (read=in.read()) != -1;){
                out.write(read);
            }
            out.close();
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存报销单信息，并开启流程
     * @param baoxiaobill
     * @param session
     * @return
     */
    @RequestMapping("/saveStartBaoxiao")
    public String saveStartBaoxiao(Baoxiaobill baoxiaobill, HttpSession session){
        //获取登录用户信息
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();

        //1. 保存报销信息
        baoxiaobill.setCreatdate(new Date());
        baoxiaobill.setState(1);
        baoxiaobill.setUserId((int)activeUser.getId());
        baoxiaoService.saveBaoxiao(baoxiaobill);
        //2. 开启流程
        workFlowService.saveStartProcess(baoxiaobill.getId(), activeUser.getUsername());

        return "redirect:/myTaskList";
    }

    /**
     * 查询待办任务列表
     * @param model
     * @return
     */
    @RequestMapping("/myTaskList")
    public String myTaskList(Model model){
        List<Task> taskList = workFlowService.findTaskList();

        model.addAttribute("taskList", taskList);
        return "workflow_task";
    }

    /**
     * 查看当前流程图
     * @param taskId
     * @return
     */
    @RequestMapping("/viewCurrentImage")
    public ModelAndView viewCurrentImage(String taskId){

        //1. 根据id 获取流程定义对象
        ProcessDefinition processDefinition = workFlowService.findProcessDefinitionBytaskId(taskId);

        //2. 保存流程id 和 资源名称
        ModelAndView mv = new ModelAndView();

        mv.addObject("deploymentId", processDefinition.getDeploymentId());
        mv.addObject("imageName", processDefinition.getDiagramResourceName());

        //3. 获取流程执行的位置
        Map<String, Object> acs = workFlowService.findCoordingBytaskId(taskId);
        mv.addObject("acs", acs);

        mv.setViewName("viewimage");
        return mv;
    }

    /**
     * 审批报销单，回显报销单、批注、可选连线
     * @param taskId
     * @return
     */
    @RequestMapping("/viewTaskForm")
    public ModelAndView viewTaskForm(String taskId){
        //1. 查询报销单信息
        Baoxiaobill baoxiaobill = workFlowService.findBaoxiaoBillBytastId(taskId);
        //2. 获取批注信息
        List<Comment> comments = workFlowService.findCommentsBytaskId(taskId);
        //3. 获取当前活动之后的连线名称
        List<String> outcomeList = workFlowService.findOutComeListBytaskId(taskId);

        ModelAndView mv = new ModelAndView();
        mv.addObject("taskId", taskId);
        mv.addObject("baoxiaoBill", baoxiaobill);
        mv.addObject("commentList", comments);
        mv.addObject("outcomeList", outcomeList);

        mv.setViewName("approve_baoxiao");
        return mv;
    }

    /**
     * 完成当前任务，并推进任务
     * @param id
     * @param taskId
     * @param comment
     * @param outcome
     * @param username
     * @return
     */
    @RequestMapping("/submitTask")
    public String submitTask(int id, String taskId, String comment, String outcome, String username){
        // 完成当前任务，并推进任务
        workFlowService.saveSubmitTask(id, taskId, comment, outcome);

        return "redirect:/myTaskList";
    }


}
