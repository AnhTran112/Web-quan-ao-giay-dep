# Thiết kế: Trang chi tiết sản phẩm kiểu Shopee (phân loại + giảm giá + breadcrumb)

Ngày: 2026-07-01
Người phụ trách: Hoàng (Người 1)

## Mục tiêu

Nâng cấp trang chi tiết sản phẩm cho giống Shopee ở mức "đầy đủ":

- **Phân loại (variants)**: mỗi loại có **giá + tồn kho riêng**.
- **Giảm giá**: admin nhập **% giảm** (`discount_percent`), trang tự tính giá sau giảm + hiện `-x%`.
- **Breadcrumb** kiểu Shopee: `Trang chủ ▸ [Danh mục] ▸ [Tên SP]`.
- Giữ nút **Thêm giỏ / Mua ngay** (gửi kèm `variantId` để bạn Anh nối giỏ sau).

Ngoài phạm vi (đã chốt): **không làm voucher**, **không code giỏ hàng** (để bạn Anh).

## Cơ sở dữ liệu (`docs/database.sql`)

- `products` thêm cột: `discount_percent INT NOT NULL DEFAULT 0` (0–100).
- Bảng mới:

```sql
CREATE TABLE product_variants (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  product_id INT NOT NULL,
  name       VARCHAR(150) NOT NULL,   -- "Bộ Heo Hồng", "1 bát + 1 thìa"
  price      DECIMAL(12,0) NOT NULL DEFAULT 0,
  quantity   INT NOT NULL DEFAULT 0,
  FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
  INDEX idx_variants_product (product_id)
);
```

- Thêm dữ liệu mẫu: vài sản phẩm có `discount_percent` > 0 và có 2–4 variant.
- Lưu ý: file DROP + tạo lại DB → phải chạy lại `database.sql`, mất dữ liệu cũ (chấp nhận với demo).

## Model

- `Product`: thêm `int discountPercent`, `List<ProductVariant> variants`, helper:
  - `boolean hasVariants()`
  - `BigDecimal getMinPrice()` / `getMaxPrice()` (nhỏ nhất / lớn nhất trong variants; nếu không có variant thì trả về `price`)
- `ProductVariant` (mới): `id, productId, name, price, quantity` + getter/setter.

Ghi chú: giá sau giảm tính ở tầng hiển thị (JSP): `giá * (100 - discountPercent) / 100`. `discount_percent` áp cho cả giá gốc lẫn giá variant.

## DAO

- `ProductVariantDAO` (mới):
  - `List<ProductVariant> getByProductId(int productId)`
  - `void insert(ProductVariant v)`
  - `void deleteByProductId(int productId)`
- `ProductDAO`:
  - `mapRow`: đọc thêm `discount_percent`.
  - `getById`: nạp kèm `variants` (gọi `ProductVariantDAO.getByProductId`).
  - `insert` / `update`: lưu `discount_percent`. Sau khi lưu product, lưu variants theo kiểu **xóa hết cũ → thêm lại từ form** (đơn giản, chắc chắn).

## Trang admin

### `product-form.jsp`
- Thêm ô **"Giảm giá (%)"** (`name="discountPercent"`, number 0–100, mặc định giá trị cũ).
- Khu **"Phân loại (tùy chọn)"**: bảng dòng động, mỗi dòng:
  - Tên loại (`variantName[]`), Giá (`variantPrice[]`), Kho (`variantQty[]`), nút **Xóa dòng**.
  - Nút **"+ Thêm phân loại"** (JS clone 1 dòng trống).
  - Khi **sửa**: đổ sẵn các variant hiện có.

### `AdminProductServlet`
- Đọc `discountPercent` (parse, kẹp 0–100, mặc định 0).
- Đọc `req.getParameterValues("variantName"/"variantPrice"/"variantQty")`, ghép thành `List<ProductVariant>`, **bỏ dòng có tên rỗng**.
- Sau khi `insert`/`update` product → lưu variants (xóa cũ + thêm mới).
- (Multipart đã bật; text field vẫn đọc được qua `getParameter`/`getParameterValues`.)

## Trang chi tiết (`product-detail.jsp`)

- **Breadcrumb**: `Trang chủ ▸ [categoryName] ▸ [product.name]` (dấu ▸).
- **Khối giá**:
  - Nếu `discountPercent > 0`: giá gốc gạch ngang + giá sau giảm (đỏ) + badge `-x%`.
  - Nếu `hasVariants()`: hiện **khoảng giá** `min – max` (sau giảm) khi chưa chọn loại; chọn loại rồi thì hiện đúng giá loại đó.
- **Chọn phân loại**: các nút bấm (mỗi variant 1 nút). Bấm → cập nhật giá + tồn kho + `max` của ô số lượng + set hidden `variantId`. (JS thuần.)
- Nếu sản phẩm có variant mà chưa chọn → cảnh báo nhẹ khi bấm Thêm giỏ.
- Ô số lượng `max` = tồn kho variant đang chọn (hoặc `product.quantity` nếu không có variant).
- Form gửi: `action=add`, `productId`, `variantId` (rỗng nếu không có), `quantity`.

## Trang chủ (`home.jsp`) — nhẹ

- Card có `discountPercent > 0`: badge `-x%` góc ảnh + giá gốc gạch ngang + giá sau giảm.

## Bàn giao cho bạn Anh (giỏ hàng)

Ghi chú trong `docs/05_phan_cong_cong_viec.md` (hoặc file bàn giao):
- `CartServlet action=add` cần đọc thêm `variantId`.
- `CartItem` cần thêm: `variantId`, `variantName`, và **giá lấy theo variant** (không phải `product.price`).
- Khóa gộp dòng trong giỏ nên là cặp `(productId, variantId)`.

## Tiêu chí hoàn thành

- [ ] Chạy lại `database.sql` tạo được bảng `product_variants` + cột `discount_percent`, có dữ liệu mẫu.
- [ ] Admin thêm/sửa sản phẩm: thêm được nhiều phân loại (giá + kho riêng) và % giảm; sửa thì đổ đúng dữ liệu cũ.
- [ ] Trang chi tiết: breadcrumb đúng; chọn loại đổi giá + tồn kho; hiện `-x%` + giá gạch khi có giảm; khoảng giá khi có nhiều loại.
- [ ] Trang chủ: badge giảm giá hiển thị đúng.
- [ ] Build `mvn clean package cargo:run` chạy được, không lỗi.
