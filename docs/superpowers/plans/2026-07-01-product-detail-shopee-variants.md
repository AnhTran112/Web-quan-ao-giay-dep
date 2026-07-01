# Trang chi tiết kiểu Shopee (phân loại + giảm giá) — Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Cho phép mỗi sản phẩm có nhiều phân loại (giá + kho riêng) và % giảm giá cấu hình ở admin, hiển thị trang chi tiết + trang chủ kiểu Shopee (breadcrumb, giá gạch, badge -x%, chọn phân loại đổi giá).

**Architecture:** Thêm bảng `product_variants` + cột `discount_percent`. `ProductDAO.getById` nạp kèm variants; admin lưu variants kiểu xóa-hết-thêm-lại. Giá sau giảm tính ở JSP. Không đụng giỏ hàng (bàn giao cho Anh).

**Tech Stack:** Java 11+ Servlet/JSP (JSTL), MySQL, Bootstrap 5, Maven + Cargo (Tomcat 9).

## Global Constraints

- Không thêm thư viện mới (giữ nguyên `pom.xml`).
- Không có test framework → kiểm chứng bằng `mvn clean package cargo:run` + curl/trình duyệt.
- Không sửa file của người khác (giỏ hàng, orders). Chỉ ghi chú bàn giao.
- Tiền hiển thị định dạng VN (dấu chấm) — locale `vi_VN` đã bật ở `home.jsp`, thêm cho `product-detail.jsp`.
- Commit message tiếng Anh.
- Nhánh: `Hoàng`.

## File Structure

- Modify `docs/database.sql` — cột `discount_percent` + bảng `product_variants` + dữ liệu mẫu.
- Create `src/main/java/com/shop/model/ProductVariant.java` — model variant.
- Modify `src/main/java/com/shop/model/Product.java` — thêm discountPercent, variants, helpers.
- Create `src/main/java/com/shop/dao/ProductVariantDAO.java` — CRUD variant.
- Modify `src/main/java/com/shop/dao/ProductDAO.java` — đọc discount, nạp variants, lưu discount + variants.
- Modify `src/main/webapp/WEB-INF/views/admin/product-form.jsp` — ô % giảm + dòng phân loại động.
- Modify `src/main/java/com/shop/controller/admin/AdminProductServlet.java` — parse + lưu discount/variants.
- Modify `src/main/webapp/WEB-INF/views/product-detail.jsp` — breadcrumb, giá, chọn phân loại.
- Modify `src/main/webapp/WEB-INF/views/home.jsp` — badge -x% + giá gạch trên card.
- Modify `docs/05_phan_cong_cong_viec.md` — ghi chú bàn giao giỏ hàng cho Anh.

---

### Task 1: DB schema + dữ liệu mẫu

**Files:**
- Modify: `docs/database.sql`

**Interfaces:**
- Produces: bảng `product_variants(id, product_id, name, price, quantity)`, cột `products.discount_percent`.

- [ ] **Step 1: Thêm cột discount_percent vào bảng products**

Trong khối `CREATE TABLE products`, sau dòng `quantity ... ,` thêm:
```sql
    discount_percent INT NOT NULL DEFAULT 0,   -- % giam gia (0-100)
```

- [ ] **Step 2: Thêm bảng product_variants** (đặt ngay sau khối `CREATE TABLE products`)

```sql
-- ----------------------------
-- Bang: product_variants (phan loai san pham) -- Nguoi 1 (Hoang) so huu
-- Moi loai co gia + ton kho rieng.
-- ----------------------------
CREATE TABLE product_variants (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    name       VARCHAR(150) NOT NULL,
    price      DECIMAL(12,0) NOT NULL DEFAULT 0,
    quantity   INT NOT NULL DEFAULT 0,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_variants_product (product_id)
);
```

- [ ] **Step 3: Sửa INSERT products cho có % giảm mẫu**

