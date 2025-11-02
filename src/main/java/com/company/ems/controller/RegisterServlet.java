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
import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * 处理GET请求，用于显示注册表单
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // JSP文件在 /webapp/register.jsp，转发路径正确
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /**
     * 处理POST请求，用于处理注册逻辑
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // 1. 从请求中提取 DTO
        UserDTO registerDTO = extractUserDTOFromRequest(request);
        String confirmPassword = request.getParameter("confirmPassword");

        String errorMessage = null;

        // 2. Perform basic validation
        if (registerDTO.getUsername().isEmpty() ||
                registerDTO.getEmail().isEmpty() ||
                registerDTO.getPassword().isEmpty() ||
                confirmPassword.isEmpty()) {
            errorMessage = "所有字段都是必填项。";
        } else if (!registerDTO.getPassword().equals(confirmPassword)) {
            errorMessage = "两次输入的密码不一致。";
        } else if (registerDTO.getPassword().length() < 6) {
            errorMessage = "密码长度不能少于6位。";
        } else {
            // 3. Perform business validation (database check)
            try (SqlSession session = MyBatisUtil.getSqlSessionFactory().openSession(true)) {

                UserMapper userMapper = session.getMapper(UserMapper.class);

                if (userMapper.findByUsername(registerDTO.getUsername()) != null) {
                    errorMessage = "该用户名已被注册。";
                }
                else if (userMapper.findByEmail(registerDTO.getEmail()) != null) {
                    errorMessage = "该邮箱已被注册。";
                } else {
                    // 4. Validation passed, execute registration logic
                    User newUserEntity = convertUserDTOToEntity(registerDTO);

                    int rowsAffected = userMapper.addUser(newUserEntity);

                    if (rowsAffected > 0) {
                        // 注册成功，重定向到登录页（路径正确）
                        response.sendRedirect(request.getContextPath() + "/login.jsp?registration=success");
                        return; // 成功后立即返回，防止继续执行转发逻辑
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
        // JSP文件在 /webapp/register.jsp，转发路径正确
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }

    /**
     * 辅助方法：从 HttpRequest 中提取数据，并组装成 DTO 对象
     */
    private UserDTO extractUserDTOFromRequest(HttpServletRequest request) {
        UserDTO dto = new UserDTO();
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        dto.setUsername(username != null ? username.trim() : "");
        dto.setEmail(email != null ? email.trim() : "");
        dto.setPassword(password != null ? password : "");

        return dto;
    }

    /**
     * DTO 转换为 Entity 的方法。
     */
    private User convertUserDTOToEntity(UserDTO dto) {
        User entity = new User();
        entity.setUsername(dto.getUsername());
        entity.setPassword(dto.getPassword());
        entity.setEmail(dto.getEmail());
        // 假设 User 实体中还有其他默认值需要设置（如：注册日期、默认角色等）
        return entity;
    }
}