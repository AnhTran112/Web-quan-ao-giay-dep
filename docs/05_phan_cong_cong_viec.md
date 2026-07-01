# 5. Phân công công việc nhóm (4 người)

> Mục tiêu: hoàn thiện website bán giày dép/quần áo (Java JSP/Servlet + MySQL + Bootstrap, mô hình MVC)
> ở mức **điểm cao (9–10)**. Mỗi thành viên **code phần nào → viết docs phần đó → thuyết trình phần đó**.

## 0. Bối cảnh & luồng nghiệp vụ (quan trọng — đọc trước)

Website **KHÔNG có thanh toán online**. Luồng đặt hàng thực tế:

```
Khách: xem sản phẩm → thêm vào giỏ (Session) → /checkout điền Tên + SĐT + Địa chỉ
      → bấm "Xác nhận đặt hàng"
      → đơn được LƯU vào DB với trạng thái PENDING (chưa thanh toán)
Admin: vào /admin/orders thấy đơn mới + SĐT khách
      → GỌI ĐIỆN liên hệ, chốt đơn, giao hàng
      → bấm "Đánh dấu đã giao" (status = DELIVERED)
```

Tức là **"đặt hàng" = gửi form đơn cho admin để admin liên hệ**, không phải cổng thanh toán.

---

## 1. Bảng tổng quan phân công

| Người | Branch git | Mảng phụ trách | Sở hữu dữ liệu |
|-------|-----------|----------------|-----------------|
| **Người 1 – Hoàng** (nhóm trưởng) | `Hoàng` | Nền tảng dự án + Quản lý sản phẩm (Admin) | bảng `products` |
| **Người 2 – Anh** | `Anh` | Khách hàng: Duyệt sản phẩm + Giỏ hàng | giỏ hàng (Session) |
| **Người 3 – Khoa** | `Khoa` | Vòng đời đơn hàng (đặt hàng + admin xử lý) | bảng `orders`, `order_items` |
| **Người 4 – Nguyên** | `Nguyên` | Đăng nhập/Bảo mật + Quản lý danh mục + Thống kê | bảng `users`, `categories` |

**Nguyên tắc vàng:** mỗi người chỉ sửa file thuộc phần mình. File dùng chung
(`common/header.jsp`, `common/footer.jsp`, `admin/admin-header.jsp`, `web.xml`, `DBConnection.java`,
`assets/css/style.css`) **do Người 1 (Hoàng) quản lý** — ai cần đổi thì báo Hoàng.

---

## 2. NGƯỜI 1 — HOÀNG · Nền tảng + Quản lý sản phẩm (Admin)

### 2.1. Vai trò
Dựng và giữ "khung xương" dự án, tích hợp (merge) code cả nhóm, và làm chức năng CRUD sản phẩm cho admin.

### 2.2. File phụ trách
**Đã có sẵn (hiểu rõ để giải thích + bảo trì):**
- `pom.xml`, `docs/database.sql`, `src/.../util/DBConnection.java`
- Toàn bộ `src/.../model/*.java` (Product, Category, Order, OrderItem, User, CartItem)
- `src/.../dao/ProductDAO.java` (phần đọc: `getAll`, `getById`, `filter`)
- `src/.../dao/CategoryDAO.java` (phần `getAll`)
- Layout dùng chung: `common/header.jsp`, `common/footer.jsp`, `admin/admin-header.jsp`,
  `admin/admin-footer.jsp`, `assets/css/style.css`, `assets/js/main.js`, `index.jsp`, `web.xml`

**Phải hoàn thiện (đang để trống):**
- `ProductDAO.insert(Product p)` — thêm sản phẩm vào DB
- `ProductDAO.update(Product p)` — cập nhật sản phẩm
- `ProductDAO.delete(int id)` — xóa sản phẩm
- `AdminProductServlet`:
  - `doGet?action=edit&id=...` → load sản phẩm bằng `getById`, đẩy sang `product-form.jsp`
  - `doGet?action=delete&id=...` → gọi `delete` rồi redirect
  - `doPost` → đọc form, phân biệt thêm mới (id rỗng) / sửa (có id), gọi `insert`/`update`
- `product-form.jsp` — đảm bảo hiển thị đúng dữ liệu khi sửa (đã có sẵn binding)

### 2.3. Nâng cấp ăn điểm (làm để lên 9–10)
- **Upload ảnh sản phẩm**: dùng `@MultipartConfig` + `Part`, lưu file vào `assets/images/`,
  thay cho việc gõ tay tên file.
