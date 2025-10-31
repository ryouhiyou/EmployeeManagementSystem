<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工列表</title>
    <%-- 确保 CSS 路径正确 --%>
    <link rel="stylesheet" href="<c:url value='/css/style.css'/>">
    <style>
        /* 样式：使搜索框和新增按钮对齐 */
        .search-form {
            display: flex;
        }
        .search-form input[type="text"] {
            padding: 8px;
            border: 1px solid #ccc;
            border-radius: 4px 0 0 4px;
            width: 300px;
        }
        .search-form button {
            padding: 8px 15px;
            background-color: #007bff;
            color: white;
            border: 1px solid #007bff;
            border-radius: 0 4px 4px 0;
            cursor: pointer;
        }
        /* 样式：分页导航 */
        .pagination {
            margin-top: 20px;
            text-align: center;
        }
        .pagination a, .pagination span {
            padding: 8px 12px;
            margin: 0 4px;
            border: 1px solid #ddd;
            text-decoration: none;
            color: #007bff;
            border-radius: 4px;
        }
        .pagination .active {
            background-color: #007bff;
            color: white;
            pointer-events: none;
            border-color: #007bff;
        }
        .pagination .disabled {
            color: #ccc;
            pointer-events: none;
        }
        /* 样式：使新增按钮和搜索表单分列两边 */
        .toolbar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 20px;
        }
    </style>
</head>
<body>

<div class="content-container">

    <div class="header-nav-bar">
        <%-- 总记录数现在从 Servlet 设置的 totalRecords 属性中获取 --%>
        <h1>员工列表 (${requestScope.totalRecords} 人)</h1>
        <a href="<c:url value='/DashboardServlet'/>" class="btn-secondary" style="margin-left: 10px;">
            返回主页
        </a>
    </div>

    <div class="toolbar">
        <a href="<c:url value='/EmployeeServlet?action=new'/>" class="btn-success">新增员工</a>

        <form action="<c:url value='/EmployeeServlet'/>" method="get" class="search-form">
            <input type="hidden" name="action" value="list">
            <input type="text" name="search" placeholder="按姓名或部门搜索..."
                   value="${requestScope.searchKeyword}" autocomplete="off">
            <button type="submit">搜索</button>
        </form>
    </div>

    <c:if test="${param.message eq 'add_success'}"><p class="success-message">员工添加成功！</p></c:if>
    <c:if test="${param.message eq 'update_success'}"><p class="success-message">员工信息更新成功！</p></c:if>
    <c:if test="${param.message eq 'delete_success'}"><p class="success-message">员工删除成功！</p></c:if>

    <%-- 搜索无结果提示 --%>
    <c:if test="${requestScope.totalRecords eq 0 && not empty requestScope.searchKeyword}">
        <p class="error-message">没有找到匹配 "${requestScope.searchKeyword}" 的员工记录。</p>
    </c:if>

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
        <%-- 循环遍历当前页的员工列表 --%>
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
        <%-- 如果当前页列表为空 --%>
        <c:if test="${empty listEmployee && empty requestScope.searchKeyword}">
            <tr><td colspan="7">当前没有员工数据。</td></tr>
        </c:if>
        </tbody>
    </table>

    <c:if test="${requestScope.totalPages > 1}">
        <div class="pagination">
                <%-- 定义基础链接，包含 action 和搜索关键字（如果存在） --%>
            <c:url var="baseLink" value="/EmployeeServlet">
                <c:param name="action" value="list"/>
                <c:if test="${not empty requestScope.searchKeyword}">
                    <c:param name="search" value="${requestScope.searchKeyword}"/>
                </c:if>
            </c:url>

                <%-- 上一页 --%>
            <c:choose>
                <c:when test="${requestScope.currentPage > 1}">
                    <a href="${baseLink}&page=${requestScope.currentPage - 1}">上一页</a>
                </c:when>
                <c:otherwise>
                    <span class="disabled">上一页</span>
                </c:otherwise>
            </c:choose>

                <%-- 页码链接 --%>
            <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                <c:choose>
                    <c:when test="${requestScope.currentPage == i}">
                        <span class="active">${i}</span>
                    </c:when>
                    <c:otherwise>
                        <a href="${baseLink}&page=${i}">${i}</a>
                    </c:otherwise>
                </c:choose>
            </c:forEach>

                <%-- 下一页 --%>
            <c:choose>
                <c:when test="${requestScope.currentPage < requestScope.totalPages}">
                    <a href="${baseLink}&page=${requestScope.currentPage + 1}">下一页</a>
                </c:when>
                <c:otherwise>
                    <span class="disabled">下一页</span>
                </c:otherwise>
            </c:choose>
        </div>
    </c:if>

</div>
</body>
</html>