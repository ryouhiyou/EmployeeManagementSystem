package com.company.ems.dto;

import java.sql.Timestamp;

/**
 * User Data Transfer Object (DTO)
 * 用于在注册、登录表单和会话 (Session) 中传递用户数据。
 * 在传输过程中，通常只包含业务必需的字段，不包含敏感的数据库信息。
 */
public class UserDTO {
    private Integer id;
    private String username;
    // ⚠️ 注意：密码字段仅用于注册/登录时的输入，不应长期存储在 DTO 或 Session 中。
    private String password;
    private String email;
    private Timestamp createdAt; // 只读字段

    public UserDTO() {}

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
