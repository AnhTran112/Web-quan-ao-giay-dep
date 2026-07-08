<%@ include file="admin-header.jsp" %>

<h3 class="mb-3">Quản lý đơn hàng</h3>

<table class="table table-bordered align-middle">
    <thead class="table-light">
        <tr>
            <th>ID</th><th>Khách hàng</th><th>SĐT</th><th>Địa chỉ</th>
            <th>Ngày đặt</th><th>Tổng tiền</th><th>Trạng thái</th><th>Hành động</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="o" items="${orders}">
            <tr>
                <td>${o.id}</td>
                <td>${o.customerName}</td>
                <td>${o.phone}</td>
                <td>${o.address}</td>
                <td>${o.createdAt}</td>
                <td class="fw-bold text-danger"><fmt:formatNumber value="${o.totalAmount}" type="number"/> đ</td>
                <td>
                    <c:choose>
                        <c:when test="${o.status == 'DELIVERED'}">
                            <span class="badge bg-success">Đã giao</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning text-dark">Đang xử lý</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <c:if test="${o.status != 'DELIVERED'}">
                        <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="d-inline">
                            <input type="hidden" name="id" value="${o.id}">
                            <button type="submit" class="btn btn-sm btn-success">Đánh dấu đã giao</button>
                        </form>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty orders}">
            <tr><td colspan="8" class="text-center text-muted">Chưa có đơn hàng</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="admin-footer.jsp" %>
