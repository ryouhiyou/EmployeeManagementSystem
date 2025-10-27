<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>404 - 页面未找到</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<div class="error-container">
    <h1>404 页面未找到</h1>
    <p>您请求的资源或页面不存在。</p>
    <a href="<%= request.getContextPath() %>/DashboardServlet" class="btn-primary">返回仪表盘</a>
</div>
</body>
</html>