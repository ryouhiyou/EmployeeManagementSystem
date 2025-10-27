package com.company.ems.dao;

import com.company.ems.model.Employee;
import com.company.ems.util.DatabaseUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAOImpl implements EmployeeDAO {

    private Employee mapResultSetToEmployee(ResultSet rs) throws SQLException {
        Employee employee = new Employee();
        employee.setId(rs.getInt("id"));
        employee.setName(rs.getString("name"));
        employee.setDepartment(rs.getString("department"));
        employee.setPosition(rs.getString("position"));
        employee.setSalary(rs.getBigDecimal("salary"));
        employee.setEmail(rs.getString("email"));
        employee.setPhone(rs.getString("phone"));
        employee.setHireDate(rs.getDate("hire_date"));
        employee.setCreatedBy(rs.getInt("created_by"));
        employee.setCreatedAt(rs.getTimestamp("created_at"));
        employee.setUpdatedAt(rs.getTimestamp("updated_at"));
        return employee;
    }

    private static final String SELECT_ALL_SQL = "SELECT * FROM employees ORDER BY id DESC";
    @Override
    public List<Employee> getAllEmployees() throws SQLException {
        List<Employee> employees = new ArrayList<>();
        try (Connection conn = DatabaseUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                employees.add(mapResultSetToEmployee(rs));
            }
        }
        return employees;
    }

    private static final String SELECT_BY_ID_SQL = "SELECT * FROM employees WHERE id = ?";
    @Override
    public Employee getEmployeeById(int id) throws SQLException {
        Employee employee = null;
        ResultSet rs = null;
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_ID_SQL)) {

            ps.setInt(1, id);
            rs = ps.executeQuery();

            if (rs.next()) {
                employee = mapResultSetToEmployee(rs);
            }
        } finally {
            DatabaseUtil.close(rs);
        }
        return employee;
    }

    private static final String INSERT_SQL =
            "INSERT INTO employees (name, department, position, salary, email, phone, hire_date, created_by) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
    @Override
    public void addEmployee(Employee employee) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setString(3, employee.getPosition());
            ps.setBigDecimal(4, employee.getSalary());
            ps.setString(5, employee.getEmail());
            ps.setString(6, employee.getPhone());
            ps.setDate(7, employee.getHireDate());
            ps.setInt(8, employee.getCreatedBy());
            ps.executeUpdate();
        }
    }

    private static final String UPDATE_SQL =
            "UPDATE employees SET name=?, department=?, position=?, salary=?, email=?, phone=?, hire_date=? WHERE id=?";
    @Override
    public void updateEmployee(Employee employee) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, employee.getName());
            ps.setString(2, employee.getDepartment());
            ps.setString(3, employee.getPosition());
            ps.setBigDecimal(4, employee.getSalary());
            ps.setString(5, employee.getEmail());
            ps.setString(6, employee.getPhone());
            ps.setDate(7, employee.getHireDate());
            ps.setInt(8, employee.getId());
            ps.executeUpdate();
        }
    }

    private static final String DELETE_SQL = "DELETE FROM employees WHERE id = ?";
    @Override
    public void deleteEmployee(int id) throws SQLException {
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_SQL)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}