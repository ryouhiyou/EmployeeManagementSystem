package com.company.ems.controller;

import com.company.ems.dao.EmployeeDAO;
import com.company.ems.model.Employee;
import com.company.ems.util.MyBatisUtil;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.TransactionIsolationLevel; // 引入隔离级别，以便更精确控制事务

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat; // 引入 DecimalFormat
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@WebServlet("/EmployeeServlet/*")
public class EmployeeServlet extends HttpServlet {
    private static final int RECORDS_PER_PAGE = 10;
    private EmployeeDAO employeeDAO;

    public void init() {
        // 保持 init 方法为空
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "insert":
                    insertEmployee(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "update":
                    updateEmployee(request, response);
                    break;
                case "view":
                    viewEmployee(request, response);
                    break;
                case "list":
                default:
                    listEmployee(request, response);
                    break;
            }
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    // ---------------------- 核心业务方法 ----------------------

    /**
     * 查找、分页和排序功能的核心方法
     */
    private void listEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // 1. 获取分页、搜索和排序参数
        int currentPage = 1;
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) { }
        }

        String searchKeyword = request.getParameter("search");
        if (searchKeyword == null) {
            searchKeyword = "";
        }

        // 获取排序参数
        String sortBy = request.getParameter("sortBy");
        String sortOrder = request.getParameter("sortOrder");

        // 默认排序逻辑：如果未指定排序参数，则默认按 ID 升序排列
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "id";
            sortOrder = "ASC";
        }

        // 安全检查：只能是 ASC 或 DESC
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            // 如果用户传入的 sortOrder 无效，则基于当前的 sortBy 默认给一个方向
            sortOrder = "ASC";
        }

        // 检查 sortBy 是否是有效字段，防止意外的 SQL 注入
        if (!isValidSortColumn(sortBy)) {
            sortBy = "id";
            sortOrder = "ASC"; // 如果字段无效，重置为默认 ID 升序
        }


        int offset = (currentPage - 1) * RECORDS_PER_PAGE;
        int limit = RECORDS_PER_PAGE;

        int totalRecords = 0;
        List<Employee> listEmployee = null;

        // 2. 使用 MyBatis 获取数据
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);

            totalRecords = employeeDAO.getTotalRecords(searchKeyword);

            // 🚀 关键：传递全部 5 个参数 (offset, limit, search, sortBy, sortOrder)
            listEmployee = employeeDAO.listAllEmployees(offset, limit, searchKeyword, sortBy, sortOrder);

        } catch (Exception e) {
            e.printStackTrace();
            // 重新包装异常，提供更清晰的日志信息
            throw new ServletException("Error listing employees with MyBatis.", e);
        }

        // 3. 计算分页信息并设置 Request 属性
        int totalPages = (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

        request.setAttribute("listEmployee", listEmployee);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("searchKeyword", searchKeyword);

        // 新增：将当前的排序参数传回 JSP
        request.setAttribute("sortBy", sortBy);
        request.setAttribute("sortOrder", sortOrder);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee/list.jsp");
        dispatcher.forward(request, response);
    }

    /**
     * 安全检查：只允许特定的列名进行排序
     */
    private boolean isValidSortColumn(String column) {
        return column.equalsIgnoreCase("id") ||
                column.equalsIgnoreCase("name") ||
                column.equalsIgnoreCase("department") ||
                column.equalsIgnoreCase("position") ||
                column.equalsIgnoreCase("salary") ||
                column.equalsIgnoreCase("hire_date");
    }


    // ---------------------- CRUD/辅助方法 ----------------------

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee/add.jsp");
        dispatcher.forward(request, response);
    }

    private void insertEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        Employee newEmployee = extractEmployeeFromRequest(request);
        // 注意：这里 insert 操作如果 hireDate 为 null 也会导致 SQLIntegrityConstraintViolationException。
        // 但是对于新增，我们假设用户会提供所有必填信息。

        // ⚠️ 事务修改：使用 try-with-resources 自动关闭 Session，并显式处理提交和回滚
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(TransactionIsolationLevel.READ_COMMITTED)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.insertEmployee(newEmployee);
            sqlSession.commit(); // 显式提交事务
            System.out.println("DEBUG: Employee insertion committed successfully.");
        } catch (Exception e) {
            // 打印详细错误信息，帮助用户调试
            System.err.println("FATAL ERROR: Failed to insert employee. Check database write permission or SQL syntax in Mapper XML.");
            e.printStackTrace();
            throw new RuntimeException("Employee insertion failed.", e); // 抛出异常，以便前端看到服务器错误
        }
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=add_success");
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));

        // ⚠️ 事务修改：使用 try-with-resources 自动关闭 Session，并显式处理提交和回滚
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(TransactionIsolationLevel.READ_COMMITTED)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.deleteEmployee(id);
            sqlSession.commit(); // 显式提交事务
            System.out.println("DEBUG: Employee deletion committed successfully for ID: " + id);
        } catch (Exception e) {
            // 打印详细错误信息，帮助用户调试
            System.err.println("FATAL ERROR: Failed to delete employee. Check database write permission or SQL syntax in Mapper XML.");
            e.printStackTrace();
            throw new RuntimeException("Employee deletion failed.", e);
        }
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=delete_success");
    }

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Employee existingEmployee = null;
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            existingEmployee = employeeDAO.selectEmployeeById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("employee", existingEmployee);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee/edit.jsp");
        dispatcher.forward(request, response);
    }

    private void updateEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        // 1. 从请求中提取用户输入的新数据
        Employee employee = extractEmployeeFromRequest(request);
        int employeeId = Integer.parseInt(request.getParameter("id"));
        employee.setId(employeeId);

        // 2. 检查 hireDate 是否为 null
        if (employee.getHireDate() == null) {
            // 🐛 修复逻辑：如果用户未提供日期，或者日期解析失败（导致为 null），
            // 则从数据库中查询原始的 hireDate 值，以避免 'hire_date cannot be null' 错误。
            try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
                employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
                Employee existingEmployee = employeeDAO.selectEmployeeById(employeeId);
                if (existingEmployee != null) {
                    // 使用数据库中的原始日期
                    employee.setHireDate(existingEmployee.getHireDate());
                    System.out.println("DEBUG: Hire date was null in request, restored original date from DB.");
                } else {
                    // 如果连旧记录都找不到，说明 ID 有问题，但这里保持原逻辑
                    System.err.println("WARNING: Cannot find existing employee with ID: " + employeeId);
                }
            } catch (Exception e) {
                System.err.println("WARNING: Failed to fetch existing employee for date restore.");
                e.printStackTrace();
            }
        }

        // 3. 执行更新操作
        // ⚠️ 事务修改：使用 try-with-resources 自动关闭 Session，并显式处理提交和回滚
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(TransactionIsolationLevel.READ_COMMITTED)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.updateEmployee(employee);
            sqlSession.commit(); // 显式提交事务
            System.out.println("DEBUG: Employee update committed successfully for ID: " + employee.getId());
        } catch (Exception e) {
            // 打印详细错误信息，帮助用户调试
            System.err.println("FATAL ERROR: Failed to update employee. Check database write permission or SQL syntax in Mapper XML.");
            e.printStackTrace();
            throw new RuntimeException("Employee update failed.", e);
        }
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=update_success");
    }

    private void viewEmployee(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        Employee employee = null;
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employee = employeeDAO.selectEmployeeById(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        request.setAttribute("employee", employee);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee/view.jsp");
        dispatcher.forward(request, response);
    }

    private Employee extractEmployeeFromRequest(HttpServletRequest request) {
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String position = request.getParameter("position");

        // 处理薪资
        BigDecimal salary = BigDecimal.ZERO;
        String salaryStr = request.getParameter("salary");

        if (salaryStr != null && !salaryStr.trim().isEmpty()) {
            try {
                // 🚀 改进：尝试用 BigDecimal 构造函数直接解析，并去除所有非数字字符（但保留小数点）
                // 这样可以处理像 "80.00" 这样的标准输入
                salary = new BigDecimal(salaryStr.trim());
            } catch (NumberFormatException e1) {
                // 如果直接解析失败，可能是因为输入中包含格式化符号 (如逗号) 或其他非标准字符
                try {
                    // 尝试使用 DecimalFormat 来解析，它能处理更复杂的数字格式
                    // 移除所有非数字和小数点的字符，只保留数字和小数点
                    String cleanSalaryStr = salaryStr.replaceAll("[^0-9.]", "");

                    if (!cleanSalaryStr.isEmpty()) {
                        salary = new BigDecimal(cleanSalaryStr);
                    } else {
                        System.err.println("WARNING: Salary input '" + salaryStr + "' resulted in empty string after cleaning.");
                    }
                } catch (Exception e2) {
                    // 再次解析失败，记录错误并回退到 0.00
                    System.err.println("WARNING: Failed to parse salary string '" + salaryStr + "'. Setting to 0.00.");
                    // 薪资保持为 BigDecimal.ZERO (初始化值)
                }
            }
        }

        // 处理入职日期
        java.sql.Date sqlHireDate = null;
        try {
            String dateString = request.getParameter("hireDate");
            if (dateString != null && !dateString.isEmpty()) {
                java.util.Date utilHireDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                sqlHireDate = new java.sql.Date(utilHireDate.getTime());
            }
        } catch (ParseException e) {
            // 如果解析失败，sqlHireDate 保持为 null，由 updateEmployee 方法处理
            System.err.println("WARNING: Failed to parse hire date string: " + request.getParameter("hireDate"));
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setSalary(salary); // 确保 salary 至少是 BigDecimal.ZERO
        employee.setHireDate(sqlHireDate);

        return employee;
    }
}