<%@ include file="admin-header.jsp" %>

<%-- Dung chung cho them moi va sua. Neu co "category" thi la sua. --%>
<h3 class="mb-3">${empty category ? 'Thêm danh mục' : 'Sửa danh mục'}</h3>

<form action="${pageContext.request.contextPath}/admin/categories" method="post" style="max-width: 600px;">
    <input type="hidden" name="id" value="${category.id}">

    <div class="mb-3">
        <label class="form-label">Tên danh mục</label>
        <input type="text" name="name" class="form-control" value="<c:out value='${category.name}'/>" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Mô tả</label>
        <textarea name="description" class="form-control" rows="3"><c:out value="${category.description}"/></textarea>
    </div>
    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/categories" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="admin-footer.jsp" %>
