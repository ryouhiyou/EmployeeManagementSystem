package com.company.ems.dto;

import java.math.BigDecimal;
import java.util.Date; // DTO 通常使用 java.util.Date 更方便前端处理
import java.sql.Timestamp;

/**
 * Employee Data Transfer Object (DTO)
 * 用于在表现层（Servlet/JSP）和服务层之间传递员工数据。
 * 它反映了用户在表单中输入或在列表中需要显示的数据结构。
 * 包含所有Employee实体的主要业务字段。
 */
public class EmployeeDTO {
    private Integer id;
    private String name;
    private String department;
    private String position;
    private BigDecimal salary;
    private String email;
    private String phone;
    private Date hireDate; // 使用 java.util.Date

    // 只读字段，通常用于显示，不接受用户输入
    private Integer createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public EmployeeDTO() {}

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public BigDecimal getSalary() {
        return salary;
    }

    public void setSalary(BigDecimal salary) {
        this.salary = salary;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Date getHireDate() {
        return hireDate;
    }

    public void setHireDate(Date hireDate) {
        this.hireDate = hireDate;
    }

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
