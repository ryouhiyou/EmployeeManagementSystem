<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>查看员工信息</title>
    <%-- 修正 CSS 链接：使用 Context Path 确保路径正确 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <%-- 引入必要的 Bootstrap/FontAwesome CSS (如果 style.css 中不包含) --%>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        /* 简单补充一些样式确保页面可用性 */
        .content-container { max-width: 800px; margin: 3rem auto; padding: 2rem; background: #fff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
        .detail-table { width: 100%; border-collapse: collapse; margin-top: 2rem; }
        .detail-table th, .detail-table td { padding: 12px 15px; text-align: left; border-bottom: 1px solid #f0f0f0; }
        .detail-table th { width: 30%; background-color: #f7f7f7; font-weight: 600; }
        .form-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem; }
        .btn-primary, .btn-secondary { padding: 0.75rem 1.5rem; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; text-align: center; }
        .btn-primary { background-color: #0ea5e9; color: white; }
        .btn-secondary { background-color: #6b7280; color: white; }
    </style>
</head>
<body>
<c:set var="emp" value="${requestScope.employeeDTO}"/> <%-- ⚠️ 建议：使用 employeeDTO 以匹配 Controller 逻辑 --%>

<div class="content-container view-page">
    <h1>员工详细信息 - ${emp.name}</h1>

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
        <%-- 🌟 修正 1: 确保编辑链接包含 Context Path --%>
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=edit&id=${emp.id}" class="btn-primary">编辑</a>

        <%-- 🌟 修正 2: 确保返回列表链接包含 Context Path --%>
        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list" class="btn-secondary">返回列表</a>
    </div>
</div>
</body>
</html>