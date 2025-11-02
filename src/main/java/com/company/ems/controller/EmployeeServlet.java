package com.company.ems.controller;

import com.company.ems.dto.EmployeeDTO;
import com.company.ems.dto.UserDTO; // 引入 UserDTO 以便从 Session 中获取用户 ID
import com.company.ems.model.Employee;
import com.company.ems.mapper.EmployeeMapper;
import com.company.ems.util.MyBatisUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.apache.ibatis.session.SqlSession;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;


@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {

    private EmployeeMapper employeeMapperInstance;

    @Override
    public void init() throws ServletException {
        // Servlet 初始化逻辑（如果需要连接池或其他初始化操作可以在此添加）
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求的字符编码
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list"; // 默认操作是列出员工
        }

        try {
            switch (action) {
                case "list":
                    listEmployees(request, response);
                    break;
                case "add_form": // 处理显示新增员工表单的请求
                    showAddForm(request, response);
                    break;
                case "view":
                    viewEmployee(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                default:
                    listEmployees(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 统一错误处理
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred: " + e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置请求的字符编码
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "insert"; // 默认 POST 操作是插入
        }

        try {
            switch (action) {
                case "insert":
                    insertEmployee(request, response);
                    break;
                case "update":
                    updateEmployee(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown action: " + action);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "An internal server error occurred: " + e.getMessage());
        }
    }

    // --- Session 帮助方法 ---

    /**
     * 统一的 Session 检查和用户ID提取方法
     */
    private Integer getUserIdFromSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }

        // 修正点：从 Session 中获取名为 "user" 的 DTO 对象，并提取其 ID
        Object userObj = session.getAttribute("user");
        if (userObj instanceof UserDTO) {
            return ((UserDTO) userObj).getId();
        }
        return null;
    }


    // --- 核心业务方法 ---

    /**
     * 处理查看员工详细信息的请求，转发到 /employee/view.jsp
     */
    private void viewEmployee(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            // 如果缺少ID，默认返回列表页
            listEmployees(request, response);
            return;
        }

        try {
            int employeeId = Integer.parseInt(idParam);

            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);

                Employee employeeEntity = employeeMapperInstance.selectEmployeeById(employeeId);

                if (employeeEntity == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found with ID: " + employeeId);
                    return;
                }

                EmployeeDTO employeeDTO = toDTO(employeeEntity);

                // 将 DTO 放入 Request 作用域，供 view.jsp 使用
                request.setAttribute("employeeDTO", employeeDTO);

                // 转发到正确的 JSP 路径 /employee/view.jsp
                request.getRequestDispatcher("/employee/view.jsp").forward(request, response);

            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID format");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Database error in viewEmployee", e);
        }
    }

    /**
     * 显示新增员工表单，转发到 /employee/add.jsp
     */
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 转发到位于 /webapp/employee/add.jsp 的页面
        request.getRequestDispatcher("/employee/add.jsp").forward(request, response);
    }

    /**
     * 处理分页、搜索和排序后的员工列表
     */
    private void listEmployees(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String search = request.getParameter("search");
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");
        int page = 1;
        int limit = 10;

        try {
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                page = Integer.parseInt(pageParam);
            }
        } catch (NumberFormatException e) {
            // 使用默认值 1
        }

        // 防止 page < 1
        if (page < 1) page = 1;

        int offset = (page - 1) * limit;

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);

            // 1. 获取总记录数
            int totalRecords = employeeMapperInstance.getTotalRecords(search);
            int totalPages = (int) Math.ceil((double) totalRecords / limit);

            // 如果计算出的 page 大于总页数，则跳回最后一页
            if (page > totalPages && totalRecords > 0) {
                page = totalPages;
                offset = (page - 1) * limit;
            } else if (totalRecords == 0) {
                // 如果没有记录，页数应为 1
                totalPages = 1;
                page = 1;
            }

            // 2. 查询员工列表（返回 Entity）
            List<Employee> employeeEntities = employeeMapperInstance.listAllEmployees(offset, limit, search, sortBy, sortOrder);

            // 3. 将 Entity 列表转换为 DTO 列表
            List<EmployeeDTO> employeeDTOs = toDTOList(employeeEntities);

            request.setAttribute("employeeDTOs", employeeDTOs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalRecords", totalRecords);
            request.setAttribute("search", search);
            request.setAttribute("sortBy", sortBy);
            request.setAttribute("sortOrder", sortOrder);

            // 转发到 /webapp/employee/list.jsp
            request.getRequestDispatcher("/employee/list.jsp").forward(request, response);

        } catch (Exception e) {
            // 数据库连接或 MyBatis 错误
            throw new ServletException("Database access error in listEmployees", e);
        }
    }

    /**
     * 插入新员工记录
     */
    private void insertEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 1. 获取用户 ID
        Integer userId = getUserIdFromSession(request);

        if (userId == null) {
            // 用户未登录，重定向到登录页
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        EmployeeDTO dto = extractEmployeeDTO(request);

        // 🌟 关键修正：在验证和转换之前，将创建人 ID 设置到 DTO 中
        // 解决 "Cannot invoke "java.lang.Integer.intValue()" because the return value of "com.company.ems.dto.EmployeeDTO.getCreatedBy()" is null" 错误
        dto.setCreatedBy(userId);

        // 2. 验证 DTO 数据
        String errorMessage = validateEmployeeDTO(dto);
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.setAttribute("employeeDTO", dto);
            // 验证失败，返回新增表单并显示错误
            request.getRequestDispatcher("/employee/add.jsp").forward(request, response);
            return;
        }

        try {
            // 3. DTO 转换为 Entity
            Employee newEmployeeEntity = toEntity(dto);
            // newEmployeeEntity.setCreatedBy(userId); // 这一行现在不再需要，因为 ID 已在 DTO 转换时处理

            // 4. 执行数据库插入
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) { // 自动提交
                employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);
                employeeMapperInstance.insertEmployee(newEmployeeEntity);

                // 插入成功后重定向到列表页
                response.sendRedirect(request.getContextPath() + "/EmployeeServlet?action=list&message=add_success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to insert employee: " + e.getMessage());
            request.setAttribute("employeeDTO", dto);
            // 数据库错误，返回新增表单
            request.getRequestDispatcher("/employee/add.jsp").forward(request, response);
        }
    }

    /**
     * 更新员工记录
     */
    private void updateEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // 1. 获取用户 ID
        Integer userId = getUserIdFromSession(request); // 确保用户已登录

        if (userId == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        EmployeeDTO dto = extractEmployeeDTO(request);

        // 🌟 关键修正：设置 CreatedBy (或者作为 UpdatedBy 的代理，避免 toEntity 报错)
        // 解决 "Cannot invoke "java.lang.Integer.intValue()" because..." 错误
        dto.setCreatedBy(userId);

        // 2. 验证 DTO 数据
        String errorMessage = validateEmployeeDTO(dto);
        if (errorMessage != null) {
            request.setAttribute("error", errorMessage);
            request.setAttribute("employeeDTO", dto);
            // 验证失败，返回编辑表单
            request.getRequestDispatcher("/employee/edit.jsp").forward(request, response);
            return;
        }

        try {
            // 3. DTO 转换为 Entity
            Employee updatedEmployeeEntity = toEntity(dto);

            // 4. 执行数据库更新
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) { // 自动提交
                employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);
                employeeMapperInstance.updateEmployee(updatedEmployeeEntity);

                // 更新成功后重定向到列表页
                response.sendRedirect(request.getContextPath() + "/EmployeeServlet?action=list&message=update_success");
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Failed to update employee: " + e.getMessage());
            request.setAttribute("employeeDTO", dto);
            // 数据库错误，返回编辑表单
            request.getRequestDispatcher("/employee/edit.jsp").forward(request, response);
        }
    }

    /**
     * 删除员工记录
     */
    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing employee ID");
            return;
        }

        try {
            int employeeId = Integer.parseInt(idParam);
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) { // 自动提交
                employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);
                employeeMapperInstance.deleteEmployee(employeeId);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID format");
            return;
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Failed to delete employee: " + e.getMessage());
            return;
        }

        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?action=list&message=delete_success");
    }

    /**
     * 显示编辑员工表单，转发到 /employee/edit.jsp
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null) {
            listEmployees(request, response);
            return;
        }

        try {
            int employeeId = Integer.parseInt(idParam);

            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                employeeMapperInstance = sqlSession.getMapper(EmployeeMapper.class);

                Employee employeeEntity = employeeMapperInstance.selectEmployeeById(employeeId);

                if (employeeEntity == null) {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Employee not found");
                    return;
                }

                EmployeeDTO employeeDTO = toDTO(employeeEntity);

                request.setAttribute("employeeDTO", employeeDTO);
                // 转发到 /webapp/employee/edit.jsp
                request.getRequestDispatcher("/employee/edit.jsp").forward(request, response);
            }

        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid employee ID format");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Database error in showEditForm", e);
        }
    }

    /**
     * 从请求中提取 DTO
     */
    private EmployeeDTO extractEmployeeDTO(HttpServletRequest request) {
        EmployeeDTO dto = new EmployeeDTO();

        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idParam));
            } catch (NumberFormatException ignored) {
                // 如果是新增操作，ID 为空是正常的
            }
        }

        dto.setName(request.getParameter("name"));
        dto.setDepartment(request.getParameter("department"));
        dto.setPosition(request.getParameter("position"));

        try {
            String salaryStr = request.getParameter("salary");
            if (salaryStr != null && !salaryStr.trim().isEmpty()) {
                dto.setSalary(new BigDecimal(salaryStr));
            }
        } catch (NumberFormatException | NullPointerException ignored) {
            // 忽略，留给 validateEmployeeDTO 检查
        }

        dto.setEmail(request.getParameter("email"));
        dto.setPhone(request.getParameter("phone"));

        // 日期处理：JSP 表单通常提交 String 格式的日期
        String hireDateStr = request.getParameter("hireDate");
        if (hireDateStr != null && !hireDateStr.isEmpty()) {
            try {
                // 简单的日期字符串解析
                java.util.Date utilDate = java.sql.Date.valueOf(hireDateStr);
                dto.setHireDate(utilDate);
            } catch (IllegalArgumentException ignored) {
                // 忽略，留给 validateEmployeeDTO 检查
            }
        }

        return dto;
    }

    // ===============================================
    // DTO 转换逻辑
    // ===============================================

    private EmployeeDTO toDTO(Employee entity) {
        if (entity == null) {
            return null;
        }
        EmployeeDTO dto = new EmployeeDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setDepartment(entity.getDepartment());
        dto.setPosition(entity.getPosition());
        dto.setSalary(entity.getSalary());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());

        if (entity.getHireDate() != null) {
            dto.setHireDate(new java.util.Date(entity.getHireDate().getTime()));
        }

        dto.setCreatedBy(entity.getCreatedBy());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());

        return dto;
    }

    private Employee toEntity(EmployeeDTO dto) {
        if (dto == null) {
            return null;
        }
        Employee entity = new Employee();
        if (dto.getId() != null) {
            entity.setId(dto.getId());
        }
        entity.setName(dto.getName());
        entity.setDepartment(dto.getDepartment());
        entity.setPosition(dto.getPosition());
        entity.setSalary(dto.getSalary());
        entity.setEmail(dto.getEmail());
        entity.setPhone(dto.getPhone());

        if (dto.getHireDate() != null) {
            // 将 java.util.Date 转换为 java.sql.Date
            entity.setHireDate(new Date(dto.getHireDate().getTime()));
        }

        // 关键点：确保 dto.getCreatedBy() 不会为 null，因为我们已经在 insertEmployee 中设置了
        entity.setCreatedBy(dto.getCreatedBy());
        entity.setCreatedAt(dto.getCreatedAt());
        entity.setUpdatedAt(dto.getUpdatedAt());

        return entity;
    }

    private List<EmployeeDTO> toDTOList(List<Employee> entities) {
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


    // ===============================================
    // 验证逻辑
    // ===============================================

    private String validateEmployeeDTO(EmployeeDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            return "员工姓名不能为空。";
        }
        if (dto.getDepartment() == null || dto.getDepartment().trim().isEmpty()) {
            return "部门不能为空。";
        }
        if (dto.getPosition() == null || dto.getPosition().trim().isEmpty()) {
            return "职位不能为空。";
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            return "电子邮件不能为空。";
        }
        // 简单的邮箱格式验证
        if (!dto.getEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            return "电子邮件格式不正确。";
        }

        // 验证薪水：必须大于零
        if (dto.getSalary() == null || dto.getSalary().compareTo(BigDecimal.ZERO) <= 0) {
            return "薪水必须是大于零的有效数字。";
        }

        if (dto.getHireDate() == null) {
            return "入职日期不能为空。";
        }

        // 手机号码验证（简单非空即可）
        if (dto.getPhone() == null || dto.getPhone().trim().isEmpty()) {
            return "电话号码不能为空。";
        }

        return null; // 验证通过
    }
}