Thay khối `INSERT INTO products (...) VALUES` để thêm cột `discount_percent` ở cuối mỗi dòng (ví dụ):
```sql
INSERT INTO products (category_id, name, description, price, image, quantity, discount_percent) VALUES
(1, 'Giay Sneaker Trang', 'Giay sneaker mau trang nang dong', 450000, 'sneaker-trang.jpg', 50, 25),
(1, 'Giay Chay Bo',       'Giay chay bo em chan',            650000, 'chay-bo.jpg',      30, 0),
(2, 'Giay Tay Den',       'Giay da nam mau den lich su',     800000, 'giay-tay-den.jpg', 20, 10),
(3, 'Ao Thun Basic',      'Ao thun co tron nhieu mau',       150000, 'ao-thun.jpg',      100, 0),
(3, 'Ao Polo',            'Ao polo co be',                   250000, 'ao-polo.jpg',      60, 15),
(4, 'Quan Jean Slimfit',  'Quan jean om dang the thao',      350000, 'jean-slim.jpg',    40, 0);
```

- [ ] **Step 4: Thêm variant mẫu** (cuối phần DU LIEU MAU)

```sql
-- Phan loai mau cho san pham 1 (Sneaker Trang) va 4 (Ao Thun Basic)
INSERT INTO product_variants (product_id, name, price, quantity) VALUES
(1, 'Size 39', 450000, 10),
(1, 'Size 40', 460000, 15),
(1, 'Size 41', 470000, 8),
(4, 'Mau Trang', 150000, 40),
(4, 'Mau Den',   150000, 35),
(4, 'Mau Xanh',  160000, 25);
```

- [ ] **Step 5: Chạy lại schema + kiểm chứng**

Run:
```bash
mysql -u root -proot < docs/database.sql
mysql -u root -proot -e "USE shop_db; SHOW COLUMNS FROM products LIKE 'discount_percent'; SELECT COUNT(*) FROM product_variants;"
```
Expected: có cột `discount_percent`; `COUNT(*)` = 6.

- [ ] **Step 6: Commit**

```bash
git add docs/database.sql
git commit -m "feat(db): add product_variants table and discount_percent column"
```

---

### Task 2: Model ProductVariant + mở rộng Product

**Files:**
- Create: `src/main/java/com/shop/model/ProductVariant.java`
- Modify: `src/main/java/com/shop/model/Product.java`

**Interfaces:**
- Produces: `ProductVariant` (getId/getName/getPrice/getQuantity/getProductId + setters); `Product.getDiscountPercent()`, `Product.setDiscountPercent(int)`, `Product.getVariants()`, `Product.setVariants(List)`, `Product.hasVariants()`, `Product.getMinPrice()`, `Product.getMaxPrice()`.

- [ ] **Step 1: Tạo ProductVariant.java**

```java
package com.shop.model;

import java.math.BigDecimal;

/** Model: 1 phan loai cua san pham (co gia + ton kho rieng). */
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
```

- [ ] **Step 2: Thêm field + helper vào Product.java**

Thêm import ở đầu file:
```java
import java.util.List;
import java.util.ArrayList;
```
Thêm field (cạnh các field khác):
```java
    private int discountPercent;
    private List<ProductVariant> variants = new ArrayList<>();
```
Thêm getter/setter + helper (cuối class, trước dấu `}`):
```java
    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }

    public List<ProductVariant> getVariants() { return variants; }
    public void setVariants(List<ProductVariant> variants) { this.variants = variants; }

    /** San pham co phan loai hay khong. */
    public boolean isHasVariants() { return variants != null && !variants.isEmpty(); }

    /** Gia thap nhat: nho nhat trong variants, hoac price neu khong co variant. */
    public java.math.BigDecimal getMinPrice() {
        if (!isHasVariants()) return price;
        java.math.BigDecimal min = variants.get(0).getPrice();
        for (ProductVariant v : variants) if (v.getPrice().compareTo(min) < 0) min = v.getPrice();
        return min;
    }

    /** Gia cao nhat: lon nhat trong variants, hoac price neu khong co variant. */
    public java.math.BigDecimal getMaxPrice() {
        if (!isHasVariants()) return price;
        java.math.BigDecimal max = variants.get(0).getPrice();
        for (ProductVariant v : variants) if (v.getPrice().compareTo(max) > 0) max = v.getPrice();
        return max;
    }
```
Ghi chú: EL gọi `${product.hasVariants}` map tới `isHasVariants()`.

