<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工管理系统 - 登录</title>
    <link rel="stylesheet" href="css/style.css">
    <style>
        /* 局部样式：确保链接样式与主色调一致 */
        .text-link {
            color: var(--primary-blue, #2563eb); /* 默认使用您外部 CSS 中的主蓝色 */
            text-decoration: none;
            font-weight: 600;
            transition: color 0.2s;
        }
        .text-link:hover {
            text-decoration: underline;
        }
        .test-accounts {
            margin-top: 1.5rem;
            color: var(--font-sub, #9ca3af);
            font-size: 0.9rem;
            line-height: 1.4;
        }
        .test-accounts strong {
            color: var(--font-main, #1f2937);
            font-weight: 500;
            display: block; /* 确保账号信息单独成行 */
            margin-top: 0.2rem;
        }
    </style>
</head>
<body>
<div class="login-container">
    <h2>员工信息管理系统</h2>
    <form action="LoginServlet" method="post">
        <%-- 使用 error-message 样式显示错误信息 --%>
        <% if (request.getAttribute("error") != null) { %>
        <p class="error-message">${requestScope.error}</p>
        <% } %>

        <%-- 如果注册成功，显示成功信息 --%>
        <% if (request.getParameter("registration") != null && request.getParameter("registration").equals("success")) { %>
        <p style="color: green; font-weight: bold; margin-bottom: 1rem;">注册成功，请登录。</p>
        <% } %>


        <div class="form-group">
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div class="form-group">
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <button type="submit" class="btn-login">登录</button>
    </form>

    <p style="margin-top: 1.5rem;">
        还没有账号？
        <%-- ✅ 修正：使用上下文路径确保链接在任何部署环境下都正确 --%>
        <a href="${pageContext.request.contextPath}/register.jsp" class="text-link">立即注册</a>
    </p>

    <div class="test-accounts">
        测试账号：
        <strong>admin / admin123</strong>
        <strong>manager / manager123</strong>
    </div>
</div>
</body>
</html>