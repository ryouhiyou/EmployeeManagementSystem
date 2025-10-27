package com.company.ems.controller;

import com.company.ems.dao.UserDAO;
import com.company.ems.dao.UserDAOImpl;
import com.company.ems.model.User;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // 引入注解
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// 使用 @WebServlet 注解进行注册和映射
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAOImpl();
    }

    // 处理 GET 请求：显示登录表单 (解决 405 错误)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // 处理 POST 请求：验证登录信息
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ... (登录逻辑与之前保持一致)
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDAO.findByUsername(username);

            if (user != null && user.getPassword().equals(password)) {
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
                session.setMaxInactiveInterval(30 * 60);

                response.sendRedirect(request.getContextPath() + "/DashboardServlet");
            } else {
                request.setAttribute("error", "用户名或密码错误。");
                request.getRequestDispatcher("/login.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "系统错误，请稍后再试。");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}