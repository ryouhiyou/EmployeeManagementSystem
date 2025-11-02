<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %> <!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>员工管理系统 - 列表</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-QWTKZyjpPEjISv5WaRU9OFeRpok6YctnYmDr5pNlyT2bRjXh0JMhjY6hW+ALEwIH" crossorigin="anonymous">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/5.15.4/css/all.min.css">
    <style>
        /* [保持原有样式不变] */
        :root {
            /* 主色调：冷静的青蓝色 (Flat Primary Color) */
            --primary-blue: #0ea5e9; /* Sky Blue 500 */
            /* 页面背景：极浅灰 */
            --light-bg: #f8fafc; /* Gray 50 */
            /* 卡片背景：纯白，轻微阴影 */
            --card-shadow: 0 1px 3px rgba(0, 0, 0, 0.05);
            /* 边框色：柔和浅灰 */
            --border-color: #e2e8f0; /* Gray 200 */
            /* 主文本色：深沉黑灰 */
            --font-main: #1e293b; /* Slate 900 */
            /* 辅助文本色：中性灰 */
            --font-sub: #64748b; /* Slate 500 */
        }

        body {
            background-color: var(--light-bg);
            font-family: 'Inter', 'Microsoft YaHei', sans-serif;
            color: var(--font-main);
        }

        /* 关键调整: 自动适应屏幕宽度，并设置合理的最大值 */
        .container {
            width: 96%; /* 使用百分比适应屏幕 */
            max-width: 1400px; /* 最大宽度保持舒适 */
            padding-top: 3rem;
            padding-bottom: 3rem;
        }

        /* 核心卡片容器：扁平化，轻微圆角，干净边框 */
        .data-card {
            background-color: #fff;
            border: 1px solid var(--border-color);
            border-radius: 8px;
            box-shadow: var(--card-shadow);
            padding: 2.5rem;
        }

        /* 顶部操作区布局 */
        .control-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 2.5rem; /* 增加与搜索区的间距 */
        }

        /* 按钮风格：扁平化，无渐变，圆角 */
        .btn {
            border-radius: 6px;
            font-weight: 500;
            transition: all 0.2s;
            padding: 0.5rem 1rem; /* 统一按钮高度 */
        }

        /* 主要按钮风格：使用主色调，无边框 */
        .btn-primary-flat {
            background-color: var(--primary-blue);
            border-color: var(--primary-blue);
            color: white;
        }
        .btn-primary-flat:hover {
            background-color: #03a0e1; /* Slightly darker */
            border-color: #03a0e1;
            color: white;
            box-shadow: 0 2px 5px rgba(14, 165, 233, 0.3);
        }

        /* 辅助按钮风格：轻微边框，背景透明 */
        .btn-outline-flat {
            border: 1px solid var(--border-color);
            color: var(--font-main);
            background-color: transparent;
        }
        .btn-outline-flat:hover {
            background-color: var(--light-bg);
            border-color: #cbd5e1; /* Gray 300 */
            color: var(--font-main);
        }

        /* 搜索框和表单控制 */
        .form-control {
            border-radius: 6px;
            border-color: var(--border-color);
            padding: 0.5rem 1rem;
        }
        .form-control:focus {
            border-color: var(--primary-blue);
            box-shadow: 0 0 0 0.25rem rgba(14, 165, 233, 0.25); /* 扁平化的焦点阴影 */
        }

        /* --- 搜索组件一体化样式 --- */
        .search-container {
            position: relative;
            width: 100%;
            max-width: 450px;
        }

        /* 搜索输入框：右侧留出按钮空间，并与容器圆角对齐 */
        .search-container .form-control {
            padding-right: 3rem; /* 为搜索图标留出空间 */
        }

        /* 搜索提交按钮/图标：绝对定位，内嵌到输入框内 */
        .search-container .search-btn {
            position: absolute;
            top: 0;
            right: 0;
            height: 100%;
            width: 3rem; /* 保持图标按钮的宽度 */
            background: transparent; /* 背景透明 */
            border: none;
            color: var(--font-sub); /* 图标使用中性灰 */
            transition: color 0.2s;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 0 6px 6px 0; /* 仅右侧圆角 */
        }

        .search-container .search-btn:hover {
            color: var(--primary-blue); /* 悬停时变色 */
        }

        /* 表格样式 (与上次一致) */
        .table-minimal th, .table-minimal td {
            padding: 1rem 0.75rem; /* 舒适的行高 */
            vertical-align: middle;
            border-top: none;
            border-bottom: 1px solid #f1f5f9; /* 极浅的水平分隔线 */
        }
        .table-minimal thead th {
            color: var(--font-sub);
            font-weight: 600;
            font-size: 0.9rem;
            text-transform: capitalize;
            background-color: #f8fafc;
            border-bottom: 1px solid var(--border-color);
            border-top: 1px solid var(--border-color);
        }
        .table-minimal tbody tr:hover {
            background-color: #f8fafc;
        }

        .salary-text, .date-text {
            color: var(--font-main);
            font-weight: 500;
            padding: 0;
        }

        /* 操作链接 (与上次一致) */
        .action-link {
            color: var(--primary-blue);
            text-decoration: none;
            font-size: 0.9rem;
            font-weight: 500;
            margin: 0 10px;
            transition: color 0.2s;
        }
        .action-link:hover {
            color: #0c4a6e;
        }
        .action-link.delete {
            color: #ef4444;
        }
        .action-link.delete:hover {
            color: #b91c1c;
        }

        /* 排序链接保持中性 */
        .sort-link {
            text-decoration: none;
            color: var(--font-sub);
            transition: color 0.2s;
        }
        .sort-link:hover {
            color: var(--primary-blue);
        }
        .sort-icon {
            font-size: 0.7rem;
            margin-left: 4px;
        }

        /* 分页样式 (与上次一致) */
        .pagination .page-link {
            border-radius: 6px;
            margin: 0 2px;
            border: 1px solid var(--border-color);
            color: var(--font-sub);
        }
        .pagination .page-item.active .page-link {
            background-color: var(--primary-blue);
            border-color: var(--primary-blue);
            color: white;
            box-shadow: none;
        }

    </style>
