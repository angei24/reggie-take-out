package com.itheima.filter;

import com.alibaba.fastjson.JSON;
import com.itheima.common.BaseContext;
import com.itheima.common.R;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;
//@Slf4j
@WebFilter(servletNames = "LoginCheckFilter", urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    //拦截器，拦截所有请求并判断
    //路径匹配器，支持通配符
    public static final AntPathMatcher matcher = new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //1.获取请求路径
        String url = request.getRequestURI();
//        log.info("拦截请求：{}",url);
        //定义不需要拦截的路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources/**",
                "/v3/api-docs/**",
        };
        //2.判断请求是否处理
        boolean checked = check(urls, url);
        //3.不需要处理则直接放行
        if (checked) {
//            log.info("不需要拦截的路径");
            filterChain.doFilter(request, response);
            return;
        }
        //4.判断登录状态
        Long empId = (Long) request.getSession().getAttribute("employee");
        Long userId = (Long) request.getSession().getAttribute("user");
        if (empId != null) {
//            log.info("用户已登录：{}", empId);
            //设置当前登录用户的id
            BaseContext.setCurrentThread(empId);
            filterChain.doFilter(request, response);
            return;
        }
        if (userId != null) {
//            log.info("用户已登录：{}", userId);
            //设置当前登录用户的id
            BaseContext.setCurrentThread(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //5.未登录返回结果
//        log.info("用户未登录");
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
    }

    //检查请求是否需要放行
    public boolean check(String[] urls, String url) {
        for (String url1 : urls) {
            if (matcher.match(url1, url))
                return true;
        }
        return false;
    }
}
