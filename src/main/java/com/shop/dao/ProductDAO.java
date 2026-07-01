package com.shop.dao;

import com.shop.model.Product;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO san pham: truy van bang products. */
public class ProductDAO {

    private final ProductVariantDAO variantDAO = new ProductVariantDAO();

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
                if (rs.next()) {
                    Product p = mapRow(rs);
                    // Nap kem danh sach phan loai (variants) cua san pham
                    p.setVariants(variantDAO.getByProductId(p.getId()));
                    return p;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Loc san pham theo danh muc va khoang gia (truyen null neu khong loc). */
    public List<Product> filter(Integer categoryId, Long minPrice, Long maxPrice, String keyword) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (categoryId != null){
            sql.append(" AND category_id = ?");
            params.add(categoryId);
        }
        if (minPrice != null){
            sql.append(" AND price >= ?");
            params.add(minPrice);
        }
        if (maxPrice != null){
            sql.append(" AND price <= ?");
            params.add(maxPrice);
        }
        if (keyword != null && !keyword.trim().isEmpty()){
            sql.append(" AND name LIKE ?");
            params.add("%" + keyword.trim() + "%");
        }
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

    // =====================================================
    // TODO (Nguoi 1 - Hoang): hoan thien CRUD san pham
    // =====================================================

    /** Them moi 1 san pham vao bang products. Tra ve true neu them thanh cong. */
    public boolean insert(Product p) {
        String sql = "INSERT INTO products(category_id, name, description, price, image, quantity, discount_percent) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getQuantity());
            ps.setInt(7, p.getDiscountPercent());
            boolean ok = ps.executeUpdate() > 0;
            // Lay id tu tang de luu cac phan loai (neu co)
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    for (com.shop.model.ProductVariant v : p.getVariants()) {
                        v.setProductId(newId);
                        variantDAO.insert(v);
                    }
                }
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cap nhat 1 san pham da co theo id. Tra ve true neu sua thanh cong. */
    public boolean update(Product p) {
        String sql = "UPDATE products SET category_id = ?, name = ?, description = ?, "
                   + "price = ?, image = ?, quantity = ?, discount_percent = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, p.getCategoryId());
            ps.setString(2, p.getName());
            ps.setString(3, p.getDescription());
            ps.setBigDecimal(4, p.getPrice());
            ps.setString(5, p.getImage());
            ps.setInt(6, p.getQuantity());
            ps.setInt(7, p.getDiscountPercent());
            ps.setInt(8, p.getId());
            boolean ok = ps.executeUpdate() > 0;
            // Cap nhat phan loai theo kieu: xoa het cu roi them lai tu form
            variantDAO.deleteByProductId(p.getId());
            for (com.shop.model.ProductVariant v : p.getVariants()) {
                v.setProductId(p.getId());
                variantDAO.insert(v);
            }
            return ok;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Tim kiem san pham theo ten (khong phan biet hoa thuong). Dung LIKE %keyword%. */
    public List<Product> search(String keyword) {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products WHERE name LIKE ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Xoa 1 san pham theo id. Tra ve true neu xoa thanh cong. */
    public boolean delete(int id) {
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
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
        p.setDiscountPercent(rs.getInt("discount_percent"));
        return p;
    }
}
