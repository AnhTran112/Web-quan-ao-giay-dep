<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý mã giảm giá</h3>
    <a href="${pageContext.request.contextPath}/admin/coupons?action=new" class="btn btn-primary">+ Thêm mã mới</a>
</div>

<c:if test="${not empty param.msg}">
    <div class="alert alert-success py-2"><c:out value="${param.msg}"/></div>
</c:if>

<table class="table table-bordered align-middle">
    <thead class="table-light">
        <tr>
            <th>Mã</th><th>Giảm</th><th>Lượt dùng</th><th>Hết hạn</th>
            <th>Trạng thái</th><th style="width:160px;">Hành động</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="cp" items="${coupons}">
            <tr>
                <td class="fw-bold"><c:out value="${cp.code}"/></td>
                <td>${cp.discountPercent}%</td>
                <td>${cp.usedCount} / ${cp.maxUses}</td>
                <td>
                    <c:choose>
                        <c:when test="${empty cp.expiresAt}"><span class="text-muted">Không hết hạn</span></c:when>
                        <c:otherwise>${cp.expiresAt}</c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:choose>
                        <c:when test="${!cp.active}"><span class="badge bg-secondary">Ngừng dùng</span></c:when>
                        <c:when test="${!cp.hasUsesLeft()}"><span class="badge bg-danger">Hết lượt</span></c:when>
                        <c:otherwise><span class="badge bg-success">Đang chạy</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/coupons?action=edit&id=${cp.id}"
                       class="btn btn-sm btn-outline-primary">Sửa</a>
                    <a href="${pageContext.request.contextPath}/admin/coupons?action=delete&id=${cp.id}"
                       class="btn btn-sm btn-outline-danger"
                       onclick="return confirm('Xóa mã ${cp.code}?');">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty coupons}">
            <tr><td colspan="6" class="text-center text-muted py-4">Chưa có mã giảm giá nào</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="admin-footer.jsp" %>
