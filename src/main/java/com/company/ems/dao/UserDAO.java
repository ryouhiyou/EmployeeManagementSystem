package com.company.ems.dao;

import com.company.ems.model.User;
import java.sql.SQLException;

public interface UserDAO {
    User findByUsername(String username) throws SQLException;
}