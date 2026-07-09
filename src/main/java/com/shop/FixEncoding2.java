package com.shop;

import com.shop.util.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class FixEncoding2 {
    public static void main(String[] args) {
        try (Connection conn = DBConnection.getConnection()) {
            PreparedStatement ps1 = conn.prepareStatement("UPDATE products SET name = 'Quần Jogger Thể Thao' WHERE id = 30");
            ps1.executeUpdate();
            
            PreparedStatement ps2 = conn.prepareStatement("UPDATE activity_logs SET description = 'Cập nhật sản phẩm: Quần Jogger Thể Thao' WHERE id = 1");
            ps2.executeUpdate();
            
            System.out.println("DONE FIXING IDS 30 AND 1");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
