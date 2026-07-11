# 🎬 KỊCH BẢN THUYẾT TRÌNH & DEMO WEBSITE BÁN QUẦN ÁO – GIÀY DÉP
### (Online qua Stream — tổng ~10 phút, cả 4 người demo)

---

## 📌 QUY TẮC CHUNG (đọc kỹ trước)
- **Khi tới lượt ai:** người đó **bật Camera 📷 + Mic 🎤 + Share màn hình 🖥️**, vừa nói vừa click phần mình code.
- Nói xong câu bàn giao → **tắt Share** → người kế tiếp mới bật Share (⚠️ **không để 2 người share cùng lúc**, sẽ giật hình).
- Mở đầu mỗi lượt nói to: *"Thưa thầy, em là [tên], em xin phép chia sẻ màn hình demo phần…"*
- Camera + Mic **bật suốt buổi** để thầy thấy mặt; chỉ luân phiên nhau phần **Share màn hình**.

## ⚙️ CHUẨN BỊ TRƯỚC BUỔI (bắt buộc — không làm sẽ vỡ demo)
1. Cả 4 máy: `cd web` → `mvn clean package cargo:run`, nạp sẵn `database/database.sql`, mở `http://localhost:8081/shop/home`.
2. **Tạo sẵn 1–2 đánh giá:** đăng nhập `khach1/123456` → vào sản phẩm khách này **đã mua** → chấm 5 sao + bình luận (để Hoàng khoe review, vì dữ liệu mẫu chưa có review nào).
3. **Cho `nhanvien/123456` đổi trạng thái 1 đơn** trước buổi (để Nhật ký có sẵn dòng "nhanvien cập nhật đơn hàng").
4. Ghi nhớ mã giảm giá **còn hiệu lực: `SALE10`** (giảm 10%). Không dùng SALE20 / HETHAN15 / NGUNG25 (hết lượt / hết hạn / đã tắt).
5. Chuẩn bị thông tin đặt hàng hợp lệ: tên, **SĐT dạng `09xxxxxxxx`**, địa chỉ.
6. Máy Nguyên: mở sẵn **3 cửa sổ trình duyệt khác nhau** (login khach / login nhanvien / login admin) để khỏi đăng xuất qua lại.

## ⏱️ PHÂN BỔ THỜI GIAN
| Phần | Người | Thời lượng |
|------|-------|:---:|
| Mở đầu + Stream 1 | Anh | ~2:00 |
| Stream 2 (CRUD sản phẩm) | Hoàng | ~2:30 |
| Stream 3 (Đặt hàng) | Khoa | ~2:00 |
| Stream 4 (Bảo mật + Thống kê) | Nguyên | ~2:30 |
| Tổng kết | Anh | ~1:00 |
| **Tổng** | | **~10:00** |

---

## 🖥️ STREAM 1 — ANH (Nhóm trưởng) · Giao diện & Giỏ hàng
**🎯 Mục tiêu:** giao diện tổng quan + trải nghiệm mua hàng không cần đăng nhập.
**Chuẩn bị tab:** Trang chủ `http://localhost:8081/shop/home` — **chưa đăng nhập**.

**💻 Thao tác (vừa click vừa nói):**
1. Bật Camera + Mic + Share. Lướt trang chủ 1 vòng (banner, lưới sản phẩm, bộ lọc danh mục/giá).
2. Bấm **biểu tượng con mắt (Xem nhanh)** trên 1 sản phẩm → modal hiện ra → **chọn phân loại** (vd "Size 40") → bấm **Thêm vào giỏ** → có thông báo Toast.
3. Bấm **Giỏ hàng** (góc phải) → xem sản phẩm trong giỏ *(vẫn đang **chưa đăng nhập**)* → chỉ vào chức năng cập nhật số lượng / xóa.
4. Quay ra, **đăng nhập `khach1/123456`** → về trang chủ → bấm **nút trái tim ❤️** trên 1 sản phẩm → bấm menu **Yêu thích** → xem trang Wishlist.

**🗣️ Lời thoại:**
> "Thưa thầy, em là **Anh**, trưởng nhóm, xin phép demo phần **Trải nghiệm khách hàng** do em phụ trách. Giao diện nhóm em thiết kế theo hướng **tối giản, hiện đại**, có bộ lọc theo danh mục và khoảng giá, tìm kiếm gợi ý. Khách có thể **xem nhanh** sản phẩm qua Quick-view và thêm vào giỏ ngay mà không rời trang.
>
> Điểm đặc biệt: **khách chưa cần đăng nhập vẫn mua được**, nhờ **giỏ hàng lưu bằng Cookie** — và hệ thống **kiểm tra tồn kho tức thì** khi bấm Thêm, chống đặt vượt số lượng còn lại. Với các tính năng cá nhân như **Yêu thích (Wishlist)**, hệ thống yêu cầu đăng nhập để bảo mật; sau khi đăng nhập, khách lưu sản phẩm yêu thích như thế này.
>
> Em xin hết phần mình, nhường màn hình cho bạn **Hoàng**." → *(tắt Share)*

