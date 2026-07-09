<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<h3 class="mb-3">Giỏ hàng</h3>

<c:if test="${param.error == 'out_of_stock'}">
    <div class="alert alert-danger" role="alert">
        Sản phẩm bạn chọn đã vượt quá số lượng tồn kho hiện có!
    </div>
</c:if>
<c:if test="${param.error == 'invalid_product'}">
    <div class="alert alert-danger" role="alert">
        Sản phẩm không hợp lệ!
    </div>
</c:if>

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
                    <c:choose>
                        <c:when test="${not empty item.productVariants}">
                            <form action="${pageContext.request.contextPath}/cart" method="post" class="mt-1">
                                <input type="hidden" name="action" value="updateSize">
                                <input type="hidden" name="productId" value="${item.productId}">
                                <input type="hidden" name="oldVariantId" value="${item.variantId}">
                                <select name="newVariantId" class="form-select form-select-sm" style="width: auto; display: inline-block;" onchange="this.form.submit()">
                                    <c:forEach var="v" items="${item.productVariants}">
                                        <option value="${v.id}" ${v.id == item.variantId ? 'selected' : ''}>${v.name}</option>
                                    </c:forEach>
                                </select>
                            </form>
                        </c:when>
                        <c:when test="${not empty item.variantName}">
                            <small class="text-muted">Phân loại: ${item.variantName}</small>
                        </c:when>
                    </c:choose>
                </td>
                <td><fmt:formatNumber value="${item.price}" type="number" maxFractionDigits="0"/> đ</td>
                <td>
                    <form action="${pageContext.request.contextPath}/cart" method="post" class="d-flex align-items-center">
                        <input type="hidden" name="action" value="update">
                        <input type="hidden" name="productId" value="${item.productId}">
                        <input type="hidden" name="variantId" value="${empty item.variantId ? 0 : item.variantId}">
                        <input type="number" name="quantity" class="form-control form-control-sm me-2" 
                               value="${item.quantity}" min="1" style="width: 70px;" onchange="this.form.submit()">
                    </form>
                </td>
                <td><fmt:formatNumber value="${item.subtotal}" type="number" maxFractionDigits="0"/> đ</td>
                <td>
                    <form action="${pageContext.request.contextPath}/cart" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="remove">
                        <input type="hidden" name="productId" value="${item.productId}">
                        <input type="hidden" name="variantId" value="${empty item.variantId ? 0 : item.variantId}">
                        <button type="submit" class="btn btn-sm btn-danger">Xóa</button>
                    </form>
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

<c:if test="${empty cart and not empty suggestedProducts}">
    <h5 class="mt-5 mb-3 text-center">Gợi ý cho bạn</h5>
    <div class="row row-cols-2 row-cols-md-4 g-3 mb-4">
        <c:forEach var="p" items="${suggestedProducts}">
            <div class="col">
                <div class="card h-100">
                    <a href="${pageContext.request.contextPath}/product?id=${p.id}"><img src="${pageContext.request.contextPath}/assets/images/${p.image}" class="card-img-top" style="object-fit: cover; height: 150px;"></a>
                    <div class="card-body p-2 text-center">
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="text-decoration-none text-dark d-block text-truncate small">${p.name}</a>
                        <strong class="text-danger small"><fmt:formatNumber value="${p.price}" type="number"/>đ</strong>
                    </div>
                </div>
            </div>
        </c:forEach>
    </div>
</c:if>

<div class="text-end">
    <a href="${pageContext.request.contextPath}/home" class="btn btn-outline-primary me-2">← Tiếp tục mua sắm</a>
    <c:if test="${not empty cart}">
        <a href="${pageContext.request.contextPath}/checkout" class="btn btn-primary">Đặt hàng →</a>
    </c:if>
</div>

<%@ include file="common/footer.jsp" %>
