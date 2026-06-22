-- =====================================================
-- DATABASE: shop_db  (Web ban giay dep / quan ao)
-- Do an mon Lap trinh Web - Java JSP + MySQL
-- =====================================================

DROP DATABASE IF EXISTS shop_db;
CREATE DATABASE shop_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE shop_db;

-- ----------------------------
-- Bang: categories (danh muc)
-- ----------------------------
CREATE TABLE categories (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    description VARCHAR(255)
);

-- ----------------------------
-- Bang: products (san pham)
-- ----------------------------
CREATE TABLE products (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    category_id INT,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    price       DECIMAL(12,0) NOT NULL DEFAULT 0,
    image       VARCHAR(255),
    quantity    INT NOT NULL DEFAULT 0,
    created_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- ----------------------------
-- Bang: orders (don hang)
-- ----------------------------
CREATE TABLE orders (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    customer_name VARCHAR(150) NOT NULL,
    phone         VARCHAR(20)  NOT NULL,
    address       VARCHAR(255) NOT NULL,
    total_amount  DECIMAL(12,0) NOT NULL DEFAULT 0,
    status        VARCHAR(30) NOT NULL DEFAULT 'PENDING', -- PENDING / DELIVERED
    created_at    DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- ----------------------------
-- Bang: order_items (chi tiet don hang)
-- ----------------------------
CREATE TABLE order_items (
    id         INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    price      DECIMAL(12,0) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);

-- ----------------------------
-- Bang: users (tai khoan admin)
-- ----------------------------
CREATE TABLE users (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    username  VARCHAR(50) NOT NULL UNIQUE,
    password  VARCHAR(255) NOT NULL,
    full_name VARCHAR(150),
    role      VARCHAR(20) NOT NULL DEFAULT 'ADMIN'
);

-- =====================================================
-- DU LIEU MAU
-- =====================================================

INSERT INTO categories (name, description) VALUES
('Giay the thao', 'Cac loai giay the thao, sneaker'),
('Giay tay',      'Giay da, giay cong so'),
('Ao thun',       'Ao thun nam nu'),
('Quan jean',     'Quan jean cac loai');

INSERT INTO products (category_id, name, description, price, image, quantity) VALUES
(1, 'Giay Sneaker Trang', 'Giay sneaker mau trang nang dong', 450000, 'sneaker-trang.jpg', 50),
(1, 'Giay Chay Bo',       'Giay chay bo em chan',            650000, 'chay-bo.jpg',      30),
(2, 'Giay Tay Den',       'Giay da nam mau den lich su',     800000, 'giay-tay-den.jpg', 20),
(3, 'Ao Thun Basic',      'Ao thun co tron nhieu mau',       150000, 'ao-thun.jpg',      100),
(3, 'Ao Polo',            'Ao polo co be',                   250000, 'ao-polo.jpg',      60),
(4, 'Quan Jean Slimfit',  'Quan jean om dang the thao',      350000, 'jean-slim.jpg',    40);

INSERT INTO users (username, password, full_name, role) VALUES
('admin', '123456', 'Quan Tri Vien', 'ADMIN');

-- Don hang mau (de test thong ke)
INSERT INTO orders (customer_name, phone, address, total_amount, status) VALUES
('Nguyen Van A', '0901234567', '123 Le Loi, TP.HCM', 600000, 'DELIVERED'),
('Tran Thi B',   '0907654321', '45 Tran Hung Dao, Ha Noi', 450000, 'PENDING');

INSERT INTO order_items (order_id, product_id, quantity, price) VALUES
(1, 4, 4, 150000),
(2, 1, 1, 450000);
