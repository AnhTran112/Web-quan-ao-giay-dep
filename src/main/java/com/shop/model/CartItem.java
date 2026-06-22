package com.shop.model;

import java.math.BigDecimal;

/**
 * Model: 1 dong trong gio hang.
 * CHI ton tai trong Session, KHONG luu database.
 */
public class CartItem {
    private int productId;
    private String name;
    private BigDecimal price;
    private String image;
    private int quantity;

    public CartItem() {}

    public CartItem(int productId, String name, BigDecimal price, String image, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.image = image;
        this.quantity = quantity;
    }

    /** Thanh tien = gia * so luong. */
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
