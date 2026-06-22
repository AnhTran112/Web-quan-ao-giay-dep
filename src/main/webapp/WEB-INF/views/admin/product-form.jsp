<%@ include file="admin-header.jsp" %>

<%-- Dung chung cho them moi va sua. Neu co "product" thi la sua. --%>
<h3 class="mb-3">${empty product ? 'Thêm sản phẩm' : 'Sửa sản phẩm'}</h3>

<form action="${pageContext.request.contextPath}/admin/products" method="post" style="max-width: 600px;">
    <input type="hidden" name="id" value="${product.id}">

    <div class="mb-3">
        <label class="form-label">Tên sản phẩm</label>
        <input type="text" name="name" class="form-control" value="${product.name}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Danh mục</label>
        <select name="categoryId" class="form-select">
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.id}" ${cat.id == product.categoryId ? 'selected' : ''}>${cat.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="mb-3">
        <label class="form-label">Giá</label>
        <input type="number" name="price" class="form-control" value="${product.price}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Số lượng</label>
        <input type="number" name="quantity" class="form-control" value="${product.quantity}" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Tên file ảnh</label>
        <input type="text" name="image" class="form-control" value="${product.image}" placeholder="vd: sneaker.jpg">
    </div>
    <div class="mb-3">
        <label class="form-label">Mô tả</label>
        <textarea name="description" class="form-control" rows="3">${product.description}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="admin-footer.jsp" %>
