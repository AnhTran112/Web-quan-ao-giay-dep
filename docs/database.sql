-- =====================================================
-- DATABASE: shop_db  (Web bán giày dép / quần áo)
-- Đồ án môn Lập trình Web - Java JSP + MySQL
-- Chạy:  mysql -u root -p < docs/database.sql
-- Lưu ý: file này DROP database cũ và tạo lại từ đầu.
-- =====================================================

DROP DATABASE IF EXISTS shop_db;
CREATE DATABASE shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_db;

-- ----------------------------
-- Bảng: categories (danh mục)  -- Người 4 (Nguyên) sở hữu
-- ----------------------------
CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- ----------------------------
-- Bảng: products (sản phẩm)    -- Người 1 (Hoàng) sở hữu
-- ----------------------------
CREATE TABLE products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    price       DECIMAL(12,0) NOT NULL DEFAULT 0,
    image       VARCHAR(255),
    quantity    INT NOT NULL DEFAULT 0,          -- tồn kho (Người 3 trừ kho khi đặt hàng)
    discount_percent INT NOT NULL DEFAULT 0,     -- % giảm giá (0-100)
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_products_category (category_id),
    INDEX idx_products_price (price)
);

-- ----------------------------
-- Bảng: product_variants (phân loại sản phẩm) -- Người 1 (Hoàng) sở hữu
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

-- ----------------------------
-- Bảng: users (tài khoản admin & khách)  -- Người 4 (Nguyên) sở hữu
-- ----------------------------
CREATE TABLE users (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    username        VARCHAR(50) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    full_name       VARCHAR(150),
    phone           VARCHAR(20) UNIQUE,
    address         VARCHAR(255),
    role            VARCHAR(20) NOT NULL DEFAULT 'CUSTOMER',
    status          VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    failed_attempts INT NOT NULL DEFAULT 0,
    lock_time       DATETIME NULL,
    remember_token  VARCHAR(255) NULL
);

-- ----------------------------
-- Bảng: activity_logs (nhật ký hoạt động admin)
-- ----------------------------
CREATE TABLE activity_logs (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    admin_username VARCHAR(50) NOT NULL,
    action         VARCHAR(50) NOT NULL,
    entity         VARCHAR(50) NOT NULL,
    entity_id      INT,
    description    TEXT,
    created_at     DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_logs_admin (admin_username),
    INDEX idx_logs_entity (entity)
);

-- ----------------------------
-- Bảng: orders (đơn hàng)      -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE orders (
    id              INT AUTO_INCREMENT PRIMARY KEY,
    user_id         INT NULL,                              -- Liên kết với tài khoản khách hàng (nếu đăng nhập)
    customer_name   VARCHAR(150) NOT NULL,
    phone           VARCHAR(20)  NOT NULL,
    address         VARCHAR(255) NOT NULL,
    note            VARCHAR(500),                          -- ghi chú của khách khi đặt
    admin_note      VARCHAR(500),                          -- ghi chú nội bộ của admin
    total_amount    DECIMAL(12,0) NOT NULL DEFAULT 0,      -- tổng cuối = tạm tính - giảm giá + phí ship
    ship_fee        DECIMAL(12,0) NOT NULL DEFAULT 0,      -- phí vận chuyển (0 nếu freeship)
    coupon_code     VARCHAR(50),                           -- mã giảm giá đã áp (nếu có)
    discount_amount DECIMAL(12,0) NOT NULL DEFAULT 0,      -- số tiền được giảm từ coupon
    status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    INDEX idx_orders_user (user_id),
    INDEX idx_orders_status (status),
    INDEX idx_orders_phone (phone)
);

-- ----------------------------
-- Bảng: order_items (chi tiết đơn hàng)  -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE order_items (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    order_id     INT NOT NULL,
    product_id   INT NOT NULL,
    variant_id   INT NULL,                        
    variant_name VARCHAR(150) NULL,               
    quantity     INT NOT NULL,
    price        DECIMAL(12,0) NOT NULL,          
    FOREIGN KEY (order_id)   REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_items_order (order_id)
);

-- ----------------------------
-- Bảng: order_status_history (lịch sử trạng thái đơn)  -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE order_status_history (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT NOT NULL,
    old_status VARCHAR(30),                       
    new_status VARCHAR(30) NOT NULL,
    changed_by VARCHAR(100) NOT NULL,             
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    INDEX idx_history_order (order_id)
);

