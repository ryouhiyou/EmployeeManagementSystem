<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新增员工</title>
    <!-- 使用新的统一 CSS 路径 -->
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<div class="content-container form-page">
    <h1>新增员工信息</h1>
    <!-- 表单使用 data-form 居中且约束宽度 -->
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
            <!-- 统一使用 input[type=number] 的扁平化样式 -->
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
            <!-- 统一使用 input[type=date] 的扁平化样式 -->
            <input type="date" id="hire_date" name="hire_date" required>
        </div>

        <div class="form-actions">
            <!-- 提交使用 btn-success 绿色 -->
            <button type="submit" class="btn-success">保存</button>
            <!-- 返回使用 btn-secondary 灰色 -->
            <a href="<c:url value='/EmployeeServlet?action=list'/>" class="btn-secondary">返回列表</a>
        </div>
    </form>
</div>
</body>
</html>
