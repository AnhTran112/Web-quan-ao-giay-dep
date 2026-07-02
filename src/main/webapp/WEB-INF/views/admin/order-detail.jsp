<%@ include file="admin-header.jsp" %>

<h3 class="mb-3">Chi tiết đơn hàng #${order.id}</h3>

<%-- Thong tin khach hang --%>
<div class="card mb-4">
    <div class="card-header fw-bold">Thông tin khách hàng</div>
    <div class="card-body">
        <table class="table table-borderless mb-0">
            <tr>
                <th style="width:150px">Khách hàng:</th>
                <td>${order.customerName}</td>
            </tr>
            <tr>
                <th>Số điện thoại:</th>
                <td>${order.phone}</td>
            </tr>
            <tr>
                <th>Địa chỉ:</th>
                <td>${order.address}</td>
            </tr>
            <tr>
                <th>Ngày đặt:</th>
                <td>${order.createdAt}</td>
            </tr>
            <tr>
                <th>Trạng thái:</th>
                <td>
                    <c:choose>
                        <c:when test="${order.status == 'DELIVERED'}">
                            <span class="badge bg-success">Đã giao</span>
                        </c:when>
                        <c:otherwise>
                            <span class="badge bg-warning text-dark">Đang xử lý</span>
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
        </table>
    </div>
</div>

<%-- Danh sach san pham trong don --%>
<div class="card mb-4">
    <div class="card-header fw-bold">Sản phẩm đã mua</div>
    <table class="table table-bordered align-middle mb-0">
        <thead class="table-light">
            <tr>
                <th>#</th>
                <th>Sản phẩm</th>
                <th class="text-end">Đơn giá</th>
                <th class="text-center">Số lượng</th>
                <th class="text-end">Thành tiền</th>
            </tr>
        </thead>
        <tbody>
            <c:forEach var="item" items="${order.items}" varStatus="stt">
                <tr>
                    <td>${stt.index + 1}</td>
                    <td>${item.productName}</td>
                    <td class="text-end"><fmt:formatNumber value="${item.price}" type="number"/> đ</td>
                    <td class="text-center">${item.quantity}</td>
                    <td class="text-end">
                        <fmt:formatNumber value="${item.price * item.quantity}" type="number"/> đ
                    </td>
                </tr>
            </c:forEach>
        </tbody>
        <tfoot>
            <tr class="fw-bold">
                <td colspan="4" class="text-end">Tổng cộng:</td>
                <td class="text-end text-danger">
                    <fmt:formatNumber value="${order.totalAmount}" type="number"/> đ
                </td>
            </tr>
        </tfoot>
    </table>
</div>

<%-- Nut hanh dong --%>
<div class="d-flex gap-2">
    <a href="${pageContext.request.contextPath}/admin/orders" class="btn btn-secondary">← Quay lại danh sách</a>
    <c:if test="${order.status != 'DELIVERED'}">
        <form action="${pageContext.request.contextPath}/admin/orders" method="post" class="d-inline">
            <input type="hidden" name="id" value="${order.id}">
            <button type="submit" class="btn btn-success">Đánh dấu đã giao</button>
        </form>
    </c:if>
</div>

<%@ include file="admin-footer.jsp" %>
