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
-- Mỗi loại có giá + tồn kho riêng.
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
-- Bảng: orders (đơn hàng)      -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE orders (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(150) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    total_amount  DECIMAL(12,0) NOT NULL DEFAULT 0,
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING / DELIVERED
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_orders_status (status)
);

-- ----------------------------
-- Bảng: order_items (chi tiết đơn hàng)  -- Người 3 (Khoa) sở hữu
-- ----------------------------
CREATE TABLE order_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    price      DECIMAL(12,0) NOT NULL,           -- giá tại thời điểm mua
    FOREIGN KEY (order_id)   REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id),
    INDEX idx_items_order (order_id)
);

-- ----------------------------
-- Bảng: users (tài khoản admin)  -- Người 4 (Nguyên) sở hữu
-- password: demo đang plain-text. Nâng cấp: mã hóa bằng BCrypt.
-- ----------------------------
CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    role      VARCHAR(20) NOT NULL DEFAULT 'ADMIN'
);

-- =====================================================
-- DỮ LIỆU MẪU
-- =====================================================

INSERT INTO categories (name, description) VALUES
('Giày thể thao', 'Các loại giày thể thao, sneaker'),
('Giày tây',      'Giày da, giày công sở'),
('Áo thun',       'Áo thun nam nữ'),
('Quần jean',     'Quần jean các loại');

INSERT INTO products (category_id, name, description, price, image, quantity, discount_percent) VALUES
(1, 'Giày Sneaker Trắng', 'Giày sneaker màu trắng năng động, phù hợp đi học đi chơi.', 450000, 'sneaker-trang.jpg', 50, 25),
(1, 'Giày Chạy Bộ',       'Giày chạy bộ êm chân, đế cao su chống trượt.',            650000, 'chay-bo.jpg',      30, 0),
(2, 'Giày Tây Đen',       'Giày da nam màu đen lịch sự, hợp đi làm và dự tiệc.',     800000, 'giay-tay-den.jpg', 20, 10),
(3, 'Áo Thun Basic',      'Áo thun cổ tròn nhiều màu, chất cotton thoáng mát.',      150000, 'ao-thun.jpg',      100, 0),
(3, 'Áo Polo',            'Áo polo cổ bẻ trẻ trung, form regular dễ mặc.',           250000, 'ao-polo.jpg',      60, 15),
(4, 'Quần Jean Slimfit',  'Quần jean ôm dáng thể thao, co giãn nhẹ thoải mái.',      350000, 'jean-slim.jpg',    40, 0);

-- Phân loại (variants) cho từng sản phẩm — mỗi loại có giá + tồn kho riêng
INSERT INTO product_variants (product_id, name, price, quantity) VALUES
-- SP1: Giày Sneaker Trắng (theo size)
(1, 'Size 39', 450000, 10),
(1, 'Size 40', 460000, 15),
(1, 'Size 41', 470000, 12),
(1, 'Size 42', 480000, 8),
-- SP2: Giày Chạy Bộ (theo size)
(2, 'Size 39', 650000, 6),
(2, 'Size 40', 650000, 9),
(2, 'Size 41', 670000, 10),
(2, 'Size 42', 680000, 5),
-- SP3: Giày Tây Đen (theo size)
(3, 'Size 39', 800000, 5),
(3, 'Size 40', 800000, 8),
(3, 'Size 41', 820000, 7),
-- SP4: Áo Thun Basic (theo màu)
(4, 'Màu Trắng', 150000, 40),
(4, 'Màu Đen',   150000, 35),
(4, 'Màu Xanh',  160000, 25),
-- SP5: Áo Polo (theo size)
(5, 'Size S', 250000, 15),
(5, 'Size M', 250000, 20),
(5, 'Size L', 260000, 15),
(5, 'Size XL', 270000, 10),
-- SP6: Quần Jean Slimfit (theo size)
(6, 'Size 29', 350000, 10),
(6, 'Size 30', 350000, 12),
(6, 'Size 31', 360000, 10),
(6, 'Size 32', 370000, 8);

INSERT INTO users (username, password, full_name, role) VALUES
('admin', '123456', 'Quản Trị Viên', 'ADMIN');

-- Đơn hàng mẫu (để test thống kê + danh sách đơn)
INSERT INTO orders (customer_name, phone, address, total_amount, status) VALUES
('Nguyễn Văn A', '0901234567', '123 Lê Lợi, TP. Hồ Chí Minh', 600000, 'DELIVERED'),
('Trần Thị B',   '0907654321', '45 Trần Hưng Đạo, Hà Nội',    450000, 'PENDING');

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 4, 4, 150000),
(2, 1, 1, 450000);