-- ----------------------------
-- Bảng: coupons (mã giảm giá)  -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE coupons (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    code             VARCHAR(50) NOT NULL UNIQUE,
    discount_percent INT NOT NULL,                
    max_uses         INT NOT NULL DEFAULT 100,    
    used_count       INT NOT NULL DEFAULT 0,      
    expires_at       DATETIME NULL,               
    active           TINYINT(1) NOT NULL DEFAULT 1,
    created_at       DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Bảng: reviews (đánh giá sản phẩm)
-- ----------------------------
CREATE TABLE reviews (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    phone      VARCHAR(20) NOT NULL,
    rating     INT NOT NULL DEFAULT 5, -- 1 đến 5 sao
    comment    TEXT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_reviews_product (product_id)
);

-- ----------------------------
-- Bảng: product_images (ảnh phụ của sản phẩm)
-- ----------------------------
CREATE TABLE product_images (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    product_id INT NOT NULL,
    image_url  VARCHAR(255) NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(id) ON DELETE CASCADE,
    INDEX idx_images_product (product_id)
);

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

INSERT INTO categories (name, description) VALUES
('Giày thể thao',    'Sneaker, giày chạy bộ, giày bóng rổ, giày tennis'),
('Giày tây',         'Giày da, giày công sở, boot da'),
('Sandal & Dép',     'Sandal, dép quai hậu, dép đi trong nhà'),
('Áo thun',          'Áo thun nam nữ, áo thun thể thao, oversize'),
('Áo polo & Sơ mi',  'Áo polo cổ bẻ, sơ mi công sở, sơ mi caro'),
('Áo khoác',         'Áo khoác gió, khoác jean, hoodie'),
('Quần jean',        'Quần jean các loại: slimfit, baggy, rách gối'),
('Quần kaki & Short','Quần kaki, quần short, quần jogger');

-- 30 sản phẩm (ảnh dùng lại 6 file có sẵn trong assets/images)
INSERT INTO products (category_id, name, description, price, image, quantity, discount_percent) VALUES
(1, 'Giày Sneaker Trắng',        'Giày sneaker màu trắng năng động.',           450000,  'sneaker-trang.jpg', 50, 25),
(1, 'Giày Chạy Bộ Runner',       'Giày chạy bộ êm chân, đế cao su chống trượt.',650000,  'chay-bo.jpg',       30, 0),
(1, 'Giày Sneaker Cổ Cao',       'Sneaker cổ cao streetwear.',                  520000,  'sneaker-trang.jpg', 35, 10),
(1, 'Giày Bóng Rổ Jumper',       'Giày bóng rổ ôm cổ chân, đế bám sân.',        890000,  'chay-bo.jpg',       18, 15),
(1, 'Giày Tennis Ace',           'Giày tennis chuyên dụng.',                    720000,  'sneaker-trang.jpg', 22, 0),
(1, 'Giày Đi Bộ EasyWalk',       'Giày đi bộ siêu nhẹ.',                        380000,  'chay-bo.jpg',       45, 5),
(2, 'Giày Tây Đen Oxford',       'Giày da nam Oxford màu đen lịch sự.',         800000,  'giay-tay-den.jpg',  20, 10),
(2, 'Giày Tây Nâu Derby',        'Giày Derby da bò màu nâu.',                   850000,  'giay-tay-den.jpg',  15, 0),
(2, 'Giày Lười Da Loafer',       'Giày lười da mềm, xỏ nhanh tiện lợi.',        690000,  'giay-tay-den.jpg',  25, 20),
(2, 'Giày Boot Da Chelsea',      'Boot Chelsea cổ chun, da bóng cao cấp.',      1200000, 'giay-tay-den.jpg',  10, 0),
(3, 'Sandal Quai Ngang',         'Sandal quai ngang khóa dán, đế PU nhẹ.',      250000,  'chay-bo.jpg',       40, 0),
(3, 'Dép Quai Hậu Thể Thao',     'Dép quai hậu thể thao năng động.',            180000,  'chay-bo.jpg',       55, 10),
(3, 'Dép Sục Nhựa EVA',          'Dép sục nhựa EVA siêu nhẹ, dễ vệ sinh.',      120000,  'sneaker-trang.jpg', 0,  0),
(4, 'Áo Thun Basic Cotton',      'Áo thun cổ tròn nhiều màu, cotton 100%.',     150000,  'ao-thun.jpg',       120, 0),
(4, 'Áo Thun Oversize Street',   'Áo thun oversize form rộng.',                 190000,  'ao-thun.jpg',       80, 15),
(4, 'Áo Thun Thể Thao Coolmax',  'Áo thun thể thao vải Coolmax thấm mồ hôi.',   220000,  'ao-thun.jpg',       60, 0),
(4, 'Áo Thun In Graphic',        'Áo thun in hình graphic cá tính.',            175000,  'ao-thun.jpg',       70, 20),
(5, 'Áo Polo Classic',           'Áo polo cổ bẻ trẻ trung, form regular.',      250000,  'ao-polo.jpg',       60, 15),
(5, 'Áo Polo Thể Thao',          'Áo polo vải cá sấu co giãn 4 chiều.',         280000,  'ao-polo.jpg',       45, 0),
(5, 'Áo Sơ Mi Trắng Công Sở',    'Sơ mi trắng dài tay chống nhăn.',             320000,  'ao-polo.jpg',       50, 10),
(5, 'Áo Sơ Mi Flannel Caro',     'Sơ mi flannel caro dày dặn.',                 350000,  'ao-polo.jpg',       30, 0),
(6, 'Áo Khoác Gió Nam',          'Áo khoác gió 2 lớp chống nước nhẹ.',          420000,  'ao-thun.jpg',       35, 25),
(6, 'Áo Khoác Jean Denim',       'Áo khoác jean denim wash nhẹ.',               480000,  'jean-slim.jpg',     25, 0),
(6, 'Áo Hoodie Nỉ',              'Hoodie nỉ bông dày ấm, mũ 2 lớp.',            390000,  'ao-thun.jpg',       40, 10),
(7, 'Quần Jean Slimfit',         'Quần jean ôm dáng thể thao.',                 350000,  'jean-slim.jpg',     40, 0),
(7, 'Quần Jean Baggy',           'Quần jean baggy ống rộng, phong cách retro.', 380000,  'jean-slim.jpg',     35, 15),
(7, 'Quần Jean Rách Gối',        'Quần jean rách gối bụi bặm.',                 400000,  'jean-slim.jpg',     28, 0),
(8, 'Quần Kaki Ống Đứng',        'Quần kaki ống đứng lịch sự.',                 320000,  'jean-slim.jpg',     45, 10),
(8, 'Quần Short Kaki',           'Quần short kaki trên gối, trẻ trung.',        220000,  'jean-slim.jpg',     65, 0),
(8, 'Quần Jogger Thể Thao',      'Quần jogger bo gấu, vải thun co giãn.',       260000,  'ao-thun.jpg',       55, 20);

INSERT INTO product_variants (product_id, name, price, quantity) VALUES
(1, 'Size 39', 450000, 10), (1, 'Size 40', 460000, 15), (1, 'Size 41', 470000, 12), (1, 'Size 42', 480000, 8),
(2, 'Size 39', 650000, 6), (2, 'Size 40', 650000, 9), (2, 'Size 41', 670000, 10), (2, 'Size 42', 680000, 5),
(3, 'Size 39', 520000, 8), (3, 'Size 40', 520000, 12), (3, 'Size 41', 540000, 9), (3, 'Size 42', 550000, 6),
(4, 'Size 40', 890000, 5), (4, 'Size 41', 890000, 6), (4, 'Size 42', 910000, 4), (4, 'Size 43', 930000, 3),
(5, 'Size 39', 720000, 5), (5, 'Size 40', 720000, 8), (5, 'Size 41', 740000, 6), (5, 'Size 42', 750000, 3),
(6, 'Size 38', 380000, 12), (6, 'Size 39', 380000, 12), (6, 'Size 40', 390000, 11), (6, 'Size 41', 390000, 10),
(7, 'Size 39', 800000, 5), (7, 'Size 40', 800000, 8), (7, 'Size 41', 820000, 7),
(8, 'Size 39', 850000, 4), (8, 'Size 40', 850000, 6), (8, 'Size 41', 870000, 5),
(9, 'Size 39', 690000, 8), (9, 'Size 40', 690000, 10), (9, 'Size 41', 710000, 7),
(10, 'Size 40', 1200000, 4), (10, 'Size 41', 1200000, 4), (10, 'Size 42', 1230000, 2),
(11, 'Size 38', 250000, 10), (11, 'Size 39', 250000, 10), (11, 'Size 40', 250000, 12), (11, 'Size 41', 260000, 8),
(12, 'Size 39', 180000, 20), (12, 'Size 40', 180000, 20), (12, 'Size 41', 185000, 15),
(14, 'Trắng - Size M', 150000, 30), (14, 'Trắng - Size L', 150000, 30), (14, 'Đen - Size M',   150000, 30), (14, 'Đen - Size L',   155000, 30),
(15, 'Size M', 190000, 30), (15, 'Size L', 190000, 30), (15, 'Size XL', 200000, 20),
(16, 'Size M', 220000, 20), (16, 'Size L', 220000, 25), (16, 'Size XL', 230000, 15),
(18, 'Size S', 250000, 15), (18, 'Size M', 250000, 20), (18, 'Size L', 260000, 15), (18, 'Size XL', 270000, 10),
(19, 'Size M', 280000, 15), (19, 'Size L', 280000, 18), (19, 'Size XL', 290000, 12),
(20, 'Size M', 320000, 18), (20, 'Size L', 320000, 20), (20, 'Size XL', 330000, 12),
(21, 'Size M', 350000, 10), (21, 'Size L', 350000, 12), (21, 'Size XL', 360000, 8),
(22, 'Size M', 420000, 12), (22, 'Size L', 420000, 13), (22, 'Size XL', 440000, 10),
(23, 'Size M', 480000, 9), (23, 'Size L', 480000, 10), (23, 'Size XL', 500000, 6),
(24, 'Size M', 390000, 15), (24, 'Size L', 390000, 15), (24, 'Size XL', 400000, 10),
(25, 'Size 29', 350000, 10), (25, 'Size 30', 350000, 12), (25, 'Size 31', 360000, 10), (25, 'Size 32', 370000, 8),
(26, 'Size 29', 380000, 9), (26, 'Size 30', 380000, 12), (26, 'Size 31', 390000, 9), (26, 'Size 32', 400000, 5),
(27, 'Size 29', 400000, 8), (27, 'Size 30', 400000, 10), (27, 'Size 31', 410000, 6), (27, 'Size 32', 420000, 4),
(28, 'Size 29', 320000, 12), (28, 'Size 30', 320000, 14), (28, 'Size 31', 330000, 11), (28, 'Size 32', 340000, 8),
(29, 'Size M', 220000, 22), (29, 'Size L', 220000, 25), (29, 'Size XL', 230000, 18),
(30, 'Size M', 260000, 18), (30, 'Size L', 260000, 22), (30, 'Size XL', 270000, 15);

-- Mã giảm giá mẫu 
INSERT INTO coupons (code, discount_percent, max_uses, used_count, expires_at, active) VALUES
('SALE10',   10, 100, 5,  DATE_ADD(NOW(), INTERVAL 30 DAY), 1),
('SALE20',   20, 50,  49, DATE_ADD(NOW(), INTERVAL 7 DAY),  1),
('HETHAN15', 15, 100, 12, DATE_SUB(NOW(), INTERVAL 1 DAY),  1),
('NGUNG25',  25, 100, 0,  NULL,                             0);

-- Dữ liệu tài khoản: Thêm 2 Admin và 5 Customer có SĐT và Địa chỉ
-- Mật khẩu mặc định: 123456 ($2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu)
INSERT INTO users (username, password, full_name, phone, address, role) VALUES
('admin',    '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Quản Trị Viên',     NULL,         NULL,                                  'ADMIN'),
('nhanvien', '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Nhân Viên',         NULL,         NULL,                                  'STAFF'),
('khach1',   '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Nguyễn Văn An',     '0901234567', '123 Lê Lợi, Q.1, TP.HCM',             'CUSTOMER'),
('khach2',   '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Trần Thị Bích',     '0907654321', '45 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội', 'CUSTOMER'),
('khach3',   '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Lê Minh Cường',     '0912345678', '78 Nguyễn Huệ, Q.1, TP.HCM',          'CUSTOMER'),
('khach4',   '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Phạm Thu Dung',     '0938111222', '12 Hai Bà Trưng, Q.3, TP.HCM',        'CUSTOMER'),
('khach5',   '$2a$10$sMknFHGoyr8qCeUYEFGmh.H7wy.g5fHByaq55ycLylyk8yvItMVNu', 'Hoàng Văn Em',      '0987654321', '56 Lý Thường Kiệt, Hải Châu, Đà Nẵng','CUSTOMER');

-- 21 đơn hàng mẫu
-- Liên kết 5 đơn đầu tiên cho 5 khách hàng đã đăng ký (user_id từ 3 đến 7)
INSERT INTO orders (user_id, customer_name, phone, address, note, total_amount, ship_fee, status, created_at) VALUES
(3,    'Nguyễn Văn An',    '0901234567', '123 Lê Lợi, Q.1, TP.HCM',                NULL,                              600000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 165 DAY)),
(4,    'Trần Thị Bích',    '0907654321', '45 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội',    NULL,                              687500,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 160 DAY)),
(5,    'Lê Minh Cường',    '0912345678', '78 Nguyễn Huệ, Q.1, TP.HCM',             'Giao giờ hành chính',             1200000, 0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 150 DAY)),
(6,    'Phạm Thu Dung',    '0938111222', '12 Hai Bà Trưng, Q.3, TP.HCM',           NULL,                              455000,  30000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 140 DAY)),
(7,    'Hoàng Văn Em',     '0987654321', '56 Lý Thường Kiệt, Hải Châu, Đà Nẵng',   NULL,                              1090000, 0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 132 DAY)),
(NULL, 'Vũ Thị Phương',    '0965432187', '89 Cách Mạng Tháng 8, Q.10, TP.HCM',     'Gọi trước khi giao',              318000,  30000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 120 DAY)),
(NULL, 'Đặng Quốc Giang',  '0909888777', '34 Võ Văn Tần, Q.3, TP.HCM',             NULL,                              1070000, 0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 110 DAY)),
(NULL, 'Bùi Thị Hà',       '0918777666', '67 Phan Chu Trinh, TP. Huế',             NULL,                              514500,  30000, 'DELIVERED', DATE_SUB(NOW(), INTERVAL 98 DAY)),
(NULL, 'Ngô Văn Khánh',    '0977666555', '23 Điện Biên Phủ, Bình Thạnh, TP.HCM',   NULL,                              756500,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 85 DAY)),
(NULL, 'Đỗ Thị Lan',       '0933444555', '90 Nguyễn Trãi, Thanh Xuân, Hà Nội',     NULL,                              780000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 75 DAY)),
(NULL, 'Trịnh Văn Minh',   '0944555666', '11 Lê Duẩn, Hải Châu, Đà Nẵng',          NULL,                              1104000, 0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 62 DAY)),
(NULL, 'Mai Thị Ngọc',     '0955666777', '42 Hoàng Diệu, Q.4, TP.HCM',             NULL,                              648000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 50 DAY)),
(NULL, 'Phan Văn Phú',     '0966777888', '15 Quang Trung, Gò Vấp, TP.HCM',         NULL,                              720000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 38 DAY)),
(NULL, 'Lý Thị Quỳnh',     '0922333444', '88 Nguyễn Văn Cừ, Long Biên, Hà Nội',    'Giao buổi tối sau 18h',           603000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 27 DAY)),
(NULL, 'Trương Văn Sơn',   '0911222333', '29 Trường Chinh, Tân Bình, TP.HCM',      NULL,                              850000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(3,    'Nguyễn Văn An',    '0901234567', '123 Lê Lợi, Q.1, TP.HCM',                NULL,                              666000,  0,     'DELIVERED', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(4,    'Trần Thị Bích',    '0907654321', '45 Trần Hưng Đạo, Hoàn Kiếm, Hà Nội',    NULL,                              675000,  0,     'CONFIRMED', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(NULL, 'Dương Thị Vy',     '0977888999', '66 Lạc Long Quân, Tây Hồ, Hà Nội',       'Để hàng ở bảo vệ tòa nhà',        500000,  0,     'SHIPPING',  DATE_SUB(NOW(), INTERVAL 3 DAY)),
(NULL, 'Đinh Văn Tài',     '0966555444', '37 Lạch Tray, Ngô Quyền, Hải Phòng',     NULL,                              620000,  0,     'PENDING',   DATE_SUB(NOW(), INTERVAL 2 DAY)),
(NULL, 'Lâm Thị Yến',      '0955444333', '20 Đồng Khởi, Q.1, TP.HCM',              'Kiểm tra hàng trước khi nhận',    550000,  0,     'PENDING',   DATE_SUB(NOW(), INTERVAL 1 DAY)),
(NULL, 'Võ Văn Hùng',      '0903777888', '102 Nguyễn Văn Linh, Q.7, TP.HCM',       NULL,                              650000,  0,     'CANCELLED', DATE_SUB(NOW(), INTERVAL 4 DAY));

INSERT INTO order_items (order_id, product_id, variant_id, variant_name, quantity, price) VALUES
(1,  14, 44, 'Trắng - Size M', 4, 150000),                                        
(2,  1,  2,  'Size 40', 1, 337500), (2,  25, 76, 'Size 29', 1, 350000),           
(3,  10, 35, 'Size 41', 1, 1200000),                                              
(4,  18, 55, 'Size M',  2, 212500),                                               
(5,  2,  6,  'Size 40', 1, 650000), (5,  29, 93, 'Size L', 2, 220000),            
(6,  20, 62, 'Size L',  1, 288000),                                               
(7,  7,  26, 'Size 40', 1, 720000), (7,  21, 65, 'Size L', 1, 350000),            
(8,  15, 49, 'Size L',  3, 161500),                                               
(9,  4,  14, 'Size 41', 1, 756500),                                               
(10, 23, 71, 'Size L',  1, 480000), (10, 14, 46, 'Đen - Size M', 2, 150000),      
(11, 9,  32, 'Size 40', 2, 552000),                                               
(12, 16, 52, 'Size L',  2, 220000), (12, 30, 95, 'Size M', 1, 208000),            
(13, 5,  18, 'Size 40', 1, 720000),                                               
(14, 26, 81, 'Size 30', 1, 323000), (14, 17, NULL, NULL, 2, 140000),              
(15, 8,  29, 'Size 40', 1, 850000),                                               
(16, 22, 67, 'Size M',  1, 315000), (16, 24, 74, 'Size L', 1, 351000),            
(17, 1,  3,  'Size 41', 2, 337500),                                               
(18, 19, 58, 'Size M',  1, 280000), (18, 29, 92, 'Size M', 1, 220000),            
(19, 11, 38, 'Size 39', 2, 250000), (19, 13, NULL, NULL, 1, 120000),              
(20, 27, 84, 'Size 29', 1, 400000), (20, 14, 45, 'Trắng - Size L', 1, 150000),    
(21, 2,  7,  'Size 41', 1, 650000);                                               

-- Lịch sử trạng thái cho các đơn gần nhất (đơn cũ bỏ qua cho gọn)
INSERT INTO order_status_history (order_id, old_status, new_status, changed_by, created_at) VALUES
(15, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 18 DAY)),
(15, 'PENDING',   'CONFIRMED', 'admin',    DATE_SUB(NOW(), INTERVAL 17 DAY)),
(15, 'CONFIRMED', 'SHIPPING',  'admin',    DATE_SUB(NOW(), INTERVAL 16 DAY)),
(15, 'SHIPPING',  'DELIVERED', 'admin',    DATE_SUB(NOW(), INTERVAL 15 DAY)),
(16, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 9 DAY)),
(16, 'PENDING',   'CONFIRMED', 'admin',    DATE_SUB(NOW(), INTERVAL 8 DAY)),
(16, 'CONFIRMED', 'SHIPPING',  'admin',    DATE_SUB(NOW(), INTERVAL 7 DAY)),
(16, 'SHIPPING',  'DELIVERED', 'admin',    DATE_SUB(NOW(), INTERVAL 6 DAY)),
(17, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 5 DAY)),
(17, 'PENDING',   'CONFIRMED', 'admin',    DATE_SUB(NOW(), INTERVAL 4 DAY)),
(18, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 3 DAY)),
(18, 'PENDING',   'CONFIRMED', 'admin',    DATE_SUB(NOW(), INTERVAL 2 DAY)),
(18, 'CONFIRMED', 'SHIPPING',  'admin',    DATE_SUB(NOW(), INTERVAL 1 DAY)),
(19, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 2 DAY)),
(20, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 1 DAY)),
(21, NULL,        'PENDING',   'customer', DATE_SUB(NOW(), INTERVAL 4 DAY)),
(21, 'PENDING',   'CANCELLED', 'admin',    DATE_SUB(NOW(), INTERVAL 3 DAY));
