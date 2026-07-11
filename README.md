# Website Bán Quần Áo – Giày Dép (E-Commerce)

> Đồ án cuối kỳ môn **Lập trình Web** — Website Java **JSP / Servlet / JDBC** theo mô hình **MVC**.
> Ứng dụng thương mại điện tử bán quần áo & giày dép: có đầy đủ Frontend, Backend, Database và CRUD.

---

## 1. Thông tin nhóm

| STT | Họ và tên | MSSV | Vai trò |
|-----|-----------|------|---------|
| 1 | **Trần Tuấn Anh** | 2305CT0418 | **Nhóm trưởng** · Trải nghiệm khách hàng, Giỏ hàng, Đánh giá |
| 2 | **Hồ Huy Hoàng** | 2305CT0393 | Nền tảng dự án (MVC), Quản lý sản phẩm (Admin) |
| 3 | **Nguyễn Hoàng Anh Khoa** | 2305CT0458 | Vòng đời đơn hàng (Đặt hàng & xử lý) |
| 4 | **Trần Trung Nguyên** | 2305CT0351 | Bảo mật/Đăng nhập, Danh mục, Thống kê |

> Nhóm trưởng: **Trần Tuấn Anh**.

## 2. Mô tả đề tài

Website bán quần áo – giày dép trực tuyến gồm **2 phân hệ**:

- **Trang khách hàng:** xem – lọc – tìm kiếm – sắp xếp sản phẩm; xem chi tiết kèm thư viện ảnh và phân
  loại (size/màu); giỏ hàng; đặt hàng; đăng ký/đăng nhập tài khoản, xem lịch sử đơn, yêu thích, đánh giá.
- **Trang quản trị (`/admin`):** quản lý sản phẩm – danh mục – đơn hàng – mã giảm giá – người dùng;
  thống kê doanh thu bằng biểu đồ; xuất báo cáo Excel; ghi nhật ký thao tác (audit log).

Mô hình bán hàng: **đặt hàng không thanh toán online** — khách gửi đơn kèm SĐT, admin gọi điện xác
nhận và giao hàng (COD/liên hệ).

## 3. Công nghệ sử dụng

| Thành phần | Công nghệ |
|------------|-----------|
| Ngôn ngữ / Web layer | Java 11, **Java Servlet** 4.0.1 (Controller) |
| View | **JSP** + JSTL 1.2 |
| Truy xuất dữ liệu | **JDBC** (MySQL Connector/J 8.0.33), PreparedStatement |
| Database | **MySQL 8** (utf8mb4) |
| Frontend | HTML, CSS, JavaScript, **Bootstrap 5** (CDN) |
| Biểu đồ / Excel | Chart.js (CDN), Apache POI 5.2.3 (xuất `.xlsx`) |
| Bảo mật mật khẩu | jBCrypt 0.4 (BCrypt) |
| Server | **Apache Tomcat 9** (Maven tự tải qua Cargo — không cần cài) |
| Build | Apache Maven (đóng gói WAR) |
| Kiến trúc | **MVC** (Model – DAO – Controller/Servlet – View/JSP) |

## 4. Các chức năng chính

1. **Quản lý sản phẩm** — CRUD đầy đủ (thêm/xem/sửa/xóa), upload ảnh, phân loại size/màu, giảm giá %, tìm kiếm theo tên.
2. **Quản lý danh mục** — CRUD danh mục sản phẩm.
3. **Giỏ hàng & Đặt hàng** — giỏ hàng lưu Cookie, thêm giỏ bằng AJAX, áp mã giảm giá, phí ship, đặt hàng trong transaction (trừ tồn kho an toàn).
4. **Tìm kiếm & Lọc sản phẩm** — lọc theo danh mục/khoảng giá, sắp xếp, tìm kiếm gợi ý (autocomplete), phân trang.
5. **Tài khoản & Phân quyền** — đăng ký/đăng nhập, ghi nhớ đăng nhập, 3 vai trò **ADMIN / STAFF / CUSTOMER**, mã hóa mật khẩu BCrypt, khóa tài khoản khi sai nhiều lần.
6. **Thống kê & Báo cáo** — dashboard doanh thu (Chart.js), top bán chạy, cảnh báo sắp hết hàng, xuất Excel; nhật ký hoạt động admin.

