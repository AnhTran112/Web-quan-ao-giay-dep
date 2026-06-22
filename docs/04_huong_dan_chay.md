# 5. Hướng dẫn chạy dự án (KHÔNG cần cài Tomcat)

Dự án dùng plugin **Cargo** — Maven sẽ **tự tải Tomcat 9** về và chạy, bạn không phải cài Tomcat thủ công.

## Cần có sẵn trên máy
1. **JDK 11+** — kiểm tra bằng: `java -version`
   - Bạn đang dùng **ServBay** thì có thể bật OpenJDK trong ServBay là được. Hoặc cài JDK riêng.
2. **Maven** — kiểm tra bằng: `mvn -version`
   - Nếu chưa có: tải tại https://maven.apache.org/download.cgi rồi thêm vào PATH.
3. **MySQL** — có thể dùng luôn MySQL của **ServBay** (rất tiện).

## Các bước chạy

### Bước 1 — Tạo database
- Mở MySQL (hoặc phpMyAdmin trong ServBay), chạy file `docs/database.sql`.
- Lệnh dòng lệnh (nếu thích):
  ```
  mysql -u root -p < docs/database.sql
  ```

### Bước 2 — Sửa thông tin kết nối
- Mở `src/main/java/com/shop/util/DBConnection.java`.
- Sửa `USER` / `PASSWORD` cho khớp MySQL của bạn (ServBay mặc định user `root`).

### Bước 3 — Chạy web (Maven tự tải Tomcat)
Mở terminal ngay trong thư mục dự án, gõ:
```
mvn clean package cargo:run
```
- **Lần đầu** chạy sẽ hơi lâu vì Maven tải Tomcat 9 + thư viện về (chờ vài phút).
- Khi thấy dòng `Tomcat ... started` là xong.

### Bước 4 — Truy cập
- Trang khách: **http://localhost:8080/shop/home**
- Trang admin: **http://localhost:8080/shop/admin/login** (admin / 123456)

### Dừng server
- Nhấn **Ctrl + C** trong terminal.

## Lỗi thường gặp
| Lỗi | Cách xử lý |
|-----|-----------|
| `port 8080 already in use` | Đổi `8080` trong `pom.xml` (mục `cargo.servlet.port`) sang `8081` |
| Tải Tomcat chậm/lỗi mạng | Chạy lại lệnh, Maven sẽ tải tiếp |
| `Communications link failure` (MySQL) | MySQL chưa bật, hoặc sai user/password trong `DBConnection.java` |
| `Unknown database 'shop_db'` | Chưa chạy `database.sql` ở Bước 1 |

> **Ghi chú về ServBay:** ServBay hiện chưa hỗ trợ chạy JSP/Servlet trực tiếp (chưa có gói Tomcat).
> Vì vậy ta để Maven tự lo phần Tomcat. ServBay vẫn dùng được cho **MySQL** và **JDK**.
