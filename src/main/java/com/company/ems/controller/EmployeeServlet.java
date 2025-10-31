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
    private static final int RECORDS_PER_PAGE = 10; // 每页显示记录数
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
     * 查找和分页功能的核心方法
     */
    private void listEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        // 1. 获取分页和搜索参数
        int currentPage = 1;
        // 确保从请求中获取的 String 参数被正确转换为 int
        if (request.getParameter("page") != null) {
            try {
                currentPage = Integer.parseInt(request.getParameter("page"));
            } catch (NumberFormatException ignored) {
                // 如果 page 不是数字，保持默认值 1
            }
        }
        String searchKeyword = request.getParameter("search");
        // 确保 searchKeyword 在 DAO 调用中不会为 null
        if (searchKeyword == null) {
            searchKeyword = "";
        }

        int offset = (currentPage - 1) * RECORDS_PER_PAGE;
        int limit = RECORDS_PER_PAGE;

        int totalRecords = 0;
        List<Employee> listEmployee = null;

        // 2. 使用 MyBatis 获取数据
        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession()) {
            employeeDAO = sqlSession.getMapper(EmployeeDAO.class);

            // 2a. 获取总记录数 (用于分页计算)
            totalRecords = employeeDAO.getTotalRecords(searchKeyword);

            // 2b. 获取当前页的员工列表
            // 🚀 关键修复：修正参数顺序为 (int offset, int limit, String searchKeyword)
            listEmployee = employeeDAO.listAllEmployees(offset, limit, searchKeyword);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ServletException("Error listing employees with MyBatis.", e);
        }

        // 3. 计算分页信息并设置 Request 属性
        int totalPages = (int) Math.ceil((double) totalRecords / RECORDS_PER_PAGE);

        request.setAttribute("listEmployee", listEmployee);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalRecords", totalRecords);
        request.setAttribute("searchKeyword", searchKeyword); // 传回搜索关键字，用于 JPS 表单回显

        RequestDispatcher dispatcher = request.getRequestDispatcher("/employee/list.jsp");
        dispatcher.forward(request, response);
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

        try (SqlSession sqlSession = MyBatisUtil.getSqlSessionFactory().openSession(true)) { // 开启自动提交
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
        employee.setId(Integer.parseInt(request.getParameter("id"))); // 确保ID被设置

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
        // 从请求中获取并转换数据
        String name = request.getParameter("name");
        String department = request.getParameter("department");
        String position = request.getParameter("position");

        // 处理薪资：如果为空或无效，设置为 0.00
        BigDecimal salary = BigDecimal.ZERO;
        try {
            // 确保处理 null 或空字符串
            String salaryStr = request.getParameter("salary");
            if (salaryStr != null && !salaryStr.isEmpty()) {
                salary = new BigDecimal(salaryStr);
            }
        } catch (Exception ignored) {
            // 忽略转换错误，保持为 ZERO
        }

        // 处理入职日期
        java.sql.Date sqlHireDate = null;

        try {
            String dateString = request.getParameter("hireDate");
            if (dateString != null && !dateString.isEmpty()) {
                // 1. 将字符串解析为 java.util.Date
                java.util.Date utilHireDate = new SimpleDateFormat("yyyy-MM-dd").parse(dateString);

                // 2. 将 java.util.Date 转换为 java.sql.Date
                sqlHireDate = new java.sql.Date(utilHireDate.getTime());
            }
        } catch (ParseException e) {
            // 忽略解析错误
        }

        Employee employee = new Employee();
        employee.setName(name);
        employee.setDepartment(department);
        employee.setPosition(position);
        employee.setSalary(salary);
        employee.setHireDate(sqlHireDate);

        return employee;
    }
}