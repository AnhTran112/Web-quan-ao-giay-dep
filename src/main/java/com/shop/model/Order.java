package com.shop.model;

import java.math.BigDecimal;
import java.util.List;

/** Model: don hang. */
public class Order {
    private int id;
    private String customerName;
    private String phone;
    private String address;
    private String note;            // ghi chu cua khach khi dat
    private String adminNote;       // ghi chu noi bo cua admin
    private BigDecimal totalAmount; // tong cuoi = tam tinh - giam gia + phi ship
    private BigDecimal shipFee;
    private String couponCode;
    private BigDecimal discountAmount;
    private String status;          // xem OrderStatus
    private String createdAt;
    private List<OrderItem> items;             // chi tiet don hang
    private List<OrderStatusHistory> history;  // lich su trang thai

    public Order() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public BigDecimal getShipFee() { return shipFee; }
    public void setShipFee(BigDecimal shipFee) { this.shipFee = shipFee; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public List<OrderItem> getItems() { return items; }
    public void setItems(List<OrderItem> items) { this.items = items; }

    public List<OrderStatusHistory> getHistory() { return history; }
    public void setHistory(List<OrderStatusHistory> history) { this.history = history; }

    /** Tam tinh (tong tien hang) = tong cuoi + giam gia - phi ship. */
    public BigDecimal getSubtotal() {
        BigDecimal sub = totalAmount != null ? totalAmount : BigDecimal.ZERO;
        if (discountAmount != null) sub = sub.add(discountAmount);
        if (shipFee != null) sub = sub.subtract(shipFee);
        return sub;
    }

    /** Nhan tieng Viet cua trang thai (tien cho JSP). */
    public String getStatusLabel() { return OrderStatus.label(status); }
}
