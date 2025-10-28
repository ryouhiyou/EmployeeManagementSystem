package com.company.ems.model;

import lombok.Data;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Data
public class Employee {
    private int id;
    private String name;
    private String department;
    private String position;
    private BigDecimal salary;
    private String email;
    private String phone;
    private Date hireDate;
    private int createdBy;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Employee() {}

}