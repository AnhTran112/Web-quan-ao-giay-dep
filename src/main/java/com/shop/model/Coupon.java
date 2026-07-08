package com.shop.model;

/** Model: ma giam gia (bang coupons). */
public class Coupon {
    private int id;
    private String code;
    private int discountPercent;  // % giam tren tam tinh
    private int maxUses;
    private int usedCount;
    private String expiresAt;     // null = khong het han
    private boolean active;
    private String createdAt;

    public Coupon() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public int getMaxUses() { return maxUses; }
    public void setMaxUses(int maxUses) { this.maxUses = maxUses; }

    public int getUsedCount() { return usedCount; }
    public void setUsedCount(int usedCount) { this.usedCount = usedCount; }

    public String getExpiresAt() { return expiresAt; }
    public void setExpiresAt(String expiresAt) { this.expiresAt = expiresAt; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    /** Con luot dung khong (chua tinh het han - viec do DAO kiem tra bang NOW() cua DB). */
    public boolean hasUsesLeft() { return usedCount < maxUses; }
}