- **Tìm kiếm sản phẩm theo tên** (thêm ô search ở trang admin + `ProductDAO.search(keyword)`).
- **Validate phía server**: tên không rỗng, giá ≥ 0, số lượng ≥ 0; báo lỗi rõ ràng.

### 2.4. Docs phụ trách
- `docs/03_cau_truc_du_an.md` (cấu trúc thư mục + mô hình MVC)
- `docs/04_huong_dan_chay.md` (hướng dẫn cài đặt & chạy)
- `README.md`
- Mục "Quản lý sản phẩm" trong báo cáo

### 2.5. Thuyết trình
- Kiến trúc tổng thể MVC, luồng 1 request (Servlet → DAO → MySQL → JSP)
- Demo thêm/sửa/xóa + upload ảnh sản phẩm

### 2.6. Checklist
- [ ] `ProductDAO.insert/update/delete` chạy đúng
- [ ] Thêm/sửa/xóa sản phẩm trên giao diện admin OK
- [ ] (Nâng cấp) Upload ảnh
- [ ] (Nâng cấp) Tìm kiếm + validate
- [ ] Docs + slide phần mình

---

## 3. NGƯỜI 2 — ANH · Khách hàng: Duyệt sản phẩm + Giỏ hàng

### 3.1. Vai trò
Phụ trách toàn bộ trải nghiệm phía khách: xem/lọc sản phẩm và giỏ hàng (lưu bằng Session).

### 3.2. File phụ trách
**Đã có sẵn (giải thích):**
- `controller/HomeServlet.java` (trang chủ + lọc theo danh mục/giá)
- `controller/ProductDetailServlet.java` (chi tiết sản phẩm)
- `views/home.jsp`, `views/product-detail.jsp`
- `model/CartItem.java`

**Phải hoàn thiện (đang để trống):**
- `controller/CartServlet.java` — quản lý giỏ trong `session.getAttribute("cart")`:
  - `action=add` → đọc `productId`, `quantity`; nếu đã có thì cộng dồn, chưa có thì thêm `CartItem`
  - `action=update` → đổi số lượng 1 dòng
  - `action=remove` → xóa 1 dòng theo `productId`
  - lưu lại `List<CartItem>` vào session, rồi forward/redirect về `cart.jsp`
- `views/cart.jsp` — nối nút Xóa / cập nhật số lượng, tính tổng tiền

### 3.3. Nâng cấp ăn điểm
- **Cập nhật số lượng ngay trong giỏ** (ô number + nút Cập nhật).
- **Badge đếm số món** trên navbar (đọc `sessionScope.cart` hiển thị số lượng).
- **Phân trang** danh sách sản phẩm trang chủ (vd 6 sản phẩm/trang) + `ProductDAO.getPage(offset, limit)`.

### 3.4. Docs phụ trách
- `docs/01_mo_ta_de_tai.md` (mục chức năng khách hàng)
- Tài liệu mô tả giỏ hàng (cơ chế Session)

### 3.5. Thuyết trình
- Trải nghiệm khách, lọc danh mục/khoảng giá
- Cơ chế giỏ hàng bằng Session (vì sao không cần bảng DB)

### 3.6. Checklist
- [ ] Thêm vào giỏ từ trang chi tiết OK
- [ ] Xóa / cập nhật số lượng trong giỏ OK
- [ ] Tính tổng tiền đúng
- [ ] (Nâng cấp) Badge số món + phân trang
- [ ] Docs + slide phần mình

### 3.7. Ghi chú tích hợp phân loại (variants) — do Hoàng bàn giao
Trang chi tiết sản phẩm giờ có **phân loại** (mỗi loại có giá + tồn kho riêng) và **giảm giá %**.
Khi làm giỏ hàng, cần xử lý thêm:
- `product-detail.jsp` gửi kèm `variantId` khi bấm **Thêm vào giỏ / Mua ngay** (rỗng nếu sản phẩm không có phân loại).
- `CartServlet action=add`: nếu `variantId` khác rỗng → lấy giá + tên **theo `ProductVariant`**
  (dùng `ProductVariantDAO.getByProductId(productId)` rồi tìm đúng id) thay cho `product.price`.
  Nhớ áp `discount_percent` của sản phẩm lên giá (giá bán = giá × (100 − discount) / 100).
