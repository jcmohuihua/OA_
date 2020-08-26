package com.web.oa.shiro;

import org.apache.shiro.web.filter.authc.FormAuthenticationFilter;
import org.apache.shiro.web.util.WebUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * @author mhh
 * 2020/8/26 0026 - 上午 10:02
 */
public class MyFormAuthenticationFilter extends FormAuthenticationFilter {
    @Override
    protected void issueSuccessRedirect(ServletRequest request, ServletResponse response) throws Exception {
        WebUtils.issueRedirect(request, response, "/index", null, true);
    }
}
