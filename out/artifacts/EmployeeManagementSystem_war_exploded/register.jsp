<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>用户注册 - 员工管理系统</title>
    <%-- 🌟 修正 1: CSS 链接使用 Context Path --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .register-container {
            max-width: 500px;
            margin: 5vh auto 0;
            padding: 2.5rem;
            text-align: center;
            background-color: var(--card-bg, #ffffff);
            border: 1px solid var(--border-color, #e5e7eb);
            border-radius: 12px;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.08);
        }
        .register-container h2 {
            color: var(--primary-blue, #2563eb);
            margin-bottom: 2rem;
            font-size: 1.5rem;
        }
    </style>
</head>
<body>

<div class="register-container">
    <h2>注册新账号</h2>

    <%-- Form Action 路径正确，保留不变 --%>
    <form action="${pageContext.request.contextPath}/RegisterServlet" method="post" class="data-form">

        <c:if test="${not empty requestScope.error}">
            <div class="error-message" style="margin-bottom: 1rem;">
                <c:out value="${requestScope.error}"/>
            </div>
        </c:if>

        <div class="form-group">
            <label for="username">用户名:</label>
            <input type="text" id="username" name="username" required>
        </div>

        <div class="form-group">
            <label for="email">邮箱:</label>
            <input type="email" id="email" name="email" required>
        </div>

        <div class="form-group">
            <label for="password">密码:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div class="form-group">
            <label for="confirmPassword">确认密码:</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>
        </div>

        <div class="form-actions" style="justify-content: space-between; margin-top: 1.5rem;">
            <%-- 🌟 修正 2: 返回登录链接使用 Context Path --%>
            <a href="${pageContext.request.contextPath}/login.jsp" class="btn-secondary">返回登录</a>
            <button type="submit" class="btn-primary">注册</button>
        </div>
    </form>
</div>

</body>
</html>