- [ ] **Step 3: Build kiểm chứng biên dịch**

Run: `mvn -q compile`
Expected: BUILD SUCCESS (không lỗi biên dịch).

- [ ] **Step 4: Commit**

```bash
git add src/main/java/com/shop/model/ProductVariant.java src/main/java/com/shop/model/Product.java
git commit -m "feat(model): add ProductVariant and discount/variants on Product"
```

---

### Task 3: ProductVariantDAO + cập nhật ProductDAO

**Files:**
- Create: `src/main/java/com/shop/dao/ProductVariantDAO.java`
- Modify: `src/main/java/com/shop/dao/ProductDAO.java`

**Interfaces:**
- Consumes: `ProductVariant`, `Product` (Task 2).
- Produces: `ProductVariantDAO.getByProductId(int)`, `.insert(ProductVariant)`, `.deleteByProductId(int)`. `ProductDAO.getById` trả product có `variants` + `discountPercent`; `insert`/`update` lưu discount + variants.

- [ ] **Step 1: Tạo ProductVariantDAO.java**

```java
package com.shop.dao;

import com.shop.model.ProductVariant;
import com.shop.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/** DAO cho bang product_variants. */
public class ProductVariantDAO {

    public List<ProductVariant> getByProductId(int productId) {
        List<ProductVariant> list = new ArrayList<>();
        String sql = "SELECT * FROM product_variants WHERE product_id = ? ORDER BY id";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ProductVariant v = new ProductVariant();
                    v.setId(rs.getInt("id"));
                    v.setProductId(rs.getInt("product_id"));
                    v.setName(rs.getString("name"));
                    v.setPrice(rs.getBigDecimal("price"));
                    v.setQuantity(rs.getInt("quantity"));
                    list.add(v);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void insert(ProductVariant v) {
        String sql = "INSERT INTO product_variants(product_id, name, price, quantity) VALUES (?,?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, v.getProductId());
            ps.setString(2, v.getName());
            ps.setBigDecimal(3, v.getPrice());
            ps.setInt(4, v.getQuantity());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteByProductId(int productId) {
        String sql = "DELETE FROM product_variants WHERE product_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
```

- [ ] **Step 2: ProductDAO.mapRow đọc discount_percent**

Trong `mapRow`, trước `return p;` thêm:
```java
        p.setDiscountPercent(rs.getInt("discount_percent"));
```

- [ ] **Step 3: ProductDAO.getById nạp kèm variants**

Thêm field ở đầu class:
```java
    private final ProductVariantDAO variantDAO = new ProductVariantDAO();
```
Trong `getById`, sau khi map được product (trước `return`), gán variants:
```java
            if (p != null) p.setVariants(variantDAO.getByProductId(p.getId()));
```
(Điều chỉnh biến cho khớp code hiện có — biến product trong getById.)

- [ ] **Step 4: insert lưu discount_percent + variants**

Sửa câu SQL insert thêm cột `discount_percent`:
```java
        String sql = "INSERT INTO products(category_id, name, description, price, image, quantity, discount_percent) "
                   + "VALUES (?,?,?,?,?,?,?)";
```
Set param thứ 7:
```java
            ps.setInt(7, p.getDiscountPercent());
```
Lấy id tự tăng để lưu variants: dùng `Statement.RETURN_GENERATED_KEYS`. Sửa `prepareStatement(sql)` thành `prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)`; sau `executeUpdate()`:
```java
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    int newId = keys.getInt(1);
                    for (ProductVariant v : p.getVariants()) {
                        v.setProductId(newId);
                        variantDAO.insert(v);
                    }
                }
            }
```

