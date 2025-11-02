<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>编辑员工</title>
    <%-- 修正 CSS 链接：使用 Context Path 确保路径正确 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%-- 使用 Controller 中设置的属性名 employeeDTO --%>
<c:set var="emp" value="${requestScope.employeeDTO}"/>

<div class="content-container form-page">
    <h1>编辑员工信息 - ${emp.name}</h1>

    <c:if test="${not empty requestScope.error}">
        <div class="error-message" style="margin-bottom: 1rem;">
            <c:out value="${requestScope.error}"/>
        </div>
    </c:if>

    <%-- 🌟 修正 1: 确保 Form Action 指向正确的 Servlet 路径 /EmployeeServlet --%>
    <form action="${pageContext.request.contextPath}/EmployeeServlet?action=update" method="post" class="data-form">
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
            <label for="hireDate">入职日期:</label>
            <c:set var="hireDateStr"><fmt:formatDate value="${emp.hireDate}" pattern="yyyy-MM-dd"/></c:set>
            <input type="date" id="hireDate" name="hireDate" value="${hireDateStr}" required>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-primary">保存修改</button>

            <%-- 🌟 修正 2: 确保取消链接指向正确的 Servlet 路径 /EmployeeServlet --%>
            <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list" class="btn-secondary">取消</a>
        </div>
    </form>
</div>
</body>
</html>