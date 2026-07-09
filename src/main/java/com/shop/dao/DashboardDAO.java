package com.shop.dao;

import com.shop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO xu ly du lieu cho Dashboard (Thong ke)
 * NGUOI PHU TRACH: Nguoi 4 (Nguyen)
 */
public class DashboardDAO {

    public List<Map<String, Object>> getTopSellingProducts() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.name, SUM(oi.quantity) as total_sold " +
                     "FROM order_items oi " +
                     "JOIN products p ON oi.product_id = p.id " +
                     "JOIN orders o ON oi.order_id = o.id " +
                     "WHERE o.status = 'DELIVERED' " +
                     "GROUP BY p.id, p.name " +
                     "ORDER BY total_sold DESC LIMIT 5";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("name", rs.getString("name"));
                map.put("total_sold", rs.getInt("total_sold"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Map<String, Object>> getLowStockProducts() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT p.name AS p_name, v.name AS v_name, v.quantity " +
                     "FROM product_variants v " +
                     "JOIN products p ON v.product_id = p.id " +
                     "WHERE v.quantity < 10 " +
                     "UNION " +
                     "SELECT name AS p_name, '' AS v_name, quantity " +
                     "FROM products " +
                     "WHERE quantity < 10 AND id NOT IN (SELECT product_id FROM product_variants) " +
                     "ORDER BY quantity ASC LIMIT 10";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                String pName = rs.getString("p_name");
                String vName = rs.getString("v_name");
                if (vName != null && !vName.trim().isEmpty()) {
                    map.put("name", pName + " (" + vName + ")");
                } else {
                    map.put("name", pName);
                }
                map.put("quantity", rs.getInt("quantity"));
                list.add(map);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Map<String, String> getRevenueLast6Months() {
        Map<String, String> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();
        
        java.time.YearMonth currentMonth = java.time.YearMonth.now();
        for (int i = 5; i >= 0; i--) {
            java.time.YearMonth m = currentMonth.minusMonths(i);
            labels.add("'Tháng " + m.getMonthValue() + "'");
            data.add(0.0);
        }
        
        String sql = "SELECT YEAR(created_at) AS yr, MONTH(created_at) AS mn, SUM(total_amount) AS revenue " +
                     "FROM orders " +
                     "WHERE status = 'DELIVERED' " +
                     "AND created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                     "GROUP BY YEAR(created_at), MONTH(created_at)";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int y = rs.getInt("yr");
                int m = rs.getInt("mn");
                double revenue = rs.getDouble("revenue");
                
                for (int i = 0; i < 6; i++) {
                    java.time.YearMonth ym = currentMonth.minusMonths(5 - i);
                    if (ym.getYear() == y && ym.getMonthValue() == m) {
                        data.set(i, revenue);
                        break;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        result.put("labels", labels.toString());
        result.put("data", data.toString());
        return result;
    }

    public long getTotalRevenue(String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT COALESCE(SUM(total_amount), 0) FROM orders WHERE status = 'DELIVERED'");
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND created_at >= ?");
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND created_at <= ?");
            params.add(toDate + " 23:59:59");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int countOrders(String fromDate, String toDate) {
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) FROM orders WHERE 1=1");
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND created_at >= ?");
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND created_at <= ?");
            params.add(toDate + " 23:59:59");
        }
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public Map<String, String> getRevenueByCategory(String fromDate, String toDate) {
        Map<String, String> result = new HashMap<>();
        List<String> labels = new ArrayList<>();
        List<Double> data = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT c.name, SUM(oi.price * oi.quantity) AS revenue " +
            "FROM order_items oi " +
            "JOIN products p ON oi.product_id = p.id " +
            "JOIN categories c ON p.category_id = c.id " +
            "JOIN orders o ON oi.order_id = o.id " +
            "WHERE o.status = 'DELIVERED'"
        );
        List<Object> params = new ArrayList<>();
        if (fromDate != null && !fromDate.isEmpty()) {
            sql.append(" AND o.created_at >= ?");
            params.add(fromDate + " 00:00:00");
        }
        if (toDate != null && !toDate.isEmpty()) {
            sql.append(" AND o.created_at <= ?");
            params.add(toDate + " 23:59:59");
        }
        sql.append(" GROUP BY c.id, c.name");

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    labels.add("'" + rs.getString("name") + "'");
                    data.add(rs.getDouble("revenue"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        result.put("labels", labels.toString());
        result.put("data", data.toString());
        return result;
    }

    public List<Map<String, Object>> getLatestOrders(int limit) {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT id, customer_name, total_amount, status, created_at FROM orders ORDER BY id DESC LIMIT ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", rs.getInt("id"));
                    map.put("customer_name", rs.getString("customer_name"));
                    map.put("total_amount", rs.getBigDecimal("total_amount"));
                    map.put("status", rs.getString("status"));
                    map.put("created_at", rs.getString("created_at"));
                    list.add(map);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
