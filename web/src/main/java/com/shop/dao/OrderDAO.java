package com.shop.dao;

import com.shop.model.Order;
import com.shop.model.OrderItem;
import com.shop.model.OrderStatus;
import com.shop.model.OrderStatusHistory;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO don hang: truy van bang orders, order_items, order_status_history.
 *
 * NGUOI PHU TRACH: Nguoi 3 (Khoa) - so huu toan bo file nay.
 * Luu y: Nguoi 4 (Nguyen) chi GOI getTotalRevenue()/countOrders() o DashboardServlet,
 *        KHONG sua file nay de tranh xung dot.
 */
public class OrderDAO {

    /** Loi nghiep vu khi dat hang / doi trang thai (mang message hien thi cho nguoi dung). */
    public static class OrderException extends Exception {
        public OrderException(String message) { super(message); }
    }

    /**
     * Tao don hang moi trong 1 TRANSACTION:
     * 1) Tru ton kho tung mon (kiem tra du hang ngay trong cau UPDATE de chong dat trung).
     * 2) Tru luot dung ma giam gia (neu co) - kiem tra han/luot ngay trong cau UPDATE.
     * 3) INSERT orders -> lay id tu tang -> INSERT order_items (luu ca variant).
     * 4) INSERT order_status_history (ban ghi tao don).
     * Bat ky buoc nao loi -> rollback toan bo.
     *
     * @return id don hang vua tao.
     * @throws OrderException het hang / ma giam gia het hieu luc / loi he thong.
     */
    public int createOrder(Order order) throws OrderException {
        String sqlOrder = "INSERT INTO orders(user_id, customer_name, phone, address, note, total_amount, "
                        + "ship_fee, coupon_code, discount_amount, status) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, '" + OrderStatus.PENDING + "')";
        String sqlItem  = "INSERT INTO order_items(order_id, product_id, variant_id, variant_name, quantity, price) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // BAT DAU TRANSACTION

            // 1) Tru ton kho tung mon. Dieu kien "quantity >= ?" nam ngay trong UPDATE
            //    nen 2 nguoi dat cung luc cung khong the tru am kho (DB tu khoa dong).
            for (OrderItem item : order.getItems()) {
                deductStock(conn, item);
            }

            // 2) Tru luot dung ma giam gia (neu co)
            if (order.getCouponCode() != null && !order.getCouponCode().isEmpty()) {
                String sqlCoupon = "UPDATE coupons SET used_count = used_count + 1 "
                                 + "WHERE code = ? AND active = 1 AND used_count < max_uses "
                                 + "AND (expires_at IS NULL OR expires_at > NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sqlCoupon)) {
                    ps.setString(1, order.getCouponCode());
                    if (ps.executeUpdate() == 0) {
                        throw new OrderException("Mã giảm giá không còn hiệu lực. Vui lòng bỏ mã và thử lại.");
                    }
                }
            }