- [ ] **Step 5: update lưu discount_percent + thay variants**

Sửa SQL update thêm `discount_percent = ?` và set đúng vị trí param. Sau `executeUpdate()`:
```java
            variantDAO.deleteByProductId(p.getId());
            for (ProductVariant v : p.getVariants()) {
                v.setProductId(p.getId());
                variantDAO.insert(v);
            }
```

- [ ] **Step 6: Build + kiểm chứng dữ liệu qua trang chi tiết**

Run: `mvn clean package cargo:run` (nền), rồi:
```bash
curl -s "http://localhost:8080/shop/product?id=1" | grep -oE "Size 39|Size 40|Size 41"
```
Expected: in ra 3 dòng size (variants của SP 1 hiển thị — sau khi làm Task 5; ở bước này chỉ cần build SUCCESS và trang trả 200).

- [ ] **Step 7: Commit**

```bash
git add src/main/java/com/shop/dao/ProductVariantDAO.java src/main/java/com/shop/dao/ProductDAO.java
git commit -m "feat(dao): load and persist product variants and discount"
```

---

### Task 4: Admin — ô % giảm + dòng phân loại động

**Files:**
- Modify: `src/main/webapp/WEB-INF/views/admin/product-form.jsp`
- Modify: `src/main/java/com/shop/controller/admin/AdminProductServlet.java`

**Interfaces:**
- Consumes: `ProductDAO.insert/update`, `Product.setDiscountPercent`, `Product.setVariants`.
- Produces: form gửi `discountPercent`, mảng `variantName[]/variantPrice[]/variantQty[]`.

- [ ] **Step 1: Thêm ô % giảm vào product-form.jsp**

Sau khối "Số lượng", thêm:
```html
    <div class="mb-3">
        <label class="form-label">Giảm giá (%)</label>
        <input type="number" name="discountPercent" class="form-control"
               value="${empty product.discountPercent ? 0 : product.discountPercent}"
               min="0" max="100">
    </div>
```

- [ ] **Step 2: Thêm khu phân loại động vào product-form.jsp** (trước nút Lưu)

```html
    <div class="mb-3">
        <label class="form-label d-block">Phân loại (tùy chọn — để trống nếu không có)</label>
        <table class="table table-sm align-middle" id="variantTable">
            <thead><tr><th>Tên loại</th><th>Giá</th><th>Kho</th><th></th></tr></thead>
            <tbody>
                <c:forEach var="v" items="${product.variants}">
                    <tr>
                        <td><input type="text" name="variantName" class="form-control" value="${v.name}"></td>
                        <td><input type="number" name="variantPrice" class="form-control" value="${v.price}" min="0"></td>
                        <td><input type="number" name="variantQty" class="form-control" value="${v.quantity}" min="0"></td>
                        <td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariantRow(this)">×</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="addVariantRow()">+ Thêm phân loại</button>
    </div>

    <script>
    function addVariantRow() {
        var tbody = document.querySelector('#variantTable tbody');
        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td><input type="text" name="variantName" class="form-control"></td>' +
            '<td><input type="number" name="variantPrice" class="form-control" min="0"></td>' +
            '<td><input type="number" name="variantQty" class="form-control" min="0"></td>' +
            '<td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariantRow(this)">×</button></td>';
        tbody.appendChild(tr);
    }
    function removeVariantRow(btn) { btn.closest('tr').remove(); }
    </script>
```

- [ ] **Step 3: AdminProductServlet parse discount + variants**

