package com.shop;

import com.shop.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class FixEncoding {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            // Fix activity_logs
            String sql = "UPDATE activity_logs SET description = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            
            PreparedStatement getPs = conn.prepareStatement("SELECT id, description FROM activity_logs");
            ResultSet rs = getPs.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String details = rs.getString("description");
                if (details != null && details.contains("Quáº§n Jogger Thá» Thao")) {
                    String fixed = details.replace("Quáº§n Jogger Thá» Thao", "Quần Jogger Thể Thao");
                    ps.setString(1, fixed);
                    ps.setInt(2, id);
                    ps.executeUpdate();
                    System.out.println("Fixed activity_log ID " + id);
                }
            }
            
            // Fix products table
            String sql2 = "UPDATE products SET name = ? WHERE id = ?";
            PreparedStatement ps2 = conn.prepareStatement(sql2);
            PreparedStatement getPs2 = conn.prepareStatement("SELECT id, name FROM products");
            ResultSet rs2 = getPs2.executeQuery();
            while (rs2.next()) {
                int id = rs2.getInt("id");
                String name = rs2.getString("name");
                if (name != null && name.contains("Quáº§n Jogger Thá» Thao")) {
                    String fixed = name.replace("Quáº§n Jogger Thá» Thao", "Quần Jogger Thể Thao");
                    ps2.setString(1, fixed);
                    ps2.setInt(2, id);
                    ps2.executeUpdate();
                    System.out.println("Fixed product ID " + id);
                }
            }
            
            System.out.println("DONE FIXING DB!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
