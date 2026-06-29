<%@ include file="admin-header.jsp" %>

<%-- Dung chung cho them moi va sua. Neu co "product" thi la sua. --%>
<h3 class="mb-3">${empty product or product.id == 0 ? 'Thêm sản phẩm' : 'Sửa sản phẩm'}</h3>

<c:if test="${not empty error}">
    <div class="alert alert-danger" style="max-width: 600px;">${error}</div>
</c:if>

<%--
    enctype="multipart/form-data" bat buoc khi form co input type="file".
    Khong co dong nay, Servlet chi nhan duoc text, khong nhan duoc file.
--%>
<form action="${pageContext.request.contextPath}/admin/products" method="post"
      enctype="multipart/form-data" style="max-width: 600px;">
    <input type="hidden" name="id" value="${product.id > 0 ? product.id : ''}">

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
        <label class="form-label">Giá (VNĐ)</label>
        <input type="number" name="price" class="form-control" value="${product.price}" min="0" required>
    </div>
    <div class="mb-3">
        <label class="form-label">Số lượng</label>
        <input type="number" name="quantity" class="form-control" value="${product.quantity}" min="0" required>
    </div>

    <%-- === KHU VUC UPLOAD ANH === --%>
    <div class="mb-3">
        <label class="form-label">Ảnh sản phẩm</label>

        <%-- Neu dang sua va da co anh, hien preview de admin biet anh hien tai --%>
        <c:if test="${not empty product.image}">
            <div class="mb-2">
                <img src="${pageContext.request.contextPath}/assets/images/${product.image}"
                     alt="Ảnh hiện tại" style="height:100px; object-fit:cover; border-radius:4px;">
                <div class="text-muted small mt-1">Ảnh hiện tại: ${product.image}</div>
            </div>
        </c:if>

        <%--
            input name="imageFile": Servlet doc bang req.getPart("imageFile").
            input name="image" (hidden): giu ten anh cu, chi bi ghi de neu co file moi duoc chon.
        --%>
        <input type="hidden" name="image" value="${product.image}">
        <input type="file" name="imageFile" class="form-control" accept="image/*">
        <div class="form-text">Chấp nhận .jpg .png .gif — tối đa 5 MB. Bỏ trống để giữ ảnh cũ.</div>
    </div>

    <div class="mb-3">
        <label class="form-label">Mô tả</label>
        <textarea name="description" class="form-control" rows="3">${product.description}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Hủy</a>
</form>

<%@ include file="admin-footer.jsp" %>