Trong `doPost`, sau khi đọc các field cũ, thêm:
```java
        // % giam gia (kep 0-100)
        int discountPercent = 0;
        try {
            discountPercent = Integer.parseInt(req.getParameter("discountPercent"));
        } catch (Exception ignore) {}
        if (discountPercent < 0) discountPercent = 0;
        if (discountPercent > 100) discountPercent = 100;

        // Phan loai: doc song song 3 mang, bo dong ten rong
        java.util.List<com.shop.model.ProductVariant> variants = new java.util.ArrayList<>();
        String[] vNames = req.getParameterValues("variantName");
        String[] vPrices = req.getParameterValues("variantPrice");
        String[] vQtys = req.getParameterValues("variantQty");
        if (vNames != null) {
            for (int i = 0; i < vNames.length; i++) {
                String vn = vNames[i] == null ? "" : vNames[i].trim();
                if (vn.isEmpty()) continue;
                java.math.BigDecimal vp = java.math.BigDecimal.ZERO;
                int vq = 0;
                try { vp = new java.math.BigDecimal(vPrices[i]); } catch (Exception ignore) {}
                try { vq = Integer.parseInt(vQtys[i]); } catch (Exception ignore) {}
                variants.add(new com.shop.model.ProductVariant(0, vn, vp, vq));
            }
        }
```

- [ ] **Step 4: Gán vào Product trước khi lưu**

Ở đoạn dựng object `Product p`, thêm:
```java
        p.setDiscountPercent(discountPercent);
        p.setVariants(variants);
```
(Đặt trước các nhánh `insert`/`update`; nếu có nhánh trả về khi lỗi validate cũng gán trước đó để form giữ lại.)

- [ ] **Step 5: Build + kiểm chứng admin lưu variant**

Run: `mvn clean package cargo:run` (nền). Đăng nhập admin, sửa 1 sản phẩm, thêm 2 phân loại + % giảm, Lưu. Rồi:
```bash
mysql -u root -proot -e "USE shop_db; SELECT product_id,name,price,quantity FROM product_variants ORDER BY id DESC LIMIT 5;"
```
Expected: thấy các variant vừa nhập.

- [ ] **Step 6: Commit**

```bash
git add src/main/webapp/WEB-INF/views/admin/product-form.jsp src/main/java/com/shop/controller/admin/AdminProductServlet.java
git commit -m "feat(admin): manage product variants and discount percent"
```

---

### Task 5: Trang chi tiết — breadcrumb + giá + chọn phân loại

**Files:**
- Modify: `src/main/webapp/WEB-INF/views/product-detail.jsp`

**Interfaces:**
- Consumes: `product.discountPercent`, `product.variants`, `product.hasVariants`, `product.minPrice/maxPrice`, `categoryName`.

- [ ] **Step 1: Bật locale VN ở đầu file** (sau include header)

```jsp
<fmt:setLocale value="vi_VN" />
```

- [ ] **Step 2: Breadcrumb kiểu Shopee** — thay khối `<nav ... breadcrumb>` hiện có bằng:

```html
<nav aria-label="breadcrumb" class="shopee-crumb my-2">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
    <span class="crumb-sep">›</span>
    <a href="${pageContext.request.contextPath}/home?categoryId=${product.categoryId}">
        ${not empty categoryName ? categoryName : 'Sản phẩm'}</a>
    <span class="crumb-sep">›</span>
    <span class="text-muted">${product.name}</span>
</nav>
```

- [ ] **Step 3: Khối giá có giảm giá + khoảng giá** — thay `<div class="detail-price-box">...</div>` bằng:

```html
<div class="detail-price-box">
    <c:choose>
        <c:when test="${product.hasVariants}">
            <span class="price" id="priceNow">
                <fmt:formatNumber value="${product.minPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                <c:if test="${product.minPrice != product.maxPrice}">
                    – <fmt:formatNumber value="${product.maxPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                </c:if> đ
            </span>
        </c:when>
        <c:otherwise>
            <span class="price" id="priceNow">
                <fmt:formatNumber value="${product.price * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/> đ
            </span>
        </c:otherwise>
    </c:choose>
    <c:if test="${product.discountPercent > 0}">
        <span class="price-old"><fmt:formatNumber value="${product.hasVariants ? product.minPrice : product.price}" type="number" maxFractionDigits="0"/> đ</span>
        <span class="discount-badge">-${product.discountPercent}%</span>
    </c:if>
</div>
```

