<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0">Quản lý sản phẩm</h3>
    <a href="${pageContext.request.contextPath}/admin/products?action=new" class="btn btn-success">+ Thêm sản phẩm</a>
</div>

<%--
    Form tim kiem dung GET (khong phai POST) vi day la thao tac DOC du lieu.
    URL se co dang ?keyword=... de co the bookmark hoac chia se ket qua.
--%>
<form action="${pageContext.request.contextPath}/admin/products" method="get" class="d-flex gap-2 mb-3" style="max-width:420px;">
    <input type="text" name="keyword" class="form-control" placeholder="Tìm theo tên sản phẩm..." value="${keyword}">
    <button class="btn btn-outline-secondary" type="submit">Tìm</button>
    <c:if test="${not empty keyword}">
        <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-danger">Xóa lọc</a>
    </c:if>
</form>

<c:if test="${not empty keyword}">
    <p class="text-muted">Kết quả tìm kiếm cho: <strong>${keyword}</strong> — ${fn:length(products)} sản phẩm</p>
</c:if>

<table class="table table-bordered table-hover align-middle">
    <thead class="table-light">
        <tr>
            <th>ID</th><th>Ảnh</th><th>Tên</th><th>Giá</th><th>Số lượng</th><th>Hành động</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${products}">
            <tr>
                <td>${p.id}</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty p.image}">
                            <img src="${pageContext.request.contextPath}/assets/images/${p.image}"
                                 alt="${p.name}" style="height:50px; width:60px; object-fit:cover; border-radius:4px;">
                        </c:when>
                        <c:otherwise>
                            <span class="text-muted small">Chưa có</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>${p.name}</td>
                <td><fmt:formatNumber value="${p.price}" type="number"/> đ</td>
                <td>${p.quantity}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/products?action=edit&id=${p.id}"
                       class="btn btn-sm btn-primary">Sửa</a>
                    <a href="${pageContext.request.contextPath}/admin/products?action=delete&id=${p.id}"
                       class="btn btn-sm btn-danger" onclick="return confirm('Xóa sản phẩm này?')">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty products}">
            <tr><td colspan="6" class="text-center text-muted">Không tìm thấy sản phẩm nào.</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="admin-footer.jsp" %>