- `CartItem` nên thêm 2 trường: `variantId` và `variantName` (để hiển thị "Size 40" trong giỏ).
- **Khóa gộp dòng** trong giỏ là cặp `(productId, variantId)` — cùng sản phẩm nhưng khác loại là 2 dòng riêng.
- Tồn kho để kiểm tra là `variant.quantity` (nếu có phân loại), ngược lại `product.quantity`.

---

## 4. NGƯỜI 3 — KHOA · Vòng đời đơn hàng (đặt hàng → admin xử lý)

### 4.1. Vai trò
Phụ trách toàn bộ luồng đơn hàng: khách gửi đơn (checkout) và admin nhận/xử lý đơn.
Sở hữu trọn `OrderDAO` + 2 bảng `orders`, `order_items`.

### 4.2. File phụ trách
**Phải hoàn thiện (đang để trống):**
- `controller/CheckoutServlet.java`:
  - `doGet` → hiển thị `checkout.jsp` (kèm tính tổng tiền từ giỏ trong session)
  - `doPost` → đọc `customerName`, `phone`, `address`; tạo `Order` từ giỏ hàng;
    gọi `OrderDAO.createOrder`; **xóa giỏ** khỏi session; forward sang `order-success.jsp`
- `dao/OrderDAO.java` (viết toàn bộ):
  - `createOrder(Order)` → INSERT vào `orders`, lấy id tự tăng, INSERT từng dòng `order_items`
    → **dùng transaction** (commit/rollback) để đảm bảo toàn vẹn; lưu `price` tại thời điểm mua
  - `getAll()` → danh sách đơn (mới nhất trước)
  - `getById(id)` → 1 đơn kèm danh sách `order_items` (cho trang chi tiết đơn)
  - `updateStatus(id, status)` → đổi PENDING ↔ DELIVERED
  - `getTotalRevenue()`, `countOrders()` → cho trang Thống kê (Người 4 sẽ gọi)
- `controller/admin/AdminOrderServlet.java`:
  - `doGet` → `getAll`, đẩy sang `order-list.jsp`
  - `doPost` → đọc `id`, gọi `updateStatus(id, "DELIVERED")`, redirect
- `views/checkout.jsp`, `views/order-success.jsp`, `views/admin/order-list.jsp`

### 4.3. Nâng cấp ăn điểm
- **Trừ tồn kho khi đặt hàng** + **chặn đặt khi sản phẩm hết hàng** (kiểm tra `quantity`).
- **Trang xem chi tiết đơn** cho admin (`order-detail.jsp`): liệt kê các món trong `order_items`,
  số lượng, giá, tổng.

### 4.4. Docs phụ trách
- `docs/02_database_erd.md` (mục bảng `orders`, `order_items`)
- Tài liệu mô tả luồng đặt hàng + transaction

### 4.5. Thuyết trình
- Luồng giỏ hàng → đơn hàng → admin liên hệ giao
- Vì sao cần transaction; vì sao lưu `price` tại thời điểm mua

### 4.6. Checklist
- [ ] Đặt hàng tạo đúng bản ghi trong `orders` + `order_items`
- [ ] Giỏ hàng được xóa sau khi đặt
- [ ] Admin xem danh sách đơn + đổi trạng thái OK
- [ ] (Nâng cấp) Trừ tồn kho + trang chi tiết đơn
- [ ] Docs + slide phần mình

---

## 5. NGƯỜI 4 — NGUYÊN · Bảo mật/Đăng nhập + Quản lý danh mục + Thống kê

### 5.1. Vai trò
Phụ trách đăng nhập & bảo vệ trang admin, quản lý danh mục sản phẩm, và trang thống kê.
Sở hữu 2 bảng `users`, `categories`.

### 5.2. File phụ trách
**Đã có sẵn (giải thích + nâng cấp bảo mật):**
- `controller/admin/LoginServlet.java`, `filter/AuthFilter.java`, `dao/UserDAO.java`, `views/admin/login.jsp`

**Phải hoàn thiện / làm mới:**
- **Quản lý danh mục (mới):**
  - `dao/CategoryDAO.java` → thêm `getById`, `insert`, `update`, `delete`
  - `controller/admin/AdminCategoryServlet.java` → list / new / edit / delete / save (đã có khung)
  - `views/admin/category-list.jsp`, `views/admin/category-form.jsp` (đã có khung)
