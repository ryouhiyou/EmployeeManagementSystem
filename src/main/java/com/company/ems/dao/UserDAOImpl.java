package com.company.ems.dao;

import com.company.ems.model.User;
import com.company.ems.util.DatabaseUtil;
import java.sql.*;

public class UserDAOImpl implements UserDAO {

    private static final String FIND_BY_USERNAME_SQL =
            "SELECT id, username, password, email, created_at FROM users WHERE username = ?";

    @Override
    public User findByUsername(String username) throws SQLException {
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        User user = null;

        try {
            conn = DatabaseUtil.getConnection();
            ps = conn.prepareStatement(FIND_BY_USERNAME_SQL);
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setCreatedAt(rs.getTimestamp("created_at"));
            }
        } finally {
            DatabaseUtil.close(rs, ps, conn);
        }
        return user;
    }
}