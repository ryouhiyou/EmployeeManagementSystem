<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑员工</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<c:set var="emp" value="${requestScope.employee}"/>

<div class="content-container form-page">
    <h1>编辑员工信息 - ${emp.name}</h1>
    <form action="<c:url value='/EmployeeServlet?action=update'/>" method="post" class="data-form">
        <input type="hidden" name="id" value="${emp.id}">

        <div class="form-group">
            <label for="name">姓名:</label>
            <input type="text" id="name" name="name" value="${emp.name}" required>
        </div>
        <div class="form-group">
            <label for="department">部门:</label>
            <input type="text" id="department" name="department" value="${emp.department}" required>
        </div>
        <div class="form-group">
            <label for="position">职位:</label>
            <input type="text" id="position" name="position" value="${emp.position}" required>
        </div>
        <div class="form-group">
            <label for="salary">薪资 (¥):</label>
            <input type="number" id="salary" name="salary" step="0.01" value="${emp.salary}" required>
        </div>
        <div class="form-group">
            <label for="email">邮箱:</label>
            <input type="email" id="email" name="email" value="${emp.email}">
        </div>
        <div class="form-group">
            <label for="phone">电话:</label>
            <input type="text" id="phone" name="phone" value="${emp.phone}">
        </div>
        <div class="form-group">
            <label for="hire_date">入职日期:</label>
            <%-- 由于Date是java.sql.Date，需要格式化为HTML date input需要的yyyy-MM-dd格式 --%>
            <c:set var="hireDateStr"><fmt:formatDate value="${emp.hireDate}" pattern="yyyy-MM-dd"/></c:set>
            <input type="date" id="hire_date" name="hire_date" value="${hireDateStr}" required>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-primary">保存修改</button>
            <a href="<c:url value='/EmployeeServlet?action=list'/>" class="btn-secondary">取消</a>
        </div>
    </form>
</div>
</body>
</html>