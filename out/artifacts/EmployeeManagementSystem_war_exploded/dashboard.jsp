<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>EMS</title>
    <!-- 引入新的统一 CSS 路径 -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* 仪表盘欢迎信息优化 */
        .content-container p {
            font-size: 1.1rem;
            color: var(--font-sub);
            margin-top: 1rem;
        }

        .dashboard-links {
            margin-top: 2.5rem;
            display: flex;
            gap: 20px;
            flex-wrap: wrap;
        }

        .dashboard-links a {
            flex-grow: 1;
            min-width: 250px;
            text-decoration: none;
            background-color: var(--card-bg);
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 2rem;
            box-shadow: var(--card-shadow);
            transition: all 0.2s;
            display: flex;
            flex-direction: column;
            align-items: center;
            text-align: center;
        }

        .dashboard-links a:hover {
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.1);
            transform: translateY(-2px);
            border-color: var(--primary-blue);
        }

        .dashboard-links i {
            font-size: 2.5rem;
            color: var(--primary-blue);
            margin-bottom: 1rem;
        }

        .dashboard-links span {
            font-size: 1.2rem;
            font-weight: 600;
            color: var(--font-main);
        }
    </style>
    <!-- 引入 Font Awesome 5 免费图标 -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
</head>
<body>
<!-- 头部导航栏使用统一的 main-header 样式 -->
<header class="main-header">
    <div class="logo">员工管理系统</div>
    <nav class="main-nav">
        <a href="${pageContext.request.contextPath}/DashboardServlet" class="active">主页</a>
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list">员工管理</a>
        <a href="${pageContext.request.contextPath}/ai_page.jsp">AI 助理</a>
        <a href="${pageContext.request.contextPath}/LogoutServlet" class="btn-logout">退出</a>
    </nav>
</header>

<div class="content-container">
    <h1>欢迎, <c:out value="${sessionScope.user.username}" default="用户"/>!</h1>
    <p>这是您的员工信息管理系统。请选择您想要进行的操作。</p>

    <div class="dashboard-links">
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list">
            <i class="fas fa-users"></i>
            <span>查看和管理员工列表</span>
        </a>
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=new">
            <i class="fas fa-user-plus"></i>
            <span>新增员工信息</span>
        </a>
        <a href="${pageContext.request.contextPath}/ai_page.jsp">
            <i class="fas fa-robot"></i>
            <span>使用 AI 助理 </span>
        </a>
    </div>
</div>
</body>
</html>
