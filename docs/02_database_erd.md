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
| discount_percent | INT | 0 | % giảm giá |
| created_at | DATETIME | CURRENT_TIMESTAMP | Ngày tạo |

### 3. `orders` — Đơn hàng
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| customer_name | VARCHAR(150) | Tên khách hàng |
| phone | VARCHAR(20) | Số điện thoại (dùng tra cứu đơn) |
| address | VARCHAR(255) | Địa chỉ giao hàng |
| note | VARCHAR(500) | Ghi chú của khách khi đặt |
| admin_note | VARCHAR(500) | Ghi chú nội bộ của admin |
| total_amount | DECIMAL(12,0) | Tổng cuối = tạm tính − giảm giá + phí ship |
| ship_fee | DECIMAL(12,0) | Phí vận chuyển (0 nếu freeship, đơn từ 500k) |
| coupon_code | VARCHAR(50) | Mã giảm giá đã áp (nếu có) |
| discount_amount | DECIMAL(12,0) | Số tiền được giảm từ coupon |
| status | VARCHAR(30) | PENDING → CONFIRMED → SHIPPING → DELIVERED, hoặc CANCELLED |
| created_at | DATETIME | Ngày đặt |

### 4. `order_items` — Chi tiết đơn hàng
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| order_id | INT, FK → orders(id) | Thuộc đơn hàng nào |
| product_id | INT, FK → products(id) | Sản phẩm nào |
| variant_id | INT, NULL | Phân loại khách chọn (NULL nếu không có) |
| variant_name | VARCHAR(150), NULL | Tên phân loại tại thời điểm mua (vd "Size 40") |
| quantity | INT | Số lượng mua |
| price | DECIMAL(12,0) | Giá tại thời điểm mua (đã áp giảm giá %) |

### 4b. `order_status_history` — Lịch sử trạng thái đơn
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| order_id | INT, FK → orders(id) | Đơn hàng nào |
| old_status | VARCHAR(30), NULL | Trạng thái cũ (NULL với bản ghi tạo đơn) |
| new_status | VARCHAR(30) | Trạng thái mới |
| changed_by | VARCHAR(100) | 'customer' hoặc username admin |
| created_at | DATETIME | Thời điểm đổi |

### 4c. `coupons` — Mã giảm giá
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| code | VARCHAR(50), UNIQUE | Mã khách nhập (vd SALE10) |
| discount_percent | INT | % giảm trên tạm tính |
| max_uses | INT | Số lượt dùng tối đa |
| used_count | INT | Đã dùng bao nhiêu lượt (trừ trong transaction đặt hàng) |
| expires_at | DATETIME, NULL | Hạn dùng (NULL = không hết hạn) |
| active | TINYINT(1) | Bật/tắt mã |

### 5. `users` — Tài khoản quản trị
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| username | VARCHAR(50) | Tên đăng nhập |
| password | VARCHAR(255) | Mật khẩu (đã được mã hóa BCrypt) |
| full_name | VARCHAR(150) | Họ tên |
| role | VARCHAR(20) | Vai trò: ADMIN |

### 6. `activity_logs` — Nhật ký hoạt động admin
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| admin_username | VARCHAR(50) | Tên đăng nhập của admin |
| action | VARCHAR(50) | Hành động (Thêm/Sửa/Xóa) |
| details | TEXT | Mô tả chi tiết hành động |
| created_at | DATETIME | Thời gian thực hiện |

### 7. `reviews` — Đánh giá sản phẩm
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| product_id | INT, FK → products(id) | Thuộc sản phẩm nào |
| phone | VARCHAR(20) | Số điện thoại đã đặt hàng |
| rating | INT | Điểm đánh giá (1-5 sao) |
| comment | TEXT | Nội dung đánh giá |
| created_at | DATETIME | Thời gian đánh giá |

### 8. `product_images` — Ảnh phụ của sản phẩm
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INT, PK, AUTO_INCREMENT | Khóa chính |
| product_id | INT, FK → products(id) | Thuộc sản phẩm nào |
| image_url | VARCHAR(255) | Tên file ảnh phụ |

## Quan hệ
- 1 **category** có nhiều **products** (1–n).
- 1 **product** có nhiều **product_images** (1–n).
- 1 **product** có nhiều **reviews** (1–n).
- 1 **order** có nhiều **order_items** (1–n).
- 1 **product** xuất hiện trong nhiều **order_items** (1–n).
- **Giỏ hàng (Cart) KHÔNG có bảng riêng** → lưu trong Cookie của user.

> File SQL tạo bảng + dữ liệu mẫu: xem `docs/database.sql`
