package com.company.ems.controller;

import com.company.ems.model.User;
import com.company.ems.util.MyBatisUtil;
import com.company.ems.dto.UserDTO;
import com.company.ems.mapper.UserMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

@WebServlet("/LoginServlet")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Handle GET requests: display the login form
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

        UserDTO loggedInUserDTO = null;
        String errorMessage = null;

        try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession()) {

            UserMapper userMapper = session.getMapper(UserMapper.class);

            // 1. Find user by username
            User userEntity = userMapper.findByUsername(username);

            if (userEntity == null) {
                errorMessage = "用户名或密码错误。";
            }
            // 2. Validate password
            else if (userEntity.getPassword().equals(password)) {

                // 临时假设：你有一个名为 UserConverter 的工具类来处理转换
                // ✅ 编译通过修改点：调用本地转换方法
                loggedInUserDTO = convertUserEntityToDTO(userEntity);

            } else {
                errorMessage = "用户名或密码错误。";
            }

        } catch (Exception e) {
            e.printStackTrace();
            errorMessage = "系统错误，登录失败。";
        }


        if (loggedInUserDTO != null) {
            // Login successful
            HttpSession session = request.getSession();

            // 🚀 重构点：将 DTO 存储到 Session 中
            session.setAttribute("user", loggedInUserDTO);
            session.setMaxInactiveInterval(30 * 60);

            // 重定向到 DashboardServlet 作为仪表盘/主页
            response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        } else {
            // Login failed, forward back to the login page with the error message
            request.setAttribute("error", errorMessage);
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }

    // ⚠️ 临时的 DTO 转换方法，你需要将其移入一个独立的 Service 或 Util 类中
    private UserDTO convertUserEntityToDTO(User userEntity) {
        UserDTO dto = new UserDTO();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        // ❌ 错误修复：移除对 userEntity.getFullName() 的调用
        dto.setEmail(userEntity.getEmail());
        // ⚠️ DTO 不应包含敏感密码，此处不设置
        return dto;
    }
}