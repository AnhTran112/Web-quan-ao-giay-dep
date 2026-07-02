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
     * Tao don hang moi. Su dung TRANSACTION de dam bao toan ven du lieu:
     * 1) INSERT vao bang orders, lay id tu tang.
     * 2) INSERT tung dong vao order_items (luu price tai thoi diem mua).
     * 3) (Nang cap) Tru ton kho trong bang products.
     * Neu co bat ky loi nao -> rollback toan bo.
     *
     * @param order doi tuong Order (chua items ben trong)
     * @return id don hang vua tao, hoac 0 neu that bai
     */
    public int createOrder(Order order) {
        // Cau lenh SQL cho tung buoc
        String sqlOrder = "INSERT INTO orders(customer_name, phone, address, total_amount, status) "
                        + "VALUES (?, ?, ?, ?, 'PENDING')";
        String sqlItem  = "INSERT INTO order_items(order_id, product_id, quantity, price) "
                        + "VALUES (?, ?, ?, ?)";
        // (Nang cap) Tru ton kho san pham khi dat hang
        String sqlStock = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // ====== BAT DAU TRANSACTION ======
            conn.setAutoCommit(false);

            // --- Buoc 1: INSERT don hang ---
            int orderId;
            try (PreparedStatement psOrder = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                psOrder.setString(1, order.getCustomerName());
                psOrder.setString(2, order.getPhone());
                psOrder.setString(3, order.getAddress());
                psOrder.setBigDecimal(4, order.getTotalAmount());
                psOrder.executeUpdate();

                // Lay id tu tang cua don hang vua tao
                try (ResultSet keys = psOrder.getGeneratedKeys()) {
                    if (keys.next()) {
                        orderId = keys.getInt(1);
                    } else {
                        throw new SQLException("Khong lay duoc id don hang tu tang.");
                    }
                }
            }

            // --- Buoc 2 + 3: INSERT tung dong order_items VA tru ton kho ---
            try (PreparedStatement psItem  = conn.prepareStatement(sqlItem);
                 PreparedStatement psStock = conn.prepareStatement(sqlStock)) {

                for (OrderItem item : order.getItems()) {
                    // Buoc 2: Luu chi tiet don hang
                    psItem.setInt(1, orderId);
                    psItem.setInt(2, item.getProductId());
                    psItem.setInt(3, item.getQuantity());
                    psItem.setBigDecimal(4, item.getPrice());
                    psItem.addBatch();

                    // Buoc 3 (Nang cap): Tru ton kho
                    // Dieu kien "quantity >= ?" dam bao khong tru thanh so am
                    psStock.setInt(1, item.getQuantity());
                    psStock.setInt(2, item.getProductId());
                    psStock.setInt(3, item.getQuantity());
                    psStock.addBatch();
                }

                // Thuc thi batch INSERT order_items
                psItem.executeBatch();

                // Thuc thi batch UPDATE ton kho va kiem tra ket qua
                int[] stockResults = psStock.executeBatch();
                for (int result : stockResults) {
                    if (result == 0) {
                        // Neu 1 san pham khong du ton kho -> rollback toan bo
                        throw new SQLException("San pham khong du ton kho. Huy don hang.");
                    }
                }
            }

            // ====== THANH CONG: COMMIT ======
            conn.commit();
            return orderId;

        } catch (SQLException e) {
            // ====== THAT BAI: ROLLBACK ======
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return 0;
        } finally {
            // Tra lai autoCommit va dong ket noi
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    /**
     * Lay danh sach tat ca don hang, sap xep moi nhat truoc.
     * Dung cho trang Admin Quan ly don hang.
     */
    public List<Order> getAll() {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapOrder(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lay 1 don hang theo id, kem theo danh sach order_items (JOIN products de lay ten san pham).
     * Dung cho trang Chi tiet don hang (Admin).
     */
    public Order getById(int id) {
        String sqlOrder = "SELECT * FROM orders WHERE id = ?";
        // JOIN voi bang products de lay ten san pham hien thi
        String sqlItems = "SELECT oi.*, p.name AS product_name "
                        + "FROM order_items oi "
                        + "JOIN products p ON oi.product_id = p.id "
                        + "WHERE oi.order_id = ?";
        try (Connection conn = DBConnection.getConnection()) {

            // Lay thong tin don hang
            Order order = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        order = mapOrder(rs);
                    }
                }
            }
            if (order == null) return null;

            // Lay danh sach san pham trong don
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
            return order;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Doi trang thai don hang. Vi du: PENDING -> DELIVERED.
     * @return true neu cap nhat thanh cong
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
        }
        return false;
    }

    /**
     * Tinh tong doanh thu tu cac don da giao (status = 'DELIVERED').
     * Nguoi 4 (Nguyen) goi ham nay o DashboardServlet.
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
     * Dem tong so don hang (tat ca trang thai).
     * Nguoi 4 (Nguyen) goi ham nay o DashboardServlet.
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

    // ===== HELPER: map 1 dong ResultSet thanh doi tuong Order =====
    private Order mapOrder(ResultSet rs) throws SQLException {
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