*(Ngoài ra: đánh giá sản phẩm cho khách đã mua, danh sách yêu thích, xem nhanh (quick-view), lịch sử trạng thái đơn hàng.)*

## 5. Hướng dẫn cài đặt & chạy

**Yêu cầu:** JDK 11+, Maven 3.6+, MySQL 8 đang chạy. *Không cần cài Tomcat* — plugin Cargo tự tải Tomcat 9.

```bash
# 1. Clone project
git clone https://github.com/AnhTran112/Web-quan-ao-giay-dep
cd Web-quan-ao-giay-dep

# 2. Tạo database (chạy script tạo bảng + dữ liệu mẫu)
mysql -u root -p < database/database.sql

# 3. Cấu hình kết nối: mở web/src/main/java/com/shop/util/DBConnection.java
#    sửa USER / PASSWORD cho khớp MySQL của bạn

# 4. Chạy web — toàn bộ mã nguồn nằm trong thư mục web/
cd web
mvn clean package cargo:run
```

Sau đó mở trình duyệt:

| Trang | Địa chỉ | Tài khoản |
|-------|---------|-----------|
| Khách hàng | http://localhost:8081/shop/home | (không cần) |
| Quản trị | http://localhost:8081/shop/admin/login | xem mục 6 |

> **Dừng server:** `Ctrl + C`. Nếu cổng 8081 bận, đổi `cargo.servlet.port` trong `pom.xml`.

**Một số lỗi thường gặp:**

| Lỗi | Cách xử lý |
|-----|-----------|
| `port 8081 already in use` | Đổi `cargo.servlet.port` trong `pom.xml` sang cổng khác |
| `Unknown database 'shop_db'` | Chưa chạy `database/database.sql` (bước 2) |
| `Communications link failure` | MySQL chưa bật hoặc sai user/password trong `DBConnection.java` |
| `Public Key Retrieval is not allowed` | Đã xử lý sẵn bằng `&allowPublicKeyRetrieval=true` trong chuỗi kết nối |

## 6. Tài khoản demo

| Vai trò | Tài khoản | Mật khẩu | Quyền |
|---------|-----------|----------|-------|
| **Admin** | `admin` | `123456` | Toàn quyền quản trị |
| **Nhân viên (STAFF)** | `nhanvien` | `123456` | Chỉ xử lý đơn hàng, xem dashboard |
| **Khách hàng** | `khach1` … `khach5` | `123456` | Mua hàng, xem lịch sử đơn, đánh giá |

## 7. Video thuyết trình & demo

> Video thuyết trình & demo: <https://github.com/AnhTran112/Web-quan-ao-giay-dep/tree/main/video>

## 8. Cấu trúc thư mục

```
Web-quan-ao-giay-dep/
├── web/                         # Toàn bộ ứng dụng Maven (chạy được)
│   ├── pom.xml                  #   khai báo thư viện + plugin Cargo
│   └── src/main/
│       ├── java/com/shop/       #   Backend: controller (Servlet), dao, model, filter, util
│       └── webapp/              #   Frontend: JSP, CSS, JS, ảnh, WEB-INF
├── database/database.sql        # Script tạo database + dữ liệu mẫu
├── report/                      # Báo cáo (.docx / .pdf)
├── slides/                      # Slide thuyết trình
├── video/                       # Video demo (hoặc link)
└── README.md
```

## 9. Tài liệu đồ án

- [`database/database.sql`](database/database.sql) — Script tạo database + dữ liệu mẫu
- **Báo cáo đầy đủ:** [`report/Bao_Cao_Do_An_Web_Ban_Quan_Ao_Giay_Dep.docx`](report/) / `.pdf` — trình bày chi tiết
  phân tích, thiết kế hệ thống, ERD, giao diện, cài đặt chức năng, phân công và kết quả.
</content>
</invoke>
