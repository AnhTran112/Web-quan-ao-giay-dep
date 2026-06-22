package com.shop.dao;

import com.shop.model.User;
import com.shop.util.DBConnection;

import java.sql.*;

/** DAO tai khoan admin. */
public class UserDAO {

    /**
     * Kiem tra dang nhap. Tra ve User neu dung, null neu sai.
     * (Demo: so sanh mat khau truc tiep. Thuc te nen ma hoa.)
     */
    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(rs.getString("role"));
                    return u;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
