<%@ include file="admin-header.jsp" %>

<div class="d-flex justify-content-between mb-3">
    <h3>Quản lý danh mục</h3>
    <a href="${pageContext.request.contextPath}/admin/categories?action=new" class="btn btn-success">+ Thêm danh mục</a>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<table class="table table-bordered table-hover align-middle">
    <thead class="table-light">
        <tr>
            <th>ID</th><th>Tên danh mục</th><th>Mô tả</th><th>Hành động</th>
        </tr>
    </thead>
    <tbody>
        <c:forEach var="cat" items="${categories}">
            <tr>
                <td>${cat.id}</td>
                <td>${cat.name}</td>
                <td>${cat.description}</td>
                <td>
                    <a href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${cat.id}"
                       class="btn btn-sm btn-primary">Sửa</a>
                    <a href="${pageContext.request.contextPath}/admin/categories?action=delete&id=${cat.id}"
                       class="btn btn-sm btn-danger" onclick="return confirm('Xóa danh mục này?')">Xóa</a>
                </td>
            </tr>
        </c:forEach>
        <c:if test="${empty categories}">
            <tr><td colspan="4" class="text-center text-muted">Chưa có danh mục</td></tr>
        </c:if>
    </tbody>
</table>

<%@ include file="admin-footer.jsp" %>
