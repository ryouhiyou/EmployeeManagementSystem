package com.company.ems.controller;

import com.company.ems.dao.EmployeeDAO;
import com.company.ems.dao.EmployeeDAOImpl;
import com.company.ems.model.Employee;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/EmployeeServlet")
public class EmployeeServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EmployeeDAO employeeDAO;

    @Override
    public void init() throws ServletException {
        this.employeeDAO = new EmployeeDAOImpl();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) {
            action = "list";
        }

        try {
            switch (action) {
                case "new":
                    showNewForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteEmployee(request, response);
                    break;
                case "view":
                    viewEmployee(request, response);
                    break;
                case "list":
                default:
                    listEmployee(request, response);
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // 在生产环境中应重定向到错误页面
            throw new ServletException("数据库操作错误: " + ex.getMessage(), ex);
        } catch (Exception ex) {
            throw new ServletException(ex);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            // 默认情况下，如果表单没有action参数，则视为新增（Insert）
            action = "insert";
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
                    response.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED, "不支持的 POST 操作：" + action);
                    break;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            throw new ServletException("数据库操作错误: " + ex.getMessage(), ex);
        }
    }

    // --- R (Read) ---

    private void listEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {

        List<Employee> listEmployee = employeeDAO.getAllEmployees();
        request.setAttribute("listEmployee", listEmployee);
        request.getRequestDispatcher("/employee/list.jsp").forward(request, response);
    }

    private void viewEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Employee employee = employeeDAO.getEmployeeById(id);
        request.setAttribute("employee", employee);
        request.getRequestDispatcher("/employee/view.jsp").forward(request, response);
    }

    // --- C (Create) ---

    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        request.getRequestDispatcher("/employee/add.jsp").forward(request, response);
    }

    private void insertEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        // 1. 获取所有表单字段
        Employee newEmployee = extractEmployeeFromRequest(request, 0); // 0代表新增，没有ID

        // 假设当前操作用户ID为 1 (实际应从Session中获取)
        newEmployee.setCreatedBy(1);

        // 2. 调用 DAO 插入数据
        employeeDAO.addEmployee(newEmployee);

        // 3. 重定向到列表页并附带成功消息
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=add_success");
    }

    // --- U (Update) ---

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException, SQLException {
        int id = Integer.parseInt(request.getParameter("id"));
        Employee existingEmployee = employeeDAO.getEmployeeById(id);
        request.setAttribute("employee", existingEmployee);
        request.getRequestDispatcher("/employee/edit.jsp").forward(request, response);
    }

    private void updateEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));
        // 1. 获取所有表单字段 (包括ID)
        Employee updatedEmployee = extractEmployeeFromRequest(request, id);

        // 2. 调用 DAO 更新数据
        employeeDAO.updateEmployee(updatedEmployee);

        // 3. 重定向到列表页并附带成功消息
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=update_success");
    }

    // --- D (Delete) ---

    private void deleteEmployee(HttpServletRequest request, HttpServletResponse response)
            throws IOException, SQLException {

        int id = Integer.parseInt(request.getParameter("id"));

        // 1. 调用 DAO 删除数据
        employeeDAO.deleteEmployee(id);

        // 2. 重定向到列表页并附带成功消息
        response.sendRedirect(request.getContextPath() + "/EmployeeServlet?message=delete_success");
    }

    // --- 工具方法 ---

    /**
     * 从 HttpServletRequest 中解析员工对象数据
     */
    private Employee extractEmployeeFromRequest(HttpServletRequest request, int id) {
        Employee employee = new Employee();

        if (id != 0) {
            employee.setId(id);
        }

        employee.setName(request.getParameter("name"));
        employee.setDepartment(request.getParameter("department"));
        employee.setPosition(request.getParameter("position"));
        employee.setEmail(request.getParameter("email"));
        employee.setPhone(request.getParameter("phone"));

        // 薪资处理 (String -> BigDecimal)
        String salaryStr = request.getParameter("salary");
        if (salaryStr != null && !salaryStr.trim().isEmpty()) {
            try {
                employee.setSalary(new BigDecimal(salaryStr));
            } catch (NumberFormatException e) {
                // 处理数字格式错误，此处简化处理，实际应抛出业务异常或返回错误信息
                employee.setSalary(BigDecimal.ZERO);
            }
        }

        // 入职日期处理 (String -> java.sql.Date)
        String hireDateStr = request.getParameter("hireDate");
        if (hireDateStr != null && !hireDateStr.trim().isEmpty()) {
            try {
                // 假设表单提交的格式是 YYYY-MM-DD
                employee.setHireDate(Date.valueOf(hireDateStr));
            } catch (IllegalArgumentException e) {
                // 处理日期格式错误
                employee.setHireDate(null);
            }
        }

        return employee;
    }
}