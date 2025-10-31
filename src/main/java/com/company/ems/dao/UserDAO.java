package com.company.ems.dao;

import com.company.ems.model.User;
import org.apache.ibatis.annotations.Param;
import java.sql.SQLException;

public interface UserDAO {

    // 用于登录和注册校验
    User findByUsername(String username) throws SQLException;

    // 用于注册校验
    User findByEmail(String email);

    // 用于注册
    int addUser(User user);
}
