<%@ include file="common/header.jsp" %>

<h3 class="mb-3">Giỏ hàng</h3>

<table class="table table-bordered align-middle">
    <thead class="table-light">
        <tr>
            <th>Sản phẩm</th>
            <th>Giá</th>
            <th>Số lượng</th>
            <th>Thành tiền</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="item" items="${sessionScope.cart}">
            <tr>
                <td>${item.name}</td>
                <td><fmt:formatNumber value="${item.price}" type="number"/> đ</td>
                <td>${item.quantity}</td>
                <td><fmt:formatNumber value="${item.subtotal}" type="number"/> đ</td>
                <td>
                    <a href="${pageContext.request.contextPath}/cart?action=remove&productId=${item.productId}"
                       class="btn btn-sm btn-danger">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty sessionScope.cart}">
            <tr><td colspan="5" class="text-center text-muted">Giỏ hàng trống</td></tr>
        </c:if>
    </tbody>
</table>

<div class="text-end">
    <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Đặt hàng →</a>
</div>

<%@ include file="common/footer.jsp" %>
