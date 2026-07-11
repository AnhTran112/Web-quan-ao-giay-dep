package com.shop.dao;

import com.shop.model.ProductVariant;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO cho bang product_variants (phan loai san pham). */
public class ProductVariantDAO {

    // Lay danh sach phan loai theo san pham
    public List<ProductVariant> getByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setId(rs.getInt("id"));
                    v.setProductId(rs.getInt("product_id"));
                    v.setName(rs.getString("name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setQuantity(rs.getInt("quantity"));
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Them 1 phan loai
    public void insert(ProductVariant v) {
        String sql = "INSERT INTO product_variants(product_id, name, price, quantity) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getProductId());
            ps.setString(2, v.getName());
            ps.setBigDecimal(3, v.getPrice());
            ps.setInt(4, v.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Xoa toan bo phan loai cua 1 san pham (dung khi cap nhat: xoa het roi them lai)
    public void deleteByProductId(int productId) {
        String sql = "DELETE FROM product_variants WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
