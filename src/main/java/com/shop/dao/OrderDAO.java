package com.shop.dao;

import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.util.DBConnection;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO don hang: truy van bang orders va order_items.
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa) - so huu toan bo file nay.
 * Luu y: Nguoi 4 (Nguyen) chi GOI getTotalRevenue()/countOrders() o DashboardServlet,
 *        KHONG sua file nay de tranh xung dot.
 */
public class OrderDAO {

    /**
     * Tao don hang moi:
     * 1) INSERT vao bang orders, lay id tu tang.
     * 2) INSERT tung dong vao bang order_items (luu price tai thoi diem mua).
     * 3) Dung TRANSACTION de dam bao toan ven du lieu.
     *
     * @return id don hang vua tao, hoac 0 neu co loi.
     */
    public int createOrder(Order order) {
        String sqlOrder = "INSERT INTO orders(customer_name, phone, address, total_amount, status) "
                        + "VALUES (?, ?, ?, ?, 'PENDING')";
        String sqlItem  = "INSERT INTO order_items(order_id, product_id, quantity, price) "
                        + "VALUES (?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BAT DAU TRANSACTION

            // 1) Insert don hang
            int orderId = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, order.getCustomerName());
                ps.setString(2, order.getPhone());
                ps.setString(3, order.getAddress());
                ps.setBigDecimal(4, order.getTotalAmount());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    }
                }
            }

            // 2) Insert tung dong chi tiet don hang
            if (orderId > 0 && order.getItems() != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                    for (OrderItem item : order.getItems()) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setBigDecimal(4, item.getPrice());
                        ps.addBatch(); // gom cac lenh lai de thuc thi 1 lan
                    }
                    ps.executeBatch();
                }
            }

            conn.commit(); // THANH CONG -> xac nhan giao dich
            return orderId;

        } catch (SQLException e) {
            // CO LOI -> huy toan bo giao dich
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return 0;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Lay tat ca don hang, sap xep moi nhat len truoc.
     */
    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrderRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lay 1 don hang theo id, kem danh sach order_items (JOIN products de lay ten san pham).
     */
    public Order getById(int id) {
        String sqlOrder = "SELECT * FROM orders WHERE id = ?";
        String sqlItems = "SELECT oi.*, p.name AS product_name "
                        + "FROM order_items oi "
                        + "LEFT JOIN products p ON oi.product_id = p.id "
                        + "WHERE oi.order_id = ?";

        try (Connection conn = DBConnection.getConnection()) {
            // Lay thong tin don hang
            Order order = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        order = mapOrderRow(rs);
                    }
                }
            }

            // Lay danh sach san pham trong don
            if (order != null) {
                List<OrderItem> items = new ArrayList<>();
                try (PreparedStatement ps = conn.prepareStatement(sqlItems)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            OrderItem item = new OrderItem();
                            item.setId(rs.getInt("id"));
                            item.setOrderId(rs.getInt("order_id"));
                            item.setProductId(rs.getInt("product_id"));
                            item.setProductName(rs.getString("product_name"));
                            item.setQuantity(rs.getInt("quantity"));
                            item.setPrice(rs.getBigDecimal("price"));
                            items.add(item);
                        }
                    }
                }
                order.setItems(items);
            }
            return order;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Doi trang thai don hang: PENDING -> DELIVERED (hoac nguoc lai).
     */
    public boolean updateStatus(int id, String status) {
        String sql = "UPDATE orders SET status = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tong doanh thu cua cac don da giao (DELIVERED).
     * Dung cho trang thong ke (DashboardServlet cua Nguoi 4).
     */
    public long getTotalRevenue() {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'DELIVERED'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Dem tong so don hang (moi trang thai).
     * Dung cho trang thong ke (DashboardServlet cua Nguoi 4).
     */
    public int countOrders() {
        String sql = "SELECT COUNT(*) FROM orders";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // ========================= HELPER =========================

    /** Chuyen 1 dong ResultSet thanh object Order. */
    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        o.setCustomerName(rs.getString("customer_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getString("created_at"));
        return o;
    }
}
