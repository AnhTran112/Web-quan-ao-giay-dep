package com.shop.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Model: 1 dong trong gio hang.
 * Khong luu database, chi dung de khoi phuc danh sach tu Cookie hien thi cho nguoi dung.
 */
public class CartItem {
    private int productId;
    private Integer variantId; // co the null neu san pham khong co phan loai
    private String name;
    private String variantName; // ten phan loai (vd: "Size 40", "Mau Do")
    private BigDecimal price;
    private String image;
    private int quantity;
    private List<ProductVariant> productVariants;

    public CartItem() {}

    public CartItem(int productId, Integer variantId, String name, String variantName, BigDecimal price, String image, int quantity) {
        this.productId = productId;
        this.variantId = variantId;
        this.name = name;
        this.variantName = variantName;
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

    public Integer getVariantId() { return variantId; }
    public void setVariantId(Integer variantId) { this.variantId = variantId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getVariantName() { return variantName; }
    public void setVariantName(String variantName) { this.variantName = variantName; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public List<ProductVariant> getProductVariants() { return productVariants; }
    public void setProductVariants(List<ProductVariant> productVariants) { this.productVariants = productVariants; }
}
