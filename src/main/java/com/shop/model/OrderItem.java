package com.shop.model;

import java.math.BigDecimal;

/** Model: chi tiet don hang (1 dong san pham trong don). */
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private String productName; // tien hien thi
    private int quantity;
    private BigDecimal price;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
}