---

## 🖥️ STREAM 2 — HOÀNG · CRUD Sản phẩm, Biến thể & Đánh giá
**🎯 Mục tiêu:** demo **CRUD đầy đủ** (Thêm / Xem / Sửa / Xóa) + hệ thống biến thể + đánh giá.
*(Đây là phần ăn điểm CRUD — làm chậm, rõ ràng.)*
**Chuẩn bị tab:** Đã login `admin/123456`, đang ở **Admin → Sản phẩm** (danh sách = **Xem/Read** ✅).

**💻 Thao tác:**
1. **➕ THÊM (Create):** Bấm **"Thêm sản phẩm"** → điền **Tên** = *"Áo Demo Test"*, **Giá** = *200000*, chọn **Danh mục**, **Upload 1 ảnh** từ máy, thêm **1 phân loại** ("Size M", giá, tồn kho) → bấm **Lưu** → quay về danh sách, sản phẩm mới hiện lên.
2. **✏️ SỬA (Update):** Bấm **"Sửa"** ngay sản phẩm vừa tạo → đổi giá thành *180000*, **thêm phân loại thứ 2** "Size L" (chỉ vào **giá + tồn kho riêng** từng size) → **Lưu**.
3. **🗑️ XÓA (Delete):** Bấm **"Xóa"** sản phẩm demo đó → xác nhận → nó biến mất khỏi danh sách.
4. Mở tab **chi tiết một sản phẩm thật** (đã có review) → kéo xuống cuối → xem phần **khách đánh giá 5 sao**.

**🗣️ Lời thoại:**
> "Thưa thầy, em là **Hoàng**, phụ trách nền tảng dự án và luồng Sản phẩm với **đầy đủ CRUD**. Đầu tiên em **Thêm** một sản phẩm mới, có **upload ảnh** và tạo phân loại — lưu xong nó xuất hiện ngay trong danh sách. Tiếp theo em **Sửa** để cập nhật giá và thêm size.
>
> Điểm kỹ thuật khó nhất là hệ thống **Biến thể (Product Variant)**: một sản phẩm chia thành nhiều Size/Màu, **mỗi loại có giá bán và tồn kho độc lập** — sát với bài toán quản lý kho thực tế. Cuối cùng em **Xóa** sản phẩm demo này đi, hoàn tất vòng Thêm – Sửa – Xóa.
>
> Ngoài ra em làm module **Đánh giá**: chỉ khách **đã mua hàng** (xác thực qua số điện thoại) mới được chấm sao và bình luận, hiển thị trực tiếp lên trang như thế này. Em xin nhường cho bạn **Khoa**." → *(tắt Share)*

---

## 🖥️ STREAM 3 — KHOA · Thanh toán & Vòng đời Đơn hàng
**🎯 Mục tiêu:** tạo đơn (Create) qua Transaction + cập nhật trạng thái (Update).
**Chuẩn bị tab:** Tab 1 = **Giỏ hàng** (có sẵn đồ). Tab 2 = **Admin → Đơn hàng**.

**💻 Thao tác:**
1. Tab 1: bấm **Thanh toán** → ô mã giảm giá nhập **`SALE10`** → bấm **Áp dụng** (thấy tiền giảm) → điền **Tên / SĐT `09…` / Địa chỉ** → bấm **Đặt hàng** → trang "Đặt hàng thành công".
2. Sang **Tab 2 (Admin) → nhấn F5** → đơn vừa đặt **nổ về** đầu danh sách.
3. Bấm vào đơn đó → đổi trạng thái **Chờ xử lý (PENDING) → Đã xác nhận (CONFIRMED)**.

**🗣️ Lời thoại:**
> "Thưa thầy, em là **Khoa**, phụ trách luồng Đặt hàng. Khi khách thanh toán, có thể nhập **mã giảm giá** — em dùng mã `SALE10` giảm 10%. Ngay khi bấm **Đặt hàng**, một **Transaction** an toàn chạy ngầm, gom 3 thao tác thành một khối: **tạo đơn – trừ tồn kho – trừ lượt mã giảm giá**. Nếu bất kỳ bước nào lỗi, ví dụ một món vừa hết hàng, toàn bộ sẽ **rollback**, không tạo đơn dở dang — đảm bảo dữ liệu luôn toàn vẹn.
>
> Bên phía Admin, đơn **nổ về ngay lập tức**, và em cập nhật trạng thái theo đúng luồng nghiệp vụ. Quy trình khép kín, đặc biệt hệ thống **tự động hoàn hàng về kho nếu đơn bị Hủy**. Em xin nhường cho bạn **Nguyên**." → *(tắt Share)*

---

## 🖥️ STREAM 4 — NGUYÊN · Bảo mật, Phân quyền & Thống kê
**🎯 Mục tiêu:** BCrypt + chống brute-force + phân quyền 2 lớp + Audit Log + Dashboard/Excel.
**Chuẩn bị:** 3 cửa sổ — CS1: trang đăng nhập (chưa login) · CS2: đã login `nhanvien/123456` · CS3: đã login `admin/123456` đang mở **Nhật ký (Audit)**.

