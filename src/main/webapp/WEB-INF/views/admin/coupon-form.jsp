<%@ include file="admin-header.jsp" %>

<h3 class="mb-3">${coupon.id == 0 ? 'Thêm mã giảm giá' : 'Sửa mã giảm giá'}</h3>

<c:if test="${not empty errors}">
    <div class="alert alert-danger" style="max-width: 480px;">
        <ul class="mb-0 ps-3">
            <c:forEach var="err" items="${errors}">
                <li><c:out value="${err}"/></li>
            </c:forEach>
        </ul>
    </div>
</c:if>

<form action="${pageContext.request.contextPath}/admin/coupons" method="post" style="max-width: 480px;">
    <input type="hidden" name="id" value="${coupon.id}">

    <div class="mb-3">
        <label class="form-label fw-semibold">Mã (khách sẽ nhập mã này)</label>
        <input type="text" name="code" class="form-control text-uppercase" required
               placeholder="VD: SALE10" value="<c:out value='${coupon.code}'/>">
    </div>
    <div class="mb-3">
        <label class="form-label fw-semibold">Phần trăm giảm (1-100)</label>
        <input type="number" name="discountPercent" class="form-control" min="1" max="100" required
               value="${coupon.discountPercent == 0 ? '' : coupon.discountPercent}">
    </div>
    <div class="mb-3">
        <label class="form-label fw-semibold">Số lượt dùng tối đa</label>
        <input type="number" name="maxUses" class="form-control" min="1" required
               value="${coupon.maxUses == 0 ? '' : coupon.maxUses}">
        <c:if test="${coupon.id > 0}">
            <small class="text-muted">Đã dùng: ${coupon.usedCount} lượt.</small>
        </c:if>
    </div>
    <div class="mb-3">
        <label class="form-label fw-semibold">Ngày hết hạn <span class="text-muted fw-normal">(bỏ trống = không hết hạn)</span></label>
        <input type="date" name="expiresAt" class="form-control"
               value="${not empty coupon.expiresAt ? fn:substring(coupon.expiresAt, 0, 10) : ''}">
    </div>
    <div class="form-check mb-4">
        <input type="checkbox" name="active" class="form-check-input" id="activeCheck"
               ${coupon.id == 0 || coupon.active ? 'checked' : ''}>
        <label class="form-check-label" for="activeCheck">Kích hoạt (khách dùng được ngay)</label>
    </div>

    <button type="submit" class="btn btn-primary px-4">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/coupons" class="btn btn-outline-secondary">Hủy</a>
</form>

<%@ include file="admin-footer.jsp" %>