- [ ] **Step 4: Nút chọn phân loại** — thêm ngay trên khối `<form ... /cart>`:

```html
<c:if test="${product.hasVariants}">
    <div class="mb-3">
        <label class="form-label fw-semibold">Phân loại</label>
        <div class="d-flex flex-wrap gap-2" id="variantGroup">
            <c:forEach var="v" items="${product.variants}">
                <button type="button" class="btn btn-outline-secondary variant-btn"
                        data-id="${v.id}"
                        data-price="${v.price * (100 - product.discountPercent) / 100}"
                        data-stock="${v.quantity}">${v.name}</button>
            </c:forEach>
        </div>
    </div>
</c:if>
```

- [ ] **Step 5: Hidden variantId trong form + JS chọn loại**

Trong `<form ... /cart>` thêm sau `productId`:
```html
                <input type="hidden" name="variantId" id="variantId" value="">
```
Thêm vào cuối khối `<script>` (cạnh changeQty):
```javascript
    // Chon phan loai: doi gia + ton kho + gioi han so luong
    document.querySelectorAll('.variant-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.variant-btn').forEach(function (b) {
                b.classList.remove('active', 'btn-primary'); b.classList.add('btn-outline-secondary');
            });
            btn.classList.add('active', 'btn-primary'); btn.classList.remove('btn-outline-secondary');
            document.getElementById('variantId').value = btn.dataset.id;
            var price = Number(btn.dataset.price);
            document.getElementById('priceNow').innerText = price.toLocaleString('vi-VN') + ' đ';
            var qty = document.getElementById('qty');
            qty.max = btn.dataset.stock;
            if (Number(qty.value) > Number(btn.dataset.stock)) qty.value = btn.dataset.stock;
        });
    });
```

- [ ] **Step 6: Chặn thêm giỏ khi chưa chọn loại** — thêm vào `<form>` thuộc tính `onsubmit`:

```html
<form action="${pageContext.request.contextPath}/cart" method="post"
      onsubmit="return (${product.hasVariants} === false) || document.getElementById('variantId').value !== '' || (alert('Vui lòng chọn phân loại'), false);">
```
(Lưu ý: `${product.hasVariants}` render thành `true`/`false`.)

- [ ] **Step 7: CSS cho breadcrumb + giá gạch + badge** — thêm vào `assets/css/style.css`:

```css
.shopee-crumb { font-size: 0.9rem; }
.shopee-crumb a { color: #2563eb; text-decoration: none; }
.shopee-crumb .crumb-sep { margin: 0 8px; color: #9ca3af; }
.price-old { color: #9ca3af; text-decoration: line-through; margin-left: 10px; font-size: 1rem; }
.discount-badge { background: #e53935; color: #fff; font-weight: 700; font-size: 0.8rem;
    border-radius: 4px; padding: 2px 6px; margin-left: 8px; vertical-align: middle; }
.variant-btn.active { font-weight: 600; }
```
Rồi tăng số phiên bản CSS ở `header.jsp`: `style.css?v=3`.

- [ ] **Step 8: Build + kiểm chứng**

Run: `mvn clean package cargo:run` (nền). Rồi:
```bash
curl -s "http://localhost:8080/shop/product?id=1" | grep -oE "Size 39|discount-badge|shopee-crumb"
```
Expected: có `shopee-crumb`, `Size 39`, `discount-badge` (SP1 giảm 25%). Mở trình duyệt bấm chọn size → giá đổi.

- [ ] **Step 9: Commit**

```bash
git add src/main/webapp/WEB-INF/views/product-detail.jsp src/main/webapp/assets/css/style.css src/main/webapp/WEB-INF/views/common/header.jsp
git commit -m "feat(storefront): Shopee-style product detail with variants and discount"
```

---

