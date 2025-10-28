<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>EMS</title>
    <%-- 确保 CSS 路径使用绝对路径 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="main-header">
    <div class="logo">员工管理系统</div>
    <nav class="main-nav">
        <%-- 确保链接路径正确 --%>
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list">员工管理</a>

        <a href="${pageContext.request.contextPath}/ai_page.jsp">AI 助理</a>

        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">退出</a>

    </nav>
</header>

<div class="content-container">
    <h1>欢迎, <c:out value="${sessionScope.user.username}" default="用户"/>!</h1>
    <p>这是您的员工管理系统。</p>
</div>
</body>
</html>