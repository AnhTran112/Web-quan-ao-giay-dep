package com.shop.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Lop tien ich: mo ket noi den MySQL.
 * Sua lai USER / PASSWORD cho dung may cua ban.
 */
public class DBConnection {

    private static final String URL =
            "jdbc:mysql://localhost:3306/shop_db?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8";
    private static final String USER = "root";
    private static final String PASSWORD = "root"; // <-- doi mat khau MySQL cua ban

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Khong tim thay MySQL Driver", e);
        }
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
