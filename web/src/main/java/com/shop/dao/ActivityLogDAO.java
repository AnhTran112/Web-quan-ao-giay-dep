package com.shop.dao;

import com.shop.model.ActivityLog;
import com.shop.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ActivityLogDAO {

    public static void log(String adminUsername, String action, String entity, Integer entityId, String description) {
        String sql = "INSERT INTO activity_logs (admin_username, action, entity, entity_id, description) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, adminUsername);
            ps.setString(2, action);
            ps.setString(3, entity);
            if (entityId != null) {
                ps.setInt(4, entityId);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, description);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ActivityLog> getAll() {
        List<ActivityLog> list = new ArrayList<>();
        String sql = "SELECT * FROM activity_logs ORDER BY id DESC LIMIT 200";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ActivityLog log = new ActivityLog();
                log.setId(rs.getInt("id"));
                log.setAdminUsername(rs.getString("admin_username"));
                log.setAction(rs.getString("action"));
                
                // Set details = entity + entity_id + description for backward compatibility with JSP
                String entity = rs.getString("entity");
                int entityId = rs.getInt("entity_id");
                String desc = rs.getString("description");
                String details = entity + (entityId > 0 ? " #" + entityId : "") + " - " + desc;
                
                log.setDetails(details);
                log.setCreatedAt(rs.getTimestamp("created_at"));
                list.add(log);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
