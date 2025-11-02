<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>新增员工</title>
    <%-- 修正 CSS 链接：使用 Context Path 确保路径正确 --%>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">

    <style>
        /* 这里的样式是可选的，用于美化表单 */
        .content-container { max-width: 800px; margin: 3rem auto; padding: 2rem; background: #fff; border-radius: 8px; box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1); }
        .form-group { margin-bottom: 1.5rem; }
        .form-group label { display: block; margin-bottom: 0.5rem; font-weight: 500; }
        .form-group input, .form-group select { width: 100%; padding: 0.75rem; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; }
        .form-actions { display: flex; justify-content: flex-end; gap: 1rem; margin-top: 2rem; }
        .btn-success, .btn-secondary { padding: 0.75rem 1.5rem; border: none; border-radius: 4px; cursor: pointer; text-decoration: none; text-align: center; }
        .btn-success { background-color: #10b981; color: white; }
        .btn-secondary { background-color: #6b7280; color: white; }
        .error-message { color: #ef4444; background-color: #fee2e2; border: 1px solid #fca5a5; padding: 0.75rem; border-radius: 4px; }
    </style>
</head>
<body>

<div class="content-container form-page">
    <h1 class="text-center">新增员工信息</h1>

    <%-- 🌟 修正 1: 确保 Form Action 指向正确的 Servlet 路径 /EmployeeServlet --%>
    <form action="${pageContext.request.contextPath}/EmployeeServlet" method="post" class="data-form">

        <input type="hidden" name="action" value="insert">

        <c:if test="${not empty requestScope.error}">
            <div class="error-message" style="margin-bottom: 1rem;">
                <i class="fas fa-exclamation-triangle me-2"></i>
                <c:out value="${requestScope.error}"/>
            </div>
        </c:if>

        <div class="row">
            <div class="col-md-6 form-group">
                <label for="name">姓名:</label>
                <input type="text" id="name" name="name" required
                       value="<c:out value='${employeeDTO.name}'/>">
            </div>
            <div class="col-md-6 form-group">
                <label for="email">邮箱:</label>
                <input type="email" id="email" name="email" required
                       value="<c:out value='${employeeDTO.email}'/>">
            </div>
        </div>

        <div class="row">
            <div class="col-md-6 form-group">
                <label for="department">部门:</label>
                <input type="text" id="department" name="department" required
                       value="<c:out value='${employeeDTO.department}'/>">
            </div>
            <div class="col-md-6 form-group">
                <label for="position">职位:</label>
                <input type="text" id="position" name="position" required
                       value="<c:out value='${employeeDTO.position}'/>">
            </div>
        </div>

        <div class="row">
            <div class="col-md-4 form-group">
                <label for="salary">薪资 (¥):</label>
                <input type="number" id="salary" name="salary" step="0.01" required
                       value="<c:out value='${employeeDTO.salary}'/>">
            </div>
            <div class="col-md-4 form-group">
                <label for="phone">电话:</label>
                <input type="text" id="phone" name="phone" required
                       value="<c:out value='${employeeDTO.phone}'/>">
            </div>
            <div class="col-md-4 form-group">
                <label for="hireDate">入职日期:</label>
                <input type="date" id="hireDate" name="hireDate" required
                       value="<c:out value='${employeeDTO.hireDate}'/>">
            </div>
        </div>

        <div class="form-actions">
            <button type="submit" class="btn-success">
                <i class="fas fa-save me-2"></i> 保存
            </button>

            <%-- 🌟 修正 2: 确保返回链接指向正确的 Servlet 路径 /EmployeeServlet --%>
            <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list" class="btn-secondary">
                <i class="fas fa-arrow-left me-2"></i> 返回列表
            </a>
        </div>
    </form>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>