<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<h3 class="mb-3">Tra cứu đơn hàng</h3>
<p class="text-muted">Nhập số điện thoại bạn đã dùng khi đặt hàng để xem tình trạng đơn.</p>

<form method="get" action="${pageContext.request.contextPath}/track-order"
      class="d-flex gap-2 mb-4" style="max-width: 420px;">
    <input type="text" name="phone" class="form-control" placeholder="Ví dụ: 0901234567"
           value="<c:out value='${phone}'/>" required>
    <button type="submit" class="btn btn-primary flex-shrink-0">Tra cứu</button>
</form>

<c:if test="${not empty error}">
    <div class="alert alert-danger" style="max-width: 420px;"><c:out value="${error}"/></div>
</c:if>

<c:if test="${searched}">
    <c:choose>
        <c:when test="${empty orders}">
            <div class="alert alert-info" style="max-width: 420px;">
                Không tìm thấy đơn hàng nào với số điện thoại <strong><c:out value="${phone}"/></strong>.
            </div>
        </c:when>
        <c:otherwise>
            <h5 class="mb-3">Tìm thấy ${orders.size()} đơn hàng</h5>
            <c:forEach var="o" items="${orders}">
                <div class="card shadow-sm border-0 mb-3">
                    <div class="card-body">
                        <div class="d-flex justify-content-between align-items-center mb-2 flex-wrap gap-2">
                            <div>
                                <span class="fw-bold">Đơn #${o.id}</span>
                                <span class="text-muted small ms-2">Đặt ngày: ${o.createdAt}</span>
                            </div>
                            <c:choose>
                                <c:when test="${o.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ xử lý</span></c:when>
                                <c:when test="${o.status == 'CONFIRMED'}"><span class="badge bg-info text-dark">Đã xác nhận</span></c:when>
                                <c:when test="${o.status == 'SHIPPING'}"><span class="badge bg-primary">Đang giao</span></c:when>
                                <c:when test="${o.status == 'DELIVERED'}"><span class="badge bg-success">Đã giao</span></c:when>
                                <c:when test="${o.status == 'CANCELLED'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                            </c:choose>
                        </div>
                        <ul class="list-unstyled small mb-2">
                            <c:forEach var="item" items="${o.items}">
                                <li class="d-flex justify-content-between border-bottom py-1">
                                    <span>
                                        <c:out value="${item.productName}"/>
                                        <c:if test="${not empty item.variantName}">
                                            <span class="text-muted">(<c:out value="${item.variantName}"/>)</span>
                                        </c:if>
                                        × ${item.quantity}
                                    </span>
                                    <span><fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ</span>
                                </li>
                            </c:forEach>
                        </ul>
                        <div class="d-flex justify-content-between align-items-center">
                            <small class="text-muted">Giao đến: <c:out value="${o.address}"/></small>
                            <span class="fw-bold text-danger"><fmt:formatNumber value="${o.totalAmount}" type="number" maxFractionDigits="0"/> đ</span>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </c:otherwise>
    </c:choose>
</c:if>

<%@ include file="common/footer.jsp" %>
