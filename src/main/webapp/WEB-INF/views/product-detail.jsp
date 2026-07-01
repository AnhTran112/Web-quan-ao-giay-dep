<%@ include file="common/header.jsp" %>
<fmt:setLocale value="vi_VN" />

<%-- Chi tiet san pham. Du lieu: request attribute "product", "categoryName" --%>

<!-- Breadcrumb kieu Shopee: Trang chu > Danh muc > Ten san pham -->
<nav aria-label="breadcrumb" class="shopee-crumb my-2">
    <a href="${pageContext.request.contextPath}/home">Trang chủ</a>
    <span class="crumb-sep">›</span>
    <a href="${pageContext.request.contextPath}/home?categoryId=${product.categoryId}">
        ${not empty categoryName ? categoryName : 'Sản phẩm'}</a>
    <span class="crumb-sep">›</span>
    <span class="text-muted">${product.name}</span>
</nav>

<!-- Khoi thong tin chinh: anh ben trai, thong tin + mua hang ben phai -->
<div class="card border-0 shadow-sm p-3 mb-4">
    <div class="row g-4">
        <div class="col-12 col-lg-5">
            <div class="detail-img-wrap">
                <img src="${pageContext.request.contextPath}/assets/images/${product.image}"
                     alt="${product.name}"
                     onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'">
            </div>
        </div>
        <div class="col-12 col-lg-7">
            <h3 class="fw-bold mb-1">${product.name}</h3>

            <div class="rating-row">
                ${not empty categoryName ? categoryName : 'Sản phẩm'}
                &nbsp;|&nbsp;
                <c:choose>
                    <c:when test="${product.quantity > 0}">Còn hàng</c:when>
                    <c:otherwise>Tạm hết hàng</c:otherwise>
                </c:choose>
            </div>

            <div class="detail-price-box">
                <c:choose>
                    <c:when test="${product.hasVariants}">
                        <span class="price" id="priceNow">
                            <fmt:formatNumber value="${product.minPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                            <c:if test="${product.minPrice != product.maxPrice}">
                                – <fmt:formatNumber value="${product.maxPrice * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/>
                            </c:if> đ
                        </span>
                    </c:when>
                    <c:otherwise>
                        <span class="price" id="priceNow">
                            <fmt:formatNumber value="${product.price * (100 - product.discountPercent) / 100}" type="number" maxFractionDigits="0"/> đ
                        </span>
                    </c:otherwise>
                </c:choose>
                <c:if test="${product.discountPercent > 0}">
                    <span class="price-old"><fmt:formatNumber value="${product.hasVariants ? product.minPrice : product.price}" type="number" maxFractionDigits="0"/> đ</span>
                    <span class="discount-badge">-${product.discountPercent}%</span>
                </c:if>
            </div>

            <%-- Nut chon phan loai (chi hien khi san pham co variants) --%>
            <c:if test="${product.hasVariants}">
                <div class="mb-3">
                    <label class="form-label fw-semibold">Phân loại</label>
                    <div class="d-flex flex-wrap gap-2" id="variantGroup">
                        <c:forEach var="v" items="${product.variants}">
                            <button type="button" class="btn btn-outline-secondary variant-btn"
                                    data-id="${v.id}"
                                    data-price="${v.price * (100 - product.discountPercent) / 100}"
                                    data-stock="${v.quantity}">${v.name}</button>
                        </c:forEach>
                    </div>
                </div>
            </c:if>

            <%-- Form them vao gio hang voi bo tang/giam so luong --%>
            <form action="${pageContext.request.contextPath}/cart" method="post"
                  onsubmit="return (${product.hasVariants} === false) || document.getElementById('variantId').value !== '' || (alert('Vui lòng chọn phân loại'), false);">
                <input type="hidden" name="action" value="add">
                <input type="hidden" name="productId" value="${product.id}">
                <input type="hidden" name="variantId" id="variantId" value="">

                <label class="form-label fw-semibold">Số lượng</label>
                <div class="input-group mb-3" style="max-width: 160px;">
                    <button type="button" class="btn btn-outline-secondary" onclick="changeQty(-1)">−</button>
                    <input type="number" id="qty" name="quantity" value="1" min="1"
                           max="${product.quantity}" class="form-control text-center">
                    <button type="button" class="btn btn-outline-secondary" onclick="changeQty(1)">+</button>
                </div>

                <div class="d-flex gap-2 flex-wrap product-actions">
                    <button type="submit" class="btn btn-success btn-lg"
                            <c:if test="${product.quantity == 0}">disabled</c:if>>
                        Thêm vào giỏ
                    </button>
                    <button type="submit" class="btn btn-buy-now btn-lg"
                            <c:if test="${product.quantity == 0}">disabled</c:if>>
                        Mua ngay
                    </button>
                </div>
            </form>

            <%-- Dai cam ket dich vu, tao cam giac chuyen nghiep --%>
            <div class="commit-strip">
                <span>Giao hàng toàn quốc</span>
                <span>Đổi trả trong 7 ngày</span>
                <span>Cam kết hàng chính hãng</span>
            </div>
        </div>
    </div>
</div>

<!-- Mo ta san pham (kieu Shopee: mot khu rieng ben duoi) -->
<h5 class="section-title">Mô tả sản phẩm</h5>
<div class="desc-box mb-4"><c:choose><c:when test="${not empty product.description}">${product.description}</c:when><c:otherwise>Sản phẩm chính hãng, chất lượng đảm bảo. Hiện chưa có mô tả chi tiết cho sản phẩm này.</c:otherwise></c:choose></div>

<!-- Bang thong so -->
<h5 class="section-title">Thông tin chi tiết</h5>
<div class="card border-0 shadow-sm mb-4">
    <table class="table table-striped mb-0 spec-table">
        <tbody>
            <tr><th>Mã sản phẩm</th><td>#${product.id}</td></tr>
            <tr><th>Danh mục</th><td>${not empty categoryName ? categoryName : 'Đang cập nhật'}</td></tr>
            <tr><th>Giá bán</th><td><fmt:formatNumber value="${product.price}" type="number"/> đ</td></tr>
            <tr><th>Tồn kho</th><td>${product.quantity} sản phẩm</td></tr>
        </tbody>
    </table>
</div>

<script>
    // Tang/giam so luong, khong cho xuong duoi 1
    function changeQty(delta) {
        var input = document.getElementById('qty');
        var value = parseInt(input.value) || 1;
        value += delta;
        if (value < 1) value = 1;
        input.value = value;
    }

    // Chon phan loai: doi gia + ton kho + gioi han so luong theo loai dang chon
    document.querySelectorAll('.variant-btn').forEach(function (btn) {
        btn.addEventListener('click', function () {
            document.querySelectorAll('.variant-btn').forEach(function (b) {
                b.classList.remove('active', 'btn-primary');
                b.classList.add('btn-outline-secondary');
            });
            btn.classList.add('active', 'btn-primary');
            btn.classList.remove('btn-outline-secondary');
            document.getElementById('variantId').value = btn.dataset.id;
            var price = Number(btn.dataset.price);
            document.getElementById('priceNow').innerText = price.toLocaleString('vi-VN') + ' đ';
            var qty = document.getElementById('qty');
            qty.max = btn.dataset.stock;
            if (Number(qty.value) > Number(btn.dataset.stock)) qty.value = btn.dataset.stock;
        });
    });
</script>

<%@ include file="common/footer.jsp" %>
