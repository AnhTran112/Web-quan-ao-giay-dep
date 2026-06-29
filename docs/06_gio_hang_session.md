# Cơ chế hoạt động của Giỏ hàng (Session)

Tài liệu này mô tả chi tiết cách thức hoạt động của giỏ hàng trong ứng dụng, được lưu trữ hoàn toàn bằng `HttpSession` thay vì sử dụng Database.

## 1. Tại sao dùng Session?

- **Hiệu năng:** Không cần gọi truy vấn Database (INSERT/UPDATE/DELETE) liên tục mỗi khi người dùng thêm, bớt hoặc thay đổi số lượng sản phẩm trong giỏ.
- **Trải nghiệm:** Khách hàng chưa cần đăng nhập cũng có thể chọn mua hàng. Dữ liệu tạm thời lưu trong bộ nhớ máy chủ (Session) gắn với trình duyệt của người dùng.
- **Vòng đời đơn giản:** Khi khách hàng tắt trình duyệt (hoặc session timeout), giỏ hàng sẽ tự động biến mất mà không để lại rác trong Database. Database chỉ ghi nhận khi khách hàng thực sự "Đặt hàng".

## 2. Cấu trúc dữ liệu

Giỏ hàng là một `List<CartItem>`, được lưu trữ trong `HttpSession` với tên thuộc tính là `"cart"`.

### Lớp `CartItem` (Model)
```java
public class CartItem {
    private int productId;
    private String name;
    private BigDecimal price;
    private String image;
    private int quantity;
    
    // ... getter, setter ...
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }
}
```

## 3. Các thao tác chính (CartServlet)

Controller `CartServlet` (URL: `/cart`) xử lý 3 hành động chính (thông qua tham số `action`):

### 3.1. Thêm vào giỏ (`action=add`)
- **Input:** `productId`, `quantity` từ trang chi tiết sản phẩm (`product-detail.jsp`).
- **Logic:**
  1. Lấy `cart` từ Session. Nếu `cart == null`, khởi tạo một `ArrayList` mới.
  2. Duyệt qua `cart` kiểm tra xem sản phẩm (`productId`) đã tồn tại chưa.
  3. Nếu đã tồn tại: Cộng dồn `quantity`.
  4. Nếu chưa tồn tại: Dùng `ProductDAO` truy vấn thông tin sản phẩm từ DB, tạo mới một đối tượng `CartItem` và thêm vào danh sách.
  5. Trả về trang `cart.jsp` bằng `sendRedirect`.

### 3.2. Cập nhật số lượng (`action=update`)
- **Input:** `productId`, `quantity` (giá trị mới) từ trang `cart.jsp`.
- **Logic:**
  1. Lấy `cart` từ Session.
  2. Tìm sản phẩm có `productId` tương ứng.
  3. Cập nhật `quantity` bằng giá trị mới. (Nếu `quantity <= 0`, có thể xử lý xóa).

### 3.3. Xóa sản phẩm (`action=remove`)
- **Input:** `productId`.
- **Logic:**
  1. Lấy `cart` từ Session.
  2. Xóa đối tượng `CartItem` có `productId` khớp khỏi danh sách.

## 4. Hiển thị ở View (JSP)

- **Trang Giỏ hàng (`cart.jsp`):** Dùng JSTL (`<c:forEach>`) lặp qua `sessionScope.cart` để in ra bảng các sản phẩm. Tính tổng tiền bằng cách cộng dồn `item.subtotal`.
- **Badge trên Navbar (`header.jsp`):** Lặp qua `sessionScope.cart` cộng dồn `item.quantity` để in ra tổng số sản phẩm hiện có trong giỏ ngay trên thanh điều hướng.
