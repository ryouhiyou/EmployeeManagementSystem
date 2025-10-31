package com.company.ems.controller;

import com.company.ems.dao.UserDAO;
import com.company.ems.model.User;
import com.company.ems.util.MyBatisUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Get form data
        String username = request.getParameter("username").trim();
        String email = request.getParameter("email").trim();
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");

        String errorMessage = null;

        // 2. Perform basic validation
        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            errorMessage = "所有字段都是必填项。";
        } else if (!password.equals(confirmPassword)) {
            errorMessage = "两次输入的密码不一致。";
        } else if (password.length() < 6) {
            errorMessage = "密码长度不能少于6位。";
        } else {
            // 3. Perform business validation (database check)
            try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true)) {

                UserDAO userDAO = session.getMapper(UserDAO.class);

                if (userDAO.findByUsername(username) != null) {
                    errorMessage = "该用户名已被注册。";
                } else if (userDAO.findByEmail(email) != null) {
                    errorMessage = "该邮箱已被注册。";
                } else {
                    // 4. Validation passed, execute registration logic
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setEmail(email);

                    // TODO: In a real project, password should be hashed
                    newUser.setPassword(password);

                    // *** The newUser.setRole() call is intentionally removed ***

                    int rowsAffected = userDAO.addUser(newUser);

                    if (rowsAffected > 0) {
                        // Registration success, redirect to login page
                        response.sendRedirect(request.getContextPath() + "/login.jsp?registration=success");
                        return;
                    } else {
                        errorMessage = "注册失败，无法写入数据库。";
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorMessage = "系统错误，注册失败：" + e.getMessage();
            }
        }

        // 5. Validation or registration failed, set error message and forward back
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
}
