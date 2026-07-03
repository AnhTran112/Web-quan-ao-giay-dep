<%@ include file="common/header.jsp" %>
<%-- Dinh dang so kieu Viet Nam: dau cham ngan cach hang nghin (1.000.000) --%>
<fmt:setLocale value="vi_VN" />

<!-- Banner chao mung -->
<div class="hero-banner">
    <h1>ShoeShop — Giày dép &amp; Thời trang</h1>
    <p>Tuyển chọn giày sneaker, giày tây, áo thun, quần jean chính hãng — giá tốt mỗi ngày.</p>
</div>

<div class="row">
    <!-- Cot trai: bo loc -->
    <div class="col-md-3">
        <div class="card filter-card mb-3">
            <div class="card-header fw-bold bg-white">Bộ lọc</div>
            <div class="card-body">
                <form action="${pageContext.request.contextPath}/home" method="get">
                    <div class="mb-3">
                        <label class="form-label">Tìm kiếm</label>
                        <input type="text" name="keyword" class="form-control"
                               placeholder="Nhập tên sản phẩm..."
                               value="<c:out value='${keyword}'/>">
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Danh mục</label>
                        <select name="categoryId" class="form-select">
                            <option value="">-- Tất cả --</option>
                            <c:forEach var="cat" items="${categories}">
                                <option value="${cat.id}">${cat.name}</option>
                            </c:forEach>
                        </select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label">Khoảng giá</label>
                        <%-- Gia tri hien tai: mac dinh 0 -> priceMax, giu lai neu da loc --%>
                        <c:set var="minVal" value="${empty minPrice ? 0 : minPrice}" />
                        <c:set var="maxVal" value="${empty maxPrice ? priceMax : maxPrice}" />
                        <div class="price-values mb-2">
                            <span id="priceMinLabel"><fmt:formatNumber value="${minVal}" type="number" maxFractionDigits="0"/></span>
                            —
                            <span id="priceMaxLabel"><fmt:formatNumber value="${maxVal}" type="number" maxFractionDigits="0"/></span>
                            VND
                        </div>
                        <%-- Mot thanh, keo duoc 2 dau: 2 input range chong len nhau --%>
                        <div class="range-slider">
                            <div class="range-track"></div>
                            <div class="range-fill" id="rangeFill"></div>
                            <input type="range" id="rangeMin" name="minPrice"
                                   min="0" max="${priceMax}" step="10000" value="${minVal}">
                            <input type="range" id="rangeMax" name="maxPrice"
                                   min="0" max="${priceMax}" step="10000" value="${maxVal}">
                        </div>
                    </div>
                    <button type="submit" class="btn btn-primary w-100">Tìm kiếm</button>
                </form>
            </div>
        </div>
    </div>

    <!-- Cot phai: danh sach san pham -->
    <div class="col-md-9">
        <div class="section-head d-flex align-items-center justify-content-between mb-3">
            <h4 class="fw-bold mb-0">Tất cả sản phẩm</h4>
            <span class="text-muted small">${products.size()} sản phẩm</span>
        </div>

        <div class="row row-cols-1 row-cols-sm-2 row-cols-xl-3 g-3">
            <c:forEach var="p" items="${products}">
                <div class="col">
                    <div class="card product-card h-100">
                        <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-thumb">
                            <c:if test="${p.discountPercent > 0}">
                                <span class="discount-badge card-discount">-${p.discountPercent}%</span>
                            </c:if>
                            <img src="${pageContext.request.contextPath}/assets/images/${p.image}"
                                 alt="${p.name}"
                                 onerror="this.src='https://via.placeholder.com/400x400?text=No+Image'">
                        </a>
                        <div class="card-body d-flex flex-column">
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}" class="product-title-link">
                                <h6 class="card-title">${p.name}</h6>
                            </a>
                            <div class="price-line mb-1">
                                <c:choose>
                                    <c:when test="${p.discountPercent > 0}">
                                        <span class="price-now"><fmt:formatNumber value="${p.price * (100 - p.discountPercent) / 100}" type="number" maxFractionDigits="0"/>đ</span>
                                        <span class="price-old"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/>đ</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="price-now"><fmt:formatNumber value="${p.price}" type="number" maxFractionDigits="0"/>đ</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            <div class="mb-3">
                                <c:choose>
                                    <c:when test="${p.quantity > 0}"><span class="stock-tag in">Còn hàng</span></c:when>
                                    <c:otherwise><span class="stock-tag out">Tạm hết hàng</span></c:otherwise>
                                </c:choose>
                            </div>
                            <a href="${pageContext.request.contextPath}/product?id=${p.id}"
                               class="btn btn-primary w-100 mt-auto">Xem chi tiết</a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>

        <c:if test="${empty products}">
            <div class="empty-state text-center text-muted py-5">
                <p class="mb-0">Không tìm thấy sản phẩm nào phù hợp.</p>
            </div>
        </c:if>

        <c:if test="${totalPages > 1}">
            <nav aria-label="Page navigation" class="mt-4">
                <ul class="pagination justify-content-center">
                    <c:forEach begin="1" end="${totalPages}" var="i">
                        <li class="page-item ${currentPage == i ? 'active' : ''}">
                            <a class="page-link" href="${pageContext.request.contextPath}/home?page=${i}&categoryId=${param.categoryId}&minPrice=${param.minPrice}&maxPrice=${param.maxPrice}&keyword=${param.keyword}">${i}</a>
                        </li>
                    </c:forEach>
                </ul>
            </nav>
        </c:if>
    </div>
</div>

<%-- Dieu khien thanh truot 2 dau: khong cho 2 dau vuot qua nhau + cap nhat nhan & vung to mau --%>
<script>
(function () {
    var minEl  = document.getElementById('rangeMin');
    var maxEl  = document.getElementById('rangeMax');
    var fill   = document.getElementById('rangeFill');
    var minLbl = document.getElementById('priceMinLabel');
    var maxLbl = document.getElementById('priceMaxLabel');
    if (!minEl || !maxEl) return;

    var cap = Number(maxEl.max) || 1;

    function fmt(v) { return Number(v).toLocaleString('vi-VN'); }

    function update() {
        var a = Number(minEl.value);
        var b = Number(maxEl.value);
        // Chan 2 dau vuot qua nhau (dau nao dang keo thi bi giu lai)
        if (a > b) {
            if (document.activeElement === minEl) { minEl.value = b; a = b; }
            else { maxEl.value = a; b = a; }
        }
        minLbl.textContent = fmt(a);
        maxLbl.textContent = fmt(b);
        fill.style.left  = (a / cap * 100) + '%';
        fill.style.right = (100 - b / cap * 100) + '%';
    }

    minEl.addEventListener('input', update);
    maxEl.addEventListener('input', update);
    update();
})();
</script>

<%@ include file="common/footer.jsp" %>
