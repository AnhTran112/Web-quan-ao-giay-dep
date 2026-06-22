package com.shop.dao;

import com.shop.model.Product;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO san pham: truy van bang products. */
public class ProductDAO {

    /** Lay tat ca san pham. */
    public List<Product> getAll() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lay 1 san pham theo id. */
    public Product getById(int id) {
        String sql = "SELECT * FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Loc san pham theo danh muc va khoang gia (truyen null neu khong loc). */
    public List<Product> filter(Integer categoryId, Long minPrice, Long maxPrice) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (categoryId != null) { sql.append(" AND category_id = ?"); params.add(categoryId); }
        if (minPrice != null)   { sql.append(" AND price >= ?");      params.add(minPrice); }
        if (maxPrice != null)   { sql.append(" AND price <= ?");      params.add(maxPrice); }
        sql.append(" ORDER BY id DESC");
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) ps.setObject(i + 1, params.get(i));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean insert(Product p) {
        return false;
    }

    public boolean update(Product p) {
        return false;
    }

    public boolean delete(int id) {
        return false;
    }

    /** Chuyen 1 dong ResultSet thanh object Product. */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setCategoryId(rs.getInt("category_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getBigDecimal("price"));
        p.setImage(rs.getString("image"));
        p.setQuantity(rs.getInt("quantity"));
        return p;
    }
}
