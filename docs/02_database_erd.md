# 2. Thiết kế cơ sở dữ liệu (Database & ERD)

## Sơ đồ thực thể liên kết (ERD)

```
+----------------+          +------------------+
|   categories   |          |     products     |
+----------------+          +------------------+
| PK id          |1        *| PK id            |
|    name        |----------| FK category_id   |
|    description |          |    name          |
+----------------+          |    description   |
                            |    price         |
                            |    image         |
                            |    quantity      |
                            |    created_at    |
                            +------------------+
                                    | 1
                                    |
                                    | *
+----------------+          +------------------+
|     orders     |          |   order_items    |
+----------------+          +------------------+
| PK id          |1        *| PK id            |
|    customer_name|---------| FK order_id      |
|    phone       |          | FK product_id    |
|    address     |          |    quantity      |
|    total_amount|          |    price         |
|    status      |          +------------------+
|    created_at  |
+----------------+

+----------------+
|     users      |   (tài khoản admin)
+----------------+
| PK id          |
|    username    |
|    password    |
|    full_name   |
|    role        |
+----------------+
```

## Mô tả các bảng

### 1. `categories` — Danh mục sản phẩm
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| name | VARCHAR(100) | Tên danh mục (Giày thể thao, Áo thun...) |
| description | VARCHAR(255) | Mô tả ngắn |

### 2. `products` — Sản phẩm
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| category_id | INT, FK → categories(id) | Thuộc danh mục nào |
| name | VARCHAR(200) | Tên sản phẩm |
| description | TEXT | Mô tả chi tiết |
| price | DECIMAL(12,0) | Giá bán (VND) |
| image | VARCHAR(255) | Tên file ảnh |
| quantity | INT | Số lượng tồn kho |
| created_at | DATETIME | Ngày thêm |

### 3. `orders` — Đơn hàng
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| customer_name | VARCHAR(150) | Tên khách hàng |
| phone | VARCHAR(20) | Số điện thoại |
| address | VARCHAR(255) | Địa chỉ giao hàng |
| total_amount | DECIMAL(12,0) | Tổng tiền |
| status | VARCHAR(30) | Trạng thái: PENDING / DELIVERED |
| created_at | DATETIME | Ngày đặt |

### 4. `order_items` — Chi tiết đơn hàng
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| order_id | INT, FK → orders(id) | Thuộc đơn hàng nào |
| product_id | INT, FK → products(id) | Sản phẩm nào |
| quantity | INT | Số lượng mua |
| price | DECIMAL(12,0) | Giá tại thời điểm mua |

### 5. `users` — Tài khoản quản trị
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| username | VARCHAR(50) | Tên đăng nhập |
| password | VARCHAR(255) | Mật khẩu (đã được mã hóa BCrypt) |
| full_name | VARCHAR(150) | Họ tên |
| role | VARCHAR(20) | Vai trò: ADMIN |

## Quan hệ
- 1 **category** có nhiều **products** (1–n).
- 1 **order** có nhiều **order_items** (1–n).
- 1 **product** xuất hiện trong nhiều **order_items** (1–n).
- **Giỏ hàng (Cart) KHÔNG có bảng riêng** → lưu trong Session của user.

> File SQL tạo bảng + dữ liệu mẫu: xem `docs/database.sql`
