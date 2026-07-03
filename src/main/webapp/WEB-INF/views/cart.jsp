<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<h3 class="mb-3">Giỏ hàng</h3>

<table class="table table-bordered align-middle">
    <thead class="table-light">
        <tr>
            <th style="width: 100px;">Ảnh</th>
            <th>Sản phẩm</th>
            <th>Giá</th>
            <th style="width: 150px;">Số lượng</th>
            <th>Thành tiền</th>
            <th></th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="item" items="${cart}">
            <tr>
                <td>
                    <img src="${pageContext.request.contextPath}/assets/images/${item.image}"
                         alt="${item.name}" class="img-fluid rounded" style="max-width: 80px;"
                         onerror="this.src='https://via.placeholder.com/80?text=No+Image'">
                </td>
                <td>
                    <h6 class="mb-1">${item.name}</h6>
                    <c:if test="${not empty item.variantName}">
                        <small class="text-muted">Phân loại: ${item.variantName}</small>
                    </c:if>
                </td>
                <td><fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ</td>
                <td>
                    <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="productId" value="${item.productId}">
                        <input type="hidden" name="variantId" value="${item.variantId != null ? item.variantId : 0}">
                        <input type="number" name="quantity" class="form-control form-control-sm me-2" 
                               value="${item.quantity}" min="1" style="width: 70px;">
                        <button type="submit" class="btn btn-sm btn-outline-secondary">Cập nhật</button>
                    </form>
                </td>
                <td><fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ</td>
                <td>
                    <a href="${pageContext.request.contextPath}/cart?action=remove&productId=${item.productId}&variantId=${item.variantId != null ? item.variantId : 0}"
                       class="btn btn-sm btn-danger">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty cart}">
            <tr><td colspan="6" class="text-center text-muted py-4">Giỏ hàng của bạn đang trống.</td></tr>
        </c:if>
    </tbody>
    <c:if test="${not empty cart}">
        <tfoot class="table-light fw-bold">
            <tr>
                <td colspan="4" class="text-end">Tổng cộng:</td>
                <td colspan="2" class="text-danger fs-5">
                    <fmt:formatNumber value="${total}" type="number" maxFractionDigits="0"/> đ
                </td>
            </tr>
        </tfoot>
    </c:if>
</table>

<div class="text-end">
    <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary me-2">← Tiếp tục mua sắm</a>
    <c:if test="${not empty cart}">
        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Đặt hàng →</a>
    </c:if>
</div>

<%@ include file="common/footer.jsp" %>
