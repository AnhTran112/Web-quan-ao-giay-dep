package com.shop;

import com.shop.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class PrintDB {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement getPs = conn.prepareStatement("SELECT id, description FROM activity_logs");
            ResultSet rs = getPs.executeQuery();
            while (rs.next()) {
                System.out.println("LOG " + rs.getInt("id") + ": " + rs.getString("description"));
            }
            
            PreparedStatement getPs2 = conn.prepareStatement("SELECT id, name FROM products");
            ResultSet rs2 = getPs2.executeQuery();
            while (rs2.next()) {
                System.out.println("PRODUCT " + rs2.getInt("id") + ": " + rs2.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
