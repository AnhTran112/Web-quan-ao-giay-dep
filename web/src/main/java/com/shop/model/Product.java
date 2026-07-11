package com.shop.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/** Model: san pham. */
public class Product {
    private int id;
    private int categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String image;
    private int quantity;
    private int discountPercent;
    private java.sql.Timestamp createdAt;
    private List<ProductVariant> variants = new ArrayList<>();

    public Product() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public java.sql.Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(java.sql.Timestamp createdAt) { this.createdAt = createdAt; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    // San pham co phan loai hay khong (EL goi ${product.hasVariants})
    public boolean isHasVariants() { return variants != null && !variants.isEmpty(); }

    // Gia thap nhat: nho nhat trong variants, hoac price neu khong co variant
    public BigDecimal getMinPrice() {
        if (!isHasVariants()) return price;
        BigDecimal min = variants.get(0).getPrice();
        for (ProductVariant v : variants) if (v.getPrice().compareTo(min) < 0) min = v.getPrice();
        return min;
    }

    // Gia cao nhat: lon nhat trong variants, hoac price neu khong co variant
    public BigDecimal getMaxPrice() {
        if (!isHasVariants()) return price;
        BigDecimal max = variants.get(0).getPrice();
        for (ProductVariant v : variants) if (v.getPrice().compareTo(max) > 0) max = v.getPrice();
        return max;
    }
}
