package com.shop.model;

/** Model: 1 lan doi trang thai cua don hang (bang order_status_history). */
public class OrderStatusHistory {
    private int id;
    private int orderId;
    private String oldStatus;   // null voi ban ghi tao don
    private String newStatus;
    private String changedBy;   // "customer" hoac username admin
    private String createdAt;

    public OrderStatusHistory() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public String getOldStatus() { return oldStatus; }
    public void setOldStatus(String oldStatus) { this.oldStatus = oldStatus; }

    public String getNewStatus() { return newStatus; }
    public void setNewStatus(String newStatus) { this.newStatus = newStatus; }

    public String getChangedBy() { return changedBy; }
    public void setChangedBy(String changedBy) { this.changedBy = changedBy; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** Nhan tieng Viet cua trang thai moi (tien cho JSP). */
    public String getNewStatusLabel() { return OrderStatus.label(newStatus); }
}
