<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<h3 class="mb-3">Thông tin đặt hàng</h3>

<div class="row g-4">
    <div class="col-md-7">
        <div class="card shadow-sm border-0 mb-4">
            <div class="card-body">
                <h4 class="card-title mb-4">Thông tin người nhận</h4>

                <%-- Loi validate phia server --%>
                <c:if test="${not empty errors}">
                    <div class="alert alert-danger">
                        <ul class="mb-0 ps-3">
                            <c:forEach var="err" items="${errors}">
                                <li><c:out value="${err}"/></li>
                            </c:forEach>
                        </ul>
                    </div>
                </c:if>
                <%-- Loi khi tao don (het hang / ma het hieu luc...) --%>
                <c:if test="${not empty orderError}">
                    <div class="alert alert-danger"><c:out value="${orderError}"/></div>
                </c:if>

                <%-- Form nhap thong tin khach. doPost cua CheckoutServlet xu ly.
                     Gia tri nhap truoc do duoc giu lai qua ${param...} khi validate loi. --%>
                <form action="${pageContext.request.contextPath}/checkout" method="post">
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Họ và tên</label>
                        <input type="text" name="customerName" class="form-control"
                               placeholder="Nguyễn Văn A" required
                               value="<c:out value='${param.customerName}'/>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Số điện thoại</label>
                        <input type="text" name="phone" class="form-control"
                               placeholder="09xxxxxxxx" required
                               value="<c:out value='${param.phone}'/>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Địa chỉ giao hàng chi tiết</label>
                        <textarea name="address" class="form-control" rows="3" required
                                  placeholder="Số nhà, đường, phường/xã, quận/huyện, tỉnh/thành phố"><c:out value="${param.address}"/></textarea>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Ghi chú <span class="text-muted fw-normal">(không bắt buộc)</span></label>
                        <textarea name="note" class="form-control" rows="2" maxlength="500"
                                  placeholder="Ví dụ: giao giờ hành chính, gọi trước khi giao..."><c:out value="${param.note}"/></textarea>
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

                <div class="checkout-items mb-3" style="max-height: 300px; overflow-y: auto;">
                    <c:forEach var="item" items="${cartItems}">
                        <div class="d-flex align-items-center mb-3">
                            <img src="${pageContext.request.contextPath}/assets/images/${item.image}" alt="<c:out value='${item.name}'/>"
                                 style="width: 60px; height: 60px; object-fit: cover; border-radius: 8px; border: 1px solid #ddd;" class="me-3">
                            <div class="flex-grow-1">
                                <h6 class="mb-0" style="font-size: 0.95rem;"><c:out value="${item.name}"/></h6>
                                <c:if test="${not empty item.variantName}">
                                    <small class="text-muted d-block">Phân loại: <c:out value="${item.variantName}"/></small>
                                </c:if>
                                <small class="text-muted">SL: ${item.quantity} x <fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ</small>
                            </div>
                            <div class="fw-bold">
                                <fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ
                            </div>
                        </div>
                    </c:forEach>
                </div>

                <hr>

                <%-- Ma giam gia --%>
                <c:if test="${not empty couponError}">
                    <div class="alert alert-warning py-2 small mb-2"><c:out value="${couponError}"/></div>
                </c:if>
                <c:if test="${not empty couponSuccess}">
                    <div class="alert alert-success py-2 small mb-2"><c:out value="${couponSuccess}"/></div>
                </c:if>
                <c:choose>
                    <c:when test="${empty appliedCoupon}">
                        <form action="${pageContext.request.contextPath}/checkout" method="get" class="d-flex gap-2 mb-3">
                            <input type="text" name="coupon" class="form-control form-control-sm text-uppercase"
                                   placeholder="Mã giảm giá (nếu có)">
                            <button type="submit" class="btn btn-sm btn-outline-primary flex-shrink-0">Áp dụng</button>
                        </form>
                    </c:when>
                    <c:otherwise>
                        <div class="d-flex justify-content-between align-items-center mb-3">
                            <span class="badge bg-success">Mã: <c:out value="${appliedCoupon}"/> (-${couponPercent}%)</span>
                            <a href="${pageContext.request.contextPath}/checkout?removeCoupon=1"
                               class="small text-danger text-decoration-none">Bỏ mã</a>
                        </div>
                    </c:otherwise>
                </c:choose>

                <div class="d-flex justify-content-between mb-2">
                    <span class="text-muted">Tạm tính:</span>
                    <span><fmt:formatNumber value="${subtotal}" type="number" maxFractionDigits="0"/> đ</span>
                </div>
                <c:if test="${discount > 0}">
                    <div class="d-flex justify-content-between mb-2 text-success">
                        <span>Giảm giá:</span>
                        <span>-<fmt:formatNumber value="${discount}" type="number" maxFractionDigits="0"/> đ</span>
                    </div>
                </c:if>
                <div class="d-flex justify-content-between mb-3">
                    <span class="text-muted">Phí giao hàng:</span>
                    <c:choose>
                        <c:when test="${shipFee > 0}">
                            <span><fmt:formatNumber value="${shipFee}" type="number" maxFractionDigits="0"/> đ</span>
                        </c:when>
                        <c:otherwise><span class="text-success">Miễn phí</span></c:otherwise>
                    </c:choose>
                </div>
                <c:if test="${shipFee > 0}">
                    <p class="small text-muted mb-3">Miễn phí giao hàng cho đơn từ 500.000 đ.</p>
                </c:if>

                <div class="d-flex justify-content-between align-items-center pt-2" style="border-top: 1px dashed #ccc;">
                    <span class="fw-bold" style="font-size: 1.1rem;">Tổng cộng:</span>
                    <span class="fw-bold text-danger" style="font-size: 1.4rem;"><fmt:formatNumber value="${grandTotal}" type="number" maxFractionDigits="0"/> đ</span>
                </div>
            </div>
        </div>
        <div class="text-center mt-3">
            <a href="${pageContext.request.contextPath}/cart" class="text-decoration-none">← Quay lại giỏ hàng</a>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
