package com.company.ems.filter;

import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 使用 @WebFilter 注解拦截所有请求
@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // 定义不需要认证的路径
    private static final String[] EXCLUDED_PATHS = {
            "/LoginServlet",
            // 🌟 修正 1: 必须允许访问注册页面本身
            "/register.jsp",
            // 🌟 修正 2: 必须允许访问 RegisterServlet 来提交注册表单
            "/RegisterServlet",
            "/css/",
            "/js/",
            "/error/",
            "/LogoutServlet"
    };

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession(false);

        // 获取相对路径 (例如: /login.jsp, /RegisterServlet)
        String path = req.getRequestURI().substring(req.getContextPath().length());

        // 检查路径是否被排除在外
        for (String excludedPath : EXCLUDED_PATHS) {
            // 注意: path.startsWith() 对于 /css/ 是好的，但对于 /login.jsp 这种精确页面的匹配需要注意
            // 为了安全和精确，可以对 .jsp 页面使用 equals() 或 startsWith()
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