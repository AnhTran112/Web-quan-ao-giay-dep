package com.shop.model;

import java.math.BigDecimal;

/** Model: 1 phân loại của sản phẩm (có giá + tồn kho riêng). */
public class ProductVariant {
    private int id;
    private int productId;
    private String name;
    private BigDecimal price;
    private int quantity;

    public ProductVariant() {}

    public ProductVariant(int productId, String name, BigDecimal price, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
