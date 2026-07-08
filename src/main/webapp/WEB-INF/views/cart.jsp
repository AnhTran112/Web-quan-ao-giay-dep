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
        <c:forEach var="item" items="${cartItems}">
            <tr>
                <td>
                    <img src="${pageContext.request.contextPath}/assets/images/${item.image}" alt="${item.name}" style="width:50px; height:50px; object-fit:cover;" class="me-2 rounded">
                    ${item.name}
                </td>
                <td><fmt:formatNumber value="${item.price}" type="number"/> đ</td>
                <td style="width: 150px;">
                    <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center m-0">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="productId" value="${item.productId}">
                        <input type="number" name="quantity" class="form-control form-control-sm me-1 text-center" value="${item.quantity}" min="1" style="width: 60px;">
                        <button type="submit" class="btn btn-sm btn-outline-secondary">Lưu</button>
                    </form>
                </td>
                <td class="fw-bold"><fmt:formatNumber value="${item.subtotal}" type="number"/> đ</td>
                <td>
                    <a href="${pageContext.request.contextPath}/cart?action=remove&productId=${item.productId}"
                       class="btn btn-sm btn-danger">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty cartItems}">
            <tr><td colspan="5" class="text-center text-muted py-4">Giỏ hàng trống</td></tr>
        </c:if>
    </tbody>
</table>

<c:if test="${not empty cartItems}">
    <div class="d-flex justify-content-between align-items-center mt-3">
        <h4 class="mb-0">Tổng tiền: <span class="text-danger fw-bold"><fmt:formatNumber value="${cartTotal}" type="number"/> đ</span></h4>
        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary btn-lg px-5">Tiến hành đặt hàng →</a>
    </div>
</c:if>

<%@ include file="common/footer.jsp" %>