- **Thống kê:**
  - `controller/admin/DashboardServlet.java` → gọi `OrderDAO.getTotalRevenue()` + `countOrders()`
  - `views/admin/dashboard.jsp`

### 5.3. Nâng cấp ăn điểm
- **Mã hóa mật khẩu (BCrypt)**: thêm thư viện `jbcrypt`, băm mật khẩu khi tạo user,
  dùng `BCrypt.checkpw` khi đăng nhập (bỏ so sánh plain-text).
- **Biểu đồ doanh thu (Chart.js)** trên dashboard + **Top sản phẩm bán chạy** + **cảnh báo sắp hết hàng**.

### 5.4. Docs phụ trách
- `docs/02_database_erd.md` (mục bảng `users`, `categories`)
- Tài liệu bảo mật (Filter, Session, mã hóa mật khẩu) + thống kê

### 5.5. Thuyết trình
- Cơ chế bảo vệ admin (Filter + Session), mã hóa mật khẩu
- Quản lý danh mục + thống kê/biểu đồ

### 5.6. Checklist
- [ ] Quản lý danh mục: thêm/sửa/xóa OK
- [ ] Dashboard hiển thị đúng doanh thu + số đơn
- [ ] (Nâng cấp) Mã hóa mật khẩu BCrypt
- [ ] (Nâng cấp) Biểu đồ Chart.js
- [ ] Docs + slide phần mình

---

## 6. Sơ đồ phụ thuộc & thứ tự làm

```
Người 1 (nền tảng) ✅ đã có → tất cả nhánh ra từ đây
        │
        ├─ Người 2 (giỏ hàng) ─────┐
        │                          ├─→ Người 3 (đặt hàng cần có giỏ)
        ├─ Người 4 (admin/danh mục)┘
        │
   Người 3 viết getTotalRevenue()/countOrders() → Người 4 gọi ở Dashboard
```

- **Tuần 1:** ai cũng nắm base + làm phần "đọc/hiển thị" của mình.
- **Tuần 2:** Người 2 xong giỏ → Người 3 làm checkout; Người 1 & 4 làm CRUD/admin song song.
- **Tuần 3:** merge vào `main`, test tổng thể, làm slide + báo cáo.

---

## 7. Quy ước tránh xung đột (đọc kỹ)

- **File dùng chung do Hoàng quản lý**, người khác không tự sửa.
- **`OrderDAO.java` do Khoa sở hữu hoàn toàn.** Nguyên KHÔNG sửa file này — chỉ **gọi**
  `getTotalRevenue()` / `countOrders()` từ `DashboardServlet`.
- Mỗi người làm trên **branch riêng**, không commit thẳng vào `main`.
- Trước khi bắt đầu mỗi ngày: `git pull origin main` để cập nhật phần đã merge.

---

## 8. Quy trình Git

```bash
# 1. Lấy branch của mình
git checkout <ten-branch>          # Hoàng / Anh / Khoa / Nguyên
git pull origin main               # đồng bộ phần mới nhất

# 2. Làm việc → commit (commit message viết tiếng Anh)
git add .
git commit -m "Implement cart add/remove logic"

# 3. Đẩy lên branch riêng
git push origin <ten-branch>

# 4. Tạo Pull Request vào main trên GitHub → nhóm review → merge
```

---

## 9. Tiêu chí chấm điểm & cách "ăn điểm"

| Tiêu chí | Cách đạt |
|----------|----------|
| Đủ chức năng | Hoàn thành toàn bộ phần "phải hoàn thiện" của 4 người |
| Đúng mô hình MVC | Giữ tách bạch Model / DAO / Controller / View (đã có sẵn) |
| Bảo mật | PreparedStatement (đã có) + **mã hóa mật khẩu** + validate dữ liệu |
| Độ khó / tính mới (điểm cộng) | Upload ảnh, tìm kiếm, phân trang, trừ tồn kho, biểu đồ Chart.js |
| Giao diện / UX | Bootstrap gọn gàng, có badge giỏ hàng, thông báo rõ ràng |
| Báo cáo + thuyết trình | Mỗi người nắm chắc phần mình; báo cáo có Use-case, ERD, ảnh demo |

**Tóm tắt:** scope cơ bản ≈ 7–8 điểm. Làm thêm các mục "nâng cấp ăn điểm" (mỗi người 1–2 mục)
→ 9–10 điểm, công việc vẫn chia đều cho 4 người.
