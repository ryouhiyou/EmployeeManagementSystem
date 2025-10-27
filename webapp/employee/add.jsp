<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新增员工</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<div class="content-container form-page">
    <h1>新增员工信息</h1>
    <form action="<c:url value='/EmployeeServlet?action=insert'/>" method="post" class="data-form">
        <div class="form-group">
            <label for="name">姓名:</label>
            <input type="text" id="name" name="name" required>
        </div>
        <div class="form-group">
            <label for="department">部门:</label>
            <input type="text" id="department" name="department" required>
        </div>
        <div class="form-group">
            <label for="position">职位:</label>
            <input type="text" id="position" name="position" required>
        </div>
        <div class="form-group">
            <label for="salary">薪资 (¥):</label>
            <input type="number" id="salary" name="salary" step="0.01" required>
        </div>
        <div class="form-group">
            <label for="email">邮箱:</label>
            <input type="email" id="email" name="email">
        </div>
        <div class="form-group">
            <label for="phone">电话:</label>
            <input type="text" id="phone" name="phone">
        </div>
        <div class="form-group">
            <label for="hire_date">入职日期:</label>
            <input type="date" id="hire_date" name="hire_date" required>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-success">保存</button>
            <a href="<c:url value='/EmployeeServlet?action=list'/>" class="btn-secondary">返回列表</a>
        </div>
    </form>
</div>
</body>
</html>