            // 3) Insert don hang
            int orderId = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                if (order.getUserId() != null) {
                    ps.setInt(1, order.getUserId());
                } else {
                    ps.setNull(1, Types.INTEGER);
                }
                ps.setString(2, order.getCustomerName());
                ps.setString(3, order.getPhone());
                ps.setString(4, order.getAddress());
                ps.setString(5, order.getNote());
                ps.setBigDecimal(6, order.getTotalAmount());
                ps.setBigDecimal(7, order.getShipFee());
                ps.setString(8, order.getCouponCode());
                ps.setBigDecimal(9, order.getDiscountAmount());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) orderId = keys.getInt(1);
                }
            }
            if (orderId == 0) {
                throw new OrderException("Đặt hàng thất bại. Vui lòng thử lại.");
            }

            // Insert tung dong chi tiet don hang
            try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                for (OrderItem item : order.getItems()) {
                    ps.setInt(1, orderId);
                    ps.setInt(2, item.getProductId());
                    if (item.getVariantId() != null) {
                        ps.setInt(3, item.getVariantId());
                    } else {
                        ps.setNull(3, Types.INTEGER);
                    }
                    ps.setString(4, item.getVariantName());
                    ps.setInt(5, item.getQuantity());
                    ps.setBigDecimal(6, item.getPrice());
                    ps.addBatch();
                }
                ps.executeBatch();
            }

            // 4) Ghi lich su: don vua duoc tao boi khach
            insertHistory(conn, orderId, null, OrderStatus.PENDING, "customer");

            conn.commit(); // THANH CONG
            return orderId;

        } catch (OrderException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            e.printStackTrace();
            throw new OrderException("Đặt hàng thất bại do lỗi hệ thống. Vui lòng thử lại sau.");
        } finally {
            closeQuietly(conn);
        }
    }

    /**
     * Tru ton kho cho 1 mon trong don:
     * - Co phan loai  -> tru kho cua variant (kem kiem tra du hang), dong thoi tru
     *   so luong tong quan cua san pham (khong am duoi 0) de trang chu hien thi dung.
     * - Khong phan loai -> tru truc tiep kho san pham (kem kiem tra du hang).
     */
    private void deductStock(Connection conn, OrderItem item) throws SQLException, OrderException {
        if (item.getVariantId() != null) {
            String sql = "UPDATE product_variants SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getVariantId());
                ps.setInt(3, item.getQuantity());
                if (ps.executeUpdate() == 0) {
                    throw new OrderException(outOfStockMessage(item));
                }
            }
            String sqlProduct = "UPDATE products SET quantity = GREATEST(quantity - ?, 0) WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlProduct)) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getProductId());
                ps.executeUpdate();
            }
        } else {
            String sql = "UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, item.getQuantity());
                ps.setInt(2, item.getProductId());
                ps.setInt(3, item.getQuantity());
                if (ps.executeUpdate() == 0) {
                    throw new OrderException(outOfStockMessage(item));
                }
            }
        }
    }

    private String outOfStockMessage(OrderItem item) {
        String name = item.getProductName() != null ? item.getProductName() : ("sản phẩm #" + item.getProductId());
        if (item.getVariantName() != null) name += " (" + item.getVariantName() + ")";
        return "Sản phẩm \"" + name + "\" không đủ hàng. Vui lòng giảm số lượng hoặc bỏ khỏi giỏ.";
    }

    /**
     * Doi trang thai don hang theo luat trong OrderStatus (trong TRANSACTION):
     * - Kiem tra buoc chuyen hop le (vd khong the giao don da huy).
     * - Neu chuyen sang CANCELLED -> hoan lai ton kho cac mon trong don.
     * - Ghi lich su vao order_status_history.
     *
     * @throws OrderException buoc chuyen khong hop le / loi he thong.
     */
    public void updateStatus(int id, String newStatus, String changedBy) throws OrderException {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // Doc trang thai hien tai (khoa dong de tranh 2 admin bam cung luc)
            String current = null;
            try (PreparedStatement ps = conn.prepareStatement(
                    "SELECT status FROM orders WHERE id = ? FOR UPDATE")) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) current = rs.getString(1);
                }
            }
            if (current == null) {
                throw new OrderException("Không tìm thấy đơn hàng #" + id + ".");
            }
            if (!OrderStatus.canTransition(current, newStatus)) {
                throw new OrderException("Không thể chuyển đơn #" + id + " từ \""
                        + OrderStatus.label(current) + "\" sang \"" + OrderStatus.label(newStatus) + "\".");
            }

            try (PreparedStatement ps = conn.prepareStatement("UPDATE orders SET status = ? WHERE id = ?")) {
                ps.setString(1, newStatus);
                ps.setInt(2, id);
                ps.executeUpdate();
            }

            // Huy don -> tra lai ton kho
            if (OrderStatus.CANCELLED.equals(newStatus)) {
                restoreStock(conn, id);
            }

            insertHistory(conn, id, current, newStatus, changedBy);
            conn.commit();

        } catch (OrderException e) {
            rollbackQuietly(conn);
            throw e;
        } catch (SQLException e) {
            rollbackQuietly(conn);
            e.printStackTrace();
            throw new OrderException("Cập nhật trạng thái thất bại do lỗi hệ thống.");
        } finally {
            closeQuietly(conn);
        }
    }

    /** Hoan lai ton kho toan bo mon trong don (dung khi huy don). */
    private void restoreStock(Connection conn, int orderId) throws SQLException {
        String sqlItems = "SELECT product_id, variant_id, quantity FROM order_items WHERE order_id = ?";
        List<int[]> rows = new ArrayList<>(); // [productId, variantId(0 = null), quantity]
        try (PreparedStatement ps = conn.prepareStatement(sqlItems)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int variantId = rs.getInt("variant_id");
                    if (rs.wasNull()) variantId = 0;
                    rows.add(new int[]{rs.getInt("product_id"), variantId, rs.getInt("quantity")});
                }
            }
        }
        for (int[] row : rows) {
            if (row[1] > 0) {
                try (PreparedStatement ps = conn.prepareStatement(
                        "UPDATE product_variants SET quantity = quantity + ? WHERE id = ?")) {
                    ps.setInt(1, row[2]);
                    ps.setInt(2, row[1]);
                    ps.executeUpdate();
                }
            }
            try (PreparedStatement ps = conn.prepareStatement(
                    "UPDATE products SET quantity = quantity + ? WHERE id = ?")) {
                ps.setInt(1, row[2]);
                ps.setInt(2, row[0]);
                ps.executeUpdate();
            }
        }
    }

    /** Ghi 1 dong lich su trang thai (dung chung connection cua transaction). */
    private void insertHistory(Connection conn, int orderId, String oldStatus, String newStatus,
                               String changedBy) throws SQLException {
        String sql = "INSERT INTO order_status_history(order_id, old_status, new_status, changed_by) "
                   + "VALUES (?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setString(2, oldStatus);
            ps.setString(3, newStatus);
            ps.setString(4, changedBy);
            ps.executeUpdate();
        }
    }

    /** Cap nhat ghi chu noi bo cua admin cho 1 don. */
    public boolean updateAdminNote(int id, String note) {
        String sql = "UPDATE orders SET admin_note = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, note);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ========================= TRUY VAN =========================

    /**
     * Tim don hang co loc + phan trang (trang quan ly don cua admin).
     *
     * @param status trang thai can loc, null/rong = tat ca
     * @param phone  loc theo SDT (LIKE), null/rong = bo qua
     * @param fromDate ngay bat dau (yyyy-MM-dd), null/rong = bo qua
     * @param toDate   ngay ket thuc (yyyy-MM-dd, tinh het ngay), null/rong = bo qua
     * @param page   trang hien tai (tu 1)
     * @param pageSize so don moi trang
     */
    public List<Order> search(String status, String phone, String fromDate, String toDate,
                              int page, int pageSize) {
        List<Order> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, status, phone, fromDate, toDate);
        sql.append(" ORDER BY id DESC LIMIT ? OFFSET ?");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrderRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Dem so don khop bo loc (de tinh so trang). */
    public int countSearch(String status, String phone, String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();
        appendFilters(sql, params, status, phone, fromDate, toDate);

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            setParams(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private void appendFilters(StringBuilder sql, List<Object> params,
                               String status, String phone, String fromDate, String toDate) {
        if (status != null && !status.isEmpty()) {
            sql.append(" AND status = ?");
            params.add(status);
        }
        if (phone != null && !phone.isEmpty()) {
            sql.append(" AND phone LIKE ?");
            params.add("%" + phone + "%");
        }
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND created_at >= ?");
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND created_at <= ?");
            params.add(toDate + " 23:59:59");
        }
    }

    private void setParams(PreparedStatement ps, List<Object> params) throws SQLException {
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
    }

    /** Lay tat ca don hang, moi nhat truoc. */
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

    /** Lay 1 don theo id, kem chi tiet mon hang + lich su trang thai. */
    public Order getById(int id) {
        String sqlOrder = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            Order order = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlOrder)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) order = mapOrderRow(rs);
                }
            }
            if (order != null) {
                order.setItems(loadItems(conn, id));
                order.setHistory(loadHistory(conn, id));
            }
            return order;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tra cuu don hang theo SDT (trang "Tra cứu đơn" cua khach).
     * Moi don kem danh sach mon de khach doi chieu.
     */
    public List<Order> getByPhone(String phone) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE phone = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, phone);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapOrderRow(rs));
                }
            }
            for (Order o : list) {
                o.setItems(loadItems(conn, o.getId()));
                o.setHistory(loadHistory(conn, o.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Tra cuu don hang theo user_id (trang "Lịch sử đơn hàng" cua khach dang nhap).
     */
    public List<Order> getByUserId(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT * FROM orders WHERE user_id = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, userId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) list.add(mapOrderRow(rs));
                }
            }
            for (Order o : list) {
                o.setItems(loadItems(conn, o.getId()));
                o.setHistory(loadHistory(conn, o.getId()));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /** Tong doanh thu cac don da giao (DELIVERED) - DashboardServlet cua Nguoi 4 goi. */
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

    /** Dem tong so don hang (moi trang thai) - DashboardServlet cua Nguoi 4 goi. */
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

    /** Doc danh sach mon cua 1 don (JOIN products lay ten + anh de hien thi). */
    private List<OrderItem> loadItems(Connection conn, int orderId) throws SQLException {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT oi.*, p.name AS product_name, p.image AS product_image "
                   + "FROM order_items oi "
                   + "LEFT JOIN products p ON oi.product_id = p.id "
                   + "WHERE oi.order_id = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setId(rs.getInt("id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    int variantId = rs.getInt("variant_id");
                    item.setVariantId(rs.wasNull() ? null : variantId);
                    item.setVariantName(rs.getString("variant_name"));
                    item.setProductName(rs.getString("product_name"));
                    item.setProductImage(rs.getString("product_image"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setPrice(rs.getBigDecimal("price"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    /** Doc lich su trang thai cua 1 don (cu -> moi). */
    private List<OrderStatusHistory> loadHistory(Connection conn, int orderId) throws SQLException {
        List<OrderStatusHistory> list = new ArrayList<>();
        String sql = "SELECT * FROM order_status_history WHERE order_id = ? ORDER BY id";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    OrderStatusHistory h = new OrderStatusHistory();
                    h.setId(rs.getInt("id"));
                    h.setOrderId(rs.getInt("order_id"));
                    h.setOldStatus(rs.getString("old_status"));
                    h.setNewStatus(rs.getString("new_status"));
                    h.setChangedBy(rs.getString("changed_by"));
                    h.setCreatedAt(rs.getString("created_at"));
                    list.add(h);
                }
            }
        }
        return list;
    }

    /** Chuyen 1 dong ResultSet thanh object Order. */
    private Order mapOrderRow(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setId(rs.getInt("id"));
        int userId = rs.getInt("user_id");
        o.setUserId(rs.wasNull() ? null : userId);
        o.setCustomerName(rs.getString("customer_name"));
        o.setPhone(rs.getString("phone"));
        o.setAddress(rs.getString("address"));
        o.setNote(rs.getString("note"));
        o.setAdminNote(rs.getString("admin_note"));
        o.setTotalAmount(rs.getBigDecimal("total_amount"));
        o.setShipFee(rs.getBigDecimal("ship_fee"));
        o.setCouponCode(rs.getString("coupon_code"));
        o.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        o.setStatus(rs.getString("status"));
        o.setCreatedAt(rs.getString("created_at"));
        return o;
    }

    private void rollbackQuietly(Connection conn) {
        if (conn != null) {
            try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
        }
    }

    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}
