# Tài liệu: Luồng đặt hàng & Transaction

> **Người phụ trách:** Người 3 — Khoa
> **Bảng liên quan:** `orders`, `order_items`, `products` (trừ tồn kho)

---

## 1. Tổng quan luồng đặt hàng

```
Khách hàng                         Server (Servlet + DAO)                  Database
    |                                      |                                  |
    |-- Thêm SP vào giỏ (Cookie) --------->|                                  |
    |                                      |                                  |
    |-- Bấm "Đặt hàng" (GET /checkout) -->|                                  |
    |                                      |-- Đọc cookie, truy vấn SP ------>|
    |<---- Hiển thị checkout.jsp ----------|                                  |
    |                                      |                                  |
    |-- Gửi form (POST /checkout) -------->|                                  |
    |                                      |-- Kiểm tra tồn kho ------------->|
    |                                      |-- BEGIN TRANSACTION              |
    |                                      |   INSERT orders ---------------->|
    |                                      |   INSERT order_items ----------->|
    |                                      |   UPDATE products (trừ kho) ---->|
    |                                      |-- COMMIT ----------------------->|
    |                                      |-- Xóa cookie giỏ hàng           |
    |<---- Hiển thị order-success.jsp -----|                                  |
```

## 2. Chi tiết từng bước

### Bước 1: Khách xem trang Checkout (`GET /checkout`)
- `CheckoutServlet.doGet()` đọc cookie giỏ hàng qua `CartUtil.getCartMap()`.
- Với mỗi `productId` trong cookie, truy vấn DB lấy thông tin sản phẩm (tên, giá, ảnh).
- Tính tổng tiền và đẩy dữ liệu sang `checkout.jsp` để hiển thị.

### Bước 2: Khách gửi đơn (`POST /checkout`)
1. **Đọc thông tin khách:** `customerName`, `phone`, `address` từ form.
2. **Kiểm tra tồn kho (Nâng cấp):** Với mỗi sản phẩm trong giỏ, so sánh số lượng muốn mua với `products.quantity` trong DB. Nếu không đủ → báo lỗi, quay lại `checkout.jsp`.
3. **Chuyển đổi:** `CartItem` → `OrderItem` (lưu giá tại thời điểm mua).
4. **Gọi `OrderDAO.createOrder(order)`** để lưu vào DB (xem mục 3).
5. **Xóa giỏ hàng:** Gọi `CartUtil.clearCartCookie()` để xóa cookie.
6. **Hiển thị:** Forward sang `order-success.jsp`.

### Bước 3: Admin xem & xử lý đơn (`/admin/orders`)
- `AdminOrderServlet.doGet()` gọi `OrderDAO.getAll()` hiển thị danh sách.
- Admin bấm "Xem chi tiết" → `OrderDAO.getById(id)` lấy đơn + danh sách sản phẩm.
- Admin bấm "Đánh dấu đã giao" → `doPost()` gọi `OrderDAO.updateStatus(id, "DELIVERED")`.

---

## 3. Vì sao cần Transaction?

### Vấn đề
Khi tạo đơn hàng, cần thực hiện **nhiều thao tác liên tiếp** trên database:
1. `INSERT INTO orders` — tạo đơn hàng
2. `INSERT INTO order_items` — thêm từng sản phẩm vào đơn
3. `UPDATE products` — trừ tồn kho

Nếu bước 1 thành công nhưng bước 3 bị lỗi (ví dụ: mất kết nối DB), ta sẽ có:
- Một đơn hàng **"ma"** trong DB (không có chi tiết sản phẩm).
- Tồn kho **không bị trừ** → sai lệch dữ liệu.

### Giải pháp: Transaction (Giao dịch)
```java
conn.setAutoCommit(false);  // Bắt đầu transaction — chưa ghi gì vào DB

// ... thực hiện INSERT orders, INSERT order_items, UPDATE products ...

conn.commit();              // TẤT CẢ thành công → ghi vào DB cùng lúc
// HOẶC
conn.rollback();            // CÓ LỖI → hủy toàn bộ, DB quay về trạng thái ban đầu
```

**Nguyên tắc:** Hoặc tất cả đều thành công (commit), hoặc không có gì thay đổi (rollback). Đây chính là tính chất **Atomicity** (Tính nguyên tử) trong ACID.

---

## 4. Vì sao lưu `price` tại thời điểm mua?

Giá sản phẩm **có thể thay đổi** theo thời gian (giảm giá, tăng giá). Nếu chỉ lưu `product_id` trong `order_items` rồi JOIN lấy giá từ bảng `products`, thì:
- Đơn hàng cũ sẽ hiển thị **giá mới** (sai thực tế).
- Tổng doanh thu bị **tính sai**.

→ Cột `price` trong `order_items` lưu **giá tại đúng thời điểm khách mua**, đảm bảo dữ liệu lịch sử chính xác.

---

## 5. Nâng cấp: Trừ tồn kho & Chặn hết hàng

### Trừ tồn kho
Trong `OrderDAO.createOrder()`, câu lệnh:
```sql
UPDATE products SET quantity = quantity - ? WHERE id = ? AND quantity >= ?
```
- Điều kiện `quantity >= ?` đảm bảo tồn kho **không bị trừ thành số âm**.
- Nếu `UPDATE` trả về 0 dòng bị ảnh hưởng → sản phẩm không đủ tồn kho → `rollback`.

### Chặn đặt hàng khi hết hàng
Trong `CheckoutServlet.doPost()`, **trước khi gọi `createOrder`**, kiểm tra:
```java
Product product = productDAO.getById(ci.getProductId());
if (product.getQuantity() < ci.getQuantity()) {
    // Báo lỗi, không cho đặt hàng
}
```
Đây là lớp kiểm tra **đầu tiên** (phía Servlet). Câu lệnh UPDATE trong DAO là lớp kiểm tra **thứ hai** (phía DB) — phòng trường hợp 2 khách đặt đồng thời (race condition).
