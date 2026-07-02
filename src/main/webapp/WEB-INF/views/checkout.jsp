<%@ include file="common/header.jsp" %>

<h3 class="mb-3">Thông tin đặt hàng</h3>

<%-- Hien thi thong bao loi neu ton kho khong du --%>
<c:if test="${not empty errorMessage}">
    <div class="alert alert-danger">${errorMessage}</div>
</c:if>

<div class="row">
    <%-- Cot trai: form nhap thong tin khach hang --%>
    <div class="col-md-6">
        <form action="${pageContext.request.contextPath}/checkout" method="post">
            <div class="mb-3">
                <label class="form-label">Họ tên</label>
                <input type="text" name="customerName" class="form-control"
                       value="${customerName}" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Số điện thoại</label>
                <input type="text" name="phone" class="form-control"
                       value="${phone}" required>
            </div>
            <div class="mb-3">
                <label class="form-label">Địa chỉ giao hàng</label>
                <textarea name="address" class="form-control" rows="3" required>${address}</textarea>
            </div>
            <button type="submit" class="btn btn-success">Xác nhận đặt hàng</button>
        </form>
    </div>

    <%-- Cot phai: tom tat gio hang va tong tien --%>
    <div class="col-md-6">
        <div class="card">
            <div class="card-header fw-bold">Đơn hàng của bạn</div>
            <ul class="list-group list-group-flush">
                <c:forEach var="item" items="${cartList}">
                    <li class="list-group-item d-flex justify-content-between">
                        <span>${item.name} × ${item.quantity}</span>
                        <span><fmt:formatNumber value="${item.subtotal}" type="number"/> đ</span>
                    </li>
                </c:forEach>
            </ul>
            <div class="card-footer d-flex justify-content-between fw-bold">
                <span>Tổng cộng</span>
                <span class="text-danger"><fmt:formatNumber value="${totalAmount}" type="number"/> đ</span>
            </div>
        </div>
    </div>
</div>

<%@ include file="common/footer.jsp" %>
