package com.company.ems.mapper;

import com.company.ems.model.User;
import org.apache.ibatis.annotations.Param;

/**
 * MyBatis Mapper 接口，负责与数据库交互。
 * 方法名必须与 UserMapper.xml 中的 SQL id 匹配。
 */
public interface UserMapper { // 👈 注意：这是一个 interface (接口)

    // 1. 用于登录和注册校验用户名 (对应 LoginServlet/RegisterServlet 调用)
    User findByUsername(@Param("username") String username);

    // 2. 用于注册校验邮箱
    User findByEmail(@Param("email") String email);

    // 3. 用于注册用户
    int addUser(User user);
}