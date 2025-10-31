package com.company.ems.controller;

import com.company.ems.dao.UserDAO;
import com.company.ems.model.User;
import com.company.ems.util.MyBatisUtil; // Import MyBatis utility class

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet; // Import annotation
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.SQLException;

import org.apache.ibatis.session.SqlSession; // Import SqlSession

// Register and map using @WebServlet annotation
@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // The dependency on the UserDAO member variable and init() method has been removed

    // Handle GET requests: display the login form (resolves 405 error)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/login.jsp").forward(request, response);
    }

    // Handle POST requests: validate login information
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User loggedInUser = null;
        String errorMessage = null;

        // Use try-with-resources to ensure SqlSession is closed
        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {

            // Get UserDAO Mapper instance
            UserDAO userDAO = session.getMapper(UserDAO.class);

            // 1. Find user by username
            User user = userDAO.findByUsername(username);

            if (user == null) {
                errorMessage = "用户名或密码错误。";
            }
            // 2. Validate password
            else if (user.getPassword().equals(password)) {
                loggedInUser = user;
                // role check has been removed
            } else {
                errorMessage = "用户名或密码错误。";
            }

        } catch (SQLException e) {
            e.printStackTrace();
            errorMessage = "系统错误，登录失败：" + e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "系统错误，登录失败。";
        }


        if (loggedInUser != null) {
            // Login successful
            HttpSession session = request.getSession();
            session.setAttribute("user", loggedInUser);
            session.setMaxInactiveInterval(30 * 60);

            // *** 重定向到 DashboardServlet 作为仪表盘/主页 ***
            response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        } else {
            // Login failed, forward back to the login page with the error message
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }
}
