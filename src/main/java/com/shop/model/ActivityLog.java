package com.shop.model;

import java.sql.Timestamp;

public class ActivityLog {
    private int id;
    private String adminUsername;
    private String action;
    private String details;
    private Timestamp createdAt;

    public ActivityLog() {}

    public ActivityLog(String adminUsername, String action, String details) {
        this.adminUsername = adminUsername;
        this.action = action;
        this.details = details;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAdminUsername() { return adminUsername; }
    public void setAdminUsername(String adminUsername) { this.adminUsername = adminUsername; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }
    
    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}
