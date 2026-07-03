# 3. Cấu trúc thư mục dự án

Dự án theo mô hình **MVC** chuẩn của một web app Java (Maven WAR).

```
Do_An-LTW/
├── pom.xml                         # Khai báo thư viện + plugin chạy Tomcat (Cargo)
├── README.md                       # Giới thiệu + hướng dẫn chạy
├── .gitignore                      # Bỏ qua target/, file IDE...
│
├── docs/                           # TÀI LIỆU ĐỒ ÁN
│   ├── 01_mo_ta_de_tai.md          # Mô tả đề tài, chức năng, công nghệ
│   ├── 02_database_erd.md          # Sơ đồ ERD + mô tả các bảng
│   ├── 03_cau_truc_du_an.md        # (file này) cấu trúc thư mục
│   ├── 04_huong_dan_chay.md        # Hướng dẫn chạy chi tiết
│   └── database.sql                # Script tạo DB + dữ liệu mẫu
│
└── src/main/
    ├── java/com/shop/
    │   ├── model/                  # MODEL: lớp dữ liệu (thuần getter/setter)
    │   │   ├── Category.java       # Danh mục
    │   │   ├── Product.java        # Sản phẩm
    │   │   ├── Order.java          # Đơn hàng
    │   │   ├── OrderItem.java      # Chi tiết đơn hàng
    │   │   ├── User.java           # Tài khoản admin
    │   │   └── CartItem.java       # 1 dòng trong giỏ (chỉ ở Cookie)
    │   │
    │   ├── dao/                    # DAO: truy vấn database
    │   │   ├── CategoryDAO.java
    │   │   ├── ProductDAO.java
    │   │   ├── OrderDAO.java
    │   │   └── UserDAO.java
    │   │
    │   ├── controller/            # CONTROLLER: các Servlet
    │   │   ├── HomeServlet.java        # Trang chủ + lọc sản phẩm
    │   │   ├── ProductDetailServlet.java  # Chi tiết sản phẩm
    │   │   ├── CartServlet.java        # Giỏ hàng (Cookie)
    │   │   ├── CheckoutServlet.java    # Đặt hàng
    │   │   └── admin/
    │   │       ├── LoginServlet.java       # Đăng nhập / đăng xuất admin
    │   │       ├── AdminProductServlet.java# Quản lý sản phẩm
    │   │       ├── AdminOrderServlet.java  # Quản lý đơn hàng
    │   │       └── DashboardServlet.java   # Thống kê
    │   │
    │   ├── util/
    │   │   └── DBConnection.java   # Hàm kết nối MySQL
    │   │
    │   └── filter/
    │       └── AuthFilter.java     # Chặn truy cập /admin/* nếu chưa login
    │
    └── webapp/
        ├── index.jsp               # Trang vào → chuyển hướng sang /home
        ├── WEB-INF/
        │   ├── web.xml             # Cấu hình welcome-file
        │   └── views/             # File JSP (để trong WEB-INF cho an toàn)
        │       ├── home.jsp             # Danh sách + bộ lọc sản phẩm
        │       ├── product-detail.jsp   # Chi tiết sản phẩm
        │       ├── cart.jsp             # Giỏ hàng
        │       ├── checkout.jsp         # Form đặt hàng
        │       ├── order-success.jsp    # Báo đặt hàng thành công
        │       ├── common/
        │       │   ├── header.jsp       # Đầu trang (navbar) dùng chung
        │       │   └── footer.jsp       # Chân trang dùng chung
        │       └── admin/
        │           ├── login.jsp
        │           ├── admin-header.jsp # Đầu trang admin dùng chung
        │           ├── admin-footer.jsp # Chân trang admin dùng chung
        │           ├── dashboard.jsp    # Thống kê
        │           ├── product-list.jsp # Danh sách sản phẩm
        │           ├── product-form.jsp # Form thêm/sửa sản phẩm
        │           └── order-list.jsp   # Danh sách đơn hàng
        │
        └── assets/                 # File tĩnh
            ├── css/style.css       # CSS tùy chỉnh
            ├── js/main.js          # JavaScript dùng chung
            └── images/             # Ảnh sản phẩm
```

## Vì sao để file JSP trong `WEB-INF/views`?
Mọi thứ trong `WEB-INF` không thể truy cập trực tiếp từ trình duyệt (gõ URL tới file `.jsp`).
Người dùng **bắt buộc đi qua Servlet** → đúng tinh thần MVC, an toàn hơn.

## Giải thích nhanh từng tầng (cho thuyết trình)

| Tầng | Vai trò | Ví dụ |
|------|---------|-------|
| **Model** | Định nghĩa dữ liệu | `Product` có id, name, price... |
| **DAO** | Nói chuyện với MySQL | `ProductDAO.getAll()` trả về danh sách sản phẩm |
| **Controller (Servlet)** | Nhận request, gọi DAO, đẩy data sang JSP | `HomeServlet` lấy sản phẩm rồi forward sang `home.jsp` |
| **View (JSP)** | Hiển thị HTML + Bootstrap | `home.jsp` lặp qua danh sách và vẽ card sản phẩm |
| **Util** | Tiện ích dùng chung | `DBConnection` mở kết nối DB |
| **Filter** | Bảo vệ trang admin | `AuthFilter` kiểm tra đã đăng nhập chưa |

## Luồng xử lý một request (ví dụ: xem trang chủ)
```
Trình duyệt  →  HomeServlet  →  ProductDAO.getAll()  →  MySQL
                    ↓
              home.jsp (Bootstrap)  →  HTML  →  Trình duyệt
```

## Upload ảnh sản phẩm — cơ chế hoạt động

Khi admin thêm/sửa sản phẩm kèm ảnh:

```
Form (enctype=multipart/form-data)
    → AdminProductServlet (@MultipartConfig)
        → req.getPart("imageFile")          ← đọc file binary
        → Part.write(uploadDir + fileName)  ← lưu vào assets/images/
        → lưu tên file (timestamp + .ext) vào cột image trong DB
```

Khi hiển thị ảnh:
```
JSP: <img src="/shop/assets/images/${p.image}">
Tomcat phục vụ file tĩnh từ thư mục assets/images/ của ứng dụng đã deploy
```

> **Lưu ý:** Ảnh được lưu vào thư mục deploy (`target/cargo/...`). Nếu chạy `mvn clean`, thư mục `target` bị xóa và ảnh mất. Với đồ án demo, điều này chấp nhận được.
