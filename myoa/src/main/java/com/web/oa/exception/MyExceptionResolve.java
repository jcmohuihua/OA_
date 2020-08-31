package com.web.oa.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.sf.json.util.JSONUtils;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * @author mhh
 * 2020/8/28 0028 - 下午 4:28
 */
public class MyExceptionResolve implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
                                         Object o, Exception e) {
        // 判断是否为 Ajax 请求
        if(!(request.getHeader("accept").contains("application/json")
                || (request.getHeader("X-Requested-With") != null
                && request.getHeader("X-Requested-With").contains("XMLHttpRequest")))){
            // 如果不是 Ajax，JSP格式返回
            //为安全起见，只有业务异常我们对前端可见，否则同意为系统异常
            Map<String, Object> map = new HashMap<>();
            map.put("success", false);

            if(e instanceof NullPointerException){
                map.put("errorMsg", ErrorCode.NULL_OBJ.toString());
            }else if(e instanceof RuntimeException){
                map.put("errorMsg", ErrorCode.UNKNOWN_ERROR.toString());
            }else{
                map.put("errorMsg", "系统异常!");
            }
            //这里需要手动打印出来，由于没有配置 log，实际生产环境应该打印到 log 里
            e.printStackTrace();
            return new ModelAndView("error", map);

        }else {
            //如果是 Ajax 请求，JSON 格式返回
            try {
                response.setContentType("application/json;charset=utf-8");
                PrintWriter out = response.getWriter();
                //为安全起见，只有业务异常我们对前端可见，否则同意为系统异常
                Map<String, Object> map = new HashMap<>();
                map.put("success", false);

                if(e instanceof NullPointerException){
                    map.put("errorMsg", ErrorCode.NULL_OBJ.toString());
                }else if(e instanceof RuntimeException){
                    map.put("errorMsg", ErrorCode.UNKNOWN_ERROR.toString());
                }else{
                    map.put("errorMsg", "系统异常!");
                }

                ObjectMapper mapper = new ObjectMapper();
                String json = mapper.writeValueAsString(map);
                out.write(json);

                out.flush();
                out.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return null;
    }
}
