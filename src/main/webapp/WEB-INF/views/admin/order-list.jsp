<%@ include file="admin-header.jsp" %>
<fmt:setLocale value="vi_VN" />

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý đơn hàng</h3>
    <span class="text-muted">Tổng: <strong>${totalOrders}</strong> đơn</span>
</div>

<%-- ===== Bộ lọc ===== --%>
<form method="get" action="${pageContext.request.contextPath}/admin/orders"
      class="row g-2 align-items-end bg-light border rounded p-3 mb-3">
    <div class="col-md-3">
        <label class="form-label small mb-1">Trạng thái</label>
        <select name="status" class="form-select form-select-sm">
            <option value="">— Tất cả —</option>
            <c:forEach var="s" items="${allStatuses}">
                <option value="${s}" ${fStatus == s ? 'selected' : ''}>
                    <c:choose>
                        <c:when test="${s == 'PENDING'}">Chờ xử lý</c:when>
                        <c:when test="${s == 'CONFIRMED'}">Đã xác nhận</c:when>
                        <c:when test="${s == 'SHIPPING'}">Đang giao</c:when>
                        <c:when test="${s == 'DELIVERED'}">Đã giao</c:when>
                        <c:when test="${s == 'CANCELLED'}">Đã hủy</c:when>
                        <c:otherwise>${s}</c:otherwise>
                    </c:choose>
                </option>
            </c:forEach>
        </select>
    </div>
    <div class="col-md-3">
        <label class="form-label small mb-1">Số điện thoại</label>
        <input type="text" name="phone" class="form-control form-control-sm"
               placeholder="Nhập SĐT khách..." value="<c:out value='${fPhone}'/>">
    </div>
    <div class="col-md-2">
        <label class="form-label small mb-1">Từ ngày</label>
        <input type="date" name="from" class="form-control form-control-sm" value="${fFrom}">
    </div>
    <div class="col-md-2">
        <label class="form-label small mb-1">Đến ngày</label>
        <input type="date" name="to" class="form-control form-control-sm" value="${fTo}">
    </div>
    <div class="col-md-2 d-flex gap-2">
        <button type="submit" class="btn btn-sm btn-primary flex-fill">Lọc</button>
        <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-sm btn-outline-secondary">Xóa lọc</a>
    </div>
</form>

<table class="table table-bordered align-middle">
    <thead class="table-light">
        <tr>
            <th>ID</th><th>Khách hàng</th><th>SĐT</th><th>Ngày đặt</th>
            <th>Tổng tiền</th><th>Trạng thái</th><th style="width:110px;"></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="o" items="${orders}">
            <tr>
                <td class="fw-bold">#${o.id}</td>
                <td><c:out value="${o.customerName}"/></td>
                <td><c:out value="${o.phone}"/></td>
                <td>${o.createdAt}</td>
                <td class="fw-bold text-danger"><fmt:formatNumber value="${o.totalAmount}" type="number" maxFractionDigits="0"/> đ</td>
                <td>
                    <c:choose>
                        <c:when test="${o.status == 'PENDING'}"><span class="badge bg-warning text-dark">Chờ xử lý</span></c:when>
                        <c:when test="${o.status == 'CONFIRMED'}"><span class="badge bg-info text-dark">Đã xác nhận</span></c:when>
                        <c:when test="${o.status == 'SHIPPING'}"><span class="badge bg-primary">Đang giao</span></c:when>
                        <c:when test="${o.status == 'DELIVERED'}"><span class="badge bg-success">Đã giao</span></c:when>
                        <c:when test="${o.status == 'CANCELLED'}"><span class="badge bg-secondary">Đã hủy</span></c:when>
                        <c:otherwise><span class="badge bg-light text-dark">${o.status}</span></c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/orders?action=view&id=${o.id}"
                       class="btn btn-sm btn-outline-primary w-100">Xem chi tiết</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty orders}">
            <tr><td colspan="7" class="text-center text-muted py-4">Không có đơn hàng nào khớp bộ lọc</td></tr>
        </c:if>
    </tbody>
</table>

<%-- ===== Phân trang (giữ nguyên bộ lọc trên link) ===== --%>
<c:if test="${totalPages > 1}">
    <c:set var="filterQuery" value="status=${fStatus}&phone=${fPhone}&from=${fFrom}&to=${fTo}"/>
    <nav>
        <ul class="pagination pagination-sm justify-content-center">
            <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                <a class="page-link" href="?${filterQuery}&page=${currentPage - 1}">«</a>
            </li>
            <c:forEach var="p" begin="1" end="${totalPages}">
                <li class="page-item ${p == currentPage ? 'active' : ''}">
                    <a class="page-link" href="?${filterQuery}&page=${p}">${p}</a>
                </li>
            </c:forEach>
            <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                <a class="page-link" href="?${filterQuery}&page=${currentPage + 1}">»</a>
            </li>
        </ul>
    </nav>
</c:if>

<%@ include file="admin-footer.jsp" %>
