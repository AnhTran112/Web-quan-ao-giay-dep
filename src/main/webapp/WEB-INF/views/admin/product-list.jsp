<%@ include file="admin-header.jsp" %>
<fmt:setLocale value="vi_VN" />

<div class="d-flex justify-content-between align-items-center mb-3">
    <div>
        <h3 class="mb-0 fw-bold">Quản lý sản phẩm</h3>
        <span class="text-muted small">${fn:length(products)} sản phẩm</span>
    </div>
    <a href="${pageContext.request.contextPath}/admin/products?action=new" class="btn btn-success">
        + Thêm sản phẩm
    </a>
</div>

<%--
    Form tim kiem dung GET (khong phai POST) vi day la thao tac DOC du lieu.
    URL se co dang ?keyword=... de co the bookmark hoac chia se ket qua.
--%>
<form action="${pageContext.request.contextPath}/admin/products" method="get" class="d-flex gap-2 mb-3" style="max-width:460px;">
    <input type="text" name="keyword" class="form-control" placeholder="Tìm theo tên sản phẩm..." value="${keyword}">
    <button class="btn btn-primary" type="submit">Tìm</button>
    <c:if test="${not empty keyword}">
        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary">Xóa lọc</a>
    </c:if>
</form>

<c:if test="${not empty keyword}">
    <p class="text-muted">Kết quả tìm kiếm cho: <strong>${keyword}</strong> — ${fn:length(products)} sản phẩm</p>
</c:if>

<div class="admin-card">
    <div class="table-responsive">
        <table class="table admin-table table-hover align-middle mb-0">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ảnh</th>
                    <th>Tên sản phẩm</th>
                    <th class="text-end">Giá</th>
                    <th class="text-center">Giảm</th>
                    <th class="text-center">Kho</th>
                    <th class="text-end">Hành động</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="p" items="${products}">
                    <tr>
                        <td class="text-muted">#${p.id}</td>
                        <td>
                            <c:choose>
                                <c:when test="${not empty p.image}">
                                    <img src="${pageContext.request.contextPath}/assets/images/${p.image}"
                                         alt="${p.name}" class="admin-thumb">
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted small">Chưa có</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="fw-semibold">${p.name}</td>
                        <td class="text-end">
                            <c:choose>
                                <c:when test="${p.discountPercent > 0}">
                                    <span class="fw-bold text-danger"><fmt:formatNumber value="${p.price * (100 - p.discountPercent) / 100}" type="number" maxFractionDigits="0"/>đ</span><br>
                                    <span class="price-old" style="margin-left:0;"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/>đ</span>
                                </c:when>
                                <c:otherwise>
                                    <span class="fw-semibold"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/>đ</span>
                                </c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <c:choose>
                                <c:when test="${p.discountPercent > 0}"><span class="discount-badge" style="margin-left:0;">-${p.discountPercent}%</span></c:when>
                                <c:otherwise><span class="text-muted">—</span></c:otherwise>
                            </c:choose>
                        </td>
                        <td class="text-center">
                            <span class="qty-badge ${p.quantity > 0 ? 'ok' : 'low'}">${p.quantity}</span>
                        </td>
                        <td class="text-end">
                            <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}"
                               class="btn btn-sm btn-outline-primary">Sửa</a>
                            <a href="${pageContext.request.contextPath}/admin/products?action=delete&id=${p.id}"
                               class="btn btn-sm btn-outline-danger"
                               onclick="return confirm('Xóa sản phẩm này?')">Xóa</a>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty products}">
                    <tr><td colspan="7" class="text-center text-muted py-4">Không tìm thấy sản phẩm nào.</td></tr>
                </c:if>
            </tbody>
        </table>
    </div>
</div>

<%@ include file="admin-footer.jsp" %>