### Task 6: Trang chủ — badge giảm giá trên card

**Files:**
- Modify: `src/main/webapp/WEB-INF/views/home.jsp`

**Interfaces:**
- Consumes: `p.discountPercent`, `p.price`.

- [ ] **Step 1: Badge + giá gạch trên card** — trong vòng `c:forEach var="p"`, sửa khối giá `<p class="product-price">` thành:

```html
                            <p class="product-price mb-2">
                                <c:choose>
                                    <c:when test="${p.discountPercent > 0}">
                                        <fmt:formatNumber value="${p.price * (100 - p.discountPercent) / 100}" type="number" maxFractionDigits="0"/> đ
                                        <span class="price-old"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> đ</span>
                                    </c:when>
                                    <c:otherwise>
                                        <fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/> đ
                                    </c:otherwise>
                                </c:choose>
                            </p>
```
Và trên ảnh card, thêm badge góc (bọc ảnh trong `position-relative`): sau `<div class="card product-card h-100">` thêm:
```html
                        <c:if test="${p.discountPercent > 0}">
                            <span class="discount-badge card-discount">-${p.discountPercent}%</span>
                        </c:if>
```
Thêm CSS vào `style.css`:
```css
.product-card { position: relative; }
.card-discount { position: absolute; top: 8px; right: 8px; z-index: 2; }
```

- [ ] **Step 2: Build + kiểm chứng**

Run: `mvn clean package cargo:run` (nền). Rồi:
```bash
curl -s "http://localhost:8080/shop/home" | grep -c "card-discount"
```
Expected: ≥ 1 (SP có giảm giá hiện badge).

- [ ] **Step 3: Commit**

```bash
git add src/main/webapp/WEB-INF/views/home.jsp src/main/webapp/assets/css/style.css
git commit -m "feat(storefront): show discount badge and old price on home cards"
```

---

### Task 7: Bàn giao giỏ hàng cho Anh

**Files:**
- Modify: `docs/05_phan_cong_cong_viec.md`

- [ ] **Step 1: Thêm mục bàn giao** (cuối phần Người 2 — Anh)

```markdown
### 3.7. Ghi chú tích hợp phân loại (variants) — do Hoàng bàn giao
- `product-detail.jsp` giờ gửi thêm `variantId` khi Thêm giỏ / Mua ngay.
- `CartServlet action=add` cần đọc `variantId`; nếu có, lấy giá + tên theo `ProductVariant`
  (dùng `ProductVariantDAO.getByProductId` rồi tìm theo id) thay vì `product.price`.
- `CartItem` cần thêm `variantId` + `variantName`; khóa gộp dòng là cặp `(productId, variantId)`.
- Tồn kho để kiểm tra là `variant.quantity` (nếu có variant), ngược lại `product.quantity`.
```

- [ ] **Step 2: Commit**

```bash
git add docs/05_phan_cong_cong_viec.md
git commit -m "docs: handoff notes for variant-aware cart (for Anh)"
```

---

## Self-Review

- **Spec coverage:** DB (Task 1), model (Task 2), DAO (Task 3), admin discount+variants (Task 4), trang chi tiết breadcrumb/giá/chọn loại (Task 5), badge trang chủ (Task 6), bàn giao Anh (Task 7). Đủ các mục spec.
- **Placeholder scan:** Mỗi step có code thật + lệnh + kết quả kỳ vọng. Không TBD.
- **Type consistency:** `isHasVariants()` ↔ EL `${product.hasVariants}`; `ProductVariant(int productId, String name, BigDecimal price, int quantity)` dùng nhất quán ở DAO + servlet; `getByProductId/insert/deleteByProductId` dùng đúng tên ở ProductDAO + servlet.
- **Rủi ro cần lưu ý khi thực thi:** vị trí tham số PreparedStatement trong `ProductDAO.update` phải khớp sau khi thêm `discount_percent = ?` — đọc code hiện tại rồi chèn đúng thứ tự.
