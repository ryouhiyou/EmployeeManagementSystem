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

import java.io.IOException;
import java.math.BigDecimal;
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

        // 默认排序：按 ID 降序
        if (sortBy == null || sortBy.isEmpty()) {
            sortBy = "id";
            sortOrder = "DESC";
        }
        // 安全检查：只能是 ASC 或 DESC
        if (sortOrder == null || (!sortOrder.equalsIgnoreCase("ASC") && !sortOrder.equalsIgnoreCase("DESC"))) {
            sortOrder = "DESC";
        }

        // 检查 sortBy 是否是有效字段，防止意外的 SQL 注入
        if (!isValidSortColumn(sortBy)) {
            sortBy = "id";
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

        // 使用 try-with-resources 确保 SqlSession 关闭，openSession(true) 开启自动提交
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.insertEmployee(newEmployee);
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=add_success");
    }

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int id = Integer.parseInt(request.getParameter("id"));
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.deleteEmployee(id);
        } catch (Exception e) {
            e.printStackTrace();
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
        Employee employee = extractEmployeeFromRequest(request);
        employee.setId(Integer.parseInt(request.getParameter("id")));

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);
            employeeDAO.updateEmployee(employee);
        } catch (Exception e) {
            e.printStackTrace();
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

        // 处理薪资：如果为空或无效，设置为 0.00
        BigDecimal salary = BigDecimal.ZERO;
        try {
            String salaryStr = request.getParameter("salary");
            if (salaryStr != null && !salaryStr.isEmpty()) {
                salary = new BigDecimal(salaryStr);
            }
        } catch (Exception ignored) { }

        // 处理入职日期
        java.sql.Date sqlHireDate = null;
        try {
            String dateString = request.getParameter("hireDate");
            if (dateString != null && !dateString.isEmpty()) {
                java.util.Date utilHireDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
                sqlHireDate = new java.sql.Date(utilHireDate.getTime());
            }
        } catch (ParseException e) { }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setHireDate(sqlHireDate);

        return employee;
    }
}