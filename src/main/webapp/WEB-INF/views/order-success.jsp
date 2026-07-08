<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<div class="text-center py-5">
    <h1 class="text-success">✓ Đặt hàng thành công!</h1>
    <c:if test="${not empty orderId}">
        <h4 class="mt-3">Mã đơn hàng của bạn là: <span class="badge bg-primary">#${orderId}</span></h4>
    </c:if>
    <c:if test="${not empty orderTotal}">
        <p class="mt-2">Tổng thanh toán khi nhận hàng:
            <span class="fw-bold text-danger"><fmt:formatNumber value="${orderTotal}" type="number" maxFractionDigits="0"/> đ</span>
        </p>
    </c:if>
    <p class="lead mt-3">Cảm ơn bạn đã mua hàng. Chúng tôi sẽ gọi điện xác nhận trong thời gian sớm nhất.</p>
    <p class="text-muted">Bạn có thể theo dõi tình trạng đơn bằng số điện thoại đã đặt hàng.</p>
    <div class="mt-3">
        <a href="${pageContext.request.contextPath}/track-order<c:if test='${not empty customerPhone}'>?phone=<c:out value="${customerPhone}"/></c:if>"
           class="btn btn-outline-primary me-2">Tra cứu đơn hàng</a>
        <a href="${pageContext.request.contextPath}/home" class="btn btn-primary">Tiếp tục mua sắm</a>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
