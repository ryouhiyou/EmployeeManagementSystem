package com.company.ems.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter; // 使用注解
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 使用 @WebFilter 注解拦截所有请求
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // 定义不需要认证的路径
    private static final String[] EXCLUDED_PATHS = {
            "/LoginServlet",
            "/css/",
            "/js/",
            "/error/",
            "/LogoutServlet" // 允许访问 LogoutServlet，以便销毁 session
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        // 获取 session，如果不存在，则返回 null
        HttpSession session = req.getSession(false);

        String path = req.getRequestURI().substring(req.getContextPath().length());

        // 检查路径是否被排除在外
        for (String excludedPath : EXCLUDED_PATHS) {
            if (path.startsWith(excludedPath)) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 检查用户是否登录
        boolean loggedIn = (session != null && session.getAttribute("user") != null);

        if (loggedIn) {
            // 已登录，放行
            chain.doFilter(request, response);
        } else {
            // 未登录，重定向到登录页面
            res.sendRedirect(req.getContextPath() + "/LoginServlet");
        }
    }

    @Override
    public void destroy() {}
}