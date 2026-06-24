package com.shop.dao;

import com.shop.model.Order;

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
     * TODO (Khoa): tao don hang.
     * - INSERT INTO orders(customer_name, phone, address, total_amount, status) ...
     * - Lay id tu tang (Statement.RETURN_GENERATED_KEYS).
     * - INSERT tung dong vao order_items (luu price tai thoi diem mua).
     * - Dung TRANSACTION: conn.setAutoCommit(false) -> commit / rollback.
     * - Tra ve id don vua tao (hoac 0 neu loi).
     */
    public int createOrder(Order order) {
        return 0;
    }

    /** TODO (Khoa): SELECT * FROM orders ORDER BY id DESC -> List<Order>. */
    public List<Order> getAll() {
        return null;
    }

    /** TODO (Khoa): lay 1 don + danh sach order_items (JOIN products de lay ten). */
    public Order getById(int id) {
        return null;
    }

    /** TODO (Khoa): UPDATE orders SET status=? WHERE id=?. */
    public boolean updateStatus(int id, String status) {
        return false;
    }

    /** TODO (Khoa): SELECT SUM(total_amount) FROM orders WHERE status='DELIVERED'. */
    public long getTotalRevenue() {
        return 0;
    }

    /** TODO (Khoa): SELECT COUNT(*) FROM orders. */
    public int countOrders() {
        return 0;
    }
}
