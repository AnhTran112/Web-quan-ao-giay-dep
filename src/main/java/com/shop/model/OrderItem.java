package com.shop.model;

import java.math.BigDecimal;

/** Model: chi tiet don hang (1 dong san pham trong don). */
public class OrderItem {
    private int id;
    private int orderId;
    private int productId;
    private Integer variantId;    // phan loai khach chon (null neu khong co)
    private String variantName;   // ten phan loai tai thoi diem mua (vd "Size 40")
    private String productName;   // tien hien thi
    private String productImage;  // tien hien thi (JOIN products)
    private int quantity;
    private BigDecimal price;

    public OrderItem() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getProductImage() { return productImage; }
    public void setProductImage(String productImage) { this.productImage = productImage; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    /** Thanh tien = gia * so luong (tien cho JSP). */
    public BigDecimal getSubtotal() {
        return price == null ? BigDecimal.ZERO : price.multiply(BigDecimal.valueOf(quantity));
    }
}
