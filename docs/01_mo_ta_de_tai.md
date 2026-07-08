# 1. Mô tả đề tài

## Tên đề tài
**Website bán giày dép / quần áo (E-Commerce đơn giản)**

## Mục tiêu
Xây dựng một website bán hàng cơ bản gồm 2 phần:
- **Trang khách hàng (User):** xem sản phẩm, lọc theo danh mục/giá, thêm vào giỏ hàng, đặt hàng.
- **Trang quản trị (Admin):** quản lý sản phẩm (thêm/sửa/xóa), quản lý đơn hàng, thống kê doanh thu cơ bản.

## Công nghệ sử dụng
| Thành phần | Công nghệ |
|------------|-----------|
| Backend | Java Servlet + JSP |
| Frontend | HTML, CSS, Bootstrap 5 |
| Database | MySQL |
| Server | Apache Tomcat 9 |
| Build tool | Maven |
| Mô hình | MVC (Model - View - Controller) |

## Chức năng chi tiết

### A. Khách hàng (User)
1. Xem danh sách sản phẩm (trang chủ).
2. Lọc sản phẩm theo **danh mục** và **khoảng giá**.
3. Xem chi tiết một sản phẩm.
4. Thêm sản phẩm vào **giỏ hàng** (lưu bằng Cookie).
5. Xem / cập nhật / xóa sản phẩm trong giỏ hàng.
6. Đặt hàng: điền **tên, số điện thoại, địa chỉ** → tạo đơn hàng.

### B. Quản trị (Admin)
1. Đăng nhập admin.
2. Quản lý sản phẩm: thêm, sửa, xóa, xem danh sách.
3. Quản lý danh mục.
4. Quản lý đơn hàng: xem danh sách, đổi trạng thái (Đang xử lý → Đã giao).
5. Thống kê doanh thu cơ bản (tổng doanh thu, số đơn hàng).

## Mô hình MVC
- **Model:** các lớp Java mô tả dữ liệu (Product, Category, Order...) + lớp DAO truy vấn database.
- **View:** các file `.jsp` hiển thị giao diện (dùng Bootstrap).
- **Controller:** các Servlet nhận request, xử lý logic, trả về View.

```
Người dùng → Servlet (Controller) → DAO → MySQL
                  ↓
                JSP (View) → trả HTML về trình duyệt
```
