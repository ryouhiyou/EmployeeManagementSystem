<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工列表</title>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
</head>
<body>

<div class="content-container">

    <div class="header-nav-bar">
        <h1>员工列表 (${listEmployee.size()} 人)</h1>
        <a href="<c:url value='/DashboardServlet'/>" class="btn-secondary" style="margin-left: 10px;">
            返回主页
        </a>
    </div>

    <div class="toolbar">
        <a href="<c:url value='/EmployeeServlet?action=new'/>" class="btn-success">新增员工</a>
    </div>

    <c:if test="${param.message eq 'add_success'}"><p class="success-message">员工添加成功！</p></c:if>
    <c:if test="${param.message eq 'update_success'}"><p class="success-message">员工信息更新成功！</p></c:if>
    <c:if test="${param.message eq 'delete_success'}"><p class="success-message">员工删除成功！</p></c:if>

    <table class="data-table">
        <thead>
        <tr>
            <th>ID</th>
            <th>姓名</th>
            <th>部门</th>
            <th>职位</th>
            <th>薪资</th>
            <th>入职日期</th>
            <th>操作</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="employee" items="${requestScope.listEmployee}">
            <tr>
                <td>${employee.id}</td>
                <td>${employee.name}</td>
                <td>${employee.department}</td>
                <td>${employee.position}</td>
                <td><fmt:formatNumber value="${employee.salary}" type="currency" currencySymbol="¥" maxFractionDigits="2"/></td>
                <td><fmt:formatDate value="${employee.hireDate}" pattern="yyyy-MM-dd"/></td>
                <td class="action-links">
                    <a href="<c:url value='/EmployeeServlet?action=view&id=${employee.id}'/>">查看</a> |
                    <a href="<c:url value='/EmployeeServlet?action=edit&id=${employee.id}'/>">编辑</a> |
                    <a href="<c:url value='/EmployeeServlet?action=delete&id=${employee.id}'/>" onclick="return confirm('确定要删除 ${employee.name} 吗?');" class="link-danger">删除</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty listEmployee}">
            <tr><td colspan="7">当前没有员工数据。</td></tr>
        </c:if>
        </tbody>
    </table>
</div>
</body>
</html>