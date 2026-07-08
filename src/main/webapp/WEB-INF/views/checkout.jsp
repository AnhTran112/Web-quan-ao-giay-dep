<%@ include file="common/header.jsp" %>

<h3 class="mb-3">Thông tin đặt hàng</h3>

<div class="row g-4">
    <div class="col-md-7">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body">
                <h4 class="card-title mb-4">Thông tin người nhận</h4>
                <c:if test="${not empty error}">
                    <div class="alert alert-danger">${error}</div>
                </c:if>
                <%-- Form nhap thong tin khach. doPost cua CheckoutServlet xu ly --%>
                <form action="${pageContext.request.contextPath}/checkout" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Họ và tên</label>
                        <input type="text" name="customerName" class="form-control" placeholder="Nguyễn Văn A" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số điện thoại</label>
                        <input type="text" name="phone" class="form-control" placeholder="09xxxxxxxxx" required>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Địa chỉ giao hàng chi tiết</label>
                        <textarea name="address" class="form-control" rows="3" placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố" required></textarea>
                    </div>
                    <button type="submit" class="btn btn-success btn-lg w-100 mt-2">Xác nhận đặt hàng</button>
                    <p class="text-muted small text-center mt-3">Chúng tôi sẽ liên hệ với bạn để xác nhận đơn hàng sau khi đặt thành công.</p>
                </form>
            </div>
        </div>
    </div>

    <div class="col-md-5">
        <div class="card shadow-sm border-0" style="background-color: #f8f9fa;">
            <div class="card-body">
                <h4 class="card-title mb-4">Tóm tắt đơn hàng</h4>
                
                <div class="checkout-items mb-3" style="max-height: 350px; overflow-y: auto;">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="d-flex align-items-center mb-3">
                            <img src="${pageContext.request.contextPath}/assets/images/${item.image}" alt="${item.name}" style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd;" class="me-3">
                            <div class="flex-grow-1">
                                <h6 class="mb-0" style="font-size: 0.95rem;">${item.name}</h6>
                                <small class="text-muted">SL: ${item.quantity} x <fmt:formatNumber value="${item.price}" type="number"/> đ</small>
                            </div>
                            <div class="fw-bold">
                                <fmt:formatNumber value="${item.subtotal}" type="number"/> đ
                            </div>
                        </div>
                    </c:forEach>
                </div>
                
                <hr>
                
                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Tạm tính:</span>
                    <span><fmt:formatNumber value="${cartTotal}" type="number"/> đ</span>
                </div>
                <div class="d-flex justify-content-between mb-3">
                    <span class="text-muted">Phí giao hàng:</span>
                    <span>Miễn phí</span>
                </div>
                
                <div class="d-flex justify-content-between align-items-center pt-2" style="border-top: 1px dashed #ccc;">
                    <span class="fw-bold" style="font-size: 1.1rem;">Tổng cộng:</span>
                    <span class="fw-bold text-danger" style="font-size: 1.4rem;"><fmt:formatNumber value="${cartTotal}" type="number"/> đ</span>
                </div>
            </div>
        </div>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">← Quay lại giỏ hàng</a>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
