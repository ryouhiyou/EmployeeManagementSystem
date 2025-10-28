package com.company.ems.model;

import lombok.Data;

import java.sql.Timestamp;
@Data
public class User {
    private int id;
    private String username;
    private String password;
    private String email;
    private Timestamp createdAt;

    public User() {}
}