<%@ include file="admin-header.jsp" %>

<%-- Dung chung cho them moi va sua. Neu co "product" thi la sua. --%>
<div class="d-flex justify-content-between align-items-center mb-3">
    <h3 class="mb-0 fw-bold">${empty product or product.id == 0 ? 'Thêm sản phẩm' : 'Sửa sản phẩm'}</h3>
    <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary btn-sm">← Quay lại danh sách</a>
</div>

<c:if test="${not empty error}">
    <div class="alert alert-danger">${error}</div>
</c:if>

<%--
    enctype="multipart/form-data" bat buoc khi form co input type="file".
    Khong co dong nay, Servlet chi nhan duoc text, khong nhan duoc file.
--%>
<form action="${pageContext.request.contextPath}/admin/products" method="post" enctype="multipart/form-data">
    <input type="hidden" name="id" value="${product.id > 0 ? product.id : ''}">

    <div class="row g-3">
        <!-- ===== COT TRAI: thong tin + phan loai ===== -->
        <div class="col-lg-8">
            <div class="admin-card p-4 mb-3">
                <div class="form-section-title">Thông tin cơ bản</div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Tên sản phẩm</label>
                    <input type="text" name="name" class="form-control" value="<c:out value='${product.name}'/>" required>
                </div>

                <div class="row g-3">
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Danh mục</label>
                        <select name="categoryId" class="form-select">
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}" ${cat.id == product.categoryId ? 'selected' : ''}>${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Giá (VNĐ)</label>
                        <input type="number" name="price" class="form-control" value="${product.price}" min="0" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Số lượng</label>
                        <input type="number" name="quantity" class="form-control" value="${product.quantity}" min="0" required>
                    </div>
                    <div class="col-md-6">
                        <label class="form-label fw-semibold">Giảm giá (%)</label>
                        <input type="number" name="discountPercent" class="form-control"
                               value="${empty product.discountPercent ? 0 : product.discountPercent}" min="0" max="100">
                    </div>
                </div>
                <div class="form-text mt-1">Giảm giá để 0 nếu không áp dụng. Tính cho cả giá gốc và giá phân loại.</div>

                <div class="mt-3">
                    <label class="form-label fw-semibold">Mô tả</label>
                    <textarea name="description" class="form-control" rows="4"><c:out value="${product.description}"/></textarea>
                </div>
            </div>

            <div class="admin-card p-4">
                <div class="d-flex justify-content-between align-items-center mb-2">
                    <div class="form-section-title mb-0 border-0 pb-0">Phân loại</div>
                    <button type="button" class="btn btn-outline-primary btn-sm" onclick="addVariantRow()">+ Thêm phân loại</button>
                </div>
                <p class="text-muted small mb-2">Để trống nếu sản phẩm không có phân loại. Mỗi loại có giá và tồn kho riêng.</p>
                <div class="table-responsive">
                    <table class="table table-sm align-middle variant-table mb-0" id="variantTable">
                        <thead>
                            <tr>
                                <th>Tên loại</th>
                                <th style="width:150px;">Giá (VNĐ)</th>
                                <th style="width:110px;">Kho</th>
                                <th style="width:48px;"></th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="v" items="${product.variants}">
                                <tr>
                                    <td><input type="text" name="variantName" class="form-control" value="<c:out value='${v.name}'/>" placeholder="VD: Size 40, Màu Đỏ..."></td>
                                    <td><input type="number" name="variantPrice" class="form-control" value="${v.price}" min="0"></td>
                                    <td><input type="number" name="variantQty" class="form-control" value="${v.quantity}" min="0"></td>
                                    <td><button type="button" class="btn btn-outline-danger btn-sm" onclick="removeVariantRow(this)">×</button></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>
        </div>

        <!-- ===== COT PHAI: anh + luu ===== -->
        <div class="col-lg-4">
            <div class="admin-card p-4 mb-3">
                <div class="form-section-title">Ảnh sản phẩm</div>
                <%-- Neu dang sua va da co anh, hien preview de admin biet anh hien tai --%>
                <c:if test="${not empty product.image}">
                    <div class="mb-3 text-center">
                        <img src="${pageContext.request.contextPath}/assets/images/${product.image}"
                             alt="Ảnh hiện tại" class="img-preview-lg">
                        <div class="text-muted small mt-1">${product.image}</div>
                    </div>
                </c:if>
                <%--
                    input name="imageFile": Servlet doc bang req.getPart("imageFile").
                    input name="image" (hidden): giu ten anh cu, chi bi ghi de neu co file moi.
                --%>
                <input type="hidden" name="image" value="${product.image}">
                <input type="file" name="imageFile" class="form-control" accept="image/*">
                <div class="form-text">Chấp nhận .jpg .png .gif — tối đa 5 MB. Bỏ trống để giữ ảnh cũ.</div>
            </div>

            <div class="admin-card p-4">
                <button type="submit" class="btn btn-primary w-100 mb-2">Lưu sản phẩm</button>
                <a href="${pageContext.request.contextPath}/admin/products" class="btn btn-outline-secondary w-100">Hủy</a>
            </div>
        </div>
    </div>
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
