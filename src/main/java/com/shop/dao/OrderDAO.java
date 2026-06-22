package com.shop.dao;

import com.shop.model.Order;

import java.util.List;

/** DAO don hang: truy van bang orders va order_items. */
public class OrderDAO {

    public int createOrder(Order order) {
        return 0;
    }

    public List<Order> getAll() {
        return null;
    }

    public Order getById(int id) {
        return null;
    }

    public boolean updateStatus(int id, String status) {
        return false;
    }

    public long getTotalRevenue() {
        return 0;
    }

    public int countOrders() {
        return 0;
    }
}
