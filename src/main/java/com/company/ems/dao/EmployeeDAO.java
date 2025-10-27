package com.company.ems.dao;

import com.company.ems.model.Employee;
import java.sql.SQLException;
import java.util.List;

public interface EmployeeDAO {
    void addEmployee(Employee employee) throws SQLException;
    void deleteEmployee(int id) throws SQLException;
    void updateEmployee(Employee employee) throws SQLException;
    Employee getEmployeeById(int id) throws SQLException;
    List<Employee> getAllEmployees() throws SQLException;
}