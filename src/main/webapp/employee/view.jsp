<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>查看员工信息</title>
    <!-- 使用新的统一 CSS 路径 -->
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>
<c:set var="emp" value="${requestScope.employee}"/>

<div class="content-container view-page">
    <h1>员工详细信息 - ${emp.name}</h1>

    <!-- 详情表格使用 detail-table 样式 -->
    <table class="detail-table">
        <tr>
            <th>ID</th>
            <td>${emp.id}</td>
        </tr>
        <tr>
            <th>姓名</th>
            <td>${emp.name}</td>
        </tr>
        <tr>
            <th>部门</th>
            <td>${emp.department}</td>
        </tr>
        <tr>
            <th>职位</th>
            <td>${emp.position}</td>
        </tr>
        <tr>
            <th>薪资</th>
            <!-- 格式化薪资 -->
            <td><fmt:formatNumber value="${emp.salary}" type="currency" currencySymbol="¥" maxFractionDigits="2"/></td>
        </tr>
        <tr>
            <th>邮箱</th>
            <td>${emp.email}</td>
        </tr>
        <tr>
            <th>电话</th>
            <td>${emp.phone}</td>
        </tr>
        <tr>
            <th>入职日期</th>
            <td><fmt:formatDate value="${emp.hireDate}" pattern="yyyy年MM月dd日"/></td>
        </tr>
        <tr>
            <th>创建时间</th>
            <td><fmt:formatDate value="${emp.createdAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
        <tr>
            <th>最后更新</th>
            <td><fmt:formatDate value="${emp.updatedAt}" pattern="yyyy-MM-dd HH:mm:ss"/></td>
        </tr>
    </table>

    <div class="form-actions">
        <a href="<c:url value='/EmployeeServlet?action=edit&id=${emp.id}'/>" class="btn-primary">编辑</a>
        <a href="<c:url value='/EmployeeServlet?action=list'/>" class="btn-secondary">返回列表</a>
    </div>
</div>
</body>
</html>