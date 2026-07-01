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
    <div class="mb-3">
        <label class="form-label">Giảm giá (%)</label>
        <input type="number" name="discountPercent" class="form-control"
               value="${empty product.discountPercent ? 0 : product.discountPercent}" min="0" max="100">
        <div class="form-text">Để 0 nếu không giảm giá. Áp dụng cho cả giá gốc và giá phân loại.</div>
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
    <div class="mb-3">
        <label class="form-label d-block fw-semibold">Phân loại (tùy chọn — để trống nếu sản phẩm không có phân loại)</label>
        <table class="table table-sm align-middle" id="variantTable">
            <thead>
                <tr>
                    <th>Tên loại</th>
                    <th style="width:150px;">Giá (VNĐ)</th>
                    <th style="width:110px;">Kho</th>
                    <th style="width:50px;"></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="v" items="${product.variants}">
                    <tr>
                        <td><input type="text" name="variantName" class="form-control" value="${v.name}" placeholder="VD: Size 40, Màu Đỏ..."></td>
                        <td><input type="number" name="variantPrice" class="form-control" value="${v.price}" min="0"></td>
                        <td><input type="number" name="variantQty" class="form-control" value="${v.quantity}" min="0"></td>
                        <td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariantRow(this)">×</button></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
        <button type="button" class="btn btn-outline-secondary btn-sm" onclick="addVariantRow()">+ Thêm phân loại</button>
    </div>

    <button type="submit" class="btn btn-primary">Lưu</button>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-secondary">Hủy</a>
</form>

<script>
    // Them 1 dong phan loai moi vao bang
    function addVariantRow() {
        var tbody = document.querySelector('#variantTable tbody');
        var tr = document.createElement('tr');
        tr.innerHTML =
            '<td><input type="text" name="variantName" class="form-control" placeholder="VD: Size 40, Màu Đỏ..."></td>' +
            '<td><input type="number" name="variantPrice" class="form-control" min="0"></td>' +
            '<td><input type="number" name="variantQty" class="form-control" min="0"></td>' +
            '<td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariantRow(this)">×</button></td>';
        tbody.appendChild(tr);
    }
    // Xoa 1 dong phan loai
    function removeVariantRow(btn) { btn.closest('tr').remove(); }
</script>

<%@ include file="admin-footer.jsp" %>