</head>
<body>
<div class="container">
    <h2 class="text-3xl font-bold mb-5 text-center text-gray-800">员工信息管理系统</h2>

    <div class="data-card">

        <div class="control-header">
            <div class="d-flex" style="gap: 12px;">
                <a href="dashboard.jsp" class="btn btn-outline-flat">
                    <i class="fas fa-home me-2"></i> 返回主页
                </a>

                <%-- 修正新增链接：使用正确的 Servlet 路径和 Context Path --%>
                <a href="${pageContext.request.contextPath}/EmployeeServlet?action=add_form" class="btn btn-primary-flat">
                    <i class="fas fa-user-plus me-2"></i> 新增员工
                </a>
            </div>

            <h4 class="mb-0 fs-5 fw-normal text-muted">
                总记录:
                <span class="ms-2 fs-6 fw-bold" style="color: var(--primary-blue);">
                    ${requestScope.totalRecords}
                </span>
            </h4>
        </div>

        <div class="d-flex mb-4 align-items-center justify-content-end">
            <%-- 修正搜索表单的 action：使用正确的 Servlet 路径和 Context Path --%>
            <form action="${pageContext.request.contextPath}/EmployeeServlet" method="GET" class="search-container">
                <input type="hidden" name="action" value="list">
                <input type="hidden" name="sortBy" value="${requestScope.sortBy}">
                <input type="hidden" name="sortOrder" value="${requestScope.sortOrder}">

                <input type="text" name="search" class="form-control" placeholder="按姓名、部门或职位搜索..." value="${requestScope.search}">

                <button type="submit" class="search-btn">
                    <i class="fas fa-search"></i>
                </button>
            </form>
        </div>

        <c:if test="${param.message != null}">
            <div class="alert alert-success alert-dismissible fade show shadow-sm mb-4" role="alert" style="border-left: 4px solid #10b981;">
                <c:choose>
                    <c:when test="${param.message eq 'update_success'}"><i class="fas fa-check-circle me-2"></i> 员工信息更新成功!</c:when>
                    <c:when test="${param.message eq 'add_success'}"><i class="fas fa-check-circle me-2"></i> 员工信息添加成功!</c:when>
                    <c:when test="${param.message eq 'delete_success'}"><i class="fas fa-check-circle me-2"></i> 员工信息删除成功!</c:when>
                    <c:otherwise><i class="fas fa-info-circle me-2"></i> 操作成功!</c:otherwise>
                </c:choose>
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <div class="table-responsive">
            <table class="table table-minimal">
                <thead>
                <tr>
                    <th scope="col" style="width: 5%;">
                        <%-- 修正 ID 排序链接：使用正确的 Servlet 路径和 Context Path --%>
                        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${requestScope.currentPage}&sortBy=id&sortOrder=<c:choose><c:when test="${requestScope.sortBy eq 'id' and requestScope.sortOrder eq 'ASC'}">DESC</c:when><c:otherwise>ASC</c:otherwise></c:choose>" class="sort-link">
                            ID
                            <c:if test="${requestScope.sortBy eq 'id'}">
                                <i class="fas sort-icon <c:choose><c:when test="${requestScope.sortOrder eq 'ASC'}">fa-sort-up</c:when><c:otherwise>fa-sort-down</c:otherwise></c:choose>"></i>
                            </c:if>
                        </a>
                    </th>
                    <th scope="col" style="width: 15%;">
                        <%-- 修正姓名排序链接：使用正确的 Servlet 路径和 Context Path --%>
                        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${requestScope.currentPage}&sortBy=name&sortOrder=<c:choose><c:when test="${requestScope.sortBy eq 'name' and requestScope.sortOrder eq 'ASC'}">DESC</c:when><c:otherwise>ASC</c:otherwise></c:choose>" class="sort-link">
                            姓名
                            <c:if test="${requestScope.sortBy eq 'name'}">
                                <i class="fas sort-icon <c:choose><c:when test="${requestScope.sortOrder eq 'ASC'}">fa-sort-up</c:when><c:otherwise>fa-sort-down</c:otherwise></c:choose>"></i>
                            </c:if>
                        </a>
                    </th>
                    <th scope="col" style="width: 15%;">部门</th>
                    <th scope="col" style="width: 15%;">职位</th>
                    <th scope="col" style="width: 15%;">
                        <%-- 修正薪资排序链接：使用正确的 Servlet 路径和 Context Path --%>
                        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${requestScope.currentPage}&sortBy=salary&sortOrder=<c:choose><c:when test="${requestScope.sortBy eq 'salary' and requestScope.sortOrder eq 'ASC'}">DESC</c:when><c:otherwise>ASC</c:otherwise></c:choose>" class="sort-link">
                            薪资
                            <c:if test="${requestScope.sortBy eq 'salary'}">
                                <i class="fas sort-icon <c:choose><c:when test="${requestScope.sortOrder eq 'ASC'}">fa-sort-up</c:when><c:otherwise>fa-sort-down</c:otherwise></c:choose>"></i>
                            </c:if>
                        </a>
                    </th>
                    <th scope="col" style="width: 15%;">
                        <%-- 修正入职日期排序链接：使用正确的 Servlet 路径和 Context Path --%>
                        <a href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${requestScope.currentPage}&sortBy=hire_date&sortOrder=<c:choose><c:when test="${requestScope.sortBy eq 'hire_date' and requestScope.sortOrder eq 'ASC'}">DESC</c:when><c:otherwise>ASC</c:otherwise></c:choose>" class="sort-link">
                            入职日期
                            <c:if test="${requestScope.sortBy eq 'hire_date'}">
                                <i class="fas sort-icon <c:choose><c:when test="${requestScope.sortOrder eq 'ASC'}">fa-sort-up</c:when><c:otherwise>fa-sort-down</c:otherwise></c:choose>"></i>
                            </c:if>
                        </a>
                    </th>
                    <th scope="col" style="width: 20%;" class="text-center">操作</th>
                </tr>
                </thead>
                <tbody>
                <c:forEach var="employee" items="${requestScope.employeeDTOs}">
                    <tr>
                        <td class="text-muted"><c:out value="${employee.id}" /></td>
                        <td><c:out value="${employee.name}" /></td>
                        <td><c:out value="${employee.department}" /></td>
                        <td><c:out value="${employee.position}" /></td>
                        <td><span class="salary-text">￥<c:out value="${employee.salary}" /></span></td>

                        <td class="text-muted">
                            <fmt:formatDate value="${employee.hireDate}" pattern="yyyy年MM月dd日"/>
                        </td>

                        <td class="text-center">
                                <%-- 修正查看链接：使用正确的 Servlet 路径和 Context Path --%>
                            <a href="${pageContext.request.contextPath}/EmployeeServlet?action=view&id=${employee.id}" class="action-link">
                                <i class="fas fa-eye me-1"></i> 查看
                            </a>
                                <%-- 修正编辑链接：使用正确的 Servlet 路径和 Context Path --%>
                            <a href="${pageContext.request.contextPath}/EmployeeServlet?action=edit&id=${employee.id}" class="action-link">
                                <i class="fas fa-edit me-1"></i> 编辑
                            </a>
                                <%-- 修正删除链接：使用正确的 Servlet 路径和 Context Path --%>
                            <a href="${pageContext.request.contextPath}/EmployeeServlet?action=delete&id=${employee.id}" class="action-link delete" onclick="return confirm('确定删除员工 ${employee.name} 吗？');">
                                <i class="fas fa-trash-alt me-1"></i> 删除
                            </a>
                        </td>
                    </tr>
                </c:forEach>

                <c:if test="${empty requestScope.employeeDTOs}">
                    <tr>
                        <td colspan="7" class="text-center text-muted py-5">
                            <i class="fas fa-info-circle me-2"></i> 未找到匹配的员工记录。
                        </td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <nav aria-label="Page navigation" class="mt-5 pt-3 border-top" style="border-color: var(--border-color) !important;">
            <ul class="pagination justify-content-center">
                <c:set var="prevPage" value="${requestScope.currentPage - 1}"/>
                <c:set var="nextPage" value="${requestScope.currentPage + 1}"/>

                <%-- 修正上一页链接：使用正确的 Servlet 路径和 Context Path --%>
                <li class="page-item <c:if test="${requestScope.currentPage == 1}">disabled</c:if>">
                    <a class="page-link" href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${prevPage}&sortBy=${requestScope.sortBy}&sortOrder=${requestScope.sortOrder}"><i class="fas fa-chevron-left"></i></a>
                </li>

                <c:forEach begin="1" end="${requestScope.totalPages}" var="i">
                    <li class="page-item <c:if test="${requestScope.currentPage == i}">active</c:if>">
                            <%-- 修正页码链接：使用正确的 Servlet 路径和 Context Path --%>
                        <a class="page-link" href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${i}&sortBy=${requestScope.sortBy}&sortOrder=${requestScope.sortOrder}"><c:out value="${i}"/></a>
                    </li>
                </c:forEach>

                <%-- 修正下一页链接：使用正确的 Servlet 路径和 Context Path --%>
                <li class="page-item <c:if test="${requestScope.currentPage == requestScope.totalPages}">disabled</c:if>">
                    <a class="page-link" href="${pageContext.request.contextPath}/EmployeeServlet?action=list&search=${requestScope.search}&page=${nextPage}&sortBy=${requestScope.sortBy}&sortOrder=${requestScope.sortOrder}"><i class="fas fa-chevron-right"></i></a>
                </li>
            </ul>
        </nav>

        <p class="text-center text-muted mt-3">
            <small>当前显示 ${requestScope.employeeDTOs.size()} 条记录 / 总记录数 ${requestScope.totalRecords} 条，共 ${requestScope.totalPages} 页</small>
        </p>
    </div>
</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js" integrity="sha384-YvpcrYf0tY3lHB60NNkmXc5s9fDVZLESaAA55NDzOxhy9GkcIdslK1eN7N6jIeHz" crossorigin="anonymous"></script>
</body>
</html>