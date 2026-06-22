<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between mb-3">
    <h3>Quản lý sản phẩm</h3>
    <a href="${pageContext.request.contextPath}/admin/products?action=new" class="btn btn-success">+ Thêm sản phẩm</a>
</div>

<table class="table table-bordered table-hover align-middle">
    <thead class="table-light">
        <tr>
            <th>ID</th><th>Tên</th><th>Giá</th><th>Số lượng</th><th>Hành động</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="p" items="${products}">
            <tr>
                <td>${p.id}</td>
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
    </tbody>
</table>

<%@ include file="admin-footer.jsp" %>
