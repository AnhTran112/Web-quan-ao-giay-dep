package com.shop.dao;

import com.shop.model.Coupon;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO ma giam gia (bang coupons).
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa).
 * Luu y: viec TRU LUOT DUNG khi dat hang nam trong transaction cua OrderDAO.createOrder,
 * khong lam o day de tranh mat luot khi don bi rollback.
 */
public class CouponDAO {

    /**
     * Tim ma giam gia CON HIEU LUC (dang bat, con luot, chua het han).
     * Dung de kiem tra khi khach nhap ma o trang checkout.
     *
     * @return Coupon neu hop le, null neu khong ton tai / het hieu luc.
     */
    public Coupon getValidByCode(String code) {
        String sql = "SELECT * FROM coupons WHERE code = ? AND active = 1 "
                   + "AND used_count < max_uses "
                   + "AND (expires_at IS NULL OR expires_at > NOW())";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /** Lay toan bo ma giam gia (trang quan ly cua admin), moi nhat truoc. */
    public List<Coupon> getAll() {
        List<Coupon> list = new ArrayList<>();
        String sql = "SELECT * FROM coupons ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Lay 1 ma theo id (form sua cua admin). */
    public Coupon getById(int id) {
        String sql = "SELECT * FROM coupons WHERE id = ?";
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

    /**
     * Them ma moi.
     * @return false neu trung ma (code UNIQUE) hoac loi khac.
     */
    public boolean insert(Coupon c) {
        String sql = "INSERT INTO coupons(code, discount_percent, max_uses, expires_at, active) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setInt(2, c.getDiscountPercent());
            ps.setInt(3, c.getMaxUses());
            setNullableDateTime(ps, 4, c.getExpiresAt());
            ps.setBoolean(5, c.isActive());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Cap nhat ma (khong cho sua used_count bang tay). */
    public boolean update(Coupon c) {
        String sql = "UPDATE coupons SET code = ?, discount_percent = ?, max_uses = ?, "
                   + "expires_at = ?, active = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, c.getCode());
            ps.setInt(2, c.getDiscountPercent());
            ps.setInt(3, c.getMaxUses());
            setNullableDateTime(ps, 4, c.getExpiresAt());
            ps.setBoolean(5, c.isActive());
            ps.setInt(6, c.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Xoa ma giam gia. */
    public boolean delete(int id) {
        String sql = "DELETE FROM coupons WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================= HELPER =========================

    /** expiresAt dang "yyyy-MM-dd HH:mm:ss" hoac "yyyy-MM-dd"; null/rong = khong het han. */
    private void setNullableDateTime(PreparedStatement ps, int index, String value) throws SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.TIMESTAMP);
        } else {
            String v = value.trim();
            if (v.length() == 10) v += " 23:59:59"; // chi nhap ngay -> het han cuoi ngay do
            ps.setString(index, v);
        }
    }

    private Coupon mapRow(ResultSet rs) throws SQLException {
        Coupon c = new Coupon();
        c.setId(rs.getInt("id"));
        c.setCode(rs.getString("code"));
        c.setDiscountPercent(rs.getInt("discount_percent"));
        c.setMaxUses(rs.getInt("max_uses"));
        c.setUsedCount(rs.getInt("used_count"));
        c.setExpiresAt(rs.getString("expires_at"));
        c.setActive(rs.getBoolean("active"));
        c.setCreatedAt(rs.getString("created_at"));
        return c;
    }
}
