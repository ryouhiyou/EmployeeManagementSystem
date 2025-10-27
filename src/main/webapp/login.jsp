<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工管理系统 - 登录</title>
    <link rel="stylesheet" href="css/style.css">
</head>
<body>
<div class="login-container">
    <h2>员工信息管理系统</h2>
    <form action="LoginServlet" method="post">
        <% if (request.getAttribute("error") != null) { %>
        <p class="error-message">${requestScope.error}</p>
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
        <p>测试账号：admin / admin123</p>
        <p>manager / manager123</p>
    </form>
</div>
</body>
</html>