**💻 Thao tác:**
1. **CS1:** cố tình **gõ sai mật khẩu 1 lần** (xem thông báo lỗi) → rồi đăng nhập đúng `khach1/123456` → vào **Lịch sử mua hàng** → chỉ vào **Timeline lịch sử trạng thái đơn**.
2. **CS2 (Nhân viên):** gõ thẳng URL **`localhost:8081/shop/admin/users`** trên thanh địa chỉ → hiện **403 Access Denied**.
3. **CS3 (Admin):** chỉ vào dòng Audit Log **"nhanvien – cập nhật đơn hàng"** → bấm sang **Dashboard** → chỉ **biểu đồ doanh thu** + **biểu đồ tròn theo danh mục** → chỉ nút **Xuất báo cáo (Excel)**.

**🗣️ Lời thoại:**
> "Thưa thầy, em là **Nguyên**, phụ trách **Bảo mật và Tài khoản**. Mật khẩu trong hệ thống được **mã hóa BCrypt**, không lưu dạng thô. Hệ thống có cơ chế **chống dò mật khẩu (brute-force)**: sai quá **5 lần** sẽ **khóa tài khoản 15 phút**. Khách sau khi đăng nhập theo dõi đơn qua **Timeline lịch sử trạng thái** rất trực quan.
>
> Về bảo mật nội bộ, em thiết kế **phân quyền 2 lớp bằng AuthFilter**: với tài khoản **Nhân viên**, các menu quản trị đã bị **ẩn**; và nếu cố tình gõ thẳng URL vào mục Tài khoản khách hàng thì vẫn **bị chặn 403** như thầy thấy. Đồng thời **Nhật ký (Audit Log)** ghi lại mọi thao tác của nhân viên để chống gian lận.
>
> Cuối cùng, chủ shop xem **Dashboard biểu đồ doanh thu** theo tháng và theo danh mục, cùng chức năng **Xuất báo cáo ra file Excel (.xlsx)** để lưu trữ. Em xin mời bạn **Anh** lên chốt lại ạ." → *(tắt Share)*

---

## 🎙️ TỔNG KẾT — ANH (bật Camera + Mic, KHÔNG share màn hình)
**🗣️ Lời thoại:**
> "Thưa thầy, để đúc kết, website của nhóm em không dừng ở mức trưng bày sản phẩm cơ bản, mà giải quyết **3 bài toán lõi** của một hệ thống thương mại điện tử thực tế:
>
> **Một – Trải nghiệm:** Giỏ hàng Cookie và Quick-view giúp mua sắm mượt mà, không rào cản đăng nhập.
> **Hai – Quản trị & Vận hành:** CRUD sản phẩm đầy đủ, quản lý kho phức tạp bằng hệ thống Biến thể Size/Màu, và Transaction đảm bảo toàn vẹn dữ liệu đơn hàng.
> **Ba – Bảo mật:** Phân quyền AuthFilter nhiều lớp, mã hóa BCrypt và Audit Log chống gian lận cả bên ngoài lẫn nội bộ.
>
> Về công nghệ, nhóm em dùng **Java Servlet/JSP, JDBC với MySQL, theo mô hình MVC**, chạy trên Tomcat. Thay mặt nhóm, em chân thành cảm ơn **thầy** đã dành thời gian theo dõi. **Kính mời thầy đặt câu hỏi phản biện ạ!**"

---

## 🧯 PHÒNG KHI BÍ GIỜ (cắt nhanh, giữ phần lõi)
- **Stream 1:** bỏ bước mở trang giỏ hàng, chỉ cần Thêm giỏ + Wishlist.
- **Stream 4:** bỏ bước gõ-sai-mật-khẩu, vào thẳng phần phân quyền 403 + Audit Log.
- **Tuyệt đối giữ:** CRUD của Hoàng, Transaction của Khoa, phân quyền 403 của Nguyên (các phần này là điều kiện chặn điểm).

---

## 👥 BẢNG PHÂN VAI (mỗi người đều Camera + Mic + Share)
| Người | Share màn hình | Nói | Nội dung chính |
|-------|:---:|:---:|----------------|
| **Trần Tuấn Anh** | ✅ | ✅ | Giao diện, Giỏ hàng Cookie, Quick-view, Wishlist + Tổng kết |
| **Hồ Huy Hoàng** | ✅ | ✅ | CRUD Sản phẩm, Biến thể Size/Màu, Đánh giá |
| **Nguyễn Hoàng Anh Khoa** | ✅ | ✅ | Thanh toán, Mã giảm giá, Transaction, trạng thái đơn |
| **Trần Trung Nguyên** | ✅ | ✅ | Đăng nhập/BCrypt, phân quyền 403, Audit Log, Dashboard/Excel |
