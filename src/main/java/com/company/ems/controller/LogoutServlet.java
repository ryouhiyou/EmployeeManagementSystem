package com.company.ems.controller;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // 使用注解注册
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 注册路由为 /LogoutServlet
@WebServlet("/LogoutServlet")
public class LogoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * 处理 GET 请求，执行退出逻辑。
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 获取当前 session，参数为 false
        HttpSession session = request.getSession(false);

        // 2. 销毁 session
        if (session != null) {
            session.invalidate();
        }

        // 3. 重定向回登录页面。
        response.sendRedirect(request.getContextPath() + "/LoginServlet");

        // 额外的头部：防止浏览器缓存
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
    }
}