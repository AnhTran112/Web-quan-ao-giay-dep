# Web Bán Giày Dép / Quần Áo — Đồ án Lập trình Web

Website thương mại điện tử đơn giản (Java JSP/Servlet + MySQL + Bootstrap), theo mô hình **MVC**.

## 🛠 Công nghệ
| Thành phần | Công nghệ |
|------------|-----------|
| Backend | Java Servlet + JSP, JSTL |
| Frontend | HTML, Bootstrap 5 (CDN) |
| Database | MySQL 8 |
| Server | Apache Tomcat 9 (Maven tự tải, không cần cài) |
| Build | Maven |

## 📁 Cấu trúc thư mục
Xem chi tiết trong [`docs/03_cau_truc_du_an.md`](docs/03_cau_truc_du_an.md).
```
docs/           → tài liệu đồ án (mô tả, ERD, cấu trúc, hướng dẫn chạy, database.sql)
src/main/java   → code Java (model, dao, controller, util, filter)
src/main/webapp → JSP + Bootstrap + assets (css/js/images)
pom.xml         → khai báo thư viện + plugin chạy Tomcat
```

---

# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN (chi tiết)

> Tóm tắt nhanh 3 lệnh:
> ```
> mysql -u root -p < docs/database.sql      # 1. tạo database
> # 2. sửa user/password trong DBConnection.java
> mvn clean package cargo:run               # 3. chạy web
> ```
> Rồi mở http://localhost:8080/shop/home

## Bước 0 — Kiểm tra máy đã có công cụ
Mở terminal (CMD / PowerShell / Terminal) gõ lần lượt:
```
java -version      # cần JDK 11 trở lên
mvn -version       # cần Maven (3.6+)
```
- Nếu **chưa có JDK**: tải Temurin tại https://adoptium.net (hoặc bật OpenJDK trong ServBay).
- Nếu **chưa có Maven**: tải tại https://maven.apache.org/download.cgi, giải nén, thêm thư mục `bin` vào biến môi trường `PATH`.
- **MySQL**: dùng MySQL đã cài, hoặc bật MySQL trong **ServBay** (rất tiện).

> Không cần cài Tomcat — Maven sẽ tự tải Tomcat 9 ở Bước 3.

## Bước 1 — Tạo cơ sở dữ liệu
Chọn 1 trong 2 cách:

**Cách A — bằng dòng lệnh:**
```
mysql -u root -p < docs/database.sql
```
**Cách B — bằng giao diện:** mở **phpMyAdmin** (có sẵn trong ServBay) hoặc **MySQL Workbench**,
copy toàn bộ nội dung `docs/database.sql` dán vào và bấm chạy.

Sau bước này sẽ có database `shop_db` cùng dữ liệu mẫu (4 danh mục, 6 sản phẩm, 1 admin).

## Bước 2 — Cấu hình kết nối database
Mở file `src/main/java/com/shop/util/DBConnection.java`, sửa cho khớp MySQL của bạn:
```java
private static final String USER = "root";     // tên đăng nhập MySQL
private static final String PASSWORD = "";      // mật khẩu MySQL của bạn
```
> ServBay mặc định user là `root`. Nếu MySQL của bạn có mật khẩu thì điền vào `PASSWORD`.

## Bước 3 — Chạy web (Maven tự tải Tomcat)
Mở terminal **ngay trong thư mục dự án** (chỗ có file `pom.xml`), gõ:
```
mvn clean package cargo:run
```
- **Lần đầu** sẽ lâu (vài phút) vì Maven tải Tomcat 9 + thư viện về máy. Các lần sau rất nhanh.
- Khi thấy dòng báo `Tomcat ... started` (server đã khởi động) là xong.
- Muốn **dừng server**: nhấn `Ctrl + C` trong terminal.

## Bước 4 — Truy cập trang web
| Trang | Địa chỉ | Tài khoản |
|-------|---------|-----------|
| Khách hàng | http://localhost:8080/shop/home | (không cần) |
| Quản trị | http://localhost:8080/shop/admin/login | **admin / 123456** |

## 🧯 Lỗi thường gặp
| Lỗi | Nguyên nhân & cách xử lý |
|-----|--------------------------|
| `port 8080 already in use` | Cổng 8080 đang bị chiếm. Mở `pom.xml`, đổi `cargo.servlet.port` từ `8080` sang `8081`, chạy lại. |
| `Unknown database 'shop_db'` | Chưa chạy `database.sql` ở Bước 1. |
| `Communications link failure` | MySQL chưa bật, hoặc sai user/password trong `DBConnection.java`. |
| `Public Key Retrieval is not allowed` | Thêm `&allowPublicKeyRetrieval=true` vào cuối chuỗi URL trong `DBConnection.java`. |
| Tải Tomcat bị lỗi mạng | Chạy lại `mvn ... cargo:run`, Maven sẽ tải tiếp phần còn thiếu. |

> Hướng dẫn đầy đủ hơn: [`docs/04_huong_dan_chay.md`](docs/04_huong_dan_chay.md)

---

## ✅ Tính năng đã hoàn thiện

**Phía khách hàng:**
- Xem danh sách sản phẩm, lọc theo danh mục và khoảng giá
- Xem chi tiết sản phẩm
- Giỏ hàng (Session): thêm, cập nhật số lượng, xóa
- Đặt hàng (checkout): điền thông tin → lưu đơn vào DB → xóa giỏ

**Phía quản trị (`/admin`):**
- Đăng nhập bảo mật (Filter chặn toàn bộ `/admin/*`)
- Quản lý sản phẩm: thêm/sửa/xóa, **upload ảnh**, **tìm kiếm theo tên**, validate phía server
- Quản lý danh mục: thêm/sửa/xóa
- Quản lý đơn hàng: xem danh sách, đổi trạng thái PENDING → DELIVERED
- Thống kê doanh thu + số đơn

## 📚 Tài liệu
- [`docs/01_mo_ta_de_tai.md`](docs/01_mo_ta_de_tai.md) — Mô tả đề tài, chức năng, công nghệ
- [`docs/02_database_erd.md`](docs/02_database_erd.md) — Sơ đồ ERD + mô tả các bảng
- [`docs/03_cau_truc_du_an.md`](docs/03_cau_truc_du_an.md) — Cấu trúc thư mục, mô hình MVC
- [`docs/04_huong_dan_chay.md`](docs/04_huong_dan_chay.md) — Hướng dẫn chạy chi tiết
